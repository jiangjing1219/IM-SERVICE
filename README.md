# 系统架构介绍
### 一、该项目对外提供即使通讯服务（单聊和群聊）的同时，可以实现的自定义多端(web、ios、android )消息同步、离线消息缓存、在线状态变更、多系统的接入、智能对话（对接通义千问大模型）等功能。
#### 1、业务系统：开启 Netty 服务（实现自定义协议）实现单聊/群聊的消息发送、即时通讯业务其他的业务逻辑。
#### 2、第三方业务系统：实现三业务系统的登陆和自定义的业务功能。
#### 3、前台服务：实现即时通讯的用户交互
### 二、技术栈
#### Netty：开启 netty 服务端，实现 tcp 连接，并实现自定义协议完成客户端和服务端的实时通讯。
#### dubbo： 实现 tcp微服务 和 业务微服务之间的接口调用
#### rabbitmq： 完成消息的分发
#### mysql： 业务数据存储
#### redis： 离线消息存储和热点数据存储
#### naocs： 实现 tcp 微服务服务注册和发现、dubbo注册中心
#### Spring Boot: 微服务框架
### 三、后台服务模块介绍
#### im-codec：netty 自定义协议相关的实现
#### im-common：公共实体类和工具类
#### im-message-store：消息持久化微服务
#### im-service：IM服务业务微服务-实现业务逻辑
#### im-tcp： netty服务-实现和客户端的连接-消息交互模块
### 四、业务优化
#### 消息的即时性：即如何保证发送端的消息及时响应给接收端，基本思路是减少消息发送到消息接受这一业务逻辑的功能单一行：1、使用线程池实现并发（引起消息的有序性问题） 2、消息的存储使用异步实现，避免入库的耗时操作。 3、消息的发送合法性校验操作提前到 tcp 层，使用 dubbo 远程调用 servcie 层的业务方法，避免非法发送请求占用不必要的资源
#### 消息的有序性：消息的发送使用了线程并发和异步存储，则无法保证消息的有序性。需要引入 sequence 该字段表示 - 实现使用 redis 自增机制获取 seq 的值。
#### 消息的可靠性：要保证消息一定发送到接收端：1、添加 ack 表示，消息到达服务端和消息到达接受端需要给客户端回复 ack 标识消息的发送状态 2、客户端提供容错机制，没有接收到发送成功的 ack 之间需要提供重发机制（引发幂等性问题）
#### 消息的幂等性：因为消息可能存在重发的机制，客户端接收消息只能有一次，所以服务需要确保相同的消息只处理一次。使用 redis 保存已经处理过的消息的 key - 设置超时事件，如果已经存在缓存说明已经处理过了，不再处理重复的消息。

![系统架构介绍](https://github.com/user-attachments/assets/37f8f29a-a389-4578-86f8-c61b3915f432)
# 数据流转时序图
![数据流转时序图](https://github.com/user-attachments/assets/ed849f9d-4797-40e5-9248-a5b2fe298a1f)

# 功能介绍
### 1、登录页面
![登录页面](https://github.com/user-attachments/assets/b87d0d1c-2809-415f-9f7d-4a35a6c29025)
### 2、聊天首页
![聊天页面](https://github.com/user-attachments/assets/e5325ee7-3309-4bd1-ab1c-44e3048bc69f)
### 3、智能问答
![智能问答](https://github.com/user-attachments/assets/61fda008-da21-44fd-b8be-7502724a50c9)
### 4、添加好友页面
![image](https://github.com/user-attachments/assets/a21ba3c0-d3a9-4013-a460-a1fa9d881bbf)
### 5、好友详情页面
![image](https://github.com/user-attachments/assets/9c0ae696-ea16-4d91-9eaa-cd53879f0175)
### 6、单聊展示
![单聊展示](https://github.com/user-attachments/assets/1b9ef42c-b37b-49ba-91bc-3a2d9a6e550d)
### 7、群聊展示
![群聊展示](https://github.com/user-attachments/assets/25e011db-b2ca-4bc5-b444-c7e1d81afbf6)
### 8、登出页面
![登出页面](https://github.com/user-attachments/assets/aef6d0c7-ad26-402a-a56d-31a4fb051cc4)
