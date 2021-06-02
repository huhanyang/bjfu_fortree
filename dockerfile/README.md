# Docker部署与上传

[TOC]

## Docker与Compose安装

curl -fsSL https://get.docker.com | bash -s docker --mirror Aliyun


## Docker镜像构建

### 新镜像构建

#### 构建命令

后端

docker build --no-cache=true -t bjfu_fortree_back:latest .

前端

docker build --no-cache=true -t bjfu_fortree_front:latest .

开发

docker build --no-cache=true -t bjfu_fortree_develop:latest .

## 使用Docker镜像部署

后端

docker run -itd --name bjfu_fortree_back --network host -p 8080:8080 bjfu_fortree_back

前端

docker run -itd --name bjfu_fortree_front --network host -p 80:80 bjfu_fortree_front

开发

docker run -itd --name bjfu_fortree_develop -p 80:80 bjfu_fortree_develop


docker run -p 3306:3306 -d --name mysql8 -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=fortree  mysql:8





### compose

Linux

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

