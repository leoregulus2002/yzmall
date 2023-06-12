package cn.yz.yzmall.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//@Schema(value = "ResultVO对象",description = "封装接口返回给前端的数据")
public class ResultVO {

//    @ApiModelProperty(value = "响应状态码",dataType = "int")
    private int code;
//    @ApiModelProperty("响应提示信息")
    private String msg;
//    @ApiModelProperty("响应数据")
    private Object data;
}
