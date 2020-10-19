FROM maven:3.6-jdk-8 AS builder

COPY . /kafka-connect-rabbitmq/

RUN cd /kafka-connect-rabbitmq && mvn package -Dmaven.test.skip=true

FROM strimzi/kafka:0.17.0-kafka-2.4.0
USER root:root
COPY --from=builder /kafka-connect-rabbitmq/target/kafka-connect-rabbitmq-0.0.3.jar /opt/kafka/plugins/
USER 1001

LABEL name="kafka-connect-rabbitmq-strimzi"
LABEL build_path="../"
LABEL version_auto_semver="true"