FROM openjdk:8

MAINTAINER dahua<love_dingwq@163.com>

COPY apollo-admin /apollo-admin

RUN chmod +x /apollo-admin/scripts/startup.sh

CMD ["/apollo-admin/scripts/startup.sh"]

EXPOSE 6090