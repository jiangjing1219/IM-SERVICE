FROM im-base:latest

# Copy the jar file
COPY target/im-service-1.0-SNAPSHOT.jar app.jar

# Copy the init.sql file
COPY src/main/resources/init.sql /app/init.sql

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
    NACOS_SERVER_ADDR=nacos:8848

# Health check
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8000/actuator/health || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Expose the service port
EXPOSE 8000
