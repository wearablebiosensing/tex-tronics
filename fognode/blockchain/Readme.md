Getting them up and running

For RPI3
docker run -d -p 5292:80 kabuki4774/blockchain:kaya0vrpip3

For x86_64 
docker run -d -p 5292:80 kabuki4774/blockchain:kaya0v1


Resgistering your node

Example fog.wbl.cloud being registered to dash.wbl.cloud

curl -X POST -H "Content-Type: application/json" -d '{
 "nodes": ["http://fog.wbl.cloud:5292"]
}' "http://dash.wbl.cloud:5292/nodes/register"


Checking the chain

curl http://dash.wbl.cloud:5292/nodes/resolve