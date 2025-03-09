FROM rabbitmq:3-management

# 复制初始化脚本
COPY rabbitmq-init.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/rabbitmq-init.sh

# 设置环境变量
ENV RABBITMQ_DEFAULT_USER=root
ENV RABBITMQ_DEFAULT_PASS=root

# 设置启动命令
CMD ["sh", "-c", "rabbitmq-server & sleep 10 && /usr/local/bin/rabbitmq-init.sh && wait"]
