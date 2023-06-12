package cn.yz.yzmall.dao;

import cn.yz.yzmall.entity.IndexImg;
import cn.yz.yzmall.general.GeneralDao;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexImgMapper extends GeneralDao<IndexImg> {

    List<IndexImg> listIndexImgs();

}