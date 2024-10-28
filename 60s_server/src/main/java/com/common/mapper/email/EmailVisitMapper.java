package com.common.mapper.email;

import com.common.pojo.Record.EmailVisitRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.sql.Date;

@Mapper
public interface EmailVisitMapper {
    @Select("select * from emailvisitrecord where ip=#{ip}")
    public EmailVisitRecord selectEmailVisitRecordByIp(String ip) ;

    @Insert("insert into emailvisitrecord (ip,lastVisitDate,visitTime) values (#{clientIp},#{now},0)")
    void insertEmailVisitRecordByIp(String clientIp, Date now);

    @Insert("update emailvisitrecord set lastVisitDate=#{today},visitTime=#{todayVisitTime} where ip=#{ip}")
    void updateEmailVisitByIp(String ip, Date today, int todayVisitTime);
}
