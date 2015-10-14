package com.tutor.model;

import java.util.Map;

import android.database.sqlite.SQLiteDatabase;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see de.greenrobot.dao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig accountDaoConfig;
    private final DaoConfig iMMessageDaoConfig;
    private final DaoConfig avatarDaoConfig;

    private final AccountDao accountDao;
    private final IMMessageDao iMMessageDao;
    private final AvatarDao avatarDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        accountDaoConfig = daoConfigMap.get(AccountDao.class).clone();
        accountDaoConfig.initIdentityScope(type);

        iMMessageDaoConfig = daoConfigMap.get(IMMessageDao.class).clone();
        iMMessageDaoConfig.initIdentityScope(type);

        avatarDaoConfig = daoConfigMap.get(AvatarDao.class).clone();
        avatarDaoConfig.initIdentityScope(type);

        accountDao = new AccountDao(accountDaoConfig, this);
        iMMessageDao = new IMMessageDao(iMMessageDaoConfig, this);
        avatarDao = new AvatarDao(avatarDaoConfig, this);

        registerDao(Account.class, accountDao);
        registerDao(IMMessage.class, iMMessageDao);
        registerDao(Avatar.class, avatarDao);
    }
    
    public void clear() {
        accountDaoConfig.getIdentityScope().clear();
        iMMessageDaoConfig.getIdentityScope().clear();
        avatarDaoConfig.getIdentityScope().clear();
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public IMMessageDao getIMMessageDao() {
        return iMMessageDao;
    }

    public AvatarDao getAvatarDao() {
        return avatarDao;
    }

}
