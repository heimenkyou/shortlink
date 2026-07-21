package cn.luowb.shortlink.project.mq.producer;

import cn.luowb.shortlink.project.common.porperties.LinkStatsRocketMQProperties;
import cn.luowb.shortlink.project.mq.message.LinkStatsRecordMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
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
        try {
            rocketMQTemplate.syncSend(properties.getLinkStatsTopic(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("发送短链接访问统计消息失败，messageId={}，fullShortUrl={} ", message.getMessageId(), message.getFullShortUrl(), e);
        } catch (Exception e) {
            log.error("发送短链接访问统计消息出现未知异常，messageId={}，fullShortUrl={}", message.getMessageId(), message.getFullShortUrl(), e);
        }
    }
}
