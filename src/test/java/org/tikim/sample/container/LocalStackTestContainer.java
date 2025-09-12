package org.tikim.sample.container;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LocalStackTestContainer implements TestContainer {

    public static final LocalStackTestContainer INSTANCE = new LocalStackTestContainer();

    private final LocalStackContainer container = new LocalStackContainer(
        DockerImageName.parse("localstack/localstack:4.4")
    )
        .withServices(LocalStackContainer.Service.SNS, LocalStackContainer.Service.SQS)
        .withReuse(true);

    private String topicArn;
    private final Map<String, String> queueUrlMap = new HashMap<>();
    private final Map<String, String> queueArnMap = new HashMap<>();

    // 이름만 변경 (reply 이벤트 → post owner 알림 / reply author 액션)
    private final String topicName = "test-board-reply-sns";
    private final List<String> queueNames = List.of(
        "test-board-post-notify-sqs",
        "test-board-reply-action-sqs"
    );

    private LocalStackTestContainer() {
    }

    @Override
    public void start() {
        if (!container.isRunning()) {
            container.start();
        }

        Region region = Region.of(container.getRegion());
        StaticCredentialsProvider creds = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(container.getAccessKey(), container.getSecretKey())
        );

        // SNS
        try (SnsClient sns = SnsClient.builder()
            .endpointOverride(container.getEndpointOverride(LocalStackContainer.Service.SNS))
            .region(region)
            .credentialsProvider(creds)
            .build()) {

            topicArn = sns.createTopic(b -> b.name(topicName)).topicArn();

            // SQS
            try (SqsClient sqs = SqsClient.builder()
                .endpointOverride(container.getEndpointOverride(LocalStackContainer.Service.SQS))
                .region(region)
                .credentialsProvider(creds)
                .build()) {

                for (String queueName : queueNames) {
                    String url = sqs.createQueue(b -> b.queueName(queueName)).queueUrl();
                    queueUrlMap.put(queueName, url);

                    GetQueueAttributesRequest getArnReq = GetQueueAttributesRequest.builder()
                        .queueUrl(url)
                        .attributeNames(QueueAttributeName.QUEUE_ARN)
                        .build();

                    String queueArn = sqs.getQueueAttributes(getArnReq)
                        .attributes()
                        .get(QueueAttributeName.QUEUE_ARN);
                    queueArnMap.put(queueName, queueArn);

                    // SQS Policy: SNS 허용
                    String policyJson = """
                        {
                          "Version": "2012-10-17",
                          "Statement": [
                            {
                              "Sid": "Allow-SNS-SendMessage",
                              "Effect": "Allow",
                              "Principal": {"AWS": "*"},
                              "Action": "sqs:SendMessage",
                              "Resource": "%s",
                              "Condition": {
                                "ArnEquals": {"aws:SourceArn": "%s"}
                              }
                            }
                          ]
                        }
                        """.formatted(queueArn, topicArn);

                    sqs.setQueueAttributes(b -> b
                        .queueUrl(url)
                        .attributes(Map.of(QueueAttributeName.POLICY, policyJson))
                    );

                    // SNS → SQS 구독
                    sns.subscribe(SubscribeRequest.builder()
                        .topicArn(topicArn)
                        .protocol("sqs")
                        .endpoint(queueArn)
                        .build());
                }
            }
        }
    }

    @Override
    public void register(DynamicPropertyRegistry registry) {
        // 공통 접속
        registry.add("aws.sns.region", container::getRegion);
        registry.add("aws.sns.endpoint", () -> container.getEndpointOverride(LocalStackContainer.Service.SNS).toString());
        registry.add("aws.sns.credentials.access-key", container::getAccessKey);
        registry.add("aws.sns.credentials.secret-key", container::getSecretKey);

        registry.add("aws.sqs.region", container::getRegion);
        registry.add("aws.sqs.endpoint", () -> container.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
        registry.add("aws.sqs.credentials.access-key", container::getAccessKey);
        registry.add("aws.sqs.credentials.secret-key", container::getSecretKey);

        // 새 이름으로 매핑
        registry.add("aws.sns.topics.board-reply", () -> topicArn);
        registry.add("aws.sqs.queues.post-notify", () -> queueUrlMap.get("test-board-post-notify-sqs"));
        registry.add("aws.sqs.queues.reply-action", () -> queueUrlMap.get("test-board-reply-action-sqs"));
    }

}
