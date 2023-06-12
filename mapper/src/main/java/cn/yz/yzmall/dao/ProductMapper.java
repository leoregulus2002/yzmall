package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.Product;
import cn.yz.yzmall.entity.ProductVO;
import cn.yz.yzmall.general.GeneralDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductMapper extends GeneralDao<Product> {
    List<ProductVO> selectRecommendProducts();

    List<ProductVO> selectTop6ByCategory(int cid);

    List<ProductVO> selectProductByCategoryId(@Param("cid") int cid,
                                              @Param("start") int start,
                                              @Param("limit") int limit);
    List<String> selectBrandByCategory(int cid);

    List<ProductVO> selectProductByKeyword(@Param("kw") String keyword,
                                              @Param("start") int start,
                                              @Param("limit") int limit);
    List<String> selectBrandByKeyword(String keyword);


}