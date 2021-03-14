# Docker部署与上传

[TOC]

## Docker镜像上传

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

docker run -itd --name bjfu_fortree_back -p 8080:8080 bjfu_fortree_back

前端

docker run -itd --name bjfu_fortree_front -p 80:80 bjfu_fortree_front

开发

docker run -itd --name bjfu_fortree_develop -p 80:80 bjfu_fortree_develop