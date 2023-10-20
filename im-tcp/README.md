# 模块实现综述
## Netty、WebSocket服务端
  使用 Spring 的启动事件，在 ContextRefreshedEvent 容器启动完成时，开启 Netty 和 WebSocket 的服务端，在 ContextClosedEvent 容器关闭时，关闭客户端
## 自定义协议实现

    