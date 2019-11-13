docker build -t apollo-portal .
docker run -v /root/apollolog/portal:/root/apollolog/portal --name apollo-portal -p 6070:6070 -dit 镜像id