FROM im-base:latest

# Copy the jar file
COPY target/im-tcp-0.0.1-SNAPSHOT.jar app.jar

# Environment variables
ENV MYSQL_HOST=mysql \
    MYSQL_PORT=3306 \
    MYSQL_DATABASE=im \
    MYSQL_USERNAME=root \
    MYSQL_PASSWORD=root \
    RABBITMQ_HOST=rabbitmq \
    RABBITMQ_PORT=5672 \
    RABBITMQ_USERNAME=root \
    RABBITMQ_PASSWORD=root \
    RABBITMQ_VIRTUAL_HOST=im \
    NACOS_SERVER_ADDR=nacos:8848 \
    TCP_PORT=9000 \
    TCP_PORT=19001 \
    HOST_IP=192.168.1.2

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8100/actuator/health || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Expose the service ports
EXPOSE 8100 9000 19001
