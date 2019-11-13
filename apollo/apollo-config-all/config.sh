docker build -t apollo-config .
docker run -dit -v /root/apollolog/config:/root/apollolog/config --name apollo-config -p 6080:6080 镜像id