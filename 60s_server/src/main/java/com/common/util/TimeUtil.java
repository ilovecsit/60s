package com.common.util;

public class TimeUtil {
    /**
     * 计算给定的时间是否比当前时间大 指定 秒s
     * 也即 改时间 距离当前是否超过 指定秒数 超过 返回true 否则返回 false
     * 如 8：58：32s ，而时间 8：58：30s,若指定时间 为 3s，显然，时间未超过 3秒
     * */
    public static boolean isExceedTotalSeconds(Long time,Long totalSeconds){
        return (System.currentTimeMillis() - time) > totalSeconds*1000;
    }

    /**
     * 将时间转化为秒*/
    public static Long getSecond(Long time){
        return time/1000;
    }
}
