package com.common.mapper.user;

import com.common.pojo.User.UserMainInf;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface UserMapper {

    /**
     * 初始化时自动创建角色
     */
    @Insert("insert into usermaininformation (userPassword,userEmail,userNickName,userPhotoUrl,userCreateTime,userLastUpdateTime,userLastLoginTime,userRole)" +
            " values (  #{userPassword},#{userEmail},#{userNickName},#{userPhotoUrl},now(),now(),'1997-1-1 00:00:00',#{userRole})")
    void initAutoInsert(String userPassword, String userEmail, String userNickName, String userPhotoUrl, Integer userRole);

    @Insert("insert into usermaininformation (userPassword,userEmail,userNickName,userCreateTime,userLastUpdateTime,userLastLoginTime) values (#{password},#{userEmail},#{userNickName},now(),now(),'1997-1-1 00:00:00')")
    void register(String userEmail, String userNickName, String verificationCode, String password);

    @Select("select * from usermaininformation where userEmail = #{userEmail}")
    UserMainInf selectByUserEmail(String userEmail);

    @Select("select * from usermaininformation where userId = #{userId}")
    UserMainInf selectByUserId(Integer userId);

    //    通过实体类对象修改数据库
    @Insert("update usermaininformation set userPassword = #{userPassword},userEmail = #{userEmail},userCreateTime = #{userCreateTime},userLastUpdateTime = #{userLastUpdateTime},userLastLoginTime = #{userLastLoginTime} where userId = #{userId}")
    void updateUserMainInf(UserMainInf userMainInf);

    @Select("select * from usermaininformation where userRole = #{userRole}")
    UserMainInf selectByUserRole(int i);

    @Update("update usermaininformation set userNickName=#{userNickName},userPhotoUrl=#{userPhotoUrl} where userId=#{userId}")
    void updateUserNameAndPhotoUrlById(UserMainInf userMainInf);
}
