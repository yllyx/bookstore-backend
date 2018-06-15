package com.myweb.service;


import com.myweb.pojo.*;
import com.myweb.vo.OneParameter;
import com.utils.Result;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

public interface OneService {

    public Result scan(OneParameter oneParameter);

    public Result isbn(OneParameter oneParameter);

    public Result list(OneParameter oneParameter);

    public Result out(OneParameter oneParameter);

    public Result weixinCode(String code);

    public Result weixinLogin(OneParameter oneParameter);

    public Result regist(OneParameter oneParameter);

    public Result login(OneParameter oneParameter);

    public Result set(OneParameter oneParameter);
}
