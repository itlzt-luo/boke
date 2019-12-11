package com.lzt.boke.dto;

import lombok.Data;

/**
 * Github授权信息传输对象
 */
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
