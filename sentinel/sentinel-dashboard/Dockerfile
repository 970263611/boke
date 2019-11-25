FROM openjdk:8

MAINTAINER dahua<love_dingwq@163.com>

COPY sentinel-dashboard.jar /sentinel-dashboard.jar

CMD ["java","-Dserver.port=1236 -Dcsp.sentinel.dashboard.server=localhost:1236 -Dsentinel.dashboard.auth.username=root -Dsentinel.dashboard.auth.password=root -Dsentinel.dashboard.app.hideAppNoMachineMillis=60000 -Dsentinel.dashboard.removeAppNoMachineMillis=120000 -Dsentinel.dashboard.unhealthyMachineMillis=30000 -Dsentinel.dashboard.autoRemoveMachineMillis=300000","-jar","sentinel-dashboard","&"]