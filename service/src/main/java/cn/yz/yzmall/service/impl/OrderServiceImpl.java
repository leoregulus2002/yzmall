package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.OrderItemMapper;
import cn.yz.yzmall.dao.OrdersMapper;
import cn.yz.yzmall.dao.ProductSkuMapper;
import cn.yz.yzmall.dao.ShoppingCartMapper;
import cn.yz.yzmall.entity.*;
import cn.yz.yzmall.service.OrderService;
import cn.yz.yzmall.utils.PageHelper;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {

    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redissonClient;
    @Transactional
    public Map<String,String> addOrder(String cids, Orders orders) throws ParseException, SQLException {
        synchronized (this) {
            Map<String, String> map = new HashMap<>();
            // 查询商品
            String[] split = cids.split(",");
            List<Integer> list = new ArrayList<>();
            for (String s : split) {
                list.add(Integer.parseInt(s));
            }
            List<ShoppingCartVO> shoppingCartVOS = shoppingCartMapper.selectShopcartByCid(list);
            boolean isLock = true;
            String[] skuIds = new String[shoppingCartVOS.size()];
            Map<String, RLock> locks = new HashMap<>();
            for (int i = 0;i < shoppingCartVOS.size();i++) {
                String skuId = shoppingCartVOS.get(i).getSkuId();
                RLock lock = redissonClient.getLock(skuId);
                boolean b;
                try {
                    b = lock.tryLock(10, 3, TimeUnit.MINUTES);
                    if (b){
                        skuIds[i] = skuId;
                        locks.put(skuId,lock);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                isLock = isLock && b;
            }
            if (isLock){
                // 判断库存充足　
                boolean flag = true;
                String untitled = "";
                shoppingCartVOS = shoppingCartMapper.selectShopcartByCid(list);
                BigDecimal total = new BigDecimal(0);
                for (ShoppingCartVO shoppingCartVO : shoppingCartVOS) {
                    if (Integer.parseInt(shoppingCartVO.getCartNum()) > shoppingCartVO.getSkuStock()) {
                        flag = false;
                        break;
                    }
                    untitled = untitled + shoppingCartVO.getProductName() + ",";
                    total = total.add(shoppingCartVO.getProductPrice().multiply(new BigDecimal(shoppingCartVO.getCartNum())));
                }

                if (flag) {
                    // 库存充足,保存订单
                    orders.setUntitled(untitled);
                    orders.setCreateTime(new Date());
                    orders.setStatus("1");
                    String orderId = UUID.randomUUID().toString().replace("-", "");
                    orders.setOrderId(orderId);
                    orders.setTotalAmount(total);
                    ordersMapper.insert(orders);
                    // 生成订单快照
                    for (ShoppingCartVO sc : shoppingCartVOS) {
                        String itemId = System.currentTimeMillis() + "" + (new Random().nextInt(89999) + 10000);
                        int cnum = Integer.parseInt(sc.getCartNum());
                        OrderItem orderItem = new OrderItem(itemId, orderId, sc.getProductId(), sc.getProductName(), sc.getProductImg(),
                                sc.getSkuId(), sc.getSkuName(), new BigDecimal(sc.getSellPrice()), cnum, new BigDecimal(sc.getSellPrice() * cnum),
                                df.parse(sc.getCartTime()), new Date(), 0);
                        orderItemMapper.insert(orderItem);
                    }
                    // 扣减库存
                    for (ShoppingCartVO sc : shoppingCartVOS) {
                        String skuId = sc.getSkuId();
                        int newStock = sc.getSkuStock() - Integer.parseInt(sc.getCartNum());

                        ProductSku productSku = new ProductSku();
                        productSku.setStock(newStock);
                        productSku.setSkuId(skuId);
                        productSkuMapper.updateByPrimaryKeySelective(productSku);
                    }
                    for (Integer cid : list) {
                        shoppingCartMapper.deleteByPrimaryKey(cid);
                    }
                    for (String skuId : skuIds) {
                        if (skuId != null && !"".equals(skuId)){
                            locks.get(skuId).unlock();
                        }
                    }
                    map.put("orderId", orderId);
                    map.put("untitled", untitled);
                    map.put("total", orders.getTotalAmount() + "");
                    return map;
                } else {
                    // 库存不足
                    return null;
                }
            }else {
                for (String skuId : skuIds) {
                    if (skuId != null && !"".equals(skuId)){
                        locks.get(skuId).unlock();
                    }
                }
                return null;
            }

        }
    }

    @Override
    public int updateOrderStatus(String orderId, String status) {
        Orders orders = new Orders();
        orders.setOrderId(orderId);
        orders.setStatus(status);
        return ordersMapper.updateByPrimaryKeySelective(orders);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void closeOrder(String orderId) {
        synchronized (this) {
            // 支付超时 还原库存
            Orders cancelOrder = new Orders();
            cancelOrder.setOrderId(orderId);
            cancelOrder.setStatus("6");
            cancelOrder.setCloseType(1);
            ordersMapper.updateByPrimaryKeySelective(cancelOrder);

            Example example1 = new Example(OrderItem.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("orderId", orderId);
            List<OrderItem> orderItems = orderItemMapper.selectByExample(example1);

            for (OrderItem orderItem : orderItems) {
                ProductSku productSku = productSkuMapper.selectByPrimaryKey(orderItem.getSkuId());
                productSku.setStock(productSku.getStock() + orderItem.getBuyCounts());
                productSkuMapper.updateByPrimaryKey(productSku);
            }
        }
    }

    @Override
    public ResultVO listOrder(String userId, String status, int pageNum, int limit) {
        int start = (pageNum-1)*limit;
        List<OrdersVO> ordersVOS = ordersMapper.selectOrders(userId, status, start, limit);
        Example example = new Example(Orders.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        if ("".equals(status)){
            criteria.andEqualTo("status",status);
        }
        int count = ordersMapper.selectCountByExample(example);
        int pageCount = count%limit == 0 ? count / limit : count / limit + 1;
        PageHelper<OrdersVO> ordersVOPageHelper = new PageHelper<>(count, pageCount, ordersVOS);
        return new ResultVO(ResStatus.OK,"success",ordersVOPageHelper );
    }
}
