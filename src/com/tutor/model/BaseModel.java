package com.tutor.model;

import java.io.Serializable;

import com.lidroid.xutils.db.annotation.Id;

/**
 * @author bruce.chen
 * 
 *         2015-8-28
 */
public class BaseModel implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	private String _id;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}
}
