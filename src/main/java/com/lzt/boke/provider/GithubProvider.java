package com.lzt.boke.provider;

import com.alibaba.fastjson.JSON;
import com.lzt.boke.dto.AccessTokenDTO;
import com.lzt.boke.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Github第三方支持
 * 使用okhttp模拟post请求
 */
@Component
@Slf4j
public class GithubProvider {

    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        System.out.println("======正在获取token=======");
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            return token;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 使用okhttp发送请求 获取用户信息
     * @param accessToken
     * @return
     */
    public GithubUser getUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            System.out.println("======正在获取用户信息=======");
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (Exception e) {
            log.error("getUser error,{}", accessToken, e);
        }
        return null;
    }
}
