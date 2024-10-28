package com.common.mapper.database;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DatabaseMapper {
    @Select("select * from isinit")
    IsInit isInit();

    @Insert("insert into isinit values(true)")
    void initDatabaseSuccessfully();
}
