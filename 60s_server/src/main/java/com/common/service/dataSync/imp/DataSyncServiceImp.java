package com.common.service.dataSync.imp;

import com.common.mapper.DataSyncMapper.DataSyncMapper;
import com.common.pojo.User.UserMainInf;
import com.common.pojo.chatRoom.GroupInf;
import com.common.pojo.chatRoom.GroupMem;
import com.common.service.dataSync.DataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 用于同步数据库的一些信息，可以从这里获取数据库数据，但有可能不是最新的*/
@Service
public class DataSyncServiceImp implements DataSyncService {
    /**
     * 同步间隔时间
     * 单位 毫秒*/
    private static final long syncTime = 5*1000;




    /**
     * 数据库数据同步器，启动入口*/
    public void start(){
        new Thread(()->{
            while (true){


                /**
                 * 群聊基本信息同步，启动！*/
                syncGroupInf();

                /**
                 * 群聊与成员关系表，启动！*/

                syncGroupMem();

                /**
                 * 同步用户信息表*/
                syncUserInf();

                try {
                    Thread.sleep(syncTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    @Autowired
    private DataSyncMapper dataSyncMapper;

    public void deleteGroup(Integer groupId){
        syncGroupInfMap.entrySet().forEach(entry->{
            if (entry.getKey().equals(groupId)){
                syncGroupInfMap.remove(groupId);
            }
        });
    }

    /**
     * 数据库群聊信息同步*/
    private static Map<Integer, GroupInf> syncGroupInfMap=new ConcurrentHashMap<>();

    public void syncGroupInf(){
        List<GroupInf> groupInfList=this.dataSyncMapper.syncGroupInf();
        for (GroupInf groupInf : groupInfList) {
            syncGroupInfMap.put(groupInf.getGroupId(),groupInf);
        }
    }

    public  Map<Integer, GroupInf> getGroupInfMap() {
        return syncGroupInfMap;
    }


    /**
     * 数据库
     * 群聊与群成员表同步*/
    private static List<GroupMem> syncGroupMemList =new CopyOnWriteArrayList<>();

    public void syncGroupMem(){
        syncGroupMemList =this.dataSyncMapper.syncGroupMemSet();
    }

    public List<GroupMem> getGroupMem() {
        return syncGroupMemList;
    }



    /**
     * 数据库
     * 用户信息表同步*/
    private static Map<Integer,UserMainInf> syncUserInfMap =new ConcurrentHashMap<>();

    public void syncUserInf(){
        List<UserMainInf> userMainInfs = this.dataSyncMapper.syncUserInf();
        for (UserMainInf userMainInf : userMainInfs) {
            syncUserInfMap.put(userMainInf.getUserId(),userMainInf);
        }
    }

    @Override
    public Map<Integer,UserMainInf> getUserInf() {
        return syncUserInfMap;
    }
}
