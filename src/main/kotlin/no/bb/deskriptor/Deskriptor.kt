package no.bb.deskriptor

import no.bb.deskriptor.service.Service

import io.grpc.ServerBuilder
import io.grpc.Server

import org.bitcoindevkit.*

fun main(args: Array<String>) {
    val port = 5000;

    val service = Service(Network.BITCOIN)
    val server: Server = ServerBuilder.forPort(port).addService(service).build()

    server.start()
    println("Server started, listening on $port")

    server.awaitTermination()
}
