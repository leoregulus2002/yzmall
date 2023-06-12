package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.ProductSku;
import cn.yz.yzmall.general.GeneralDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSkuMapper extends GeneralDao<ProductSku> {
    List<ProductSku> selectLowermostPriceByProductId(String productId);
}