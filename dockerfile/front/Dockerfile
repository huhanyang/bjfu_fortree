#构建编译环境
FROM node:15.3
RUN npm config set registry https://registry.npm.taobao.org \
    && git clone https://github.com/tank59he/bjfu_fortree_react.git /usr/src/bjfu_fortree \
    && cd /usr/src/bjfu_fortree \
    && npm install \
    && npm run build


FROM nginx:1.19
COPY --from=0 /usr/src/bjfu_fortree/build /usr/src/bjfu_fortree
COPY nginx.conf /etc/nginx/
EXPOSE 80