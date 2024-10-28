package com.common.service.SystemManager;

import com.common.mapper.SystemManager.SystemMangerMapper;
import com.common.pojo.User.UserMainInf;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemManagerService {
     @Autowired
     private SystemMangerMapper systemMangerMapper;

    private UserMainInf systemManager;
    /**
     * 初始时获取系统管理员的信息*/
    public void setManagerId(){
        systemManager=this.systemMangerMapper.getManger();
    }

    public UserMainInf getSystemManager(){
        return systemManager;
    }
}
