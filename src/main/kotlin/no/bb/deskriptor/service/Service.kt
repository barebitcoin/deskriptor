package no.bb.deskriptor.service

import no.bb.deskriptor.v1.*

class Service : DeskriptorServiceGrpcKt.DeskriptorServiceCoroutineImplBase() {
    override suspend fun derive(request: DeriveRequest): DeriveResponse {
        return deriveResponse {
            out = "out: ${request.`in`}"
        }
    }
}
