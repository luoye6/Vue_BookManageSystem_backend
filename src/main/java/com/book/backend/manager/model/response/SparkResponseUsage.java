package com.book.backend.manager.model.response;

import java.io.Serializable;

/**
 * $.payload.usage
 *
 * @author briqt
 */
public class SparkResponseUsage implements Serializable {
    private static final long serialVersionUID = 2181817132625461079L;

    private SparkTextUsage text;

    public SparkTextUsage getText() {
        return text;
    }

    public void setText(SparkTextUsage text) {
        this.text = text;
    }
}
