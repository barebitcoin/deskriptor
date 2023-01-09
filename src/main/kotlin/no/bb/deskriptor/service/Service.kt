package no.bb.deskriptor.service

import com.google.rpc.Code
import com.google.rpc.Status
import com.google.protobuf.Any
import io.grpc.StatusException
import io.grpc.protobuf.StatusProto
import no.bb.deskriptor.v1alpha.*
import no.bb.deskriptor.logger
import org.bitcoindevkit.*

class Service(private val network: Network) : DeskriptorServiceGrpcKt.DeskriptorServiceCoroutineImplBase() {
    fun innerDerive(scriptType: ScriptType, input: String, index: Int): AddressInfo {
        val descriptor = "${Script(scriptType)}($input/$index)"

        try {
            val wallet = Wallet(
                descriptor, null,
                network, DatabaseConfig.Memory,
            )

            return wallet.getAddress(addressIndex = AddressIndex.LAST_UNUSED)
        } catch (exc: BdkException) {
            val (message, code) = when {
                exc.message!!.contains("Error while parsing xkey") ->
                    Pair("invalid XPUB", ErrorCode.ERROR_CODE_INVALID_XPUB)

                else -> Pair(exc.message!!, ErrorCode.ERROR_CODE_UNSPECIFIED)
            }

            throw newStatus(Code.INVALID_ARGUMENT, message, code)
        } catch (exc: Exception) {
            logger.warn { "unhandled exception: $exc" }
            throw exc
        }
    }

    // This solely exists to not be a suspended function. Torkel doesn't know
    // how to test those in his poorly written unit tests.
    fun deriveNonSuspend(request: DeriveRequest): DeriveResponse {
        when {
            request.scriptType == ScriptType.SCRIPT_TYPE_UNSPECIFIED ->
                throw newStatus(Code.INVALID_ARGUMENT, "script type cannot be empty")

            request.input == "" ->
                throw newStatus(Code.INVALID_ARGUMENT, "input cannot be empty")

            !request.input.startsWith("xpub") ->
                throw newStatus(
                    Code.INVALID_ARGUMENT, "did not receive an xpub",
                    ErrorCode.ERROR_CODE_UNSUPPORTED_INPUT,
                )
        }

        val extChain = if (request.change) "1" else "0"
        val addr = innerDerive(
            request.scriptType,
            "${request.input}/$extChain",
            request.index

        )
        return DeriveResponse.newBuilder()
            .setAddress(addr.address)
            .setIndex(addr.index.toInt())
            .build()
    }

    override suspend fun derive(request: DeriveRequest): DeriveResponse {
        return deriveNonSuspend(request)
    }
}

data class Script(private val script: ScriptType) {
    override fun toString(): String = when (script) {
        ScriptType.SCRIPT_TYPE_WPKH -> "wpkh"
        ScriptType.SCRIPT_TYPE_PKH -> "pkh"
        ScriptType.SCRIPT_TYPE_UNSPECIFIED -> throw newStatus(Code.INVALID_ARGUMENT, "unspecified")
        ScriptType.UNRECOGNIZED -> throw newStatus(Code.INVALID_ARGUMENT, "unrecognized script type: $script")
    }
}

fun newStatus(code: Code, msg: String, error: ErrorCode? = null): StatusException {

    var sBuilder = Status.newBuilder()
        .setCode(code.number)
        .setMessage(msg)

    if (error != null) {
        val errorProto = Error.newBuilder().setError(error).build()
        sBuilder = sBuilder.addDetails(Any.pack(errorProto))
    }

    val status = sBuilder.build()

    return StatusProto.toStatusException(status)
}