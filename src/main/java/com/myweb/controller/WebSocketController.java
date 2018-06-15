package com.myweb.controller;

import com.myweb.vo.SocketMessage;
import com.myweb.vo.TwoParameter;
import com.utils.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@CrossOrigin("*")
public class WebSocketController {
    @Autowired
    SimpMessagingTemplate messagingTemplate;


    @ApiOperation(value = "实时消息", notes = "endpoint地址：/ws/chat/endpoint，采用基于SockJS的STOMP协议，发送地址：chat/send，订阅地址：/queue/send")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromUserId", value = " 发送者（必需）", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "toUserid", value = "接收者（必需）", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "msssage", value = "信息（可选）", required = true, dataType = "Strig")
    })
    @MessageMapping("/chat/send")
    @SendTo("/queue/send")
    public SocketMessage send(SocketMessage message) throws Exception {
        message.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        return message;
    }

    @ApiOperation(value = "此接口仅用于生成swagger说明WebSocket调用方法,请不要使用这个接口", notes = "endpoint地址：/ws/chat/endpoint，采用基于SockJS的STOMP协议，发送实时消息地址：chat/send，订阅地址：/queue/send（订阅此地址的用户可接收到chat/send发送的实时消息）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromUserId", value = " 发送者（必需）", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "toUserid", value = "接收者（必需）", required = true, dataType = "Integer"),
            @ApiImplicitParam(name = "msssage", value = "信息（可选）", required = true, dataType = "Strig")
    })
    @ResponseBody
    @GetMapping("/test")
    public Result test() {
        return null;
    }
}
