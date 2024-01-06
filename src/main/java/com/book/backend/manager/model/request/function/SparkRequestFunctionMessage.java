package com.book.backend.manager.model.request.function;

import java.io.Serializable;

/**
 * $.payload.functions.text
 *
 * @author briqt
 */
public class SparkRequestFunctionMessage implements Serializable {

    private static final long serialVersionUID = 6587302404547694700L;

    /**
     * 必传；function名称；用户输入命中后，会返回该名称
     */
    private String name;

    /**
     * 必传；function功能描述；描述function功能即可，越详细越有助于大模型理解该function
     */
    private String description;

    /**
     * 必传；function参数列表
     */
    private SparkRequestFunctionParameters parameters;

    public SparkRequestFunctionMessage() {
    }

    public SparkRequestFunctionMessage(String name, String description, SparkRequestFunctionParameters parameters) {
        this.name = name;
        this.description = description;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SparkRequestFunctionParameters getParameters() {
        return parameters;
    }

    public void setParameters(SparkRequestFunctionParameters parameters) {
        this.parameters = parameters;
    }
}
