# boot-jpa-postgres
BOOT기반으로 REST + JPA + liquibase Sample
### build.gradle
```
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.liquibase:liquibase-core'
    runtimeOnly 'org.postgresql:postgresql'
    ...
```
### JPA + Postgre + liquibase setting - application.yml
SpringBoot+JPA+Postgre 가이드 : https://jee-goo.tistory.com/entry/Spring-Boot-PostgreSQL-BackEnd
```
spring:
  profiles: local
  datasource:
    url: jdbc:postgresql://192.168.137.101:31191/postgresdb
    username: postgresadmin
    password: admin123

#Connection Pool
    hikari:
      connection-timeout: 20000
      maximum-pool-size: 10

#JPA
  jpa:
    database: postgresql
    database-platform: org.hibernate.dialect.PostgreSQL95Dialect
    show-sql: true
    generate-ddl: true
    hibernate:
      ddl-auto: validate
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
      use-new-id-generator-mappings: false
    properties:
      hibernate:
        enable_lazy_load_no_trans: true
        format_sql: true
        jdbc:
          lob:
            non_contextual: true

#liquibase
  liquibase:
    change-log: classpath:/db/db.changelog-yaml.yaml
```
### liquibase
liquibase는 database schema 변경을 tracking 하여 관리할 수 있게 해주는 open source(flyway도 있다), MSA로 가면서 Table관리에 유용할 것 같음.
```
#db.changelog-yaml.yaml
databaseChangeLog:
  - include:
      file: classpath:/db/changelog-master.xml
  - include:
      file: classpath:/db/accesslog-master.xml
```
여러개의 파일을 관리하기 위해 yaml파일로 중간에 두었고, 하나의 로그만 관리하면 xml을 바로 change-log에 지정하면 됨.
```
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog
        xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:ext="http://www.liquibase.org/xml/ns/dbchangelog-ext"
        xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-3.1.xsd
    http://www.liquibase.org/xml/ns/dbchangelog-ext http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-ext.xsd">
    <preConditions>
        <runningAs username="postgresadmin"/>
    </preConditions>
    <changeSet id="1" author="postgresadmin">
        <sql>
            create table SERVER_IP(
                log_id   VARCHAR(36) not null,
                server_ip VARCHAR(40),
                login_dt TIMESTAMP(0) WITH TIME ZONE,
                CONSTRAINT SERVER_IP_PKEY PRIMARY KEY (log_id)
            );
        </sql>
    </changeSet>
    <changeSet id="2" author="postgresadmin">
        <sql>
            ALTER TABLE public.server_ip ALTER COLUMN server_ip TYPE varchar(1000) USING server_ip::varchar;
        </sql>
    </changeSet>
    <changeSet id="3" author="postgresadmin">
        <modifyDataType tableName="server_ip" columnName="log_id" newDataType="varchar(64)"></modifyDataType>
    </changeSet>
</databaseChangeLog>
```
changSet id로 순차적으로 변경 쿼리를 실행하고 내부적으로 databasechangelog테이블을 생성하여 실행한 이력을 관리함.
즉 3번을 다시 돌리고 싶으면 databasechangelog테이블에서 3번 이력정보를 삭제하면 재실행시 다시 실행함.
## Cloud적용
### Dockerfile
```
FROM openjdk:8-jdk-alpine
ENV TZ=Asia/Seoul
# author
MAINTAINER "EMRO"
# volume
VOLUME /tmp
# port
EXPOSE 8080
# version
ARG JAR_VERSION=0.0.1
# args
ARG JAR_FILE=./build/libs/boot-jpa-postgres-${JAR_VERSION}-SNAPSHOT.jar
# docker image 에 jar 추가
ADD ${JAR_FILE} boot-jpa-postgres.jar

# spring profiles active
ARG SPRING_PROFILES_ACTIVE=k8s
RUN echo $SPRING_PROFILES_ACTIVE
ENV SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE

# run jar file
ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/boot-jpa-postgres.jar" ]
```
k8s ConfigMap/Secret 사용하여 설정정보를 외부에서 결정하게 함.
참고Site: https://medium.com/@jwlee98/gcp-gke-%EC%B0%A8%EA%B7%BC-%EC%B0%A8%EA%B7%BC-%EC%95%8C%EC%95%84%EB%B3%B4%EA%B8%B0-4%ED%83%84-gke-%EC%84%A4%EC%A0%95-%EC%99%B8%EB%B6%80%ED%99%94-configmap-secrets-354da3a91edf
### Deployment
```
#생략
  template:
    metadata:
      labels:
        app: spring-jpa-postgres
    spec:
      containers:
        - name: spring-jpa-postgres
          image: zalkdo/spring-jpa-postgres
          ports:
            - containerPort: 8080
          env:
            - name: POSTGRES_DB
              valueFrom:
                configMapKeyRef:
                  name: postgres-config
                  key: POSTGRES_DB
            - name: POSTGRES_PASSWORD
              valueFrom:
                configMapKeyRef:
                  name: postgres-config
                  key: POSTGRES_PASSWORD
            - name: POSTGRES_USER
              valueFrom:
                configMapKeyRef:
                  name: postgres-config
                  key: POSTGRES_USER
#생략
```
k8s의 configMap을 통해서 applicationi.yml의 변수 값을 할당 하는 예제.
> **Tip**: Securets은 valueForm에 secretKeyRef:고 가져옴. db가 pod로 실행 중이라면 내부 dns를 사용하여 접근해야 함.
>
```
#k8s에서 임시pod로 타 pod ping하기(DNS)
$sudo kubectl run -i --tty --rm debug --image=alicek106/ubuntu:curl --restart=Never -- bash
$cat /etc/resolv.conf
$ping <<서비스명>>.default.svc.cluster.local
```