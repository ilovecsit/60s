package com.common.mapper.socket.group;

import com.common.pojo.User.GagUser;
import com.common.pojo.chatRoom.GroupInf;
import com.common.pojo.chatRoom.GroupMem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * webSocket统一处理管理器
 * 可以改写成service层
 * 管理所有的websocket
 */
@Mapper
public interface GroupSocketMapper {

    @Select("select groupId,groupMemId from group_mem where `delete` = 0")
    public List<GroupMem> selectGroupMem();

    @Select("select groupId from all_group_mes")
    List<GroupInf> selectAllGroupInf();

    @Select("select groupId,groupMemId from group_mem where userKind=3")
    List<GagUser> initGagMap();
}



