package com.book.backend.manager.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;


/**
 * $.payload.usage.text
 *
 * @author briqt
 */
public class SparkTextUsage implements Serializable {
    private static final long serialVersionUID = 8295933047877077971L;

    /**
     * 包含历史问题的总tokens大小(提问tokens大小)
     */
    @JsonProperty("prompt_tokens")
    private Integer promptTokens;

    /**
     * 回答的tokens大小
     */
    @JsonProperty("completion_tokens")
    private Integer completionTokens;

    /**
     * prompt_tokens和completion_tokens的和，也是本次交互计费的tokens大小
     */
    @JsonProperty("total_tokens")
    private Integer totalTokens;

    /**
     * 保留字段，可忽略
     */
    @JsonProperty("question_tokens")
    private Integer questionTokens;

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public Integer getQuestionTokens() {
        return questionTokens;
    }

    public void setQuestionTokens(Integer questionTokens) {
        this.questionTokens = questionTokens;
    }
}
