package com.book.backend.manager.model.request;


import com.book.backend.manager.model.request.function.SparkRequestFunctions;

import java.io.Serializable;

/**
 * $.payload
 *
 * @author briqt
 */
public class SparkRequestPayload implements Serializable {
    private static final long serialVersionUID = 2084163918219863102L;

    private SparkRequestMessage message;

    private SparkRequestFunctions functions;

    public SparkRequestPayload() {
    }

    public SparkRequestPayload(SparkRequestMessage message) {
        this.message = message;
    }

    public SparkRequestPayload(SparkRequestMessage message, SparkRequestFunctions functions) {
        this.message = message;
        this.functions = functions;
    }

    public SparkRequestMessage getMessage() {
        return message;
    }

    public void setMessage(SparkRequestMessage message) {
        this.message = message;
    }

    public SparkRequestFunctions getFunctions() {
        return functions;
    }

    public void setFunctions(SparkRequestFunctions functions) {
        this.functions = functions;
    }
}
