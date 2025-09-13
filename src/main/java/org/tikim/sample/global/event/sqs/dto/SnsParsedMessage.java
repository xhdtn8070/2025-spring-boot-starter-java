// SnsParsedMessage.java
package org.tikim.sample.global.event.sqs.dto;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

public record SnsParsedMessage<T>(
    T payload,                         // 제네릭 페이로드
    String snsMessageId,               // SNS MessageId (RAW일 땐 null)
    String snsTopicArn,                // SNS TopicArn (RAW일 땐 null)
    Instant snsTimestamp,              // SNS Timestamp (RAW일 땐 null)
    Map<String, String> messageAttributes, // SNS MessageAttributes (RAW일 땐 빈 맵)
    String rawBody                     // 원문 (디버깅/감사 로그용)
) {
    public Map<String, String> messageAttributes() {
        return messageAttributes == null ? Collections.emptyMap() : messageAttributes;
    }
}
