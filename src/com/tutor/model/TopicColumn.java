package com.tutor.model;

import java.io.Serializable;

/**
 * 首页栏目映射实体
 * 
 * @author jerry.yao
 * 
 */
public class TopicColumn implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int sequence;
	private int status;
	private String columnName;
	private String columnCode;
	private String type;

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnCode() {
		return columnCode;
	}

	public void setColumnCode(String columnCode) {
		this.columnCode = columnCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
