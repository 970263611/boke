docker build -t apollo-admin .
docker run -v /root/apollolog/admin:/root/apollolog/admin --name apollo-admin -p 6090:6090 -dit 镜像id