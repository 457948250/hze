package com.healthy.com.healing.util;

import com.healthy.com.healing.litepal.SportRecordList;
import com.healthy.com.healing.litepal.User;

import org.litepal.crud.DataSupport;

import java.util.List;


public class SportRecordUtil {
    private static SportRecordList sportRecordList;

    public static SportRecordList addSportRecordList(String account){
        sportRecordList = new SportRecordList();
        sportRecordList.setUseraccount(account);
        sportRecordList.save();
        return sportRecordList;
    }

    //从SportRecordList表中确认登录账号是否存在,并返回SportRecordList对象
    public static SportRecordList querySportRecordList(String useraccount){

        List<SportRecordList> sportRecordLists = DataSupport.where("useraccount = ?",String.valueOf(useraccount)).find(SportRecordList.class);
//        Cursor c = DataSupport.findBySQL("select * from User where user_id=?"+id);
//        for(User user : users){
//            LogUtil.d("this","id is"+user.getUser_id());
//        }
        if(!sportRecordLists.isEmpty()){
            return sportRecordLists.get(0);
        }
        else{
            return null;
        }
    }



    //更新SportRecordList表
    public static void updateSportRecordList(SportRecordList sportRecordList){
        sportRecordList.save();
    }
}
