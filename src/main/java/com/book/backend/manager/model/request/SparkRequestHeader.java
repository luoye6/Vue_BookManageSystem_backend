package com.book.backend.manager.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * $.header
 *
 * @author briqt
 */
public class SparkRequestHeader implements Serializable {
    private static final long serialVersionUID = -1426143090218924505L;

    /**
     * 应用appid，从开放平台控制台创建的应用中获取<br/>
     * 必传
     */
    @JsonProperty("app_id")
    private String appId;

    /**
     * 每个用户的id，用于区分不同用户<br/>
     * 非必传，最大长度32
     */
    private String uid;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
