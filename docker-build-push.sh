image=barebitcoin/deskriptor:$(git rev-parse --short HEAD)
echo building and pushing $image

docker buildx build --platform linux/amd64 -t $image .
docker push $image