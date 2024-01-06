package com.book.backend.manager.model.request.function;

import java.io.Serializable;
import java.util.List;

/**
 * $.payload.functions
 *
 * @author briqt
 */
public class SparkRequestFunctions implements Serializable {

    private static final long serialVersionUID = -7696196392354475586L;

    private List<SparkRequestFunctionMessage> text;

    public SparkRequestFunctions() {
    }

    public SparkRequestFunctions(List<SparkRequestFunctionMessage> text) {
        this.text = text;
    }

    public List<SparkRequestFunctionMessage> getText() {
        return text;
    }

    public void setText(List<SparkRequestFunctionMessage> text) {
        this.text = text;
    }
}
