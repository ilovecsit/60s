package com.common.pojo.Record;

import lombok.Data;

import java.sql.Date;

@Data
public class EmailVisitRecord {
    String ip;
    Date lastVisitDate;
    Integer visitTime;
    Integer maxVisitTime;
}
