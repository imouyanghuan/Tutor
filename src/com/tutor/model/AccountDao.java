package com.tutor.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCOUNT".
*/
public class AccountDao extends AbstractDao<Account, String> {

    public static final String TABLENAME = "ACCOUNT";

    /**
     * Properties of entity Account.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, String.class, "id", true, "ID");
        public final static Property MemberId = new Property(1, Integer.class, "memberId", false, "MEMBER_ID");
        public final static Property Role = new Property(2, Integer.class, "role", false, "ROLE");
        public final static Property Status = new Property(3, Integer.class, "status", false, "STATUS");
        public final static Property Email = new Property(4, String.class, "email", false, "EMAIL");
        public final static Property Pswd = new Property(5, String.class, "pswd", false, "PSWD");
        public final static Property Phone = new Property(6, String.class, "phone", false, "PHONE");
        public final static Property HkidNumber = new Property(7, String.class, "hkidNumber", false, "HKID_NUMBER");
        public final static Property ResidentialAddress = new Property(8, String.class, "residentialAddress", false, "RESIDENTIAL_ADDRESS");
        public final static Property FacebookId = new Property(9, String.class, "facebookId", false, "FACEBOOK_ID");
        public final static Property ImAccount = new Property(10, String.class, "imAccount", false, "IM_ACCOUNT");
        public final static Property ImPswd = new Property(11, String.class, "imPswd", false, "IM_PSWD");
        public final static Property Token = new Property(12, String.class, "token", false, "TOKEN");
        public final static Property CreatedTime = new Property(13, String.class, "createdTime", false, "CREATED_TIME");
    };


    public AccountDao(DaoConfig config) {
        super(config);
    }
    
    public AccountDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCOUNT\" (" + //
                "\"ID\" TEXT PRIMARY KEY NOT NULL ," + // 0: id
                "\"MEMBER_ID\" INTEGER," + // 1: memberId
                "\"ROLE\" INTEGER," + // 2: role
                "\"STATUS\" INTEGER," + // 3: status
                "\"EMAIL\" TEXT," + // 4: email
                "\"PSWD\" TEXT," + // 5: pswd
                "\"PHONE\" TEXT," + // 6: phone
                "\"HKID_NUMBER\" TEXT," + // 7: hkidNumber
                "\"RESIDENTIAL_ADDRESS\" TEXT," + // 8: residentialAddress
                "\"FACEBOOK_ID\" TEXT," + // 9: facebookId
                "\"IM_ACCOUNT\" TEXT," + // 10: imAccount
                "\"IM_PSWD\" TEXT," + // 11: imPswd
                "\"TOKEN\" TEXT," + // 12: token
                "\"CREATED_TIME\" TEXT);"); // 13: createdTime
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCOUNT\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Account entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getId());
 
        Integer memberId = entity.getMemberId();
        if (memberId != null) {
            stmt.bindLong(2, memberId);
        }
 
        Integer role = entity.getRole();
        if (role != null) {
            stmt.bindLong(3, role);
        }
 
        Integer status = entity.getStatus();
        if (status != null) {
            stmt.bindLong(4, status);
        }
 
        String email = entity.getEmail();
        if (email != null) {
            stmt.bindString(5, email);
        }
 
        String pswd = entity.getPswd();
        if (pswd != null) {
            stmt.bindString(6, pswd);
        }
 
        String phone = entity.getPhone();
        if (phone != null) {
            stmt.bindString(7, phone);
        }
 
        String hkidNumber = entity.getHkidNumber();
        if (hkidNumber != null) {
            stmt.bindString(8, hkidNumber);
        }
 
        String residentialAddress = entity.getResidentialAddress();
        if (residentialAddress != null) {
            stmt.bindString(9, residentialAddress);
        }
 
        String facebookId = entity.getFacebookId();
        if (facebookId != null) {
            stmt.bindString(10, facebookId);
        }
 
        String imAccount = entity.getImAccount();
        if (imAccount != null) {
            stmt.bindString(11, imAccount);
        }
 
        String imPswd = entity.getImPswd();
        if (imPswd != null) {
            stmt.bindString(12, imPswd);
        }
 
        String token = entity.getToken();
        if (token != null) {
            stmt.bindString(13, token);
        }
 
        String createdTime = entity.getCreatedTime();
        if (createdTime != null) {
            stmt.bindString(14, createdTime);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Account readEntity(Cursor cursor, int offset) {
        Account entity = new Account( //
            cursor.getString(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // memberId
            cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // role
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // status
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // email
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // pswd
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // phone
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // hkidNumber
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // residentialAddress
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // facebookId
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // imAccount
            cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11), // imPswd
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // token
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13) // createdTime
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Account entity, int offset) {
        entity.setId(cursor.getString(offset + 0));
        entity.setMemberId(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setRole(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setStatus(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setEmail(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPswd(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPhone(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setHkidNumber(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setResidentialAddress(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setFacebookId(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setImAccount(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setImPswd(cursor.isNull(offset + 11) ? null : cursor.getString(offset + 11));
        entity.setToken(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setCreatedTime(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Account entity, long rowId) {
        return entity.getId();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Account entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
