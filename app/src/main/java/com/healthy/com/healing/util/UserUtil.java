package com.healthy.com.healing.util;

import com.healthy.com.healing.litepal.User;

import org.litepal.crud.DataSupport;

import java.util.List;


public class UserUtil {
    private static User user;

    public static void addUser(String account,String password){
        user = new User();
        user.setUseraccount(account);
        user.setUserpassword(password);
        user.setStep(0);
        user.setWeight(0);
        user.setSex("保密");
        user.setHeadshot("/data/data/com.healthy.com.healing/files/6ebd7c8866745b369cc7d60a6bf307929657378a1ca53-38p0eZ_fw658.jpg");
        user.save();
    }
    //更新user表
    public static void updateUser(User user){
        user.save();
    }
    //从user表中确认登录账号是否存在
    public static User queryUser(String useraccount){

        List<User> users = DataSupport.where("useraccount = ?",String.valueOf(useraccount)).find(User.class);
//        Cursor c = DataSupport.findBySQL("select * from User where user_id=?"+id);
//        for(User user : users){
//            LogUtil.d("this","id is"+user.getUser_id());
//        }
        if(!users.isEmpty()){
            return users.get(0);
        }
        else{
            return null;
        }
    }
    //删除账号
    public static void deleteUser(String useraccount){
        DataSupport.deleteAll(User.class,"userid=?",String.valueOf(useraccount));
    }
}
