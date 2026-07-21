package cn.luowb.shortlink.project.mq.producer;

import cn.hutool.core.util.IdUtil;
import cn.luowb.shortlink.project.common.porperties.LinkStatsRocketMQProperties;
import cn.luowb.shortlink.project.mq.message.LinkStatsRecordMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 短链接访问统计消息生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LinkStatsMessageProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final LinkStatsRocketMQProperties properties;
    private final ObjectMapper objectMapper;

    /**
     * 发送访问统计消息
     *
     * @param message 访问统计消息
     */
    public void send(LinkStatsRecordMessage message) {
        String messageId = IdUtil.fastSimpleUUID();
        try {
            rocketMQTemplate.syncSend(
                    properties.getLinkStatsTopic(),
                    MessageBuilder.withPayload(objectMapper.writeValueAsString(message))
                            .setHeader(RocketMQHeaders.KEYS, messageId)
                            .build()
            );
        } catch (JsonProcessingException e) {
            log.error("发送短链接访问统计消息失败，messageId={}，fullShortUrl={} ", messageId, message.getFullShortUrl(), e);
        } catch (Exception e) {
            log.error("发送短链接访问统计消息出现未知异常，messageId={}，fullShortUrl={}", messageId, message.getFullShortUrl(), e);
        }
    }
}
