package com.example.utils;

import com.alibaba.fastjson2.JSONObject;
import com.example.entity.ConnectionConfig;
import com.example.entity.Response;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Component
public class NetUtils {
    private final HttpClient client = HttpClient.newHttpClient();

    @Lazy
    @Resource
    ConnectionConfig config;

    public boolean registerToServer(String address, String token) {
        log.info("正在向服务器注册请稍后...");
        Response response = this.doGet("/register",address,token);
        if (response.success()) {
            log.info("客户端注册完成");
        }else {
            log.error("客户端注册失败:{}",response.message());
        }
        return response.success();
    }

    private Response doGet(String url) {
        return this.doGet(url, config.getAddress(), config.getToken());
    }


    private Response doGet(String url, String address, String token) {
        try {
            HttpRequest request = HttpRequest.newBuilder().GET()
                    .uri(new URI(address + "/monitor" + url))
                    .header("Authorization",token)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return JSONObject.parseObject(response.body()).to(Response.class);

        } catch (Exception e) {
            log.error("在向服务器发起请求时出错",e);
            return Response.errorResponse(e);
        }
    }




}
