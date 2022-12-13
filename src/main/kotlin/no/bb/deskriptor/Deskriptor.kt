package no.bb.deskriptor

import no.bb.deskriptor.service.Service

import io.grpc.ServerBuilder
import io.grpc.Server

fun main(args: Array<String>) {
    val port = 5000;

    val server: Server = ServerBuilder.forPort(port).addService(Service()).build()

    server.start()
    println("Server started, listening on $port")

    server.awaitTermination()
}
