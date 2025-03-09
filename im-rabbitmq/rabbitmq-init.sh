#!/bin/bash

# 等待 RabbitMQ 服务启动
echo "Waiting for RabbitMQ to start..."
until rabbitmqctl status > /dev/null 2>&1; do
  sleep 2
done
echo "RabbitMQ is running"

# 创建用户 root 密码 root
rabbitmqctl add_user root root || echo "User root already exists"

# 创建虚拟主机 im
rabbitmqctl add_vhost im || echo "Virtual host im already exists"

# 设置用户 root 对虚拟主机 im 的权限
rabbitmqctl set_permissions -p im root ".*" ".*" ".*"

# 设置用户 root 为管理员
rabbitmqctl set_user_tags root administrator

echo "RabbitMQ initialization completed" 