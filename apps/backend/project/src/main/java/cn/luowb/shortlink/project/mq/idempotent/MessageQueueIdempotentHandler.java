package cn.luowb.shortlink.project.mq.idempotent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static cn.luowb.shortlink.common.constant.RedisCacheKeyEnum.MESSAGE_QUEUE_IDEMPOTENT_KEY;

/**
 * 消息队列消费幂等处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageQueueIdempotentHandler {

    private static final long STATUS_TIMEOUT_SECONDS = 10 * 60;
    private static final String PROCESSING_STATUS = "0";
    private static final String COMPLETED_STATUS = "1";

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 在幂等控制下执行消息消费逻辑
     *
     * @param messageId 消息业务唯一标识
     * @param consumer  消息消费逻辑
     */
    public void execute(String messageId, Runnable consumer) {
        String consumeKey = MESSAGE_QUEUE_IDEMPOTENT_KEY.getKey(messageId);
        Boolean acquired = stringRedisTemplate.opsForValue()
                .setIfAbsent(consumeKey, PROCESSING_STATUS, STATUS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        if (!Boolean.TRUE.equals(acquired)) {
            // 已完成的重复消息正常返回，使 MQ 确认消费；处理中消息抛出异常等待重试。
            if (COMPLETED_STATUS.equals(stringRedisTemplate.opsForValue().get(consumeKey))) {
                log.info("消息已消费，跳过重复消息，messageId={}", messageId);
                return;
            }
            throw new IllegalStateException("消息正在处理中，messageId=" + messageId);
        }

        try {
            consumer.run();
            // 完成状态继续保留 10 分钟，用于拦截生产端短时间内发送的重复消息。
            stringRedisTemplate.opsForValue()
                    .set(consumeKey, COMPLETED_STATUS, STATUS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            // 失败后立即释放状态，避免阻塞 RocketMQ 的下一次重试。
            stringRedisTemplate.delete(consumeKey);
            throw e;
        }
    }
}
