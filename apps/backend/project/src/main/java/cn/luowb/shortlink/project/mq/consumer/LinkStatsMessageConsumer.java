package cn.luowb.shortlink.project.mq.consumer;

import cn.hutool.core.util.StrUtil;
import cn.luowb.shortlink.project.common.porperties.LinkStatsRocketMQProperties;
import cn.luowb.shortlink.project.mq.idempotent.MessageQueueIdempotentHandler;
import cn.luowb.shortlink.project.mq.message.LinkStatsRecordMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

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
public class LinkStatsMessageConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {

    private final LinkStatsRocketMQProperties properties;
    private final ObjectMapper objectMapper;
    private final LinkStatsMessageHandler linkStatsMessageHandler;
    private final MessageQueueIdempotentHandler messageQueueIdempotentHandler;

    /**
     * 消费消息
     *
     * @param message RocketMQ 原始消息
     */
    @Override
    public void onMessage(MessageExt message) {
        String messageBody = new String(message.getBody(), StandardCharsets.UTF_8);
        try {
            LinkStatsRecordMessage recordMessage = objectMapper.readValue(messageBody, LinkStatsRecordMessage.class);
            String messageId = message.getKeys();
            if (StrUtil.isBlank(messageId)) {
                throw new IllegalArgumentException("短链接访问统计消息缺少唯一标识");
            }
            // 消息队列消费幂等处理
            messageQueueIdempotentHandler.execute(messageId, () -> linkStatsMessageHandler.handle(recordMessage));
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
