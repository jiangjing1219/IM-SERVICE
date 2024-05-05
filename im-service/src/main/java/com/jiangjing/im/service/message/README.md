## 消息的存储分析

### 1、读扩散

只维护一个消息队列，对于各个用户来说只需要读取一个消息队列即可

#### 优点

节省存空间，消息只需要保存一份，储存结果如下：
messageId，fromId，toId，messageBody

0001 A B 你好
0002 B A 你好啊

#### 缺点

查询dsql： select * from （fromId = A and toId = B） || （fromId = B and toId = A）
查询SQL比较复杂，效率也是比较慢的。 如果数据量比较的涉及分库分表的，没有分片键

### 2、扩散

#### 缺点

每个用户都维护自己的消息队列，1万个用户就需要写一万分，一旦有消息撤回，那么消息的维护就是重量的。
消息存储结结果如下：（有ownerId标识当前消息属于谁）
messageId，fromId，toId，ownerId，messageBody

0001 A B A 你好
0002 A B B 你好
0003 B A A 你好啊
0004 B A B 你好啊

#### 优点：

查询sql比较简单
select * from ownerId = A and toTd = B
对于给后期的分库分表来说子需要迁移自己的消息即可。

#### 优化：

对于写扩散来说，消息体都是一样的，那么可已将消息体拆分出来，只需要维护消息的关系即可，减少messageBody的存储空间
messageId，fromId，toId，ownerId

messageId，messageBody

方案的选取：
对于单聊消息来说：可以使用写扩散，并会不会造成服务器压力，有利于维护和查询。
对于群聊消息来说，可以采用读扩散，维护多份数据，服务器压力会很大

## 已读功能的实现

写扩散：因为每个用户自己都有一份消息的副本，那么每条消息的记录都可以维护一个是否已读的标识
读扩散：所有成员都共享一个消息副本，可以利用 messageSeq 作为已读消息的标识。引入 会话 的概概念，在【会话】中会记录最新的消息的seq即可。