package cn.luowb.shortlink.project.mq.consumer;

import cn.luowb.shortlink.project.common.porperties.LinkStatsRocketMQProperties;
import cn.luowb.shortlink.project.mq.message.LinkStatsRecordMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

/**
 * 短链接访问统计消息消费者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = "${app.mq.link-stats-topic}",
        consumerGroup = "${app.mq.consumer-group}",
        messageModel = MessageModel.CLUSTERING,
        consumeMode = ConsumeMode.CONCURRENTLY
)
public class LinkStatsMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private final LinkStatsRocketMQProperties properties;
    private final ObjectMapper objectMapper;
    private final LinkStatsMessageHandler linkStatsMessageHandler;

    /**
     * 消费消息
     *
     * @param messageBody 消息体
     */
    @Override
    public void onMessage(String messageBody) {
        try {
            LinkStatsRecordMessage message = objectMapper.readValue(messageBody, LinkStatsRecordMessage.class);
            linkStatsMessageHandler.handle(message);
        } catch (Exception e) {
            log.error("解析或处理短链接访问统计消息失败，body={}", messageBody, e);
            throw new IllegalStateException(e);
        }
    }

    /**
     * 初始化消费者线程数
     *
     * @param consumer RocketMQ 消费者
     */
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        int cpuCoreCount = Runtime.getRuntime().availableProcessors();
        int maxThreadCount = Math.max(cpuCoreCount, (int) Math.ceil(cpuCoreCount * 1.5D));
        consumer.setConsumeThreadMin(cpuCoreCount);
        consumer.setConsumeThreadMax(maxThreadCount);
        log.info("短链接访问统计消费者启动成功，topic={}", properties.getLinkStatsTopic());
    }
}
