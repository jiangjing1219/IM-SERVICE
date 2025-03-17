FROM nginx:alpine

# 安装必要的工具
RUN apk add --no-cache curl

# 复制构建好的Vue文件到Nginx服务目录
COPY dist/ /usr/share/nginx/html/

# 复制Nginx配置文件
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost/ || exit 1

# 暴露端口
EXPOSE 80

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"] 