package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.IndexImgMapper;
import cn.yz.yzmall.entity.IndexImg;
import cn.yz.yzmall.service.IndexImgService;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class IndexImgServiceImpl implements IndexImgService {

    @Autowired
    private IndexImgMapper indexImgMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public ResultVO listIndexImgs() {
        List<IndexImg> indexImgs = null;
        try {
            String imgsStr = stringRedisTemplate.opsForValue().get("indexImgs");
            if (imgsStr != null){
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, IndexImg.class);
                indexImgs = objectMapper.readValue(imgsStr,javaType);
            }else {
                synchronized  (this){
                    String s = stringRedisTemplate.opsForValue().get("indexImgs");
                    if (s == null){
                        indexImgs = indexImgMapper.listIndexImgs();
                        if (indexImgs != null){
                            stringRedisTemplate.boundValueOps("indexImgs").set(objectMapper.writeValueAsString(indexImgs));
                            stringRedisTemplate.boundValueOps("indexImgs").expire(1, TimeUnit.DAYS);
                        }else {
                            List<IndexImg> arr = new ArrayList<>();
                            stringRedisTemplate.boundValueOps("indexImgs").set(objectMapper.writeValueAsString(arr));
                            stringRedisTemplate.boundValueOps("indexImgs").expire(10, TimeUnit.SECONDS);
                        }
                    }else {
                        JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, IndexImg.class);
                        indexImgs = objectMapper.readValue(imgsStr,javaType);
                    }

                }

            }
        }catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (indexImgs == null){
            return new ResultVO(ResStatus.NO,"fail",indexImgs);
        }else {
            return new ResultVO(ResStatus.OK,"success",indexImgs);
        }
    }
}
