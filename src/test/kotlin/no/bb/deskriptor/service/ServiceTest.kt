package no.bb.deskriptor.service

import io.grpc.StatusException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import no.bb.deskriptor.v1alpha.ScriptType
import no.bb.deskriptor.v1alpha.ScriptType.*
import no.bb.deskriptor.v1alpha.deriveRequest
import org.bitcoindevkit.Network

data class DeriveInput(val scriptType: ScriptType, val index: Int, val input: String, val address: String) {
    fun assert() {
        val srv = Service(Network.BITCOIN)
        val s = scriptType
        val idx = index
        val inp = input

        val req = deriveRequest {
            index = idx
            input = inp
            scriptType = s
        }
        val derived = srv.deriveNonSuspend(req)
        derived.address shouldBe address
    }
}

class ServiceTest : FunSpec({
    context("invalid xpub/zpub/ypub") {
        val invalids = listOf("sumthinelse", "xpub1111", "zpub1111", "ypub1111")
        invalids.forEach { invalid ->

            shouldThrow<StatusException> {
                val srv = Service(Network.BITCOIN)
                srv.deriveNonSuspend(deriveRequest {
                    input = invalid
                    scriptType = SCRIPT_TYPE_PKH
                })
            }
        }
    }
    context("torkels electrum wallet") {
        withData(
            DeriveInput(
                SCRIPT_TYPE_WPKH,
                0,
                "xpub68wbshdUKfMu3LVp329fG7JbZPZsquCb5vNAbYi9LQPUeKQmyQrnsZF8Tachg6wqyue8srM8tVBbHaHJMZuxu8RRXtubaMVZL19nsC7NAts",
                "bc1qhz0dr4xddcqhey0qn05pa9ux6zchuekjjwnxca",
            ),
            DeriveInput(
                SCRIPT_TYPE_WPKH,
                1,
                "xpub68wbshdUKfMu3LVp329fG7JbZPZsquCb5vNAbYi9LQPUeKQmyQrnsZF8Tachg6wqyue8srM8tVBbHaHJMZuxu8RRXtubaMVZL19nsC7NAts",
                "bc1qz3jzn7sdajfm0zsrssdk22fmrdak7a7wuf77h0",
            ),
            DeriveInput(
                SCRIPT_TYPE_WPKH,
                2,
                "xpub68wbshdUKfMu3LVp329fG7JbZPZsquCb5vNAbYi9LQPUeKQmyQrnsZF8Tachg6wqyue8srM8tVBbHaHJMZuxu8RRXtubaMVZL19nsC7NAts",
                "bc1q8eje0m8dpdslkcwycwv3qwnxk6jk33ps7au952",
            ),
        ) { it.assert() }
    }

    // generated with https://iancoleman.io/bip39/
    context("iancoleman data") {

        // grief stairs dust provide surface often mutual salt margin trophy bleak spend
        context("zpub") {
            val srv = Service(Network.BITCOIN)
            val derived = srv.deriveNonSuspend(deriveRequest {
                input =
                    "zpub6rDFfjN1U9sKvLCzW6Gfz7ibFdw4qos7X3ufHVxGqi5cR6pSeEEcm3yrx96guzCYLd9hiREUGEeqjWiYnBNKKcW8dg3scTFep8E9MvEPSmd"
                scriptType = SCRIPT_TYPE_WPKH
            })
            derived.address shouldBe "bc1qtk4mftgkya9fc4suxu3q20hw5xx8q5ly3034n5"
        }

        // seed: filter west draft sudden upon kite dawn client sadness turkey senior parrot
        context("pkh") {
            withData(
                DeriveInput(
                    SCRIPT_TYPE_PKH,
                    0,
                    "xpub6BxSaAvAbRts1A46zeMcXHMCG5FLZ3V8D7W5q7HCRPHSaj3XcFKkpcUChmABkXsqb3WHzj1PTF3iwX3gjfQeWH8bcP2Xwazd7GuG3ysY1y8",
                    "1J11LxfGympHfRE5u3GnuhipNGLvSvPDjf",
                ),
                DeriveInput(
                    SCRIPT_TYPE_PKH,
                    1,
                    "xpub6BxSaAvAbRts1A46zeMcXHMCG5FLZ3V8D7W5q7HCRPHSaj3XcFKkpcUChmABkXsqb3WHzj1PTF3iwX3gjfQeWH8bcP2Xwazd7GuG3ysY1y8",
                    "1C2AtaCPPDATJzx99fMn6eWoX4TdhrypXK",
                ),
                DeriveInput(
                    SCRIPT_TYPE_PKH,
                    2,
                    "xpub6BxSaAvAbRts1A46zeMcXHMCG5FLZ3V8D7W5q7HCRPHSaj3XcFKkpcUChmABkXsqb3WHzj1PTF3iwX3gjfQeWH8bcP2Xwazd7GuG3ysY1y8",
                    "1PanpbEiu2CB2ZWEJPNpeea4KfVCdjgJbW",
                ),
            ) { it.assert() }
        }

        // seed: bag pill wine embark dragon scrap whisper boil inflict link note narrow
        context("wpkh") {
            withData(
                DeriveInput(
                    SCRIPT_TYPE_WPKH,
                    0,
                    "xpub6CLreXSYMsHSE9rMc1EsdVuM8LW9GvZA45AKTipwWgTZrkoH5TQADKGuaSRAjhWVK5MgZAPG8KfeV1TbRa8vn1D5dd44J3o5MZ7agPLw47Z",
                    "bc1qfzcm358sr5tm68wt80wcmy82w4s7w95zdcfnff",
                ),
                DeriveInput(
                    SCRIPT_TYPE_WPKH,
                    1,
                    "xpub6CLreXSYMsHSE9rMc1EsdVuM8LW9GvZA45AKTipwWgTZrkoH5TQADKGuaSRAjhWVK5MgZAPG8KfeV1TbRa8vn1D5dd44J3o5MZ7agPLw47Z",
                    "bc1qdy7zy40xcsec2dukr0mp7ddgcqdczzds2exncg",
                ),
                DeriveInput(
                    SCRIPT_TYPE_WPKH,
                    2,
                    "xpub6CLreXSYMsHSE9rMc1EsdVuM8LW9GvZA45AKTipwWgTZrkoH5TQADKGuaSRAjhWVK5MgZAPG8KfeV1TbRa8vn1D5dd44J3o5MZ7agPLw47Z",
                    "bc1q3khmgvvmy2a5cm9c88sp6fggg5lh3mzm452ajn",
                ),
            ) { it.assert() }
        }
    }
})