package cn.yz.yzmall.controller;

import cn.yz.yzmall.service.ProductCommentsService;
import cn.yz.yzmall.service.ProductService;
import cn.yz.yzmall.vo.ResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/product")
@Tag(name = "商品接口管理")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductCommentsService productCommentsService;

    @GetMapping("/detail-info/{pid}")
    @Operation(summary = "商品基本信息接口")
    public ResultVO getProductBasicInfo(@PathVariable("pid") String pid){
        return productService.getProductBasicInfo(pid);
    }

    @GetMapping("/detail-params/{pid}")
    @Operation(summary = "商品参数信息接口")
    public ResultVO getProductParams(@PathVariable("pid") String pid){
        return productService.getProductParamsById(  pid);
    }

    @GetMapping("/detail-comments/{pid}")
    @Operation(summary = "商品评论信息接口")
    @Parameters({
            @Parameter(name = "pageNum",description ="当前页码",required = true),
            @Parameter(name = "limit",description ="每页显示条数 ",required = true),
    })
    public ResultVO getProductComments(@PathVariable("pid") String pid,int pageNum,int limit){
        return productCommentsService.listCommentsByProductId(pid,pageNum,limit);
    }

    @GetMapping("/detail-commentscount/{pid}")
    @Operation(summary = "商品评论统计查询接口")
    public ResultVO getProductCommentsCount(@PathVariable("pid") String pid){
        return productCommentsService.getCommentsCountByProductId(pid);
    }

    @GetMapping("/listbycid/{cid}")
    @Operation(summary = "根据类别查询商品接口")
    @Parameters({
            @Parameter(name = "pageNum",description ="当前页码",required = true),
            @Parameter(name = "limit",description ="每页显示条数 ",required = true),
    })
    public ResultVO getProductsByCategoryId(@PathVariable("cid") int cid,int pageNum,int limit){
        return productService.getProductByCategoryId(cid,pageNum,limit);
    }

    @GetMapping("/listbrands/{cid}")
    @Operation(summary = "根据类别查询商品品牌接口")
    public ResultVO getBrandsByCategoryId(@PathVariable("cid") int cid){
        return productService.listBrands(cid);
    }

    @GetMapping("/listbykeyword")
    @Operation(summary = "根据关键字查询商品接口")
    @Parameters({
            @Parameter(name = "keyword",description ="关键字",required = true),
            @Parameter(name = "pageNum",description ="当前页码",required = true),
            @Parameter(name = "limit",description ="每页显示条数 ",required = true),
    })
    public ResultVO getProductsByCategoryId(String keyword,int pageNum,int limit){
        return productService.searchProduct(keyword,pageNum,limit);
    }

    @GetMapping("/listbrands-keyword")
    @Operation(summary = "根据关键字查询商品品牌接口")
    public ResultVO getBrandsByKeyword(String keyword){
        return productService.listBrands(keyword);
    }
}
