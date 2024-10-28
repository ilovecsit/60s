package com.common.mapper.DataSyncMapper;

import com.common.pojo.User.UserMainInf;
import com.common.pojo.chatRoom.GroupInf;
import com.common.pojo.chatRoom.GroupMem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

@Mapper
public interface DataSyncMapper {
    /**
     * 查询所有的群聊表的信息
     */
    @Select("select * from all_group_mes")
    List<GroupInf> syncGroupInf() ;

    /**查询用户与群聊对应表*/
    @Select("select * from group_mem")
    List<GroupMem> syncGroupMemSet();


    void addNewGroupMem(GroupMem groupMem);

    @Select("select * from usermaininformation")
    List<UserMainInf> syncUserInf();
}
