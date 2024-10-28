package com.common.service;

import com.common.mapper.database.DatabaseMapper;
import com.common.mapper.group.GroupMapper;
import com.common.mapper.user.UserMapper;
import com.common.pojo.User.UserMainInf;
import com.common.pojo.chatRoom.GroupInf;
import com.common.service.Socket.group.GroupSocketService;
import com.common.service.SystemManager.SystemManagerService;
import com.common.service.dataSync.DataSyncService;
import com.common.service.group.GroupService;
import com.common.util.Md5Util;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InitMyService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private GroupMapper groupMapper;

    /**
     * 自动注入
     * websockets管理器
     */
    @Autowired
    private GroupSocketService groupSocketService;

    /**
     * 自动注入
     * 数据库信息同步器
     */
    @Autowired
    private DataSyncService dataSyncService;

    @Value("${groupSetting.autoPublicGroupNum}")
    public Integer autoPublicGroupNum;

    @Autowired
    private SystemManagerService systemManagerService;
    @PostConstruct
    public void myInit() {

        /**
         * 初始化数据库*/
        initDatabase();

        /**
         * 加载websockets管理器
         * */
        groupSocketService.start();

        /**
         * 启动数据库信息同步器*/
        dataSyncService.start();

        systemManagerService.setManagerId();
    }

    @Autowired
    private DatabaseMapper databaseMapper;


    /**
     * 初始化数据库
     * 如果没有初始化则开始初始化
     * 后期可以考虑无需自己建立数据库的版本
     */
    @Transactional
    public void initDatabase() {

        String groupAvatars[] = {
                "https://img0.baidu.com/it/u=168755884,2755842788&fm=253&fmt=auto&app=120&f=JPEG?w=500&h=500",
                "https://n.sinaimg.cn/sinakd20116/96/w2048h2048/20240323/24a7-52a54c327350fe430e27f8b5847a0bf5.jpg",
                "https://ww1.sinaimg.cn/mw690/6910b6f2gy1hrkg6qz6ejj20n00n0ac9.jpg"
        };

        if (this.databaseMapper.isInit() == null) {
            System.out.println("正在进行数据库初始化……");
            /**
             * 自动创建一个系统管理员角色*/
            this.userMapper.initAutoInsert(
                    Md5Util.getMD5String("60sAdmin"),
                    "admin@qq.com",
                    "系统管理员",
                    "https://gips0.baidu.com/it/u=3602773692,1512483864&fm=3028&app=3028&f=JPEG&fmt=auto?w=960&h=1280",
                    0
            );

            Integer managerId = this.userMapper.selectByUserRole(0).getUserId();

            /**
             *自动创建公共群聊，如果没有的话 */
            for (int i = 0; i < autoPublicGroupNum; i++) {
                this.groupMapper.addAutoGroup("系统群--[" + (i + 1) + "群]",
                        managerId,
                        LocalDateTime.now(),
                        1000,
                        10,
                        groupAvatars[i % groupAvatars.length],
                        0
                );
            }

            List<GroupInf> groupInfList = this.groupMapper.getAllGroupInf();

            groupInfList.forEach((groupInf) -> {
                this.groupMapper.addUserToGroup(managerId, groupInf.getGroupId(), 0);
            });

            this.databaseMapper.initDatabaseSuccessfully();

        }

        System.out.println("数据库已经初始化……");


    }
}
