package customer.realm;

import com.alibaba.fastjson.JSON;
import customer.dao.CustomerDao;
import entity.Follow;
import entity.LeaveMes;
import entity.Share;
import entity.Tag;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import util.StaticAddressUtil;

import java.util.List;

@Component
public class CustomerRealm {

    //rocketmq地址
    private static final String ipAddress = StaticAddressUtil.rocketMQTelnet;

    @Autowired
    private CustomerDao dao;

    public CustomerRealm() {
        start();
    }

    public void start() {
        //需要一个consumer group名字作为构造方法的参数，这里为customer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("customer");

        //同样也要设置NameServer地址
        consumer.setNamesrvAddr(ipAddress);

        //这里设置的是一个consumer的消费策略
        //CONSUME_FROM_LAST_OFFSET 默认策略，从该队列最尾开始消费，即跳过历史消息
        //CONSUME_FROM_FIRST_OFFSET 从队列最开始开始消费，即历史消息（还储存在broker的）全部消费一遍
        //CONSUME_FROM_TIMESTAMP 从某个时间点开始消费，和setConsumeTimestamp()配合使用，默认是半个小时以前
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        //设置consumer所订阅的Topic和Tag，*代表全部的Tag
        try {
            consumer.subscribe("dahuaboke", "*");
        } catch (MQClientException e1) {
            e1.printStackTrace();
        }

        //设置一个Listener，主要进行消息的逻辑处理
        consumer.registerMessageListener(new MessageListenerConcurrently() {

            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                for (MessageExt msg : msgs) {
                    if (new String(msg.getTags()).equals("top")) {
                        String id = new String(msg.getBody());
                        dao.top(id);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else if (new String(msg.getTags()).equals("untop")) {
                        String id = new String(msg.getBody());
                        dao.untop(id);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else if (new String(msg.getTags()).equals("follow")) {
                        Follow follow = JSON.parseObject(new String(msg.getBody()), Follow.class);
                        //Follow follow = JSON.parseObject(JSONObject.toJSONString(o), Follow.class);
                        dao.follow(follow);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else if (new String(msg.getTags()).equals("isdel")) {
                        String id = new String(msg.getBody());
                        dao.isdel(id);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else if (new String(msg.getTags()).equals("tagSave")) {
                        Tag map = JSON.parseObject(new String(msg.getBody()), Tag.class);
                        dao.tagSave(map);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else if (new String(msg.getTags()).equals("share")) {
                        Share map = JSON.parseObject(new String(msg.getBody()), Share.class);
                        dao.shareSave(map);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    } else if (new String(msg.getTags()).equals("leaveMes")) {
                        LeaveMes map = JSON.parseObject(new String(msg.getBody()), LeaveMes.class);
                        dao.leaveMesSave(map);
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                }

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
