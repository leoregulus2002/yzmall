package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.CategoryVO;
import cn.yz.yzmall.general.GeneralDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper extends GeneralDao<CategoryVO> {

//    List<CategoryVO> selectAllCategories();

    List<CategoryVO> selectAllCategories(int parentId);

    List<CategoryVO> selectFirstLevelCategories();
}