package com.scb.booking;


import java.io.Serializable;

/**
 * a unified response with response msg,
 * in common sense, here should also have a response code property
 * @author loki
 */
public class BaseResponse<T> implements Serializable {
    
    private T content;

    private String resultMsg = "success";
    
	public BaseResponse() {
	}

	public T getContent() {
		return content;
	}

	public void setContent(T content) {
		this.content = content;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public BaseResponse<?> withContent(T content){
		this.content = content;
		return this;
	}

	public BaseResponse<?> withResultMsg(String resultMsg){
		this.resultMsg = resultMsg;
		return this;
	}

	@Override
	public String toString() {
		return resultMsg;
	}
}
