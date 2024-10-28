package com.common.pojo.chatRoom;

import com.common.pojo.message.GroupMessage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 群聊实体*/
public class GroupEntity {
    /**
     * 群聊基本信息*/
    private GroupInf groupInf;

    /**
     * 群成员*/
    private final List<GroupMem> groupMem=new ArrayList<>();

    /**
     * 群管理员*/
    private final List<Manager> managers=new ArrayList<>();

    /**
     * 群消息*/
    private final List<GroupMessage> groupMessages=new LinkedList<>();

    @Override
    public String toString() {
        return "GroupEntity{" +
                "groupInf=" + groupInf +
                ", groupMem=" + groupMem +
                ", managers=" + managers +
                ", groupMessages=" + groupMessages +
                '}';
    }

    public GroupInf getGroupInf() {
        return groupInf;
    }

    public void setGroupInf(GroupInf groupInf) {
        this.groupInf = groupInf;
    }

    public List<Manager> getManagers() {
        return managers;
    }

    public List<GroupMem> getGroupMem() {
        return groupMem;
    }

    public List<GroupMessage> getGroupMessages() {
        return groupMessages;
    }
}
