package com.tutor.im;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Registration;
import org.jivesoftware.smack.packet.XMPPError;

import com.tutor.params.Constants;

public class XmppManager {

	private static XmppManager instance;

	private XmppManager() {}

	public static XmppManager getInstance() {
		if (instance == null) {
			instance = new XmppManager();
		}
		return instance;
	}

	/**
	 * 登陆
	 * 
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 * @return
	 */
	public int login(String userName, String password) {
		try {
			XMPPConnection connection = XMPPConnectionManager.getManager().getConnection();
			if (!connection.isConnected()) {
				connection.connect();
			}
			connection.login(userName, password, connection.getServiceName());
			// OfflineMsgManager.getInstance(activitySupport).dealOfflineMsg(connection);//处理离线消息
			// 默认在线状态
			connection.sendPacket(new Presence(Presence.Type.available));
			return Constants.Xmpp.LOGIN_SECCESS;
		} catch (Exception e) {
			if (e instanceof XMPPException) {
				XMPPException xe = (XMPPException) e;
				final XMPPError error = xe.getXMPPError();
				int errorCode = 0;
				if (error != null) {
					errorCode = error.getCode();
				}
				if (errorCode == 401) {
					return Constants.Xmpp.LOGIN_ERROR_ACCOUNT_PASS;
				} else if (errorCode == 403) {
					return Constants.Xmpp.LOGIN_ERROR_ACCOUNT_PASS;
				} else {
					return Constants.Xmpp.SERVER_UNAVAILABLE;
				}
			} else {
				return Constants.Xmpp.LOGIN_ERROR;
			}
		}
	}

	/**
	 * 注册用户
	 * 
	 * @param account
	 *            用户名
	 * @param pswd
	 *            密码
	 * @return
	 */
	public int register(String account, String pswd) {
		Registration registration = new Registration();
		registration.setType(IQ.Type.SET);
		registration.setTo(XMPPConnectionManager.getManager().getServiceName());
		// registration.setUsername(account);
		// registration.setPassword(pswd);
		Map<String, String> map = new HashMap<String, String>();
		map.put("username", account);
		map.put("password", pswd);
		// registration.addAttribute("android",
		// "geolo_createUser_android");
		registration.setAttributes(map);
		System.out.println("reg:" + registration);
		try {
			XMPPConnection connection = XMPPConnectionManager.getManager().getConnection();
			if (!connection.isConnected()) {
				connection.connect();
			}
			PacketFilter filter = new AndFilter(new PacketIDFilter(registration.getPacketID()), new PacketTypeFilter(IQ.class));
			PacketCollector collector = connection.createPacketCollector(filter);
			connection.sendPacket(registration);
			IQ iq = (IQ) collector.nextResult(Constants.Xmpp.CONNECT_TIMEOUT);
			collector.cancel();
			if (null == iq) {
				return Constants.Xmpp.REGISTER_ERROR;
			}
			if (IQ.Type.ERROR == iq.getType()) {
				if (iq.getError().toString().equalsIgnoreCase("conflict(409)")) {
					return Constants.Xmpp.REGISTER_ACCOUNT_EXIST;
				} else {
					return Constants.Xmpp.REGISTER_ERROR;
				}
			} else if (IQ.Type.RESULT == iq.getType()) {
				return Constants.Xmpp.REGISTER_SUCCESS;
			} else {
				return Constants.Xmpp.REGISTER_ERROR;
			}
		} catch (XMPPException e) {
			e.printStackTrace();
			return Constants.Xmpp.REGISTER_ERROR;
		} finally {
			XMPPConnectionManager.getManager().disconnect();
		}
	}
}
