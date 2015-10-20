package com.tutor.im;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import com.tutor.TutorApplication;
import com.tutor.model.User;
import com.tutor.util.StringUtils;

/**
 * 联系人管理
 * 
 * @author bruce.chen
 * 
 */
public class ContactManager {

	/**
	 * 保存着所有的联系人信息
	 */
	private Map<String, User> contacters = null;
	private static ContactManager contactManager;

	private ContactManager() {}

	public static ContactManager getManager() {
		if (contactManager == null) {
			contactManager = new ContactManager();
		}
		return contactManager;
	}

	public boolean init(Connection connection) {
		try {
			contacters = new HashMap<String, User>();
			for (RosterEntry entry : connection.getRoster().getEntries()) {
				contacters.put(entry.getUser(), transEntryToUser(entry, connection.getRoster()));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public Map<String, User> getContacters() {
		return contacters;
	}

	/**
	 * 根据RosterEntry创建一个user
	 * 
	 * @param entry
	 * @param roster
	 * @return
	 */
	public User transEntryToUser(RosterEntry entry, Roster roster) {
		User user = new User();
		user.setJID(entry.getUser());
		if (null == entry.getName()) {
			user.setName(StringUtils.getUserNameByJid(entry.getUser()));
		} else {
			user.setName(entry.getName());
		}
		System.out.println(entry.getUser() + "  transEntryToUser");
		Presence presence = roster.getPresence(entry.getUser());
		user.setFroms(presence.getFrom());
		user.setStatus(presence.getStatus());
		user.setSize(entry.getGroups().size());
		user.setAvailable(presence.isAvailable());
		user.setType(entry.getType());
		return user;
	}

	public void destory() {
		if (contacters != null) {
			contacters.clear();
			contacters = null;
		}
	}

	/**
	 * 获取联系人列表
	 * 
	 * @return
	 */
	public List<User> getContacterList() {
		if (null == contacters) {
			throw new RuntimeException("contacters is null");
		}
		List<User> users = new ArrayList<User>();
		for (String key : contacters.keySet()) {
			users.add(contacters.get(key));
		}
		return users;
	}

	/**
	 * 获得所有未分组的联系人列表
	 * 
	 * @return
	 */
	public List<User> getNoGroupUserList(Roster roster) {
		List<User> userList = new ArrayList<User>();
		// 服务器的用户信息改变后，不会通知到unfiledEntries
		for (RosterEntry entry : roster.getUnfiledEntries()) {
			userList.add(contacters.get(entry.getUser()).clone());
		}
		return userList;
	}

	/**
	 * 获取所有联系人列表
	 * 
	 * @param roster
	 * @return
	 */
	// public List<FriendsGroup> getGroups(Roster roster) {
	// if (contacters == null) {
	// throw new RuntimeException("contacters is null");
	// }
	// List<FriendsGroup> groups = new ArrayList<FriendsGroup>();
	// groups.add(new FriendsGroup("所有好友", getContacterList()));
	// for (RosterGroup group : roster.getGroups()) {
	// List<User> groupUsers = new ArrayList<User>();
	// for (RosterEntry entry : group.getEntries()) {
	// groupUsers.add(contacters.get(entry.getUser()));
	// }
	// groups.add(new FriendsGroup(group.getName(), groupUsers));
	// }
	// groups.add(new FriendsGroup("未分组好友", getNoGroupUserList(roster)));
	// return groups;
	// }
	/**
	 * 修改好友昵称
	 * 
	 * @param user
	 * @param nick
	 * @param connection
	 */
	public boolean setNick(User user, String nick, XMPPConnection connection) {
		try {
			RosterEntry entry = connection.getRoster().getEntry(user.getJID());
			entry.setName(nick);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 把一个好友添加到一个组中
	 * 
	 * @param user
	 * @param groupName
	 */
	public void addUserToGroup(final User user, final String groupName, final XMPPConnection connection) {
		if (groupName == null || user == null)
			return;
		// 将一个rosterEntry添加到group中是PacketCollector，会阻塞线程
		new Thread() {

			public void run() {
				RosterGroup group = connection.getRoster().getGroup(groupName);
				// 这个组已经存在就添加到这个组，不存在创建一个组
				RosterEntry entry = connection.getRoster().getEntry(user.getJID());
				try {
					if (group != null) {
						if (entry != null)
							group.addEntry(entry);
					} else {
						RosterGroup newGroup = connection.getRoster().createGroup(groupName);
						if (entry != null)
							newGroup.addEntry(entry);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 把一个好友从组中删除
	 * 
	 * @param user
	 * @param groupName
	 */
	public void removeUserFromGroup(final User user, final String groupName, final XMPPConnection connection) {
		if (groupName == null || user == null)
			return;
		new Thread() {

			public void run() {
				RosterGroup group = connection.getRoster().getGroup(groupName);
				if (group != null) {
					try {
						System.out.println(user.getJID() + "----------------");
						RosterEntry entry = connection.getRoster().getEntry(user.getJID());
						if (entry != null)
							group.removeEntry(entry);
					} catch (XMPPException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}

	/**
	 * 
	 * 根据jid获得用户昵称
	 * 
	 * @param Jid
	 * @param connection
	 * @return
	 */
	public User getNickname(String Jid, XMPPConnection connection) {
		Roster roster = connection.getRoster();
		for (RosterEntry entry : roster.getEntries()) {
			String params = entry.getUser();
			if (params.split("/")[0].equals(Jid)) {
				return transEntryToUser(entry, roster);
			}
		}
		return null;
	}

	/**
	 * 添加分组 .
	 * 
	 * @param user
	 * @param groupName
	 * @param connection
	 */
	public void addGroup(final String groupName, final XMPPConnection connection) {
		if (StringUtils.empty(groupName)) {
			return;
		}
		// 将一个rosterEntry添加到group中是PacketCollector，会阻塞线程
		new Thread() {

			public void run() {
				try {
					RosterGroup g = connection.getRoster().getGroup(groupName);
					if (g != null) {
						return;
					}
					connection.getRoster().createGroup(groupName);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 获得所有组名
	 * 
	 * @return
	 */
	public List<String> getGroupNames(Roster roster) {
		List<String> groupNames = new ArrayList<String>();
		for (RosterGroup group : roster.getGroups()) {
			groupNames.add(group.getName());
		}
		return groupNames;
	}

	/**
	 * 
	 * 从花名册中删除用户.
	 * 
	 * @param Jid
	 * @throws XMPPException
	 */
	public void deleteUser(String userJid) throws XMPPException {
		Roster roster = XMPPConnectionManager.getManager().getConnection().getRoster();
		RosterEntry entry = roster.getEntry(userJid);
		XMPPConnectionManager.getManager().getConnection().getRoster().removeEntry(entry);
	}

	/**
	 * 根据用户jid得到用户
	 * 
	 * @param userJid
	 * @param nickname
	 */
	public User getByUserJid(String userJId, XMPPConnection connection) {
		Roster roster = connection.getRoster();
		RosterEntry entry = connection.getRoster().getEntry(userJId);
		if (null == entry) {
			return null;
		}
		User user = new User();
		if (entry.getName() == null) {
			user.setName(StringUtils.getUserNameByJid(entry.getUser()));
		} else {
			user.setName(entry.getName());
		}
		user.setJID(entry.getUser());
		System.out.println(entry.getUser());
		Presence presence = roster.getPresence(entry.getUser());
		user.setFroms(presence.getFrom());
		user.setStatus(presence.getStatus());
		user.setSize(entry.getGroups().size());
		user.setAvailable(presence.isAvailable());
		user.setType(entry.getType());
		return user;
	}

	/**
	 * 添加好友
	 * 
	 * @param imId
	 * @param nickName
	 */
	public boolean addFriend(String imId, String nickName) {
		try {
			XMPPConnection connection = TutorApplication.connectionManager.getConnection();
			connection.getRoster().createEntry(imId, "", new String[] { "Friends" });
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
