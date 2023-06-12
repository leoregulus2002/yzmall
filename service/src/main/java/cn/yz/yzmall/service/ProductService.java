package cn.yz.yzmall.service;

import cn.yz.yzmall.vo.ResultVO;

public interface ProductService {

    ResultVO listRecommend();
    ResultVO getProductBasicInfo(String productId);
    ResultVO getProductParamsById(String productId);
    ResultVO getProductByCategoryId(int categoryId,int pageNum,int limit);
    ResultVO listBrands(int categoryId);

    ResultVO searchProduct(String kw,int pageNum,int limit);
    ResultVO listBrands(String kw);



}
