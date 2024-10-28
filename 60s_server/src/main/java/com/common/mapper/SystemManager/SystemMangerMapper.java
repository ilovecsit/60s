package com.common.mapper.SystemManager;

import com.common.pojo.User.UserMainInf;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemMangerMapper {

    @Select("select * from usermaininformation where userRole=0")
    UserMainInf getManger();
}
