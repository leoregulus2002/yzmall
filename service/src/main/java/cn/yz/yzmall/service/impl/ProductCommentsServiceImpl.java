package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.ProductCommentsMapper;
import cn.yz.yzmall.entity.ProductComments;
import cn.yz.yzmall.entity.ProductCommentsVO;
import cn.yz.yzmall.service.ProductCommentsService;
import cn.yz.yzmall.utils.PageHelper;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductCommentsServiceImpl implements ProductCommentsService {

    @Autowired
    private ProductCommentsMapper productCommentsMapper;
    @Override
    public ResultVO listCommentsByProductId(String productId,int pageNum,int limit) {
        Example example = new Example(ProductComments.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId",productId);
        int count = productCommentsMapper.selectCountByExample(example);
        int pageCount = count%limit==0?count/limit:count/limit+1;
        int start = (pageNum-1)*limit;
        List<ProductCommentsVO> list = productCommentsMapper.selectCommentsByProductId(productId, start, limit);

        return new ResultVO(ResStatus.OK,"success",new PageHelper<ProductCommentsVO>(count,pageCount,list));
    }

    @Override
    public ResultVO getCommentsCountByProductId(String productId) {
        Example example = new Example(ProductComments.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId",productId)
                .andEqualTo("commType",1);
        int goodTotal = productCommentsMapper.selectCountByExample(example);

        Example example1 = new Example(ProductComments.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("productId",productId)
                .andEqualTo("commType",0);
        int midTotal = productCommentsMapper.selectCountByExample(example1);

        Example example2 = new Example(ProductComments.class);
        Example.Criteria criteria2 = example2.createCriteria();
        criteria2.andEqualTo("productId",productId)
                .andEqualTo("commType",-1);
        int badTotal = productCommentsMapper.selectCountByExample(example2);
        int total = goodTotal + midTotal + badTotal;

        double percent = (Double.parseDouble(goodTotal + "") / Double.parseDouble(total + "")) * 100;
        String percentValue = (percent+"").substring(0,(percent+"").lastIndexOf(".")+2);
        Map<String,Object> map = new HashMap<>();
        map.put("total",total);
        map.put("goodTotal",goodTotal);
        map.put("midTotal",midTotal);
        map.put("badTotal",badTotal);
        map.put("percent",percentValue);
        return new ResultVO(ResStatus.OK,"success",map);
    }
}
