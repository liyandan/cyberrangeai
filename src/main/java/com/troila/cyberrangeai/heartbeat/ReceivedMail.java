package com.troila.cyberrangeai.heartbeat;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedMail {

    public static final int MAIL_STAT_RECEIVED = 0; //邮件收到未处理状态
    public static final int MAIL_STAT_REPLYED  = 1; //邮件已回复状态
    public static final int MAIL_STAT_TIMEOUT  = 2; //邮件已超时尚未处理状态

    private String      from;               //这封邮件的发送者mail
    private String      cc;                 //这封邮件同时还抄送给的mail地址
    private String      subject;            //邮件标题
    private String      body;               //邮件正文
    private String      responseSubject;    //由算法自动生成
    private String      responseBody;       //由算法自动生成
    private String      responseCC;         //由算法自动生成
    private Long        eventOccurTime;     //从心跳包中接收到的邮件接收时间,以秒为单位
    private int         stat;               //接收到状态 0 、已回复状态 1、 已超时 2

}
