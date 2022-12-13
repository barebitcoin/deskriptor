package no.bb.deskriptor

import no.bb.deskriptor.service.Service

import io.grpc.ServerBuilder
import io.grpc.Server

import org.bitcoindevkit.*

fun main(args: Array<String>) {
    val port = 5000;

    val externalDescriptor = "wpkh([c258d2e4/84h/1h/0h]tpubDDYkZojQFQjht8Tm4jsS3iuEmKjTiEGjG6KnuFNKKJb5A6ZUCUZKdvLdSDWofKi4ToRCwb9poe1XdqfUnP4jaJjCB2Zwv11ZLgSbnZSNecE/0/*)"
    val internalDescriptor = "wpkh([c258d2e4/84h/1h/0h]tpubDDYkZojQFQjht8Tm4jsS3iuEmKjTiEGjG6KnuFNKKJb5A6ZUCUZKdvLdSDWofKi4ToRCwb9poe1XdqfUnP4jaJjCB2Zwv11ZLgSbnZSNecE/1/*)"

    val database = DatabaseConfig.Memory
    try {
        val wallet =  Wallet(
            externalDescriptor, internalDescriptor,
            Network.TESTNET, database,
        )
        val addr = wallet.getAddress(AddressIndex.LAST_UNUSED)
        println(addr)
    } catch (exc: BdkException.Descriptor) {
        println(exc)
    }

    val server: Server = ServerBuilder.forPort(port).addService(Service()).build()

    server.start()
    println("Server started, listening on $port")

    server.awaitTermination()
}
