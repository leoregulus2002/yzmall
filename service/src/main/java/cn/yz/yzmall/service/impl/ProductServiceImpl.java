package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.ProductImgMapper;
import cn.yz.yzmall.dao.ProductMapper;
import cn.yz.yzmall.dao.ProductParamsMapper;
import cn.yz.yzmall.dao.ProductSkuMapper;
import cn.yz.yzmall.entity.*;
import cn.yz.yzmall.service.ProductService;
import cn.yz.yzmall.utils.PageHelper;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductImgMapper productImgMapper;
    @Autowired
    private ProductSkuMapper productSkuMapper;
    @Autowired
    private ProductParamsMapper productParamsMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public ResultVO listRecommend() {
        List<ProductVO> productVOS = productMapper.selectRecommendProducts();
        return new ResultVO(ResStatus.OK,"success",productVOS);
    }

    @Transactional(propagation=Propagation.SUPPORTS)
    public ResultVO getProductBasicInfo(String productId) {
        try {
            String productInfo = (String) stringRedisTemplate.boundHashOps("products").get(productId);
            if (productInfo != null){
                Product product = objectMapper.readValue(productInfo, Product.class);
                String imgsStr = (String) stringRedisTemplate.boundHashOps("productImgs").get(productId);
                JavaType javaType1 = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, ProductImg.class);
                List<ProductImg> productImgs = objectMapper.readValue(imgsStr,javaType1);
                String skusStr = (String) stringRedisTemplate.boundHashOps("productSkus").get(productId);
                JavaType javaType2 = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, ProductSku.class);
                List<ProductSku> productSkus = objectMapper.readValue(skusStr,javaType2);
                HashMap<String,Object> basicInfoMap = new HashMap<>();
                basicInfoMap.put("product",productInfo);
                basicInfoMap.put("productImgs",productImgs);
                basicInfoMap.put("productSkus",productSkus);
                return new ResultVO(ResStatus.OK,"success",basicInfoMap);
            }else {
                Example example = new Example(Product.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("productId",productId)
                        .andEqualTo("productStatus",1);
                List<Product> products = productMapper.selectByExample(example);
                if (products.size() > 0){
                    Product product = products.get(0);
                    String jsonStr = objectMapper.writeValueAsString(product);
                    stringRedisTemplate.boundHashOps("products").put(productId,jsonStr);
                    Example example1 = new Example(ProductImg.class);
                    Example.Criteria criteria1 = example1.createCriteria();
                    criteria1.andEqualTo("itemId",productId);
                    List<ProductImg> productImgs = productImgMapper.selectByExample(example1);
                    stringRedisTemplate.boundHashOps("productImgs").put(productId,objectMapper.writeValueAsString(productImgs));
                    Example example2 = new Example(ProductSku.class);
                    Example.Criteria criteria2 = example2.createCriteria();
                    criteria2.andEqualTo("productId",productId)
                            .andEqualTo("status",1);
                    List<ProductSku> productSkus = productSkuMapper.selectByExample(example2);
                    stringRedisTemplate.boundHashOps("productSkus").put(productId,objectMapper.writeValueAsString(productSkus));
                    HashMap<String,Object> basicInfoMap = new HashMap<>();
                    basicInfoMap.put("product",products.get(0));
                    basicInfoMap.put("productImgs",productImgs);
                    basicInfoMap.put("productSkus",productSkus);
                    return new ResultVO(ResStatus.OK,"success",basicInfoMap);
                }else {
                    return new ResultVO(ResStatus.NO,"查询的商品不存在",null);
                }
            }
        }catch (Exception e){

        }
        return null;

    }

    @Override
    public ResultVO getProductParamsById(String productId) {
        Example example = new Example(ProductParams.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("productId",productId);
        List<ProductParams> productParams =  productParamsMapper.selectByExample(example);
        if (productParams.size()>0){
            return new ResultVO(ResStatus.OK,"success",productParams.get(0));
        }else {
            return new ResultVO(ResStatus.NO,"此商品可能为三无商品",null);
        }
    }

    @Override
    public ResultVO getProductByCategoryId(int categoryId, int pageNum, int limit) {
        int start = (pageNum-1)*limit;
        List<ProductVO> productVOS = productMapper.selectProductByCategoryId(categoryId, start, limit);
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("categoryId",categoryId);
        int count = productMapper.selectCountByExample(example);
        int pageCount = count%limit==0?count/limit:count/limit+1;
        PageHelper<ProductVO> productVOPageHelper = new PageHelper<>(count,pageCount,productVOS);
        return new ResultVO(ResStatus.OK,"success",productVOPageHelper);
    }

    @Override
    public ResultVO listBrands(int categoryId) {
        List<String> brands = productMapper.selectBrandByCategory(categoryId);
        return new ResultVO(ResStatus.OK,"success",brands);
    }

    @Override
    public ResultVO searchProduct(String kw, int pageNum, int limit) {
        kw = "%" + kw + "%";
        int start = (pageNum - 1) * limit;
        List<ProductVO> productVOS = productMapper.selectProductByKeyword(kw, start, limit);
        Example example = new Example(Product.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLike("productName",kw);
        int count = productMapper.selectCountByExample(example);
        int pageCount = count % limit == 0 ? count / limit : count / limit + 1;
        PageHelper<ProductVO> productVOPageHelper = new PageHelper<>(count, pageCount, productVOS);
        return new ResultVO(ResStatus.OK,"success",productVOPageHelper);
    }

    @Override
    public ResultVO listBrands(String kw) {
        kw = "%" + kw + "%";
        List<String> brands = productMapper.selectBrandByKeyword(kw);
        return new ResultVO(ResStatus.OK,"success",brands);
    }
}
