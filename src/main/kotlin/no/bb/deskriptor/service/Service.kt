package no.bb.deskriptor.service

import com.google.rpc.Code
import com.google.rpc.Status
import com.google.protobuf.Any
import io.grpc.StatusException
import io.grpc.protobuf.StatusProto
import no.bb.deskriptor.diffpub.toXpub
import no.bb.deskriptor.v1alpha.*
import no.bb.deskriptor.logger
import org.bitcoindevkit.*

class Service(private val network: Network) : DeskriptorServiceGrpcKt.DeskriptorServiceCoroutineImplBase() {
    private fun innerDerive(scriptType: ScriptType, input: String, index: Int): Pair<AddressInfo, String> {
        var descriptor = "${Script(scriptType)}($input/$index)"
        if (scriptType == ScriptType.SCRIPT_TYPE_WPKH_NESTED) {
            descriptor += ")"
        }

        try {
            val wallet = Wallet(
                descriptor, null,
                network, DatabaseConfig.Memory,
            )

            return Pair(wallet.getAddress(addressIndex = AddressIndex.LAST_UNUSED), descriptor)
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
        }

        // The BDK descriptor code only support XPUBs. YPUBs/ZPUBs are really
        // just rather dumb wrappers around the same data. Convert it to an XPUB,
        // and use that. Problem solved!
        fun xpubOrThrow(input: String, code: ErrorCode): String {
            try {
                return toXpub(input)
            } catch (exc: Exception) {
                throw newStatus(Code.INVALID_ARGUMENT, exc.message!!, code)
            }
        }

        val xpub = when {
            request.input.startsWith("xpub") -> xpubOrThrow(request.input, ErrorCode.ERROR_CODE_INVALID_XPUB)
            request.input.startsWith("zpub") -> xpubOrThrow(request.input, ErrorCode.ERROR_CODE_INVALID_ZPUB)
            request.input.startsWith("ypub") -> xpubOrThrow(request.input, ErrorCode.ERROR_CODE_INVALID_YPUB)

            else -> throw newStatus(Code.INVALID_ARGUMENT, "did not receive an xpub, zpub or ypub")
        }

        val extChain = if (request.change) "1" else "0"
        val (addr, descriptor) = innerDerive(
            request.scriptType,
            "${xpub}/$extChain",
            request.index

        )
        return DeriveResponse.newBuilder()
            .setAddress(addr.address)
            .setDesc(descriptor)
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
        // This is kinda bad... Should instead code up a scheme with wrapping
        // operators, and replace this `when` block with something that returns
        // a list of operators. For another day.
        ScriptType.SCRIPT_TYPE_WPKH_NESTED -> "sh(wpkh"
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