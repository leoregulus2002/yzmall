package cn.yz.yzmall.service;

import cn.yz.yzmall.vo.ResultVO;

public interface ProductCommentsService {
    ResultVO listCommentsByProductId(String productId,int pageNum,int limit);

    ResultVO getCommentsCountByProductId(String productId);
}
