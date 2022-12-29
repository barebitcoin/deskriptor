`deskriptor` is a small Kotlin application that exposes some BDK descriptor
functionality over gRPC. All operations happen in-memory, with no persistence.

# BIP44/BIP32 address generation

Because it's impossible to remember:

* BIP32 HD wallets
* BIP44: Multi-account hierarchies

This service supports generating addresses from XPUBs. If you don't have an XPUB,
you need to convert it before passing it in. This can be done with this tool: https://jlopp.github.io/xpub-converter/.
This might be added as functionality here, who knows.

# Why?

The observant reader might notice that the Kotlin code here is shit. The idea
was to utilize the FFI bindings of BDK to call into Rust code for managing
descriptors, and then expose that over a gRPC API so that it could be consumed
from Go code. A mouthful! Why didn't we just write the gRPC server in Rust? Good question. Might have been the better
idea. 