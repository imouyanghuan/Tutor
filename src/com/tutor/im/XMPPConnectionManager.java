package com.tutor.im;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.GroupChatInvitation;
import org.jivesoftware.smackx.PrivateDataManager;
import org.jivesoftware.smackx.packet.ChatStateExtension;
import org.jivesoftware.smackx.packet.LastActivity;
import org.jivesoftware.smackx.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.provider.DataFormProvider;
import org.jivesoftware.smackx.provider.DelayInformationProvider;
import org.jivesoftware.smackx.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.provider.MUCAdminProvider;
import org.jivesoftware.smackx.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.provider.MUCUserProvider;
import org.jivesoftware.smackx.provider.MessageEventProvider;
import org.jivesoftware.smackx.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.provider.RosterExchangeProvider;
import org.jivesoftware.smackx.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.provider.VCardProvider;
import org.jivesoftware.smackx.provider.XHTMLExtensionProvider;
import org.jivesoftware.smackx.search.UserSearch;

import com.tutor.params.ApiUrl;

/**
 * 即时消息连接管理
 * 
 * @author bruce.chen
 * 
 *         2015-8-27
 */
public class XMPPConnectionManager {

	private static XMPPConnectionManager instance;
	// ip
	private String host;
	// 端口
	private int port;
	// 服务器名称
	private String serviceName;
	// xmpp协议对象
	private XMPPConnection connection;
	// 参数对象
	private ConnectionConfiguration configuration;

	private XMPPConnectionManager() {
		init();
	}

	private void init() {
		// 开启debug模式
		Connection.DEBUG_ENABLED = true;
		ProviderManager pm = ProviderManager.getInstance();
		configure(pm);
		// 服务器相关信息
		host = ApiUrl.XMPP_HOST;
		port = ApiUrl.XMPP_PORT;
		serviceName = ApiUrl.XMPP_SERVERNAME;
		configuration = new ConnectionConfiguration(host, port, serviceName);
		// 不使用SASL验证，设置为false
		configuration.setSASLAuthenticationEnabled(false);
		// 设置不启用安全模式
		configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
		// 允许自动连接
		configuration.setReconnectionAllowed(true);
		// 登陆后更新状态
		configuration.setSendPresence(true);
		// 收到好友邀请后manual表示需要经过同意,accept_all表示不经同意自动为好友
		Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);
		// 初始化连接
		connection = new XMPPConnection(configuration);
	}

	private void configure(ProviderManager pm) {
		// Private Data Storage
		pm.addIQProvider("query", "jabber:iq:private", new PrivateDataManager.PrivateDataIQProvider());
		// Time
		try {
			pm.addIQProvider("query", "jabber:iq:time", Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {}
		// XHTML
		pm.addExtensionProvider("html", "http://jabber.org/protocol/xhtml-im", new XHTMLExtensionProvider());
		// Roster Exchange
		pm.addExtensionProvider("x", "jabber:x:roster", new RosterExchangeProvider());
		// Message Events
		pm.addExtensionProvider("x", "jabber:x:event", new MessageEventProvider());
		// Chat State
		pm.addExtensionProvider("active", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("composing", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("paused", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("inactive", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		pm.addExtensionProvider("gone", "http://jabber.org/protocol/chatstates", new ChatStateExtension.Provider());
		// FileTransfer
		pm.addIQProvider("si", "http://jabber.org/protocol/si", new StreamInitiationProvider());
		// Group Chat Invitations
		pm.addExtensionProvider("x", "jabber:x:conference", new GroupChatInvitation.Provider());
		// Service Discovery # Items
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#items", new DiscoverItemsProvider());
		// Service Discovery # Info
		pm.addIQProvider("query", "http://jabber.org/protocol/disco#info", new DiscoverInfoProvider());
		// Data Forms
		pm.addExtensionProvider("x", "jabber:x:data", new DataFormProvider());
		// MUC User
		pm.addExtensionProvider("x", "http://jabber.org/protocol/muc#user", new MUCUserProvider());
		// MUC Admin
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#admin", new MUCAdminProvider());
		// MUC Owner
		pm.addIQProvider("query", "http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());
		// Delayed Delivery
		pm.addExtensionProvider("x", "jabber:x:delay", new DelayInformationProvider());
		// Version
		try {
			pm.addIQProvider("query", "jabber:iq:version", Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {}
		// VCard
		pm.addIQProvider("vCard", "vcard-temp", new VCardProvider());
		// Offline Message Requests
		pm.addIQProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageRequest.Provider());
		// Offline Message Indicator
		pm.addExtensionProvider("offline", "http://jabber.org/protocol/offline", new OfflineMessageInfo.Provider());
		// Last Activity
		pm.addIQProvider("query", "jabber:iq:last", new LastActivity.Provider());
		// User Search
		pm.addIQProvider("query", "jabber:iq:search", new UserSearch.Provider());
		// SharedGroupsInfo
		pm.addIQProvider("sharedgroup", "http://www.jivesoftware.org/protocol/sharedgroup", new SharedGroupsInfo.Provider());
		// JEP-33: Extended Stanza Addressing
		pm.addExtensionProvider("addresses", "http://jabber.org/protocol/address", new MultipleAddressesProvider());
	}

	public static XMPPConnectionManager getManager() {
		if (null == instance) {
			instance = new XMPPConnectionManager();
		}
		return instance;
	}

	public XMPPConnection getConnection() {
		if (null == connection) {
			throw new RuntimeException("请先初始化XMPPConnection连接");
		}
		return connection;
	}

	/**
	 * 
	 * 销毁xmpp连接.
	 * 
	 */
	public void disconnect() {
		if (connection != null) {
			connection.disconnect();
		}
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getServiceName() {
		return serviceName;
	}
}
