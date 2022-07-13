[TOC]



# 基于AntDesignPro+SpringBoot的hadoop网盘

目标：完整了解做项目的思路，接触一些企业级的开发技术，以后都能轻松做出管理系统！



### 项目源码

用户中心前端项目源码：[Files · SpringBoot · 5478-芾栗兽 / SpringBoot+AntDesignPro学习项目 · GitLab (code-nav.cn)](http://gitlab.code-nav.cn/mifuRD/Hadoop/-/tree/SpringBoot)

用户中心后端项目源码：[Files · AntDesignPro · 5478-芾栗兽 / SpringBoot+AntDesignPro学习项目 · GitLab (code-nav.cn)](http://gitlab.code-nav.cn/mifuRD/Hadoop/-/tree/AntDesignPro)



## 企业做项目流程

需求分析 => 设计（概要设计、详细设计）=> 技术选型 => 初始化 / 引入需要的技术 => 写 Demo => 写代码（实现业务逻辑） => 测试（单元测试、系统测试）=> 代码提交 / 代码评审 => 部署 => 发布上线



## 需求分析

1. 登录 / 注册
2. 用户管理（仅管理员可见）对用户的查询或者修改
3. 用户校验（仅星球用户可见）



## 技术选型

前端：三件套 + React + 组件库 Ant Design + Umi + Ant Design Pro（现成的管理系统）

后端：

- java
- spring（依赖注入框架，帮助你管理 Java 对象，集成一些其他的内容）
- springmvc（web 框架，提供接口访问、restful接口等能力）
- mybatis（Java 操作数据库的框架，持久层框架，对 jdbc 的封装）
- mybatis-plus（对 mybatis 的增强，不用写 sql 也能实现增删改查）
- springboot（**快速启动** / 快速集成项目。不用自己管理 spring 配置，不用自己整合各种框架）
- junit 单元测试库
- mysql 数据库

部署：服务器 / 容器（平台）



## 3 种初始化 Java 项目的方式

1. GitHub 搜现成的代码
2. SpringBoot 官方的模板生成器（https://start.spring.io/）
3. 直接在 IDEA 开发工具中生成  ✔

如果要引入 java 的包，可以去 maven 中心仓库寻找（http://mvnrepository.com/）



## 数据库设计

什么是数据库？存数据的

数据库里有什么？数据表（理解为 excel 表格）

java 操作数据库？程序代替人工



### 什么是设计数据库表？

有哪些表（模型）？表中有哪些字段？字段的类型？数据库字段添加索引？表与表之间的关联？



举例：性别是否需要加索引？



### 用户表设计

id（主键）bigint

username 昵称  varchar

userAccount 登录账号 

avatarUrl 头像 varchar

gender 性别 tinyint

userPassword 密码  varchar

phone 电话 varchar

email 邮箱 varchar

userStatus 用户状态 int  0 - 正常 

createTime 创建时间（数据插入时间）datetime

updateTime 更新时间（数据更新时间）datetime

isDelete 是否删除 0 1（逻辑删除）tinyint

userRole 用户角色 0 - 普通用户 1 - 管理员



### 自动生成器的使用

MyBatisX 插件，自动根据数据库生成：

- domain：实体对象
- mapper：操作数据库的对象
- mapper.xml：定义了 mapper 对象和数据库的关联，可以在里面自己写 SQL
- service：包含常用的增删改查
- serviceImpl：具体实现 service

从而提高开发效率！



## 注册逻辑设计

1. 用户在前端输入账户和密码、以及校验码（todo）
2. 校验用户的账户、密码、校验密码，是否符合要求
   1. 非空
   2. 账户长度 **不小于** 4 位
   3. 密码就 **不小于** 8 位吧
   4. 账户不能重复
   5. 账户不包含特殊字符
   6. 密码和校验密码相同
3. 对密码进行加密（密码千万不要直接以明文存储到数据库中,非常不安全）
4. 向数据库插入用户数据



## 登录功能

### 接口设计

接受参数：用户账户、密码

请求类型：POST 

请求体：JSON 格式的数据

> 请求参数很长时不建议用 get

返回值：用户信息（ **脱敏** ）



### 登录逻辑

1. 校验用户账户和密码是否合法

   1. 非空
   2. 账户长度不小于 4 位
   3. 密码就不小于 8 位
   4. 账户不包含特殊字符

2. 校验密码是否输入正确，要和数据库中的密文密码去对比

3. 用户信息脱敏，隐藏敏感信息，防止数据库中的字段泄露

4. 我们要记录用户的登录态（session），将其存到服务器上（用后端 SpringBoot 框架封装的服务器 tomcat 去记录）

   cookie

5. 返回脱敏后的用户信息



### 实现

控制层 Controller 封装请求

application.yml 指定接口全局路径前缀：

```
servlet:
  context-path: /api
```

控制器注解：

``` 
@RestController 适用于编写 restful 风格的 api，返回值默认为 json 类型
```

校验写在哪里？

- controller 层倾向于对请求参数本身的校验，不涉及业务逻辑本身（越少越好）
- service 层是对业务逻辑的校验（有可能被 controller 之外的类调用）



### 如何知道是哪个用户登录了？

> javaweb 这一块的知识

1. 连接服务器端后，得到一个 session 状态（匿名会话），返回给前端

2. 登录成功后，得到了登录成功的 session，并且给该sessio n设置一些值（比如用户信息），返回给前端一个设置 cookie 的 ”命令“ 

   **session => cookie** 

3. 前端接收到后端的命令后，设置 cookie，保存到浏览器内

4. 前端再次请求后端的时候（相同的域名），在请求头中带上cookie去请求

5. 后端拿到前端传来的 cookie，找到对应的 session

6. 后端从 session 中可以取出基于该 session 存储的变量（用户的登录信息、登录名）



## 用户管理

接口设计关键：必须鉴权！！！(这里我们前后端都需要去做)

1. 查询用户（允许根据用户名查询）
2. 删除用户



## 写代码流程

1. 先做设计
2. 代码实现
3. 持续优化！！！（复用代码、提取公共逻辑 / 常量）



## 前后端交互

前端需要向后端发送请求才能获取数据 / 执行操作。

怎么发请求：前端使用 ajax 来请求后端



### 前端请求库及封装关系

- axios 封装了 ajax

- request 是 ant design 项目又封装了一次



追踪 request 源码：用到了 umi 的插件、requestConfig 配置文件



## 代理

正向代理：替客户端向服务器发送请求，可以解决跨域问题

反向代理：替服务器统一接收请求。

怎么实现代理？

- Nginx 服务器
- Node.js 服务器



原本请求：http://localhost:8000/api/user/login

代理到请求：http://localhost:8080/api/user/login

![](https://xingqiu-tuchuang-1256524210.cos.ap-shanghai.myqcloud.com/5478/image-20220319005859098.png)





## 前端框架介绍

### Ant Design Pro（Umi 框架）权限管理

- app.tsx：项目全局入口文件，定义了整个项目中使用的公共数据（比如用户信息）
- access.ts 控制用户的访问权限



获取初始状态流程：首次访问页面（刷新页面），进入 app.tsx，执行 getInitialState 方法，该方法的返回值就是全局可用的状态值。



### ProComponents 高级表单

1. 通过 columns 定义表格有哪些列
2. columns 属性
   - dataIndex 对应返回数据对象的属性
   - title 表格列名
   - copyable 是否允许复制
   - ellipsis 是否允许缩略
   - valueType：用于声明这一列的类型（dateTime、select）



### 框架关系

Ant Design 组件库 => 基于 React 实现

Ant Design Procomponents => 基于 Ant Design 实现

Ant Design Pro 后台管理系统 => 基于 Ant Design + React + Ant Design Procomponents + 其他的库实现



### 其他知识

MFSU：前端编译优化



## 后端优化

处理之前的一堆返回值为-1和null的家伙,再把之前的todo完成



### 通用返回对象

目的：给对象补充一些信息，告诉前端这个请求在业务层面上是成功还是失败

200、404、500、502、503

```json
{
    "name": "yupi"
}

↓

// 成功
{
    "code": 0 // 业务状态码
    "data": {
        "name": "yupi"
    },
	"message": "ok"
}


// 错误
{
    "code": 50001 // 业务状态码
    "data": null
	"message": "用户操作异常、xxx"
}
```

自定义错误码，返回类支持返回正常和错误

### 封装全局异常处理器

### 实现

1. 定义业务异常类

   1. 相对于 java 的异常类，支持更多字段
   2. 自定义构造函数，更灵活 / 快捷的设置字段
2. 编写全局异常处理器（利用 Spring AOP，在调用方法前后进行额外的处理）


### 作用

1. 捕获代码中所有的异常，内部消化，让前端得到更详细的业务报错 / 信息
2. 同时屏蔽掉项目框架本身的异常（不暴露服务器内部状态）
3. 集中处理，比如记录日志



## 前端优化

后端做了很多的优化,并且把返回的结果重新封装了,我们前端需要大改,但是这样做比较麻烦，试着写了全局响应处理器,踩了很多的坑,浪费了很多时间，而且有点刻意优化了，发现有些业务逻辑是不需要这样去返回的。



todo:这个我们后期还要再去处理！！！



### 全局响应处理

应用场景：我们需要对接口的 **通用响应** 进行统一处理，比如从 response 中取出 data；或者根据 code 去集中处理错误，比如用户未登录、没权限之类的。

优势：不用在每个接口请求中都去写相同的逻辑

实现：参考你用的请求封装工具的官方文档，比如 umi-request（https://github.com/umijs/umi-request#interceptor、https://blog.csdn.net/huantai3334/article/details/116780020）。如果你用 **axios**，参考 axios 的文档。

创建新的文件，在该文件中配置一个全局请求类。在发送请求时，使用我们自己的定义的全局请求类。



## 用户校验

> 仅适用于用户可信的情况

先让用户自己填：2 - 5 位编号，全凭自觉。

后台补充对编号的校验：长度校验、唯一性校验

前端补充输入框，适配后端。

> 后期拉取星球数据，定期清理违规用户



## 赶小学期进度

### 整合上个星期写的Hadoop网盘,整合后端

但是这里又发现有一些小问题，就是之前的项目springboot的版本稍低，和本项目最新的springboot版本，有些地方有冲突！导致项目整合后跑不起来，这里发现了主要是swagger的问题，去参考了一篇文章[解决 高版本SpringBoot整合Swagger 启动报错Failed to start bean ‘documentationPluginsBootstrapper‘ 问题_摸鱼佬的博客-CSDN博客](https://blog.csdn.net/weixin_39792935/article/details/122215625)，成功解决！

todo:这里我们项目全局响应处理器和之前简写的hdfs网盘后端有了一点小插曲，虽然不是很影响，但是感觉这种方式比较笨拙，方式不不统一，这个需要后期再去改善、解耦合，不过现在希望的是hdfs部分返回的数据和用户中心是不一样的，不然对接前端才写的那个全局响应处理器太过于麻烦了，我看后面再想个什么办法去解决。好像现在又碰到了一个问题，前端那个全局响应处理器会和我们这个hdfs网盘后端传过来的数据应该会有冲突，感觉整合前端好像不是很方便

### 整合前端

偷个懒吧，不然感觉又要踩坑，把后端偷的懒和这个后面一起完善吧！这里我就弄个页面跳转吧，启动两个项目就行，把之前那个请求接口改一下，然后hdfs这个跨域问题就在后端解决。



## todo:项目优化点

1. 功能扩充
   1. 管理员创建用户、修改用户信息、删除用户
   2. 上传头像
   3. 按照更多的条件去查询用户
   4. 更改权限
2. 修改 Bug(目前发现的是前后端优化完成后,输入错误信息会自动跳转到框架自定义的错误日志,需要重新刷新,现在先赶小学期的进度，不去优化了)
3. 项目登录改为分布式 session（单点登录 - redis）
4. 通用性
   1. set-cookie domain 域名更通用，比如改为 *.xxx.com
   2. 把用户管理系统 => 用户中心（之后所有的服务都请求这个后端）
5. 后台添加全局请求拦截器（统一去判断用户权限、统一记录请求日志）
6. 为了赶进度处理的很多东西，想办法完善
7. 改善之后就是拿本项目做一个SpringBoot的万用模板吧，这样能快速初始化(目标等后面开始做了再写)
