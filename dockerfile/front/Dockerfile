#构建编译环境
FROM node:16
RUN yarn config set registry https://registry.npm.taobao.org \
    && git clone https://github.com/tank59he/bjfu_fortree_react.git /usr/src/bjfu_fortree_react \
    && cd /usr/src/bjfu_fortree_react \
    && yarn install \
    && yarn run build


FROM nginx:1.19
COPY --from=0 /usr/src/bjfu_fortree_react/build /usr/src/bjfu_fortree_react
COPY nginx.conf /etc/nginx/
EXPOSE 80