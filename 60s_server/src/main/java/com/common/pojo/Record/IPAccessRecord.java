package com.common.pojo.Record;

import java.time.Instant;

public class IPAccessRecord {
    private int record_num=3;
    private int requestNum = 0; // 请求总数
    private int newIdx = -1; // 最新访问记录的索引
    private int oldIdx = 0; // 最旧访问记录的索引
    private final Long[] accessTimes = new Long[record_num]; // 存储访问时间的数组

    public synchronized void recordAccess() {
        // 更新最新访问记录的索引
        newIdx = (newIdx + 1) % record_num;

        // 记录新的访问时间
        accessTimes[newIdx] = System.currentTimeMillis();

        // 更新请求总数
        requestNum++;

        // 如果请求总数超过N，循环数组
        if (requestNum > record_num) {
            oldIdx = (oldIdx+1)%record_num;
        }


    }

    public int getRequestNum() {
        return requestNum;
    }

    /**
     * 返回最近N条请求的平均访问速率
     * 单位：条/秒*/
    public Long getRate(){
        if (requestNum<record_num){
            return null;
        }
        return record_num/(((accessTimes[newIdx]-accessTimes[oldIdx])/1000)+1);
    }


}