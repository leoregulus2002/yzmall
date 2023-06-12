package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.ProductImg;
import cn.yz.yzmall.general.GeneralDao;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImgMapper extends GeneralDao<ProductImg> {

    ProductImg selectProductImgByProductId(int productId);
}