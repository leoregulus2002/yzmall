package cn.yz.yzmall.dao;

import cn.yz.yzmall.ApiApplication;
import cn.yz.yzmall.entity.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplication.class)
public class UserDaoTest {

   @Autowired
   private CategoryMapper categoryMapper;

   @Autowired
   private ProductMapper productMapper;
   @Autowired
   private ProductCommentsMapper productCommentsMapper;
   @Autowired
   private ShoppingCartMapper shoppingCartMapper;

   @Autowired
   private OrdersMapper ordersMapper;

   @Autowired
   private StringRedisTemplate stringRedisTemplate;
   @Test
   public void testRedis(){
       String s = stringRedisTemplate.opsForValue().get("indexImg");
       System.out.println(s);

   }
   @Test
   public void testCheckClose(){
       Example example = new Example(Orders.class);
       Example.Criteria criteria = example.createCriteria();
       Date time = new Date(System.currentTimeMillis() - 30 * 60 * 1000);
       criteria.andEqualTo("status",1)
               .andLessThan("createTime",time);
       List<Orders> orders = ordersMapper.selectByExample(example);
       for (Orders order : orders) {
           System.out.println(order.getOrderId()+"\t"+order.getCreateTime());
       }
   }
   @Test
   public void testShopCartByCids(){
       List<Integer> cids = new ArrayList<>();
       cids.add(1);
       cids.add(8);
       cids.add(10);
       List<ShoppingCartVO> shoppingCartVOS = shoppingCartMapper.selectShopcartByCid(cids);
       for (ShoppingCartVO shoppingCartVO : shoppingCartVOS) {
           System.out.println(shoppingCartVO);
       }
   }
   @Test
   public void testSelectShopCartByUserId(){
       List<ShoppingCartVO> shoppingCartVOS = shoppingCartMapper.selectShopcartByUserId(1);
       for (ShoppingCartVO shoppingCartVO : shoppingCartVOS) {
           System.out.println(shoppingCartVO);
       }
   }
    @Test
    public void testProductComments(){
        List<ProductCommentsVO> productCommentsVOS = productCommentsMapper.selectCommentsByProductId("3",1,2);
        for (ProductCommentsVO productCommentsVO : productCommentsVOS) {
            System.out.println(productCommentsVO);
        }
    }

   @Test
   public void SelectFirstLevelCategoriesTest(){
       List<CategoryVO> categoryVOS = categoryMapper.selectFirstLevelCategories();
       for (CategoryVO categoryVO : categoryVOS) {
           System.out.println(categoryVO);
       }
   }
   @Test
   public void productRecommendTest(){
       List<ProductVO> productVOS = productMapper.selectRecommendProducts();
       for (ProductVO productVO : productVOS) {
           System.out.println(productVO);
       }
   }

   @Test
   public void categoryTest(){
       List<CategoryVO> categoryVOS = categoryMapper.selectAllCategories(0);
       for (CategoryVO c1 : categoryVOS) {
           System.out.println(c1);
           for (CategoryVO c2 : c1.getCategories()) {
               System.out.println("\t"+c2);
               for (CategoryVO c3 : c2.getCategories()) {
                   System.out.println("\t\t"+c3 );
               }
           }
       }
   }
}