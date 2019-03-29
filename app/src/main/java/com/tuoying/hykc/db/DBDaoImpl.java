package com.tuoying.hykc.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tuoying.hykc.entity.LocationEntity;
import com.tuoying.hykc.entity.MsgEntity;
import com.tuoying.hykc.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/23.
 */

public class DBDaoImpl implements DBDao {
    private DBHelper helper;
    private Context context;

    public DBDaoImpl(Context context) {
        helper = DBHelper.getInstance(context);
        this.context = context;
    }

    @Override
    public List<User> findAllUser() {
        List<User> list = new ArrayList<User>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from " + User.TABLE_NAME,
                null);
        while (cursor.moveToNext()) {
            User user = new User();
            String userid = cursor.getString(cursor.getColumnIndex(User.USERID));
            user.setUserId(userid);
            String username = cursor.getString(cursor.getColumnIndex(User.USERNAME));
            user.setUserName(username);
            String psd = cursor.getString(cursor.getColumnIndex(User.PSD));
            user.setPsd(psd);
            String token = cursor.getString(cursor.getColumnIndex(User.TOKEN));
            user.setToken(token);
            String rz = cursor.getString(cursor.getColumnIndex(User.RZ));
            user.setRz(rz);
            String of=cursor.getString(cursor.getColumnIndex(User.OFWLGSINFO));
            user.setOfwlgsinfo(of);
            list.add(user);
        }
        cursor.close();
        db.close();
        return list;
    }
    
    @Override
    public User findUserInfoById(String userid) {
        List<User> list = new ArrayList<User>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + User.TABLE_NAME + " where userid=?",
                new String[]{userid});
        while (cursor.moveToNext()) {
            User user = new User();
            String id = cursor.getString(cursor.getColumnIndex(User.USERID));
            user.setUserId(id);
            String username = cursor.getString(cursor.getColumnIndex(User.USERNAME));
            user.setUserName(username);
            String psd = cursor.getString(cursor.getColumnIndex(User.PSD));
            user.setPsd(psd);
            String token = cursor.getString(cursor.getColumnIndex(User.TOKEN));
            user.setToken(token);
            String rz = cursor.getString(cursor.getColumnIndex(User.RZ));
            user.setRz(rz);
            String of=cursor.getString(cursor.getColumnIndex(User.OFWLGSINFO));
            user.setOfwlgsinfo(of);
            list.add(user);
        }
        cursor.close();
        db.close();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }

    }

    @Override
    public void delUserInfoById(String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + User.TABLE_NAME + " where userid=?",
                new Object[]{userid});
        db.close();
    }

    @Override
    public boolean findUserIsExist(String userid) {
        boolean flag = false;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from " + User.TABLE_NAME + " where userid=? ",
                new String[]{userid});
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        db.close();
        return flag;

    }

    @Override
    public void addUserInfo(User user) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "insert into " + User.TABLE_NAME + "(" + User.USERID + "," + User.USERNAME + "," + User.PSD + "," + User.TOKEN +","+User.RZ+","+User.OFWLGSINFO+")" + " values(?,?,?,?,?,?)",
                new Object[]{user.getUserId(), user.getUserName(),
                        user.getPsd(),user.getToken(),user.getRz(),user.getOfwlgsinfo()});
        db.close();

    }

    @Override
    public void updateUserInfo(User user, String userid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE " + User.TABLE_NAME + " SET "+User.OFWLGSINFO + "=?," +User.RZ + "=?,"+ User.TOKEN + "=?,"+ User.USERID + "=?," + User.USERNAME + "=?," + User.PSD + "=? where userid=?", new Object[]{
                user.getOfwlgsinfo(),user.getRz(),user.getToken(), user.getUserId(), user.getUserName(), user.getPsd(), userid});
        db.close();
    }

    @Override
    public List<MsgEntity> findAllMsg() {
        List<MsgEntity> list = new ArrayList<MsgEntity>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from " + MsgEntity.TABLE_NAME,
                null);
        while (cursor.moveToNext()) {
            MsgEntity entity = new MsgEntity();
            int id = cursor.getInt(cursor.getColumnIndex("_id"));
            entity.setId(id+"");
            String msg = cursor.getString(cursor.getColumnIndex(MsgEntity.MSG));
            entity.setMsg(msg);
            String time = cursor.getString(cursor.getColumnIndex(MsgEntity.TIME));
            entity.setTime(time);
            list.add(entity);
        }
        cursor.close();
        db.close();
        return list;
    }

    @Override
    public void delMsgById(String id) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + MsgEntity.TABLE_NAME + " where _id=?",
                new Object[]{id});
        db.close();
    }

    @Override
    public void delAllMsg() {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + MsgEntity.TABLE_NAME
               );
        db.close();
    }

    @Override
    public void addMsgInfo(MsgEntity entity) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "insert into " + MsgEntity.TABLE_NAME + "(" + MsgEntity.MSG + "," +MsgEntity.TIME+")" + " values(?,?)",
                new Object[]{entity.getMsg(),entity.getTime()});
        db.close();
    }

    @Override
    public LocationEntity findLocInfoById(String rowid) {
        List<LocationEntity> list = new ArrayList<>();
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + LocationEntity.TABLE_NAME + " where _rowid=?",
                new String[]{rowid});
        while (cursor.moveToNext()) {
            LocationEntity entity=new LocationEntity();
            String id = cursor.getString(cursor.getColumnIndex(LocationEntity.ROWID));
            entity.setRowid(id);
            String location = cursor.getString(cursor.getColumnIndex(LocationEntity.LOCATION));
            entity.setLocation(location);
            list.add(entity);
        }
        cursor.close();
        db.close();
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    @Override
    public boolean findLocInfoIsExist(String rowid) {
        boolean flag = false;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "select * from " + LocationEntity.TABLE_NAME + " where _rowid=? ",
                new String[]{rowid});
        while (cursor.moveToNext()) {
            flag = true;
        }
        cursor.close();
        db.close();
        return flag;
    }

    @Override
    public void delLocInfo(String rowid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from " + LocationEntity.TABLE_NAME + " where _rowid=?",
                new Object[]{rowid});
        db.close();
    }

    @Override
    public void updateLocInfo(LocationEntity locationEntity, String rowid) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("UPDATE " + LocationEntity.TABLE_NAME + " SET "+LocationEntity.ROWID + "=?," + LocationEntity.LOCATION + "=? where _rowid=?", new Object[]{
                locationEntity.getRowid(),locationEntity.getLocation(), rowid});
        db.close();
    }

    @Override
    public void addLocInfo(LocationEntity locationEntity) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(
                "insert into " + LocationEntity.TABLE_NAME + "(" + LocationEntity.ROWID + ","+LocationEntity.LOCATION+")" + " values(?,?)",
                new Object[]{locationEntity.getRowid(), locationEntity.getLocation()
                       });
        db.close();
    }
}
