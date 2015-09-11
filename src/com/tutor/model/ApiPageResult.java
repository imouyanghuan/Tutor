package com.tutor.model;

/**
 * @author bruce.chen
 * 
 *         2015-9-2
 * @param <T>
 */
public class ApiPageResult<T> extends ApiResult<T> {

	private static final long serialVersionUID = 1L;
	private Page page;

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}

	@Override
	public String toString() {
		return super.toString() + " ApiPageResult [page=" + page + "]";
	}
}
