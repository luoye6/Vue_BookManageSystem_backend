package com.book.backend.manager.model.request.function;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * $.payload.functions.text
 *
 * @author briqt
 */
public class SparkRequestFunctionParameters implements Serializable {

    private static final long serialVersionUID = 1079801431462837232L;

    /**
     * 必传；参数类型
     */
    private String type;

    /**
     * 必传；参数信息描述；该内容由用户定义，命中该方法时需要返回哪些参数<br/>
     * key：参数名称<br/>
     * value：参数信息描述
     */
    private Map<String,SparkRequestFunctionProperty> properties;

    /**
     * 必传；必须返回的参数列表；该内容由用户定义，命中方法时必须返回的字段;properties中的key
     */
    private List<String> required;

    public SparkRequestFunctionParameters() {
    }

    public SparkRequestFunctionParameters(String type, Map<String, SparkRequestFunctionProperty> properties, List<String> required) {
        this.type = type;
        this.properties = properties;
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, SparkRequestFunctionProperty> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, SparkRequestFunctionProperty> properties) {
        this.properties = properties;
    }

    public List<String> getRequired() {
        return required;
    }

    public void setRequired(List<String> required) {
        this.required = required;
    }
}
