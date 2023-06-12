package cn.yz.yzmall.service;

import cn.yz.yzmall.vo.ResultVO;

public interface CategoryService {

    ResultVO listCategories();

    ResultVO listFirstLevelCategories();
}
