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

    private final String topicName = "test-poc-chat-message-sns";
    private final List<String> queueNames = List.of(
            "test-poc-chat-sqs-websocket",
            "test-poc-chat-sqs-push",
            "test-poc-chat-sqs-confirm"
    );

    private LocalStackTestContainer() {}

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
        registry.add("aws.sns.region", container::getRegion);
        registry.add("aws.sns.endpoint", () -> container.getEndpointOverride(LocalStackContainer.Service.SNS).toString());
        registry.add("aws.sns.credentials.access-key", container::getAccessKey);
        registry.add("aws.sns.credentials.secret-key", container::getSecretKey);

        registry.add("aws.sqs.region", container::getRegion);
        registry.add("aws.sqs.endpoint", () -> container.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
        registry.add("aws.sqs.credentials.access-key", container::getAccessKey);
        registry.add("aws.sqs.credentials.secret-key", container::getSecretKey);

        // 토픽/큐 URL
        registry.add("aws.sns.topics.poc-chat-message-sns", () -> topicArn);
        registry.add("aws.sqs.queues.poc-chat-sqs-websocket", () -> queueUrlMap.get("test-poc-chat-sqs-websocket"));
        registry.add("aws.sqs.queues.poc-chat-sqs-push", () -> queueUrlMap.get("test-poc-chat-sqs-push"));
        registry.add("aws.sqs.queues.poc-chat-sqs-confirm", () -> queueUrlMap.get("test-poc-chat-sqs-confirm"));
        // 필요시 ARN도 추가 가능
    }
}
