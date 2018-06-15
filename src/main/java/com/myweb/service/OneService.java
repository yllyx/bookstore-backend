package com.myweb.service;

import com.myweb.vo.OneParameter;
import com.utils.Result;

public interface OneService
{

    public Result scan(OneParameter oneParameter);

    public Result isbn(OneParameter oneParameter);

    public Result list(OneParameter oneParameter);

    public Result out(OneParameter oneParameter);

    public Result weixinCode(String code);

    public Result weixinLogin(OneParameter oneParameter);

    public Result regist(OneParameter oneParameter);

    public Result login(OneParameter oneParameter);

    public Result set(OneParameter oneParameter);

    /**
     * 获取微信用户详细信息
     * 
     * @param oneParameter
     * @return
     */
    public Result weixinUserInfo(OneParameter oneParameter);
}
