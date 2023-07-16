`deskriptor` is a small Kotlin application that exposes some BDK descriptor
functionality over gRPC. All operations happen in-memory, with no persistence.

# Quickstart

```bash
$ ./gradlew bufGenerate # generate code
$ ./gradlew build       # build app (also runs tests)

$ docker buildx build --platform linux/amd64 -t deskriptor . 
$ docker run -p 5005:5005 --rm deskriptor 

$ request='{"input":"xpub6CggtnWmbpCdtUsZqyzAZcSNf5gFtybrRn4jgiWkRY7Nmu765xD2ZnjUdwwRMK59hPqwkdGNiLmkZq7kpTyzmdaFsJySVXwpTNCdVKP5b58", "index":3, "scriptType":"SCRIPT_TYPE_WPKH", "change":false}'

$ buf curl  --protocol grpc --http2-prior-knowledge \
            --emit-defaults --data=$request http://localhost:5005/bb.deskriptor.v1alpha.DeskriptorService/Derive
{
  "address": "bc1qmtkc6ecfvxhsg95xn7m74xgw85qzj5x26vxddw",
  "desc": "wpkh(xpub6CggtnWmbpCdtUsZqyzAZcSNf5gFtybrRn4jgiWkRY7Nmu765xD2ZnjUdwwRMK59hPqwkdGNiLmkZq7kpTyzmdaFsJySVXwpTNCdVKP5b58/0/3)"
}
```

# Build Docker image

```bash
$ docker buildx build --platform linux/amd64 -t barebitcoin/deskriptor .
``` 

# BIP44/BIP32 address generation

Because it's impossible to remember:

* BIP32 HD wallets
* BIP44: Multi-account hierarchies

This service supports generating addresses from XPUBs, ZPUBs and YPUBs. 

# Why?

The observant reader might notice that the Kotlin code here is shit. The idea
was to utilize the FFI bindings of BDK to call into Rust code for managing
descriptors, and then expose that over a gRPC API so that it could be consumed
from Go code. A mouthful! Why didn't we just write the gRPC server in Rust? Good question. Might have been the better
idea. 