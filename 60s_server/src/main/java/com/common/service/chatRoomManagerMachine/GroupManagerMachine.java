package com.common.service.chatRoomManagerMachine;

import com.common.pojo.chatRoom.GroupEntity;
import com.common.pojo.message.GroupMessage;
import com.common.service.Socket.group.imp.GroupSocketServiceImp;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 群聊管理器
 * 管理所有群聊
 */

/**
 * 注意，考虑到性能问题，消息不发送到具体的聊天室实体中，而是通过一个线程管理同时管理多个群聊的消息
 */
public class GroupManagerMachine {
    // 定义管理器数量
    private static final long THREAD_APPLY_NUM = 10;
    // 定义管理器销毁休眠时间 单位 :ms
    public static final long MANAGER_DESTROY_SLEEP_TIME = 3 * 1000;
    // 定义管理器发送休眠时间
    public static final long MANAGER_SEND_SLEEP_TIME = (long) (0.05 * 1000);

    /**
     * 消息过期时间
     * 单位：s
     */
    public static final long EXPIRATION_TIME = 60;

    // 定义单线程管理群聊的数组
    private static SingleThreadManageGroup[] singleThreadManageGroups;

    static {
        singleThreadManageGroups = new SingleThreadManageGroup[(int) THREAD_APPLY_NUM];
    }

    /**
     * 定义为工具类
     */
    private GroupManagerMachine() {
    }

    /**
     * 启动群聊管理器
     * 1.自动销毁服务
     * 2.自动转发服务
     */
    public static void start() throws InterruptedException {
        System.out.println("群聊管理器启动:" + LocalDateTime.now());
        for (int i = 0; i < THREAD_APPLY_NUM; i++) {
            Thread.sleep(MANAGER_DESTROY_SLEEP_TIME / THREAD_APPLY_NUM);
            singleThreadManageGroups[i] = new SingleThreadManageGroup();
            int finalI = i;
            new Thread(() -> {
                singleThreadManageGroups[finalI].start();
            }, "群聊管理器--子管理器" + i).start();
        }
        new Thread(() -> {
            sendMessageToWebSockets();
        }).start();
    }

    // 定义群聊数量
    private static int groupNum = 0;
    // 定义群聊与对应管理器的映射关系
    private static final Map<Integer, Integer> groupManageGroupHashMap = new HashMap<>();

    /**
     * 将群聊实体注入管理器中
     * 没有实际使用
     * @param groupEntity 群聊实体
     */
    public static synchronized void addGroup(GroupEntity groupEntity) {
        int managerId = (int) (groupNum++ % THREAD_APPLY_NUM);
        singleThreadManageGroups[managerId].addGroup(groupEntity);
        groupManageGroupHashMap.put(groupEntity.getGroupInf().getGroupId(), managerId);
    }


    /**
     * 将消息分配到子管理器进行管理
     *
     * @param groupMessage 群聊消息
     */
    public static void sendMessageToSingleMachine(GroupMessage groupMessage) {
        if (isMessageLegal(groupMessage)) {
            Integer managerId = groupManageGroupHashMap.get(groupMessage.getGroupId());
            if (managerId != null) {
                SingleThreadManageGroup singleThreadManageGroup = singleThreadManageGroups[managerId];
                if (singleThreadManageGroup != null) {
                    singleThreadManageGroup.allGroupMessages.add(groupMessage);
                }
            }
        }
    }

    /**
     * 取出子管理器中的所有消息，转发给websocket集中管理器
     */
    public static void sendMessageToWebSockets() {
        while (true) {
            try {
                Thread.sleep(MANAGER_SEND_SLEEP_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < THREAD_APPLY_NUM; i++) {
                singleThreadManageGroups[i].allGroupMessages.forEach(groupMessage -> {
                    if (groupMessage != null) {
                        GroupSocketServiceImp.sendMesToGoalSocket(groupMessage);
                    }
                });
            }
        }
    }


    /**
     * 检查消息合法性
     *
     * @param groupMessage 群聊消息
     * @return 消息是否合法
     */
    public static boolean isMessageLegal(GroupMessage groupMessage) {
        // 实现具体的验证逻辑
        // 例如检查发送人和消息内容等
        return true; // 示例返回值
    }


    /**
     * 接收从websockets中传来的消息
     */
    public static void messageIntoMachine(List<GroupMessage> list) {
        list.forEach(GroupManagerMachine::sendMessageToSingleMachine);
    }
}
