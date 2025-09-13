// SnsBodyParser.java
package org.tikim.sample.global.event.sqs.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.tikim.sample.global.event.sqs.dto.SnsParsedMessage;

@Component
@RequiredArgsConstructor
public class SnsBodyParser {

    private final ObjectMapper objectMapper;

    /** SNS 래핑(JSON)만 지원: { "Message":"<네 DTO JSON>", "MessageId":"...", "TopicArn":"...", ... } */
    public <T> SnsParsedMessage<T> parse(String body, Class<T> type) {
        try {
            JsonNode root = objectMapper.readTree(body);

            // (선택) 타입 확인 — LocalStack/실서버 모두 "Notification"이 일반적
            JsonNode typeNode = root.get("Type");
            if (typeNode == null || typeNode.isNull() || !"Notification".equalsIgnoreCase(typeNode.asText())) {
                throw new IllegalArgumentException("Invalid SNS envelope: Type != 'Notification'");
            }

            JsonNode msgNode = root.get("Message");
            if (msgNode == null || msgNode.isNull()) {
                throw new IllegalArgumentException("Invalid SNS envelope: missing 'Message'");
            }
            String inner = msgNode.isTextual() ? msgNode.asText() : msgNode.toString();
            if (inner.isBlank()) {
                throw new IllegalArgumentException("Invalid SNS envelope: 'Message' is blank");
            }

            T payload = objectMapper.readValue(inner, type);

            String snsMessageId = text(root, "MessageId");
            String topicArn     = text(root, "TopicArn");
            Instant ts          = root.hasNonNull("Timestamp") ? Instant.parse(root.get("Timestamp").asText()) : null;
            Map<String, String> attrs = readMessageAttributes(root.get("MessageAttributes"));

            return new SnsParsedMessage<>(payload, snsMessageId, topicArn, ts, attrs, body);

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse SNS-wrapped SQS message", e);
        }
    }

    private static String text(JsonNode root, String field) {
        JsonNode n = root.get(field);
        return (n == null || n.isNull()) ? null : n.asText();
    }

    /** SNS Notification의 MessageAttributes 파싱
     *  표준 형식: "MessageAttributes": { "key": { "Type":"String", "Value":"..." }, ... }
     *  구현체에 따라 value가 문자열로 바로 오는 경우도 방어
     */
    private static Map<String, String> readMessageAttributes(JsonNode node) {
        Map<String, String> out = new HashMap<>();
        if (node == null || !node.isObject()) return out;

        Iterator<Map.Entry<String, JsonNode>> it = node.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> e = it.next();
            JsonNode val = e.getValue();
            String s = null;
            if (val != null && val.isObject()) {
                JsonNode v = val.get("Value");
                if (v != null && !v.isNull()) s = v.asText();
            } else if (val != null && val.isTextual()) {
                s = val.asText();
            }
            if (s != null) out.put(e.getKey(), s);
        }
        return out;
    }
}
