package com.text.util;

import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageExt;
import com.text.user.service.impl.UserServiceImpl;

public class RocketMQUtil implements ApplicationContextAware{
	
	/**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
    
	
	/**
	 * 生产者公用方法
	 * @param ip   			  ip地址
	 * @param producterName   生产者的group
	 * @param topicName       topic名称
	 * @param tagName         tag名称
	 * @param body            消息内容
	 */
	public static void producer(String ip,String producterName,String topicName,String tagName,String body){
		//声明并初始化一个producer
	    //需要一个producer group名字作为构造方法的参数，这里为producer1
	    DefaultMQProducer producer = new DefaultMQProducer(producterName);
	    //producer.getCreateTopicKey();
	    //设置NameServer地址,此处应改为实际NameServer地址，多个地址之间用；分隔
	    //NameServer的地址必须有，但是也可以通过环境变量的方式设置，不一定非得写死在代码里
	    producer.setNamesrvAddr(ip);
	    
	    //调用start()方法启动一个producer实例
	    try {
			producer.start();
		} catch (MQClientException e1) {
			e1.printStackTrace();
		}

	    //发送10条消息到Topic为TopicTest，tag为TagA，消息内容为“Hello RocketMQ”拼接上i的值
	    for (int i = 0; i < 3; i++) {
	        try {
	            Message msg = new Message(topicName,// topic
	            		tagName,// tag
	            		body.getBytes()// body
	            );
	            
	            //调用producer的send()方法发送消息
	            //这里调用的是同步的方式，所以会有返回结果
	            SendResult sendResult = producer.send(msg);
	            
	            //打印返回结果，可以看到消息发送的状态以及一些相关信息
	            System.out.println(sendResult);
	        } catch (Exception e) {
	            e.printStackTrace();
	            try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
	        }
	    }

	    //发送完消息之后，调用shutdown()方法关闭producer
	    producer.shutdown();
		
	}
	
	/**
	 * 消费者方法
	 * @param ip            ip
	 * @param customerName  消费者组
	 * @param topicName     topic名称
	 * @param beanName      调用的dao层bean的名字
	 */
	public static void customer(String ip,String customerName,String topicName){
		
		final UserServiceImpl userServiceImpl = (UserServiceImpl) getApplicationContext().getBean("userServiceImpl");
		
		//声明并初始化一个consumer
        //需要一个consumer group名字作为构造方法的参数，这里为consumer1
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(customerName);

        //同样也要设置NameServer地址
        consumer.setNamesrvAddr(ip);

        //这里设置的是一个consumer的消费策略
        //CONSUME_FROM_LAST_OFFSET 默认策略，从该队列最尾开始消费，即跳过历史消息
        //CONSUME_FROM_FIRST_OFFSET 从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
        //CONSUME_FROM_TIMESTAMP 从某个时间点开始消费，和setConsumeTimestamp()配合使用，默认是半个小时以前
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //设置consumer所订阅的Topic和Tag，*代表全部的Tag
        try {
			consumer.subscribe(topicName,"*");
		} catch (MQClientException e1) {
			e1.printStackTrace();
		}

        //设置一个Listener，主要进行消息的逻辑处理
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs,ConsumeConcurrentlyContext context) {

            	userServiceImpl.custom(msgs);
                
                //返回消费状态
                //CONSUME_SUCCESS 消费成功
                //RECONSUME_LATER 消费失败，需要稍后重新消费
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
        });

        //调用start()方法启动consumer
        try {
			consumer.start();
		} catch (MQClientException e) {
			e.printStackTrace();
		}

        System.out.println("Consumer Started.");
    }

}
