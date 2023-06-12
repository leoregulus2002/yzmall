package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.ProductComments;
import cn.yz.yzmall.entity.ProductCommentsVO;
import cn.yz.yzmall.general.GeneralDao;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCommentsMapper extends GeneralDao<ProductComments> {

    List<ProductCommentsVO> selectCommentsByProductId(@Param("productId") String productId,
                                                      @Param("start") int start,
                                                      @Param("limit") int limit);
}