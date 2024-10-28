package com.common.service.dataSync;

import com.common.pojo.User.UserMainInf;
import com.common.pojo.chatRoom.GroupInf;
import com.common.pojo.chatRoom.GroupMem;

import java.util.List;
import java.util.Map;

public interface DataSyncService{
    void start();

    void syncGroupInf();


    void syncGroupMem();
    List<GroupMem> getGroupMem();

    Map<Integer, GroupInf> getGroupInfMap();

    Map<Integer, UserMainInf> getUserInf();

    void deleteGroup(Integer groupId);
}