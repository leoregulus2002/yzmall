package cn.yz.yzmall.controller;

import cn.yz.yzmall.service.CategoryService;
import cn.yz.yzmall.service.IndexImgService;
import cn.yz.yzmall.service.ProductService;
import cn.yz.yzmall.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@Tag(name = "首页管理")
@RequestMapping("/index")
public class IndexImgController {

    @Autowired
    private IndexImgService indexImgService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Operation(summary = "轮播图列表")
    @GetMapping("/indeximg")
    public ResultVO indexImg(){
        return indexImgService.listIndexImgs();
    }

    @Operation(summary = "商品分类接口")
    @GetMapping("/category-list")
    public ResultVO listCategory(){
        return categoryService.listCategories();
    }

    @Operation(summary = "新品推荐接口")
    @GetMapping("/list-recommends")
    public ResultVO listRecommend(){ return productService.listRecommend();}

    @Operation(summary ="分类推荐接口")
    @GetMapping("/category-recommends")
    public ResultVO listFirstLevelCategories(){return categoryService.listFirstLevelCategories();}
}
