package com.tutor.im;

import java.util.ArrayList;
import java.util.List;

import com.tutor.TutorApplication;
import com.tutor.model.IMMessage;
import com.tutor.model.IMMessageDao;
import com.tutor.model.IMMessageDao.Properties;
import com.tutor.util.LogUtils;
import com.tutor.util.StringUtils;

import android.database.Cursor;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;

/**
 * 消息管理
 * 
 * @author bruce.chen
 * 
 */
public class IMMessageManager {

	// 1、public QueryBuilder<T> where(WhereCondition cond, WhereCondition...
	// condMore)：使用and连接多个查询条件。
	// 2、public QueryBuilder<T> whereOr(WhereCondition cond1, WhereCondition
	// cond2, WhereCondition... condMore)：使用or连接多个查询条件。
	// 3、public QueryBuilder<T> orderDesc(Property... properties)：排序
	// 4、public WhereCondition or(WhereCondition cond1, WhereCondition cond2,
	// WhereCondition... condMore)：使用or构成查询条件
	// 5、public WhereCondition or(WhereCondition cond1, WhereCondition cond2,
	// WhereCondition... condMore)：使用and构成查询条件
	private static IMMessageManager imMessageManager;
	private IMMessageDao imMessageDao;

	private IMMessageManager() {
		imMessageDao = TutorApplication.getImMessageDao();
	}

	public static IMMessageManager getManager() {
		if (imMessageManager == null) {
			imMessageManager = new IMMessageManager();
		}
		return imMessageManager;
	}

	/**
	 * 保存消息
	 * 
	 * @param message
	 */
	public long sava(IMMessage message) {
		if (null == message) {
			return -1;
		}
		if (IMMessage.MESSAGE_TYPE_ADD_FRIEND == message.getNoticeType()) {
			QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
			WhereCondition condition = qb.and(Properties.FromSubJid.eq(message.getFromSubJid()), Properties.NoticeType.eq("" + IMMessage.MESSAGE_TYPE_ADD_FRIEND));
			DeleteQuery<IMMessage> db = qb.where(condition).buildDelete();
			if (null != db) {
				db.executeDeleteWithoutDetachingEntities();
			}
		}
		return imMessageDao.insert(message);
	}

	/**
	 * 更新读取状态
	 * 
	 * @param id
	 * @param status
	 */
	public void updateStatus(String id, Integer status) {
		if (StringUtils.empty(id)) {
			return;
		}
		IMMessage message = imMessageDao.load(id);
		message.setReadStatus(status);
		message.setNoticeSum(0);
		imMessageDao.update(message);
	}

	/**
	 * 查询与某人的聊天记录,从最后开始往前查
	 * 
	 * @param fromJid
	 *            jid
	 * @param currentSize
	 *            当前已显示的数据大小
	 * @param pageSize
	 *            每页大小
	 * @return
	 */
	public List<IMMessage> searchChatMessages(String fromSubJid, int currentSize, int pageSize) {
		LogUtils.e("currentSize == " + fromSubJid);
		LogUtils.e("currentSize == " + currentSize);
		int size = (int) getMsgCountWithSomeBody(fromSubJid);
		int offset = size - (currentSize + pageSize);
		LogUtils.e("offset == " + offset);
		if (offset < 0 && currentSize < size) {
			offset = 0;
			pageSize = size - currentSize;
		} else if (offset < 0 && currentSize >= size) {
			return null;
		}
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		WhereCondition condition = qb.and(Properties.FromSubJid.eq(fromSubJid), Properties.NoticeType.eq("" + IMMessage.MESSAGE_TYPE_CHAT_MSG));
		List<IMMessage> list = qb.where(condition).limit(pageSize).offset(offset).list();
		return list;
	}

	/**
	 * 查询与某人的聊天记录总数
	 * 
	 * @param fromJid
	 * @return
	 */
	public long getMsgCountWithSomeBody(String fromSubJid) {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		WhereCondition condition = qb.and(Properties.FromSubJid.eq(fromSubJid), Properties.NoticeType.eq("" + IMMessage.MESSAGE_TYPE_CHAT_MSG));
		qb.where(condition);
		long count = qb.count();
		return count;
	}

	/**
	 * 删除与某人的聊天记录
	 * 
	 * @param formJid
	 * @return
	 */
	public boolean deleteMsgWhereJid(String fromSubJid) {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		DeleteQuery<IMMessage> db = qb.where(Properties.FromSubJid.eq(fromSubJid)).buildDelete();
		if (null != db) {
			db.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}

	/**
	 * 删除某类型的记录
	 * 
	 * @param formJid
	 * @return
	 */
	public boolean deleteMsgWhereType(Integer type) {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		DeleteQuery<IMMessage> db = qb.where(Properties.NoticeType.eq(type)).buildDelete();
		if (null != db) {
			db.executeDeleteWithoutDetachingEntities();
			return true;
		}
		return false;
	}

	/**
	 * 获取最近聊天人聊天最后一条消息和未读消息总数
	 * 
	 * @return
	 * @throws DbException
	 */
	public List<IMMessage> getRecentContactsWithLastMsg() {
		List<IMMessage> list = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from ( ");
		sqlBuffer.append(" select ");
		sqlBuffer.append(" m.ID,m.CONTENT,m.TITLE,m.TIME,m.FROM_SUB_JID,m.NOTICE_TYPE ");
		sqlBuffer.append(" from ");
		sqlBuffer.append(IMMessageDao.TABLENAME).append(" m ");
		sqlBuffer.append(" where ");
		sqlBuffer.append(" m.NOTICE_TYPE = ").append(IMMessage.MESSAGE_TYPE_CHAT_MSG).append(" group by m.FROM_SUB_JID ");
		sqlBuffer.append(" union ");
		sqlBuffer.append(" select ");
		sqlBuffer.append(" m.ID,m.CONTENT,m.TITLE,m.TIME,m.FROM_SUB_JID,m.NOTICE_TYPE ");
		sqlBuffer.append(" from ");
		sqlBuffer.append(IMMessageDao.TABLENAME).append(" m ");
		sqlBuffer.append(" where ");
		sqlBuffer.append(" m.NOTICE_TYPE = ").append(IMMessage.MESSAGE_TYPE_ADD_FRIEND).append(" group by m.NOTICE_TYPE ");
		sqlBuffer.append(" union ");
		sqlBuffer.append(" select ");
		sqlBuffer.append(" m.ID,m.CONTENT,m.TITLE,m.TIME,m.FROM_SUB_JID,m.NOTICE_TYPE ");
		sqlBuffer.append(" from ");
		sqlBuffer.append(IMMessageDao.TABLENAME).append(" m ");
		sqlBuffer.append(" where ");
		sqlBuffer.append(" m.NOTICE_TYPE = ").append(IMMessage.MESSAGE_TYPE_SYS_MSG).append(" group by m.NOTICE_TYPE ");
		sqlBuffer.append(" ) t order by t.time desc ");
		System.out.println(sqlBuffer.toString());
		Cursor cursor = TutorApplication.getDatabase().rawQuery(sqlBuffer.toString(), null);
		if (cursor != null) {
			list = new ArrayList<IMMessage>();
			while (cursor.moveToNext()) {
				IMMessage message = new IMMessage();
				message.setId(cursor.getString(cursor.getColumnIndex("ID")));
				message.setContent(cursor.getString(cursor.getColumnIndex("CONTENT")));
				message.setTitle(cursor.getString(cursor.getColumnIndex("TITLE")));
				message.setTime(cursor.getString(cursor.getColumnIndex("TIME")));
				message.setFromSubJid(cursor.getString(cursor.getColumnIndex("FROM_SUB_JID")));
				message.setNoticeType(cursor.getInt(cursor.getColumnIndex("NOTICE_TYPE")));
				list.add(message);
			}
		}
		if (null != list && list.size() > 0) {
			for (IMMessage message : list) {
				long sum = getUnReadNoticeCountByTypeAndFrom(message.getNoticeType(), message.getFromSubJid());
				message.setNoticeSum((int) sum);
			}
		}
		return list;
	}

	/**
	 * 获取所有未读消息
	 * 
	 * @return
	 */
	public List<IMMessage> getUnreadNotices() {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		List<IMMessage> list = qb.where(Properties.ReadStatus.eq("" + IMMessage.READ_STATUS_UNREAD)).list();
		return list;
	}

	/**
	 * 更新添加好友状态.
	 * 
	 * @param id
	 * @param status
	 * @param content
	 */
	public void updateAddFriendStatus(String id, Integer status, String content) {
		IMMessage notice = imMessageDao.load(id);
		notice.setReadStatus(status);
		notice.setContent(content);
		imMessageDao.update(notice);
	}

	/**
	 * 获得未读消息条数
	 * 
	 * @return
	 */
	public Long getUnReadNoticeCount() {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		return qb.where(Properties.ReadStatus.eq("" + IMMessage.READ_STATUS_UNREAD)).count();
	}

	/**
	 * 根据id获取消息
	 * 
	 * @param id
	 * @return
	 */
	public IMMessage getNoticeById(String id) {
		return imMessageDao.load(id);
	}

	/**
	 * 获得分类状态下的未读消息
	 * 
	 * @param type
	 * @return
	 */
	public List<IMMessage> getUnReadNoticeListByType(int type) {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		WhereCondition condition = qb.and(Properties.ReadStatus.eq("" + IMMessage.READ_STATUS_UNREAD), Properties.NoticeType.eq("" + type));
		qb.where(condition);
		return qb.list();
	}

	/**
	 * 获得分类状态下的所有
	 * 
	 * @param type
	 * @return
	 */
	public List<IMMessage> getAllMessageListByType(int type) {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		qb.where(Properties.NoticeType.eq("" + type));
		return qb.list();
	}

	/**
	 * 获得所有的系统消息和好友请求消息
	 * 
	 * @param type
	 * @return
	 */
	public List<IMMessage> getAllSysOrAddFriendMessageList() {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		WhereCondition condition = qb.or(Properties.NoticeType.eq("" + IMMessage.MESSAGE_TYPE_ADD_FRIEND), Properties.NoticeType.eq("" + IMMessage.MESSAGE_TYPE_SYS_MSG));
		qb.where(condition);
		return qb.list();
	}

	/**
	 * 根据分类获取某人未读消息的条数
	 * 
	 * @param type
	 * @return
	 */
	public long getUnReadNoticeCountByTypeAndFrom(int type, String fromSubJid) {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		WhereCondition condition = qb.and(Properties.ReadStatus.eq("" + IMMessage.READ_STATUS_UNREAD), Properties.NoticeType.eq("" + type), Properties.FromSubJid.eq(fromSubJid));
		qb.where(condition);
		return qb.count();
	}

	/**
	 * 更新某人所有通知状态.
	 * 
	 * @param xfrom
	 * @param status
	 */
	public void updateStatusByFrom(String fromSubJid, Integer status) {
		QueryBuilder<IMMessage> qb = imMessageDao.queryBuilder();
		qb.where(Properties.FromSubJid.eq(fromSubJid));
		List<IMMessage> list = qb.list();
		if (list != null && list.size() > 0) {
			for (IMMessage notice : list) {
				notice.setReadStatus(status);
				notice.setNoticeSum(0);
			}
			imMessageDao.updateInTx(list);
		}
	}

	/**
	 * 根据id删除记录
	 * 
	 * @param noticeId
	 */
	public void delById(String noticeId) {
		imMessageDao.deleteByKey(noticeId);
	}

	/**
	 * 
	 * 删除全部记录
	 * 
	 * @update 2013-4-15 下午6:33:19
	 */
	public void delAll() {
		imMessageDao.deleteAll();
	}
}
