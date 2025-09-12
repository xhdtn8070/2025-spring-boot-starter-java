# TIKIM - 로컬 통합 개발환경 빠른시작 가이드

---

## 🖥️ 1. 개요

* 이 문서는 **TIKIM 개발팀**이 로컬에서 **MySQL, Valkey(Redis), LocalStack(AWS)**을
  **한 번에 올릴 수 있는 실전 자동화 가이드**입니다.
* Docker와 docker-compose, 그리고 **Terraform**만 있으면, **명령어 한 줄로 개발환경 세팅** 완료!

---

## ⚙️ 2. 환경 구조/서비스 요약

| 서비스           | 이미지                       | 호스트 포트 | 컨테이너 포트 | 계정/비번                              | 비고              |
| ------------- | ------------------------- | ------ | ------- | ---------------------------------- | --------------- |
| LocalStack    | localstack/localstack:4.4 | 4566   | 4566    | accessKey: test, secretKey: test   | **SNS, SQS**만 활성화 |
| Valkey(Redis) | valkey/valkey:8.0         | 63790  | 6379    | 없음 (default)                       | Redis 호환        |
| MySQL         | mysql:8.4.3               | 33060  | 3306    | root/1q2w3e4r, db: tikim\_local | utf8mb4, KST      |

---

## 🏗️ 3. 프로젝트 구조

프로젝트 루트 디렉토리 `/tikim` 아래에 `local` 디렉토리가 추가되었습니다.

```
/tikim
 ├── local/
 │   ├── docker-compose.yml
 │   └── terraform/
 │       └── main.tf
 └── ... (나머지 코드 및 설정 파일)
```

* **`local/docker-compose.yml`**:
    세 컨테이너(MySQL, Valkey, LocalStack)와 **Terraform 초기화 컨테이너**를 한 번에 올립니다.
    LocalStack 컨테이너가 완전히 준비되면 Terraform 초기화가 자동 실행됩니다.
* **`local/terraform/main.tf`**:
    LocalStack 기동 시 **Terraform을 통해 SNS/SQS 리소스를 선언적으로 자동 생성**합니다.
    (컨테이너 올릴 때마다 LocalStack 리소스가 항상 동일하게 유지됩니다.)

---

## 🚀 4. 사용법 (완전 자동화)

1.  **`local` 디렉토리로 이동**:
    프로젝트 루트 디렉토리 `/tikim`에서 `local` 디렉토리로 이동합니다.

    ```sh
    cd ./local
    ```

2.  **모든 컨테이너 및 LocalStack 리소스 한 번에 실행**:
    이 명령어 한 줄로 모든 개발 환경이 자동으로 준비됩니다.

    ```sh
    docker-compose up -d
    ```

    * `mysql`, `valkey`, `localstack` 컨테이너가 실행됩니다.
    * `localstack` 컨테이너가 완전히 준비되면, `terraform_initializer` 컨테이너가 자동으로 실행되어 LocalStack에 SNS 및 SQS 리소스들을 프로비저닝합니다.

3.  **컨테이너 중지**:
    모든 컨테이너를 중지하고 제거합니다. (LocalStack 데이터는 `localstack-data` 볼륨에 유지됩니다.)

    ```sh
    docker-compose down
    ```

4.  **컨테이너 및 모든 데이터/볼륨까지 완전히 초기화하고 재시작**:
    새로운 변경사항을 적용하거나, 문제가 발생하여 모든 것을 초기화하고 싶을 때 사용합니다.

    ```sh
    docker-compose down -v --remove-orphans && docker-compose up -d
    ```

---

## 🧑‍💻 5. 컨테이너/상태/리소스 확인

* **실행 중인 컨테이너 목록 확인**:
    ```sh
    docker ps
    ```
* **특정 컨테이너의 실시간 로그 확인**:
    ```sh
    docker logs -f [컨테이너명]
    ```
    (예: `docker logs -f tikim_localstack`, `docker logs -f tikim_terraform_initializer`)

* **컨테이너 접속 (쉘 실행)**:
    아래의 모든 MySQL, Valkey, LocalStack 리소스 확인을 위해 먼저 해당 컨테이너로 접속해야 합니다.
    ```sh
    docker exec -it [컨테이너명] bash
    ```

* **MySQL 접속 및 확인**:
    먼저 `tikim_mysql_local` 컨테이너로 접속한 후, 다음 명령어를 실행합니다.
    ```sh
    docker exec -it tikim_mysql_local bash
    ```
    컨테이너 쉘 안에서:
    ```sh
    mysql -h 127.0.0.1 -P 3306 -u root -p1q2w3e4r
    # 비밀번호: 1q2w3e4r
    # 접속 후 `SHOW DATABASES;` 등으로 확인 가능
    ```
    (참고: `127.0.0.1:3306`은 컨테이너 내부의 MySQL 서버 주소입니다. 호스트 머신에서 접속할 때의 `33060` 포트와 다릅니다.)

* **Valkey(Redis) 접속 및 확인**:
    먼저 `tikim_valkey_local` 컨테이너로 접속한 후, 다음 명령어를 실행합니다.
    ```sh
    docker exec -it tikim_valkey_local bash
    ```
    컨테이너 쉘 안에서:
    ```sh
    valkey-cli
    # 접속 후 `ping` 또는 `info` 등으로 확인 가능
    ```

* **LocalStack 리소스 확인**:
    먼저 `tikim_localstack` 컨테이너로 접속한 후, 다음 `awslocal` 명령어를 사용합니다.
    ```sh
    docker exec -it tikim_localstack bash
    ```
    컨테이너 쉘 안에서:
    ```sh
    awslocal sns list-topics    # 생성된 SNS 토픽 목록 확인
    awslocal sqs list-queues    # 생성된 SQS 큐 목록 확인
  
  # SNS 토픽 구독 연결 상태 확인
    # 'local-poc-chat-message-sns' 토픽의 ARN을 조회하여 사용합니다.
    TOPIC_ARN=$(awslocal sns list-topics --query 'Topics[?contains(TopicArn, `local-board-reply-sns`)].TopicArn' --output text)
    awslocal sns list-subscriptions-by-topic --topic-arn "$TOPIC_ARN"
    ```
---

## ⚡ 6. 문제해결 체크리스트

* **`cd ./local` 이동 확인**: 모든 `docker-compose` 명령어는 `local` 디렉토리 안에서 실행해야 합니다.
* **포트 충돌**: `docker ps`로 기존 컨테이너가 사용하는 포트(4566, 33060, 63790)가 호스트에 이미 사용 중인지 확인하고, 있다면 해당 프로세스를 중지하거나 `docker-compose down`으로 컨테이너를 내립니다.
* **LocalStack Health 체크**: `localstack` 컨테이너가 정상적으로 시작되었는지 `docker logs -f tikim_localstack`로 확인합니다.
* **Terraform 초기화 오류**: `docker logs -f tikim_terraform_initializer`를 통해 Terraform 실행 중 발생한 오류를 확인합니다. `Apply complete!` 메시지가 없다면 오류가 발생한 것입니다.
* **DB/Redis 연결 오류**: DB/Redis 비밀번호나 기본값이 올바른지 확인합니다.
* **완전 초기화**: `docker-compose down -v --remove-orphans` 명령은 문제가 해결되지 않을 때 모든 컨테이너와 데이터를 완전히 초기화하는 데 도움이 됩니다.

---

## 📝 7. 환경 업데이트/기여

* 컨테이너 환경/LocalStack 리소스가 바뀌면 **`local/docker-compose.yml` 및 `local/terraform/main.tf` 파일만 수정**하면 됩니다.
* 구성 관련 개선/문의: README 또는 PR/이슈로 요청

---

### ✅ 누구나 아래 명령어 한 줄로 환경 완전 자동화!

**`local` 디렉토리로 이동 후:**

```sh
docker-compose up -d
```

---

**`local/docker-compose.yml`과 `local/terraform/main.tf`는 코드에서 참고하세요. 환경세팅/Onboarding, 모두 한눈에!**

---