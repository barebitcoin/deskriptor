package no.bb.deskriptor

import io.grpc.*
import io.grpc.ForwardingServerCall.SimpleForwardingServerCall
import io.grpc.protobuf.services.ProtoReflectionService
import mu.KotlinLogging
import no.bb.deskriptor.service.Service
import org.bitcoindevkit.*

val logger = KotlinLogging.logger { }

fun main(args: Array<String>) {
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

        logger.info { "$method: $desc $metadataKeyValue" }
        super.close(status, trailers)
    }
}