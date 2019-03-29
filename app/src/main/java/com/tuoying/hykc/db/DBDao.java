package com.tuoying.hykc.db;

import com.tuoying.hykc.entity.LocationEntity;
import com.tuoying.hykc.entity.MsgEntity;
import com.tuoying.hykc.entity.User;

import java.util.List;

/**
 * Created by zzu on 2016/4/6.
 */
public interface DBDao {

    //查询所有用户信息
    public List<User> findAllUser();

    //根据id查找用户信息
    public User findUserInfoById(String userid);

    //删除用户信息
    public void delUserInfoById(String userid);

    //判断用户是否存在
    public boolean findUserIsExist(String userid);

    //添加用户信息
    public void addUserInfo(User user);

    //修改用户信息
    public void updateUserInfo(User user, String userid);

    //查询全部消息
    public List<MsgEntity> findAllMsg();

    //删除单条信息
    public void delMsgById(String userid);

    //删除全部信息
    public void delAllMsg();

    //添加信息
    public void addMsgInfo(MsgEntity entity);


    public LocationEntity findLocInfoById(String rowid);

    public boolean findLocInfoIsExist(String rowid);

    public void delLocInfo(String rowid);

    public void updateLocInfo(LocationEntity locationEntity, String rowid);
    public void addLocInfo(LocationEntity locationEntity);
}
