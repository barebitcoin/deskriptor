package no.bb.deskriptor

import io.grpc.Server
import io.grpc.ServerBuilder
import no.bb.deskriptor.service.Service
import org.bitcoindevkit.*

fun main(args: Array<String>) {
    if ("-h" in args || "--help" in args) {
        println("usage: just start it")
        return
    }

    val port = 5000

    val service = Service(Network.BITCOIN)
    val server: Server = ServerBuilder
        .forPort(port)
        .addService(service)
        .build()

    server.start()
    println("Server started, listening on $port")

    server.awaitTermination()
}
