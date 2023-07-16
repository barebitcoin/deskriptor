package no.bb.deskriptor

import io.grpc.*
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.protobuf.services.ProtoReflectionService
import no.bb.deskriptor.service.Service
import org.bitcoindevkit.Network
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.logging.LogManager
import java.util.logging.Logger

val logger = Logger.getGlobal()!!

fun main(args: Array<String>) {
    // If a file logging.properties exists in the working directory,
    // read it and pass it into the logging manager.
    // Why is Java logging configuration such a clusterfuck?
    // I've spent multiple hours just setting some logging
    // values...
    try {
        val fis = FileInputStream("logging.properties")
        LogManager.getLogManager().readConfiguration(fis)
    } catch (exc: FileNotFoundException) {
        // The file doesn't exist, and that's OK.
    }

    if ("-h" in args || "--help" in args) {
        println("usage: just start it")
        return
    }

    val port = 5005

    val service = Service(Network.BITCOIN)
    val server: Server =
        ServerBuilder.forPort(port)
            .addService(service)
            .addService(ProtoReflectionService.newInstance())
            .intercept(ServerLogInterceptor()).build()

    server.start()
    logger.info("Server started, listening on $port")

    server.awaitTermination()
}


class ServerLogInterceptor : ServerInterceptor {
    override fun <ReqT, RespT> interceptCall(
        call: ServerCall<ReqT, RespT>?, headers: Metadata?, next: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        val listener = LogDelegate(call!!, headers!!)
        return next.startCall(listener, headers)
    }
}

class LogDelegate<ReqT, RespT>(private val delegate: ServerCall<ReqT, RespT>, private val headers: Metadata) :
    SimpleForwardingServerCall<ReqT, RespT>(delegate) {

    override fun close(status: Status?, trailers: Metadata?) {
        val method = delegate.methodDescriptor.fullMethodName
        val desc = if (status?.code == Status.Code.OK) "OK" else {
            "${status?.code}: ${status?.description}"
        }

        val metadataKeyValue = headers.keys().joinToString(separator = "\t", prefix = "\t") { keyName ->
            val key = Metadata.Key.of(keyName, Metadata.ASCII_STRING_MARSHALLER)
            val value = headers.get(key)
            "$keyName=$value"
        }

        logger.info("$method: $desc $metadataKeyValue")
        super.close(status, trailers)
    }
}