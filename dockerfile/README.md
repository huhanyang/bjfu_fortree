# 部署文档

[TOC]

## Docker与Compose安装

### Docker安装

curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun

### Docker Compose安装

Linux 上我们可以从 Github 上下载它的二进制包来使用，最新发行的版本地址：https://github.com/docker/compose/releases。

运行以下命令以下载 Docker Compose 的当前稳定版本：

```
$ sudo curl -L "https://github.com/docker/compose/releases/download/1.24.1/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
```

要安装其他版本的 Compose，请替换 1.24.1。

将可执行权限应用于二进制文件：

```
$ sudo chmod +x /usr/local/bin/docker-compose
```

创建软链：

```
$ sudo ln -s /usr/local/bin/docker-compose /usr/bin/docker-compose
```

测试是否安装成功：

```
$ docker-compose --version
cker-compose version 1.24.1, build 4667896b
```


## 开发环境

**构建**

docker build --no-cache=true -t bjfu_fortree_develop:latest .

**运行**

Nginx代理`docker run -itd --name bjfu_fortree_develop -p 80:80 bjfu_fortree_develop`

Mysql8`docker run -p 3306:3306 -d --name mysql8 -e MYSQL_DATABASE=fortree  mysql:8`

Minio服务器`docker run -p 9000:9000 minio/minio server /data`

运行后端在8080端口

运行前端在3000端口

## 线上环境

clone源码`git clone https://gitee.com/tank59he/bjfu_fortree.git`

切换位置`cd bjfu_fortree/dockerfile`

构建并运行`docker-compose -p fortree up -d`

## 管理员账号生成

### 注册普通账号

前往网页注册新账号

### 登录MySQL

1. ssh登录服务器
2. 执行`sudo su`
3. 执行`docker ps -a`查看mysql容器的CONTAINER ID
4. 执行`docker exec -it 容器id bash`进入mysql容器
5. 执行`mysql -uroot -p`并输入密码：my-secret-pw

### 修改账号类型

1. MySQL里执行`use fortree;`切换到fortree应用数据库
2. 执行`update fortree.fortree_user set type = 1 where account = 注册的账户`

