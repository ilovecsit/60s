package com.common.service.chatRoomManagerMachine;

import com.common.pojo.chatRoom.GroupEntity;
import com.common.pojo.message.GroupMessage;
import com.common.service.Socket.group.imp.GroupSocketServiceImp;
import com.common.util.TimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.common.service.chatRoomManagerMachine.GroupManagerMachine.EXPIRATION_TIME;
import static com.common.service.chatRoomManagerMachine.GroupManagerMachine.MANAGER_DESTROY_SLEEP_TIME;

/**
 * 单线程管理群聊类
 */
class SingleThreadManageGroup {
    // 同步列表存储群聊实体
    private final List<GroupEntity> groupEntities = Collections.synchronizedList(new ArrayList<>());
    // 使用 ConcurrentLinkedQueue 存储所有群消息
    final ConcurrentLinkedQueue<GroupMessage> allGroupMessages = new ConcurrentLinkedQueue<>();

    /**
     * 群聊管理器，启动
     */
    public void start() {
        System.out.println(Thread.currentThread().getName() + "---启动");
        scheduleMessageDestroy();
    }

    /**
     * 消息销毁方法，定期清理过期消息
     */
    private void scheduleMessageDestroy() {
        while (true) {
            try {
                Thread.sleep(MANAGER_DESTROY_SLEEP_TIME);
//                System.out.println(Thread.currentThread().getName() + " - " + this.allGroupMessages);
                try {
                    // 使用迭代器遍历队列并移除过期的消息
                    Iterator<GroupMessage> iterator = allGroupMessages.iterator();
                    while (iterator.hasNext()) {
                        GroupMessage groupMessage = iterator.next();

                        if (TimeUtil.isExceedTotalSeconds(groupMessage.getMessageSendTime(),EXPIRATION_TIME)) {
                            iterator.remove();
                            GroupSocketServiceImp.groupMesNumMap.put(groupMessage.getGroupId(),GroupSocketServiceImp.groupMesNumMap.get(groupMessage.getGroupId())-1);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("消息销毁器" + Thread.currentThread().getName() + "失效" + e.getMessage());
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("消息销毁器被中断：" + e.getMessage());
                break;
            }
        }
    }


    /**
     * 添加群聊管理
     *
     * @param groupEntity 群聊实体
     */
    public synchronized void addGroup(GroupEntity groupEntity) {
        groupEntities.add(groupEntity);
    }

    /**
     * 添加所有群消息到各群聊中
     */
    public synchronized void addAllGroupMessage() {
        groupEntities.forEach(groupEntity -> {
            allGroupMessages.stream()
                    .filter(groupMessage -> groupMessage.getGroupId() == groupEntity.getGroupInf().getGroupId())
                    .forEach(groupEntity.getGroupMessages()::add);
        });
    }
}
