package com.tutor.model;

import java.io.Serializable;

public class FAQModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String category;
	private String renderText;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getRenderText() {
		return renderText;
	}

	public void setRenderText(String renderText) {
		this.renderText = renderText;
	}
}
