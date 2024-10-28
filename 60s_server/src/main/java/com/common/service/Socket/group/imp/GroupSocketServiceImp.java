package com.common.service.Socket.group.imp;

import com.common.mapper.socket.group.GroupSocketMapper;
import com.common.pojo.User.GagUser;
import com.common.pojo.User.UserMainInf;
import com.common.pojo.chatRoom.GroupEntity;
import com.common.pojo.chatRoom.GroupInf;
import com.common.pojo.chatRoom.GroupMem;
import com.common.pojo.message.GroupMessage;
import com.common.service.Socket.group.GroupSocketService;
import com.common.service.chatRoomManagerMachine.GroupManagerMachine;
import com.common.service.dataSync.DataSyncService;
import com.common.util.JsonUtil;
import com.common.util.TimeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * webSocket统一处理管理器
 * 可以改写成service层
 * 管理所有的websocket
 */
@Service
public class GroupSocketServiceImp implements GroupSocketService {
    @Autowired
    private GroupSocketMapper groupSocketMapper;


    /**
     * 群成员信息同步时间
     * 单位 s
     */
    @Value("${webSocketsManager.syncTime}")
    private int syncTime;
    /**
     * 定义2个哈希表，管理所有的websocket，键为userId
     */
    private static final Map<Integer, WebSocketSession> websockets = new ConcurrentHashMap<>();
    private static final Map<WebSocketSession, Integer> websocketsInv = new ConcurrentHashMap<>();

    public static void gagUserRemove(Integer groupId, Integer gagId) {
        for (Integer integer : groupGagUserMap.get(groupId)) {
            if (integer.equals(gagId)) {
                groupGagUserMap.get(groupId).remove(integer);
                return;
            }
        }
    }


    /**
     * 将websocket加入管理器
     */
    @Override
    public void add(Integer userID, WebSocketSession session) {
        deleteWebSocketMore(userID);
        websockets.put(userID, session);
        websocketsInv.put(session, userID);
    }

    /**
     * 关闭重复的websession
     */
    private void deleteWebSocketMore(Integer userID) {
        if (websockets.containsKey(userID)) {
            WebSocketSession session = websockets.get(userID);
            websockets.remove(userID);
//            关闭该 websocket session
            try {
                session.close();
                websocketsInv.remove(session);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * websocket退出管理器
     */
    @Override
    public void delete(Integer userID, WebSocketSession session) {
        websockets.remove(userID);
        websocketsInv.remove(session);
    }

    /**
     * websocket管理器启动
     * 同时将会自动启动 群聊管理器
     */
    public void start() {

        /**
         * 启动服务,开始管理websocket
         * */
        System.out.println("websockets管理器启动:" + LocalDateTime.now());


        /**
         * 禁言名单初始化*/

        new Thread(() -> {
            initGagMap();
        }).start();
        /**
         * 群成员信息同步器，启动*/
        new Thread(() -> {
            syncGroupData();
        }).start();
        /**
         * socket同步管理器,启动
         * */
        new Thread(() -> {
            syncGroupSocket();
        }).start();

        /**
         * 群消息管理器，启动*/
        try {
            GroupManagerMachine.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        /**
         * 初始化群聊管理器
         * 向其中注入要管理的群聊*/
        initGroupManagerMachine();
        /**
         * 消息转入器，启动*/
        new Thread(() -> {
            messageInGroupManagerMachine();
        }).start();

        /**
         * 信息--用户已发送表自动删除,启动*/
        new Thread(() -> {
            deleteExMes();
        }).start();

    }

    private void initGagMap() {
        List<GagUser> gagUserList = this.groupSocketMapper.initGagMap();
        for (GagUser gagUser : gagUserList) {
            Set gagSetInGroup = groupGagUserMap.getOrDefault(gagUser.getGroupId(), new HashSet<>());
            gagSetInGroup.add(gagUser.getGroupMemId());
            groupGagUserMap.put(gagUser.getGroupId(), gagSetInGroup);
        }
    }

    /**
     * 群聊及其禁言名单
     */
    private static Map<Integer, Set<Integer>> groupGagUserMap = new ConcurrentHashMap<>();

    public static void gagUserAdd(Integer groupId, Integer userID) {
        Set<Integer> gagUsers;
        if (groupGagUserMap.containsKey(groupId)) {
            gagUsers = groupGagUserMap.get(groupId);
        } else {
            gagUsers = new HashSet<>();
        }
        gagUsers.add(userID);
        groupGagUserMap.put(groupId, gagUsers);
    }

    /**
     * 初始化群聊管理器
     * 1.像其中注入要管理的群聊
     */
    private void initGroupManagerMachine() {
        List<GroupInf> groupInfs = this.groupSocketMapper.selectAllGroupInf();
        for (GroupInf groupInf : groupInfs) {
            GroupEntity groupEntity = new GroupEntity();
            groupEntity.setGroupInf(groupInf);
            GroupManagerMachine.addGroup(groupEntity);
        }
    }

    /**
     * 存放未转入到消息处理器的消息
     */
    private List<GroupMessage> groupMessages = new LinkedList<>();


    /**
     * 将websockets信息转入群聊管理器的时间
     */
    @Value("${webSocketsManager.messageInMachineWaitTime}")
    private long messageInMachineWaitTime;

    /**
     * 每隔一段时间，将信息转入到群聊消息处理器
     */
    private void messageInGroupManagerMachine() {
        while (true) {
//          将信息转发到群聊消息处理器
            GroupManagerMachine.messageIntoMachine(groupMessages);
            groupMessages.clear();
            try {
                Thread.sleep(messageInMachineWaitTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 用一个哈希表维护组id与对应的sockets
     */
    private static Map<Integer, List<WebSocketSession>> groupSocketMap = new ConcurrentHashMap<>();

    /**
     * 同步 群聊 与 对应的 socket
     */
    private void syncGroupSocket() {
        while (true) {
            synchronized (groupMemMap) {
                groupMemMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().stream()
                                        .map(userId -> websockets.get(userId))
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList())
                        ))
                        .forEach((groupID, list) -> groupSocketMap.put(groupID, list));
            }
            try {
                Thread.sleep(syncTime * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 最小的消息发送间隔
     */
    private static int smallestIntervalSend = 2;

    private boolean lessIntervalSecond(Map<Integer, Long> userIdSendTimeMap, Integer senderID, Long sendTime) {
        if (userIdSendTimeMap.containsKey(senderID)) {
            if (TimeUtil.getSecond(sendTime-userIdSendTimeMap.get(senderID))<2) {
                return true;
            } else {
                userIdSendTimeMap.put(senderID, sendTime);
            }
        } else {
            userIdSendTimeMap.put(senderID, sendTime);
        }
        return false;
    }

    /**
     * 消息接收处理器
     * 接收websocket传来的所有信息
     * 并将消息处理后转发给对应的群聊管理器
     */
    @Autowired
    DataSyncService dataSyncService;

    @Autowired
    private SensitiveWordBs sensitiveWordBs;

    @Override
    public void receiveMessage(WebSocketSession webSocketSession, Integer senderID, String message, Long sendTime) {
        if (message.equals("I need old message")) {
            getOldMessages(webSocketSession, senderID);
        } else {
            if (lessIntervalSecond(userIdSendTimeMap, senderID, sendTime)) {
                if (webSocketSession.isOpen()) {
                    try {
                        webSocketSession.sendMessage(new TextMessage("fast"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return;
            }
            GroupMessage groupMessage = parseJson(message);
            if (groupMessage.getMessage().length() >= 255) {
                return;
            }
            if (groupGagUserMap.containsKey(groupMessage.getGroupId()) && groupGagUserMap.get(groupMessage.getGroupId()).contains(senderID)) {
                if (webSocketSession.isOpen()) {
                    try {
                        webSocketSession.sendMessage(new TextMessage("gag"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return;
            }
            groupMessage.setMessage(sensitiveWordBs.replace(groupMessage.getMessage()));
            if (!checkMessageIllegal(groupMessage, senderID)) {
                return;
            }
            groupMessage.setMessageSendTime(sendTime);

            Map<Integer, UserMainInf> userMainInf = dataSyncService.getUserInf();
            if (userMainInf.containsKey(senderID)) {
                UserMainInf userMainInf1 = userMainInf.get(senderID);
                groupMessage.setMessageSender(userMainInf1);
            }
            Integer groupId=groupMessage.getGroupId();
            groupMesNumMap.put(groupId,groupMesNumMap.getOrDefault(groupId,0)+1);
            groupMessages.add(groupMessage);
        }
    }

    private Map<Integer, Long> userIdSendTimeMap = new ConcurrentHashMap<>();

    @Override
    public void groupUserExit() {

    }

    /**
     * 检查消息是否合法
     * 合法 返回true
     * 不合法 返回false
     */
    private boolean checkMessageIllegal(GroupMessage groupMessage, Integer senderID) {
/*        System.out.println(groupMessage.getMessageSender().getUserId());
        System.out.println(senderID);
        System.out.println(groupMessage.getMessageSender().getUserId().equals(senderID));*/
        synchronized (groupMemMap) {
            return groupMessage != null/**消息是否规范*/
                    && groupMessage.getMessageSender().getUserId().equals(senderID)/**发送者是否是当前会话人*/
                    && groupMemMap.containsKey(groupMessage.getGroupId())/**目标群聊是否存在*/
                    && groupMemMap.get(groupMessage.getGroupId()).contains(groupMessage.getMessageSender().getUserId())/**该用户是否在该群聊中*/;
        }
    }


    /**
     * 处理消息，
     * 解析json数据，
     * 如果格式没问题则返回处理后的处理，
     * 否则返回null*/

    /**
     * {
     * "messageSender": {
     * "userId": 10012
     * },
     * "messageType": 0,
     * "message": "Hello, json!",
     * "groupId": 10
     * }
     */
    public GroupMessage parseJson(String message) {
//    用json解析工具解析json
        try {
            GroupMessage groupMessage = new ObjectMapper().readValue(message, GroupMessage.class);
            return groupMessage;
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * 用来记录组和组成员的关系
     */
    private static Map<Integer, Set<Integer>> groupMemMap = new ConcurrentHashMap<>();


    /**
     * 同步数据库中的信息
     * 1-同步信息项如下
     * 1.群成员 与 群 的关系信息 通过一张哈希表 (groupID,userID) 记录
     * 2.待定
     * 2-每10s同步一次
     * 3-存在很大的问题，有优化空间
     */
    public void syncGroupData() {
        while (true) {
            List<GroupMem> groupMems = this.groupSocketMapper.selectGroupMem();
            synchronized (groupMemMap) {
                for (GroupMem groupMem : groupMems) {
                    Set<Integer> userIdSet = groupMemMap.getOrDefault(groupMem.getGroupId(), new HashSet<>());
                    userIdSet.add(groupMem.getGroupMemId());
                    groupMemMap.put(groupMem.getGroupId(), userIdSet);
                }
            }
            try {
                Thread.sleep(syncTime * 1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * 删除缓存中的群信息
     */
    public synchronized void deleteGroup(Integer groupId) {
        for (GroupMessage groupMessage : groupMessages) {
            if (groupMessage.getGroupId() == groupId) {
                groupMessages.remove(groupMessage);
            }
        }
        groupMemMap.entrySet().removeIf(entry -> entry.getKey().equals(groupId));
        groupMemMap.entrySet().removeIf(entry -> entry.getKey().equals(groupId));
    }

    /**
     * 为每一条消息创建一个 消息--已发用户表
     * 每隔一分钟删除该表
     */
    private static Map<GroupMessage, Set<Integer>> groupMessageUserMap = new ConcurrentHashMap<>();

    /**
     * 当用户刷新请求时，重新把消息发过来
     */
    private static synchronized void getOldMessages(WebSocketSession webSocketSession, Integer userId) {
        groupMessageUserMap.forEach((groupMessage, set) -> {
            if (set.contains(userId)) {
                try {
                    if (webSocketSession.isOpen()) {
                        webSocketSession.sendMessage(new TextMessage(JsonUtil.mapper.writeValueAsString(groupMessage)));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 删除过期的 已发消息 用户 对应序列
     */
    public static void deleteExMes() {
        while (true) {
            try {
                Thread.sleep((long) (GroupManagerMachine.EXPIRATION_TIME * 1000));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Set<Map.Entry<GroupMessage, Set<Integer>>> entries = groupMessageUserMap.entrySet();
            for (Map.Entry<GroupMessage, Set<Integer>> entry : entries) {
                if (TimeUtil.isExceedTotalSeconds(entry.getKey().getMessageSendTime(),  GroupManagerMachine.EXPIRATION_TIME)) {
                    groupMessageUserMap.remove(entry.getKey());
                }
            }
        }
    }



    /**
     * 将从群聊管理器中发来的消息转发到对应的目标websocket中
     * 已经接收到消息的人不会再次接收
     */
    public static void sendMesToGoalSocket(GroupMessage groupMessage) {

        groupSocketMap.get(groupMessage.getGroupId()).forEach(websocketSession -> {
//            判断消息类型 目前只有文本信息
//            通过websocket向用户发送消息
//            判断消息是否已经发送给该用户？如果发送了，不必发送
            if (groupMessageUserMap.containsKey(groupMessage)) {
                Set<Integer> userIdSet = groupMessageUserMap.get(groupMessage);
                if (userIdSet.contains(websocketsInv.get(websocketSession))) {
                    return;
                }
            }
            if (websocketSession.isOpen()) {
                try {
                    websocketSession.sendMessage(new TextMessage(JsonUtil.mapper.writeValueAsString(groupMessage)));
                    Set<Integer> set = groupMessageUserMap.getOrDefault(groupMessage, new HashSet<>());
                    set.add(websocketsInv.get(websocketSession));
                    groupMessageUserMap.put(groupMessage, set);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 统计组中保留的消息数量
     * 消息被发送的时候增加，否则删除
     * */
    public static Map<Integer,Integer> groupMesNumMap=new ConcurrentHashMap<>();

}

