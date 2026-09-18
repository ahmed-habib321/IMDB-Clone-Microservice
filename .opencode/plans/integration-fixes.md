# IMDB Clone Microservice — Integration Fixes

Goal: fix the four cross-service integration defects found during the full-project
scan, then verify the whole active module set builds and tests green.

Active modules (root `pom.xml`): `shared-module`, `shared-outbox`, `discovery-server`,
`config-server`, `api-gateway`, `user-service`, `notification-service`.

Verified aligned (no change needed):
- Gateway -> user-service Feign `POST /api/v1/private/user/from-credential`
  (`UserServiceClient.java`) matches `UserPrivateController.registerFromCredential`
  and `UserService.register(CreateUserRequest)`. `password` is intentionally unused.
- Kafka topics shared via `TopicNames` (`auth.*`) between gateway outbox producer
  and notification-service consumers.
- `auth_credential`, `refresh_token`, `imdb_outbox` migrations match validate-mode entities.
- Local `database.*`/`mail.*` placeholders resolve from each service `-local.yaml`.

---

## Fix 1 — `pom.xml` (line 146-149): scope the webmvc test starter

`spring-boot-starter-webmvc-test` is declared in the shared `<dependencies>` block of the
root `pom.xml` WITHOUT `<scope>test</scope>`, so it leaks a test starter into every
module's compile classpath.

Change:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Fix 2 — `config-repo/api-gateway/api-gateway-Docker.yaml` line 13: casing bug

`mail.Password` (capital P) does not match the `mail.password` reference used elsewhere,
breaking SMTP password placeholder resolution in the Docker profile.

Change:
```yaml
mail:
  host: ${MAIL_HOST}
  port: ${MAIL_PORT}
  username: ${MAIL_USERNAME}
  password: ${MAIL_PASSWORD}
```

## Fix 3 — New `config-repo/notification-service/notification-service-Docker.yaml`

notification-service has NO Docker variant, so `${database.*}` / `${mail.*}` are
unresolvable in Docker. Create it mirroring the gateway's env-var pattern. The
`email.provider: MOCK` keeps a real SMTP server optional.

```yaml
database:
  host: ${DATABASE_HOST}
  name: ${DATABASE_NAME}
  port: ${DATABASE_PORT}
  username: ${DATABASE_USERNAME}
  password: ${DATABASE_PASSWORD}

mail:
  host: ${MAIL_HOST}
  port: ${MAIL_PORT}
  username: ${MAIL_USERNAME}
  password: ${MAIL_PASSWORD}

email:
  provider: MOCK
```

## Fix 4 — `docker-compose.yml`: add notification-service + its DB

docker-compose defines `auth-db` (the gateway's DB) but has NO `notification-service`
service and NO `notification-db`. The `auth.*` email events the gateway publishes via its
outbox are therefore never consumed in Docker.

1. Add a `notification-db` Postgres container (port 5444 host, following the pattern of
   the other `*-db` services), e.g.:
   ```yaml
   notification-db:
     image: postgres
     container_name: notification-db
     environment:
       POSTGRES_USER: admin
       POSTGRES_PASSWORD: admin
       POSTGRES_DB: notification_db
     ports:
       - "5444:5432"
     volumes:
       - postgres_notification_data:/var/lib/postgresql
     networks:
       - IMDB-Clone-Microservice
   ```
2. Add the `notification-service` app service (port 9092) mirroring `user-service`'s env
   wiring, and add `postgres_notification_data` to the `volumes:` block:
   ```yaml
   notification-service:
     build:
       context: .
       dockerfile: Dockerfile
       args:
         JAR_FILE: notification-service/target/*.jar
     container_name: notification-service
     environment:
       EUREKA_ORIGIN: http://discovery-server:8761
       SPRING_PROFILES_ACTIVE: Docker
       DATABASE_HOST: notification-db
       DATABASE_NAME: notification_db
       DATABASE_PORT: 5432
       DATABASE_USERNAME: admin
       DATABASE_PASSWORD: admin
       MAIL_HOST: smtp.gmail.com
       MAIL_PORT: 587
       MAIL_USERNAME: your@email.com
       MAIL_PASSWORD: your-password
       LOKI_HOST: loki
       LOKI_PORT: 3100
       KAFKA_SERVER: kafka:9092
     networks:
       - IMDB-Clone-Microservice
     depends_on:
       config-server:
         condition: service_healthy
       notification-db:
         condition: service_started
       kafka:
         condition: service_started
   ```

---

## Verification
1. `mvn clean compile` — whole reactor builds.
2. `mvn test` — unit tests green (user-service 15 tests previously passing).
3. Optional: `docker compose config` to validate compose syntax.