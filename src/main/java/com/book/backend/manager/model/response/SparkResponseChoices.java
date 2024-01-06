package com.book.backend.manager.model.response;

import com.book.backend.manager.model.SparkMessage;

import java.io.Serializable;
import java.util.List;

/**
 * $.payload.choices
 *
 * @author briqt
 */
public class SparkResponseChoices implements Serializable {
    private static final long serialVersionUID = 3908073548592366629L;

    /**
     * 文本响应状态，取值为[0,1,2]; 0代表首个文本结果；1代表中间文本结果；2代表最后一个文本结果
     */
    private Integer status;

    /**
     * 返回的数据序号，取值为[0,9999999]
     */
    private Integer seq;

    /**
     * 消息列表
     */
    private List<SparkMessage> text;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSeq() {
        return seq;
    }

    public void setSeq(Integer seq) {
        this.seq = seq;
    }

    public List<SparkMessage> getText() {
        return text;
    }

    public void setText(List<SparkMessage> text) {
        this.text = text;
    }
}
