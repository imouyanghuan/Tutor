package com.tutor.model;

/**
 * 即時通訊消息model
 * 
 * @author bruce.chen
 * 
 *         2015-8-28
 */
public class IMMessage extends BaseModel {

	private static final long serialVersionUID = 1L;
	// 消息是否读取状态
	public static final int READ_STATUS_READ = 0;
	public static final int READ_STATUS_UNREAD = 1;
	public static final int READ_STATUS_All = 2;
	public static final int READ_STATUS_ARRAY = 3;
	public static final int READ_STATUS_UNARRAY = 4;
	// 消息是否发送成功
	public static final int SEND_STATUS_SUCCESS = 0;
	public static final int SEND_STATUS_ERROR = 1;
	// 网络消息类型
	public static final int MESSAGE_TYPE_ADD_FRIEND = 1;// 好友请求
	public static final int MESSAGE_TYPE_SYS_MSG = 2; // 系统消息
	public static final int MESSAGE_TYPE_CHAT_MSG = 3;// 聊天消息
	// 聊天消息来源状态
	public static final int CHAT_MESSAGE_TYPE_RECEIVE = 0;
	public static final int CHAT_MESSAGE_TYPE_SEND = 1;
	/**
	 * 消息发送状态
	 */
	private Integer sendStatus;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 消息内容
	 */
	private String content;
	/**
	 * 消息时间
	 */
	private String time;
	/**
	 * 存在本地，表示与谁聊天
	 */
	private String fromSubJid;
	/**
	 * 最后通知去向
	 */
	private String toJid;
	/**
	 * 读取状态
	 */
	private Integer readStatus;
	/**
	 * 最后通知时间
	 */
	private String noticeTime;
	/**
	 * 收到未读消息总数、
	 */
	private Integer noticeSum;
	/**
	 * 消息类型 1.好友请求 2.系统消息3.聊天消息
	 */
	private Integer noticeType;
	/**
	 * 聊天消息的来源状态 0:接受 1：发送
	 */
	private Integer msgType = 0;

	public IMMessage() {
		this.sendStatus = SEND_STATUS_SUCCESS;
	}

	public Integer getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(Integer sendStatus) {
		this.sendStatus = sendStatus;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getFromSubJid() {
		return fromSubJid;
	}

	public void setFromSubJid(String fromSubJid) {
		this.fromSubJid = fromSubJid;
	}

	public String getToJid() {
		return toJid;
	}

	public void setToJid(String toJid) {
		this.toJid = toJid;
	}

	public Integer getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(Integer readStatus) {
		this.readStatus = readStatus;
	}

	public String getNoticeTime() {
		return noticeTime;
	}

	public void setNoticeTime(String noticeTime) {
		this.noticeTime = noticeTime;
	}

	public Integer getNoticeSum() {
		return noticeSum;
	}

	public void setNoticeSum(Integer noticeSum) {
		this.noticeSum = noticeSum;
	}

	public Integer getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(Integer noticeType) {
		this.noticeType = noticeType;
	}

	public Integer getMsgType() {
		return msgType;
	}

	public void setMsgType(Integer msgType) {
		this.msgType = msgType;
	}

	@Override
	public String toString() {
		return "IMMessage [sendStatus=" + sendStatus + ", title=" + title + ", content=" + content + ", time=" + time + ", fromSubJid=" + fromSubJid + ", toJid=" + toJid + ", readStatus="
				+ readStatus + ", noticeTime=" + noticeTime + ", noticeSum=" + noticeSum + ", noticeType=" + noticeType + ", msgType=" + msgType + "]";
	}
}
