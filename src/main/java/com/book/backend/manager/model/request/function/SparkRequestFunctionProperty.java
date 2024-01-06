package com.book.backend.manager.model.request.function;

import java.io.Serializable;

/**
 * $.payload.functions.text.parameters.properties.*
 *
 * @author briqt
 */
public class SparkRequestFunctionProperty implements Serializable {

    private static final long serialVersionUID = -343415637582994606L;

    /**
     * 必传；参数信息描述；该内容由用户定义，需要返回的参数是什么类型
     */
    private String type;

    /**
     * 必传；参数详细描述；该内容由用户定义，需要返回的参数的具体描述
     */
    private String description;

    public SparkRequestFunctionProperty() {
    }

    public SparkRequestFunctionProperty(String type, String description) {
        this.type = type;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
