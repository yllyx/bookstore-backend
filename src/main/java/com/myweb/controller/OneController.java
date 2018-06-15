package com.myweb.controller;


import com.myweb.service.OneService;
import com.myweb.vo.OneParameter;
import com.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api
@CrossOrigin("*")
@Controller
public class OneController {

    @Autowired
    public OneService oneService;

    @ApiOperation(value = "扫描isbn", notes = "用户扫描isbn,通过豆瓣接口获得书详细信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isbn", value = "isbn（必需）", required = true, dataType = "String")
    })
    @ResponseBody
    @GetMapping("/book/scan")
    public Result scan(@ModelAttribute OneParameter oneParameter) {
        return oneService.scan(oneParameter);
    }

    @ApiOperation(value = "上架", notes = "用户扫描书上的isbn,通过豆瓣接口获得书详细信息并上架到书店")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "isbn", value = "isbn（必需）", required = true, dataType = "String"),
            @ApiImplicitParam(name = "userid", value = "userid（必需）", required = true, dataType = "Integer")
    })
    @ResponseBody
    @PostMapping("/book/isbn")
    public Result isbn(@ModelAttribute OneParameter oneParameter) {
        return oneService.isbn(oneParameter);
    }

    @ApiOperation(value = "下架", notes = "用户下载图书（仅可以下架自己上传的图书，且图书为自有状态，即status为1状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "bookids", value = "bookids（必需）,这个id是book的id,多选以逗号隔开，如1,2,3,4", required = true, dataType = "String"),
            @ApiImplicitParam(name = "ownerid", value = "ownerid（必需)，这个id是user的id", required = true, dataType = "Integer")
    })
    @ResponseBody
    @PostMapping("/bookstore/out")
    public Result out(@ModelAttribute OneParameter oneParameter) {
        return oneService.out(oneParameter);
    }

    @ApiOperation(value = "分类显示图书", notes = "查询用户书架所有的图书")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ownerid", value = "ownerid（必需）,这个id是user的id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "status", value = "status（可选）,0为全部（不传此参数时的默认值），1为自有，2为借出，3为借入", required = true, dataType = "Integer")
    })
    @ResponseBody
    @GetMapping("/bookstore/list")
    public Result list(@ModelAttribute OneParameter oneParameter) {
        return oneService.list(oneParameter);
    }

    @ApiOperation(value = "设置图书属性", notes = "设置图书属性（仅可以设置自己上传的图书，且图书为自有状态，即status为1状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ownerid", value = "ownerid（必需）,这个id是user的id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "bookid", value = "bookid（必需）,这个id是book的id", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "days", value = "归还天数（可选）,整数", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "fee", value = "借阅费用（可选）,2位小数", required = true, dataType = "BigDecimal"),
            @ApiImplicitParam(name = "deposit", value = "押金数额（可选）,2位小数", required = true, dataType = "BigDecimal"),
            @ApiImplicitParam(name = "weight", value = "单位（可选，默认为0）0天，1周，2月", required = true, dataType = "BigDecimal")
    })
    @ResponseBody
    @PostMapping("/bookstore/set")
    public Result set(@ModelAttribute OneParameter oneParameter) {
        return oneService.set(oneParameter);
    }

    @ApiOperation(value = "用户注册", notes = "用户使用账号密码注册,如注册成功用户,则自动完成登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名（必需）", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码（必需）", required = true, dataType = "String")
    })
    @ResponseBody
    @PostMapping("user/regist")
    public Result regist(@ModelAttribute OneParameter oneParameter) {
        return oneService.regist(oneParameter);
    }

    @ApiOperation(value = "用户登录", notes = "用户使用账号密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名（必需）", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码（必需）", required = true, dataType = "String")
    })
    @ResponseBody
    @GetMapping("user/login")
    public Result login(@ModelAttribute OneParameter oneParameter) {
        return oneService.login(oneParameter);
    }

    @ApiOperation(value = "微信授权", notes = "微信授权,第一次使用，则自动注册用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "code（必需）", required = true, dataType = "String"),
    })
    @PostMapping("user/weixin/code/{code}")
    @ResponseBody
    public Result weixinCode(@PathVariable String code) {
        return oneService.weixinCode(code);
    }

    @ApiOperation(value = "微信登录", notes = "微信登录,通过微信授权后，前端保存openid，后续都将openid做为登录凭证使用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "openid", value = "openid（必需）", required = true, dataType = "String"),
    })
    @GetMapping("user/weixin/login")
    @ResponseBody
    public Result weixinOpenId(@ModelAttribute OneParameter oneParameter) {
        return oneService.weixinLogin(oneParameter);
    }
}
