package com.bfei.icrane.core.service.impl;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.model.Message;
import com.aliyun.oss.OSSClient;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.iot.model.v20170620.PubRequest;
import com.aliyuncs.iot.model.v20170620.PubResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.bfei.icrane.common.util.PropFileManager;
import com.bfei.icrane.common.util.StringUtils;
import com.bfei.icrane.core.service.AliyunService;

/**
 * Author: mwan
 * Version: 1.1
 * Date: 2017/09/22
 * Description: 阿里云服务接口实现.
 * Copyright (c) 2017 伴飞网络. All rights reserved.
 */
public class AliyunServiceImpl implements AliyunService {

    //private static final String accessKey = "LTAIjAZ0jY2Kd7ZX";
    //private static final String accessSecret = "Eoypyu2XDccz7TyZduA6JoBU7panS7";
    //初始化ascClient需要的几个参数
    private static final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
    private static final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）

    private DefaultAcsClient iotClient; //物联网套件客户端
    private IAcsClient acsClient; //短信客户端
    private MNSClient mnsClient; //消息服务客户端
    private OSSClient ossClient; //OSS客户端

    private static AliyunServiceImpl _instance;

    public static AliyunServiceImpl getInstance() {
        if (_instance == null) {
            return (new AliyunServiceImpl());
        } else {
            return _instance;
        }
    }

    private AliyunServiceImpl() {
        //初始化key和secret
        PropFileManager propFileMgr = new PropFileManager("aliyun.properties");
        String accessKey = propFileMgr.getProperty("aliyun.accessKey");
        String accessSecret = propFileMgr.getProperty("aliyun.accessSecret");
        String mnsAccountEndPoint = propFileMgr.getProperty("aliyun.mnsAccountEndPoint");
        //初始化Iot套件SDK客户端
        try {
            DefaultProfile.addEndpoint("cn-shenzhen", "cn-shenzhen", "Iot", "iot.cn-shenzhen.aliyuncs.com");
        } catch (ClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        IClientProfile iotProfile = DefaultProfile.getProfile("cn-shenzhen", accessKey, accessSecret);
        iotClient = new DefaultAcsClient(iotProfile);
        //初始化MNS SDK客户端
        CloudAccount account = new CloudAccount(
                accessKey,
                accessSecret,
                mnsAccountEndPoint);
        mnsClient = account.getMNSClient();

        //初始化短信ascClient,暂时不支持多region
        IClientProfile smsProfile = DefaultProfile.getProfile("cn-shenzhen", accessKey, accessSecret);
        try {
            DefaultProfile.addEndpoint("cn-shenzhen", "cn-shenzhen", product, domain);
        } catch (ClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        acsClient = new DefaultAcsClient(smsProfile);

        //初始化OSS客户端
        ossClient = new OSSClient("http://oss-cn-shenzhen.aliyuncs.com", accessKey, accessSecret);
    }

    public Object pubMessageIot(String ProductKey, String topicName, String message, int... Qos) throws ServerException, ClientException {
        // TODO Auto-generated method stub
        PubRequest request = new PubRequest();
        request.setProductKey(ProductKey);
        request.setMessageContent(Base64.encodeBase64String(message.getBytes()));
        request.setTopicFullName(topicName);
        if (Qos == null || Qos.length == 0) {
            request.setQos(0); //默认设置为QoS0
        } else {
            request.setQos(Qos[0]); //目前支持QoS0和QoS1
        }

        PubResponse response = iotClient.getAcsResponse(request);
        //System.out.println("向阿里云发送消息："+message);
        return response;
    }

    public Object pubMessageMnsTopic(String topicName, String message, int... Qos) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getMessageMnsQueue(String queueName) throws Exception {
        // TODO Auto-generated method stub
        Message message = null;
        CloudQueue cloudQueue = mnsClient.getQueueRef(queueName);
        do {
            try {
                message = cloudQueue.popMessage(5);
            } catch (Exception e) {
                System.out.println("在从队列" + queueName + "polling消息的时候发生异常:" + e);
                throw e;
            }
        } while (message == null);
        cloudQueue.deleteMessage(message.getReceiptHandle());
        return message;
    }

    public Object getMessageMnsQueue(String queueName, String topicFilter) {
        // TODO Auto-generated method stub
        Message message = null;
        CloudQueue cloudQueue = mnsClient.getQueueRef(queueName);
        String topicName = null;
        do {
            try {
                message = cloudQueue.popMessage(30);
            } catch (Exception e) {
                System.out.println("Exception Happened when polling popMessage: " + e);
            }

            if (message != null) {
                topicName = StringUtils.parseData(message.getMessageBodyAsRawString()).get("t");
                if (topicName == null || !(topicName.equals(topicFilter))) {
                    message = null;
                }
            }
        } while (message == null);

        cloudQueue.deleteMessage(message.getReceiptHandle());
        return message;
    }

    @Override
    public boolean sendSMSForCode(String targetPhone, String signName, String templateCode, String code) throws ClientException {
        // TODO Auto-generated method stub

        SendSmsRequest request = new SendSmsRequest();
        //使用post提交
        request.setMethod(MethodType.POST);
        //必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
        request.setPhoneNumbers(targetPhone);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");
        //请求失败这里会抛ClientException异常
        SendSmsResponse sendSmsResponse;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
                //请求成功
                return true;
            }
        } catch (ClientException e) {
            // TODO Auto-generated catch block
            throw e;
        }
        return false;
    }


    //上传文件流到OSS
    @Override
    public boolean putFileStreamToOSS(String bucketName, String objectKey, InputStream inputStream) {
        // TODO Auto-generated method stub
        try {
            // 上传文件流
            ossClient.putObject(bucketName, objectKey, inputStream);

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    //从OSS删除文件
    @Override
    public boolean deleteFileFromOSS(String bucketName, String objectKey) {
        // TODO Auto-generated method stub
        try {
            // 删除
            ossClient.deleteObject(bucketName, objectKey);

            return true;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;

    }

    //生成一个签名的URL
    @Override
    public URL generatePresignedUrl(String bucketName, String objectKey, int expireHour) {
        // TODO Auto-generated method stub
        try {
            // 设置URL过期时间为1小时
            Date expiration = new Date(new Date().getTime() + 3600l * 1000 * expireHour);
            // 生成URL
            URL url = ossClient.generatePresignedUrl(bucketName, objectKey, expiration);

            return url;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }

}
