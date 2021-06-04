#构建编译环境
FROM node:16
RUN yarn config set registry https://registry.npm.taobao.org \
    && git clone https://gitee.com/tank59he/bjfu_fortree_front.git /usr/src/bjfu_fortree \
    && cd /usr/src/bjfu_fortree \
    && yarn install \
    && yarn run build


FROM nginx:1.19
COPY --from=0 /usr/src/bjfu_fortree/build /usr/src/bjfu_fortree
COPY nginx.conf /etc/nginx/
EXPOSE 80