package com.dahua.boke.rocketmqTest;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.dahua.boke.entity.Follow;
 
/**
 * Created by lance on 2017/2/10.
 */
public class RocketMQProducerTest {
 
	public static void main(String[] args) throws MQClientException, InterruptedException {

        //声明并初始化一个producer
        //需要一个producer group名字作为构造方法的参数，这里为producer1
        DefaultMQProducer producer = new DefaultMQProducer("dwq");
        //producer.getCreateTopicKey();
        //设置NameServer地址,此处应改为实际NameServer地址，多个地址之间用；分隔
        //NameServer的地址必须有，但是也可以通过环境变量的方式设置，不一定非得写死在代码里
        producer.setNamesrvAddr("101.132.136.190:9876");
        
        //调用start()方法启动一个producer实例
        producer.start();

        //发送10条消息到Topic为TopicTest，tag为TagA，消息内容为“Hello RocketMQ”拼接上i的值
        for (int i = 0; i < 1; i++) {
        	Follow f = new Follow();
        	f.setParentId(1);
        	f.setChildId(2);
        	f.setCreateTime(new Date());
            try {
                Message msg = new Message("new",// topic
                        "follow",// tag
                        JSONObject.toJSONString(f).getBytes()// body
                );
                
                //调用producer的send()方法发送消息
                //这里调用的是同步的方式，所以会有返回结果
                SendResult sendResult = producer.send(msg);
                
                //打印返回结果，可以看到消息发送的状态以及一些相关信息
                System.out.println(sendResult);
            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }

        //发送完消息之后，调用shutdown()方法关闭producer
        producer.shutdown();
    }

}
