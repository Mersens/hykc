package com.tuoying.hykc.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tuoying.hykc.entity.MsgEntity;
import com.tuoying.hykc.entity.User;


/**
 * Created by zzu on 2016/4/6.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final int VERSION = 5;
    private static final String NAME = "CITY_FREIGHT.db";

    private static final String SQL_LOGIN_HISTORY_CREAT = "create table "+ User.TABLE_NAME+"(_id integer primary key autoincrement,"
            +User.USERID+" text ,"+User.USERNAME+" text, "+User.PSD+" text, "+User.TOKEN+" text,"+User.RZ+" text, "+User.OFWLGSINFO+" text)";
    private static final String SQL_LOGIN_HISTORY_DROP = "drop table if exists "+User.TABLE_NAME;

    private static final String SQL_MSG_CREAT = "create table "+ MsgEntity.TABLE_NAME+"(_id integer primary key autoincrement,"
           +MsgEntity.MSG+" text,"+MsgEntity.TIME+" text)";
    private static final String SQL_MSG_DROP = "drop table if exists "+MsgEntity.TABLE_NAME;

    public static DBHelper helper = null;
    public static Context mContext;

    private DBHelper(Context context) {
        super(context, NAME, null, VERSION);
    }

    public static DBHelper getInstance(Context context) {
        if (helper == null) {
            synchronized (DBHelper.class) {
                if (helper == null) {
                    helper = new DBHelper(context.getApplicationContext());
                }
            }
        }
        mContext = context;
        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_LOGIN_HISTORY_CREAT);
        db.execSQL(SQL_MSG_CREAT);
    }

    /**
     * 当数据库更新时，调用该方法
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearCache(db);
    }

    /**
     * 清空数据缓存
     *
     * @param db
     */
    public void clearCache(SQLiteDatabase db) {
        db.execSQL(SQL_LOGIN_HISTORY_DROP);
        db.execSQL(SQL_LOGIN_HISTORY_CREAT);

        db.execSQL(SQL_MSG_DROP);
        db.execSQL(SQL_MSG_CREAT);
    }
}
