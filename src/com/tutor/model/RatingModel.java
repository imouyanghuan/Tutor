package com.tutor.model;

/**
 * 评分model
 * 
 * @author jerry.yao
 * 
 */
public class RatingModel {

	private int teachingContentGrade;
	private int attributeAndPatienceGrade;
	private int punctualityGrade;
	private int communicationSkillGrade;
	private String comment;
	private String nickName;
	private String userName;
	private String avatar;
	private String createdTime;
	private int id;

	public int getTeachingContentGrade() {
		return teachingContentGrade;
	}

	public void setTeachingContentGrade(int teachingContentGrade) {
		this.teachingContentGrade = teachingContentGrade;
	}

	public int getAttributeAndPatienceGrade() {
		return attributeAndPatienceGrade;
	}

	public void setAttributeAndPatienceGrade(int attributeAndPatienceGrade) {
		this.attributeAndPatienceGrade = attributeAndPatienceGrade;
	}

	public int getPunctualityGrade() {
		return punctualityGrade;
	}

	public void setPunctualityGrade(int punctualityGrade) {
		this.punctualityGrade = punctualityGrade;
	}

	public int getCommunicationSkillGrade() {
		return communicationSkillGrade;
	}

	public void setCommunicationSkillGrade(int communicationSkillGrade) {
		this.communicationSkillGrade = communicationSkillGrade;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
