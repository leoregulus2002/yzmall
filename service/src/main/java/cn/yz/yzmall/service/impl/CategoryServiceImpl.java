package cn.yz.yzmall.service.impl;

import cn.yz.yzmall.dao.CategoryMapper;
import cn.yz.yzmall.entity.CategoryVO;
import cn.yz.yzmall.service.CategoryService;
import cn.yz.yzmall.vo.ResStatus;
import cn.yz.yzmall.vo.ResultVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${timeout.categories}")
    private Integer categoryTimeout;
    @Override
    public ResultVO listCategories() {
        List<CategoryVO> categoryVOS = null;
        try {
            String categories = stringRedisTemplate.opsForValue().get("categories");
            if (categories != null){
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(ArrayList.class, CategoryVO.class);
                categoryVOS = objectMapper.readValue(categories,javaType);
            }else {
                categoryVOS = categoryMapper.selectAllCategories(0);
                String str = objectMapper.writeValueAsString(categoryVOS);
                stringRedisTemplate.boundValueOps("categories").set(str,categoryTimeout, TimeUnit.DAYS);
             }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
            return new ResultVO(ResStatus.OK,"success",categoryVOS);
    }

    @Override
    public ResultVO listFirstLevelCategories() {
        List<CategoryVO> categoryVOS = categoryMapper.selectFirstLevelCategories();
        return new ResultVO(ResStatus.OK,"success",categoryVOS);
    }
}
