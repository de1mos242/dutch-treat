FROM openjdk:8 AS BUILD_IMAGE

ARG MONGO_URL
ARG MONGO_DATABASE
ARG DIALOGFLOW

ENV APP_HOME=/root/dev/dutch-treat/
RUN mkdir -p $APP_HOME/src/main/java
WORKDIR $APP_HOME
COPY build.gradle.kts gradlew gradlew.bat $APP_HOME
COPY gradle $APP_HOME/gradle
# download dependencies
RUN ./gradlew build -x test --continue
COPY . .
RUN mongodb_url=$MONGO_URL mongodb_database=$MONGO_DATABASE dialogflow_base64=$DIALOGFLOW app_version=build-docker \
    ./gradlew build -i

FROM openjdk:8-jre
WORKDIR /root/
COPY --from=BUILD_IMAGE /root/dev/dutch-treat/build/libs/*-all.jar app.jar
EXPOSE 8080
CMD ["java", "-server", "-XX:+UnlockExperimentalVMOptions", \
     "-XX:+UseCGroupMemoryLimitForHeap", "-XX:InitialRAMFraction=2", \
     "-XX:MinRAMFraction=2", "-XX:MaxRAMFraction=2", "-XX:+UseG1GC", \
     "-XX:MaxGCPauseMillis=100", "-XX:+UseStringDeduplication", \
     "-jar","app.jar"]