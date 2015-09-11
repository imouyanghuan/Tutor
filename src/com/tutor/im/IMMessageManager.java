package com.tutor.im;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.db.table.TableUtils;
import com.lidroid.xutils.exception.DbException;
import com.tutor.TutorApplication;
import com.tutor.model.Account;
import com.tutor.model.IMMessage;
import com.tutor.util.LogUtils;
import com.tutor.util.StringUtils;

/**
 * 消息管理
 * 
 * @author bruce.chen
 * 
 */
public class IMMessageManager {

	private static IMMessageManager imMessageManager;
	private DbUtils dbUtils;
	private String currentIMAccount = null;

	private IMMessageManager() {
		dbUtils = TutorApplication.dbUtils;
		try {
			Account account = dbUtils.findFirst(Account.class);
			if (null != account) {
				currentIMAccount = account.getImAccount() + "@" + XMPPConnectionManager.getManager().getServiceName();
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		if (null == currentIMAccount) {
			throw new IllegalArgumentException("not login");
		}
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
		try {
			if (IMMessage.MESSAGE_TYPE_ADD_FRIEND == message.getNoticeType()) {
				dbUtils.delete(IMMessage.class, WhereBuilder.b("fromSubJid", "=", message.getFromSubJid()).and("noticeType", "=", "" + IMMessage.MESSAGE_TYPE_ADD_FRIEND));
				dbUtils.save(message);
			} else {
				dbUtils.save(message);
			}
			return 0;
		} catch (DbException e) {
			e.printStackTrace();
			return -1;
		}
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
		try {
			IMMessage message = dbUtils.findById(IMMessage.class, id);
			message.setReadStatus(status);
			message.setNoticeSum(0);
			dbUtils.saveOrUpdate(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		try {
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
			List<IMMessage> list = dbUtils.findAll(Selector.from(IMMessage.class).where("fromSubJid", "=", fromSubJid).and("toJid", "=", currentIMAccount)
					.and("noticeType", "=", IMMessage.MESSAGE_TYPE_CHAT_MSG + "").limit(pageSize).offset(offset));
			System.out.println(list.size());
			return list;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询与某人的聊天记录总数
	 * 
	 * @param fromJid
	 * @return
	 */
	public long getMsgCountWithSomeBody(String fromSubJid) {
		try {
			long count = dbUtils.count(Selector.from(IMMessage.class).where("fromSubJid", "=", fromSubJid).and("toJid", "=", currentIMAccount));
			LogUtils.e("count == " + count);
			return count;
		} catch (DbException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 删除与某人的聊天记录
	 * 
	 * @param formJid
	 * @return
	 */
	public boolean deleteMsgWhereJid(String fromSubJid) {
		try {
			dbUtils.delete(IMMessage.class, WhereBuilder.b("fromSubJid", "=", fromSubJid).and("toJid", "=", currentIMAccount));
			return true;
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除某类型的记录
	 * 
	 * @param formJid
	 * @return
	 */
	public boolean deleteMsgWhereType(Integer type) {
		try {
			dbUtils.delete(IMMessage.class, WhereBuilder.b("noticeType", "=", "" + type).and("toJid", "=", currentIMAccount));
			return true;
		} catch (DbException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 获取最近聊天人聊天最后一条消息和未读消息总数
	 * 
	 * @return
	 * @throws DbException
	 */
	public List<IMMessage> getRecentContactsWithLastMsg() throws DbException {
		List<IMMessage> list = null;
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append(" select * from ( ");
		sqlBuffer.append(" select ");
		sqlBuffer.append(" m._id,m.content,m.title,m.time,m.fromSubJid,m.noticeType ");
		sqlBuffer.append(" from ");
		sqlBuffer.append(TableUtils.getTableName(IMMessage.class)).append(" m ");
		sqlBuffer.append(" where ");
		sqlBuffer.append(" m.noticeType = ").append(IMMessage.MESSAGE_TYPE_CHAT_MSG).append(" and m.toJid = " + currentIMAccount).append(" group by m.fromSubJid ");
		sqlBuffer.append(" union ");
		sqlBuffer.append(" select ");
		sqlBuffer.append(" m._id,m.content,m.title,m.time,m.fromSubJid,m.noticeType ");
		sqlBuffer.append(" from ");
		sqlBuffer.append(TableUtils.getTableName(IMMessage.class)).append(" m ");
		sqlBuffer.append(" where ");
		sqlBuffer.append(" m.noticeType = ").append(IMMessage.MESSAGE_TYPE_ADD_FRIEND).append(" and m.toJid = " + currentIMAccount).append(" group by m.noticeType ");
		sqlBuffer.append(" union ");
		sqlBuffer.append(" select ");
		sqlBuffer.append(" m._id,m.content,m.title,m.time,m.fromSubJid,m.noticeType ");
		sqlBuffer.append(" from ");
		sqlBuffer.append(TableUtils.getTableName(IMMessage.class)).append(" m ");
		sqlBuffer.append(" where ");
		sqlBuffer.append(" m.noticeType = ").append(IMMessage.MESSAGE_TYPE_SYS_MSG).append(" and m.toJid = " + currentIMAccount).append(" group by m.noticeType ");
		sqlBuffer.append(" ) t order by t.time desc ");
		System.out.println(sqlBuffer.toString());
		Cursor cursor = dbUtils.execQuery(sqlBuffer.toString());
		if (cursor != null) {
			list = new ArrayList<IMMessage>();
			while (cursor.moveToNext()) {
				IMMessage message = new IMMessage();
				message.set_id(cursor.getString(cursor.getColumnIndex("_id")));
				message.setContent(cursor.getString(cursor.getColumnIndex("content")));
				message.setTitle(cursor.getString(cursor.getColumnIndex("title")));
				message.setTime(cursor.getString(cursor.getColumnIndex("time")));
				message.setFromSubJid(cursor.getString(cursor.getColumnIndex("fromSubJid")));
				message.setNoticeType(cursor.getInt(cursor.getColumnIndex("noticeType")));
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
		try {
			List<IMMessage> list = dbUtils.findAll(Selector.from(IMMessage.class).where("readStatus", "=", "" + IMMessage.READ_STATUS_UNREAD).and("toJid", "=", currentIMAccount));
			return list;
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 更新添加好友状态.
	 * 
	 * @param id
	 * @param status
	 * @param content
	 */
	public void updateAddFriendStatus(String id, Integer status, String content) {
		try {
			IMMessage notice = dbUtils.findById(IMMessage.class, id);
			notice.setReadStatus(status);
			notice.setContent(content);
			dbUtils.saveOrUpdate(notice);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得未读消息条数
	 * 
	 * @return
	 */
	public Long getUnReadNoticeCount() {
		try {
			return dbUtils.count(Selector.from(IMMessage.class).where("readStatus", "=", "" + IMMessage.READ_STATUS_UNREAD).and("toJid", "=", currentIMAccount));
		} catch (DbException e) {
			e.printStackTrace();
			return 0l;
		}
	}

	/**
	 * 根据id获取消息
	 * 
	 * @param id
	 * @return
	 */
	public IMMessage getNoticeById(String id) {
		try {
			return dbUtils.findById(IMMessage.class, id);
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得分类状态下的未读消息
	 * 
	 * @param type
	 * @return
	 */
	public List<IMMessage> getUnReadNoticeListByType(int type) {
		try {
			return dbUtils.findAll(Selector.from(IMMessage.class).where("readStatus", "=", "" + IMMessage.READ_STATUS_UNREAD).and("noticeType", "=", "" + type).and("toJid", "=", currentIMAccount));
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得分类状态下的所有
	 * 
	 * @param type
	 * @return
	 */
	public List<IMMessage> getAllMessageListByType(int type) {
		try {
			return dbUtils.findAll(Selector.from(IMMessage.class).where("noticeType", "=", "" + type).and("toJid", "=", currentIMAccount));
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获得所有的系统消息和好友请求消息
	 * 
	 * @param type
	 * @return
	 */
	public List<IMMessage> getAllSysOrAddFriendMessageList() {
		try {
			return dbUtils.findAll(Selector.from(IMMessage.class).where("toJid", "=", currentIMAccount).and("noticeType", "=", "" + IMMessage.MESSAGE_TYPE_ADD_FRIEND)
					.or("noticeType", "=", "" + IMMessage.MESSAGE_TYPE_SYS_MSG));
		} catch (DbException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据分类获取未读消息的条数
	 * 
	 * @param type
	 * @return
	 */
	public long getUnReadNoticeCountByType(int type) {
		try {
			return dbUtils.count(Selector.from(IMMessage.class).where("readStatus", "=", "" + IMMessage.READ_STATUS_UNREAD).and("noticeType", "=", "" + type).and("toJid", "=", currentIMAccount));
		} catch (DbException e) {
			e.printStackTrace();
			return 0l;
		}
	}

	/**
	 * 根据分类获取某人未读消息的条数
	 * 
	 * @param type
	 * @return
	 */
	public long getUnReadNoticeCountByTypeAndFrom(int type, String fromSubJid) {
		try {
			return dbUtils.count(Selector.from(IMMessage.class).where("readStatus", "=", "" + IMMessage.READ_STATUS_UNREAD).and("noticeType", "=", "" + type).and("fromSubJid", "=", fromSubJid)
					.and("toJid", "=", currentIMAccount));
		} catch (DbException e) {
			e.printStackTrace();
			return 0l;
		}
	}

	/**
	 * 更新某人所有通知状态.
	 * 
	 * @param xfrom
	 * @param status
	 */
	public void updateStatusByFrom(String fromSubJid, Integer status) {
		try {
			List<IMMessage> list = dbUtils.findAll(Selector.from(IMMessage.class).where("fromSubJid", "=", fromSubJid).and("toJid", "=", currentIMAccount));
			if (list != null && list.size() > 0) {
				for (IMMessage notice : list) {
					notice.setReadStatus(status);
					notice.setNoticeSum(0);
				}
				dbUtils.saveOrUpdateAll(list);
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据id删除记录
	 * 
	 * @param noticeId
	 */
	public void delById(String noticeId) {
		try {
			dbUtils.deleteById(IMMessage.class, noticeId);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * 删除全部记录
	 * 
	 * @update 2013-4-15 下午6:33:19
	 */
	public void delAll() {
		try {
			dbUtils.deleteAll(IMMessage.class);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
}
