FROM openjdk:8

MAINTAINER dahua<love_dingwq@163.com>

COPY apollo-config /apollo-config

RUN chmod +x /apollo-config/scripts/startup.sh

CMD ["/apollo-config/scripts/startup.sh"]

EXPOSE 6080