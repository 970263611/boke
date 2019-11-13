FROM openjdk:8

MAINTAINER dahua<love_dingwq@163.com>

COPY apollo-portal /apollo-portal

RUN chmod +x /apollo-portal/scripts/startup.sh

CMD ["/apollo-portal/scripts/startup.sh"]

EXPOSE 6070