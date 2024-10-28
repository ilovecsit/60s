package com.common.pojo.message;


import com.common.pojo.User.UserMainInf;

import java.time.LocalDateTime;

/**
 * 群聊消息，不存入数据库*/
public class GroupMessage {
 /**
  * 消息发送者*/
 private UserMainInf messageSender;

 /**
  * 消息发送时间*/
 private Long messageSendTime;

 /**
  * 消息类型
  * 0:文本
  * 1.图片
  * 2.视频
  * 3.音频
  * 4.提醒（会提醒消息接收对象）
  * 5.链接
  * 100.其它信息
  **/
 private int messageType;

 /**
  * 消息
  * 不论是什么类型，这里只处理文本，链接可以用其它服务器
  * */

 private String message;

 /**
  * 接受消息的群聊id*/
 private int groupId;




 @Override
 public String toString() {
  return "GroupMessage{" +
          "messageSender=" + messageSender +
          ", messageSendTime=" + messageSendTime +
          ", messageType=" + messageType +
          ", message='" + message + '\'' +
          ", groupId=" + groupId +
          '}';
 }

 public UserMainInf getMessageSender() {
  return messageSender;
 }

 public void setMessageSender(UserMainInf messageSender) {
  this.messageSender = messageSender;
 }

 public Long getMessageSendTime() {
  return messageSendTime;
 }

 public void setMessageSendTime(Long messageSendTime) {
  this.messageSendTime = messageSendTime;
 }

 public int getMessageType() {
  return messageType;
 }

 public void setMessageType(int messageType) {
  this.messageType = messageType;
 }

 public String getMessage() {
  return message;
 }

 public void setMessage(String message) {
  this.message = message;
 }

 public int getGroupId() {
  return groupId;
 }

 public void setGroupId(int groupId) {
  this.groupId = groupId;
 }
}
