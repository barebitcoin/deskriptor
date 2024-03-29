package no.bb.deskriptor.service

import io.grpc.StatusException
import io.kotest.assertions.throwables.shouldThrow
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

    // https://github.com/bitcoin/bips/blob/master/bip-0086.mediawiki
    context("bip86 test vectors") {
        withData(
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                0,
                "xpub6BgBgsespWvERF3LHQu6CnqdvfEvtMcQjYrcRzx53QJjSxarj2afYWcLteoGVky7D3UKDP9QyrLprQ3VCECoY49yfdDEHGCtMMj92pReUsQ",
                "bc1p5cyxnuxmeuwuvkwfem96lqzszd02n6xdcjrs20cac6yqjjwudpxqkedrcr",
            ),
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                1,
                "xpub6BgBgsespWvERF3LHQu6CnqdvfEvtMcQjYrcRzx53QJjSxarj2afYWcLteoGVky7D3UKDP9QyrLprQ3VCECoY49yfdDEHGCtMMj92pReUsQ",
                "bc1p4qhjn9zdvkux4e44uhx8tc55attvtyu358kutcqkudyccelu0was9fqzwh",
            ),
        ) { it.assert() }
    }

    // stand sail fiction affair arch deer grow sight keen thing faculty virus
    context("torkels sparrow wallet") {
        withData(
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                0,
                "xpub6DDqmwVaNoavxHc8Agj4XuEVCC3RvuXHcQVcmVv59KivcBEoJG6brcQ49NCRtf25vA3YNPqeJwEa4TFCw1y5RGzULb72D3TQGV67xpEdH4j",
                "bc1pcz7de4f64c4jm537f436cr7sq7wgfpfhdy4nf9avf9x9nac8fa5qwccp0z",
            ),
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                1,
                "xpub6DDqmwVaNoavxHc8Agj4XuEVCC3RvuXHcQVcmVv59KivcBEoJG6brcQ49NCRtf25vA3YNPqeJwEa4TFCw1y5RGzULb72D3TQGV67xpEdH4j",
                "bc1pzstq526x3xujh88jnmpvaq2tq8yk6dl2cu6a87qvzyslumxckwxq3yl7nf",
            ),
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                2,
                "xpub6DDqmwVaNoavxHc8Agj4XuEVCC3RvuXHcQVcmVv59KivcBEoJG6brcQ49NCRtf25vA3YNPqeJwEa4TFCw1y5RGzULb72D3TQGV67xpEdH4j",
                "bc1ph2r5c6c25520gqxsemvpt0pnue28jp2tlza2kdvmmtkqva30hs4qjp9nhj",
            ),
        ) { it.assert() }
    }

    context("bos sparrow wallet") {
        withData(
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                0,
                "xpub6CPJGRoLhx72USPrYaGsiDZq6rBu6gzJruACdcyoRjffLGmecgW9rcojBmzQgw5Qsd8PN4KfSbiycVzDicvUW5m6RG9gVd41SjAVPcjWdq9",
                "bc1pzzr5kkz5rtxw733tst7yxex8kgy2hanq0jchtt0xvp7cajqz9dkqj4pxev",
            ),
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                1,
                "xpub6CPJGRoLhx72USPrYaGsiDZq6rBu6gzJruACdcyoRjffLGmecgW9rcojBmzQgw5Qsd8PN4KfSbiycVzDicvUW5m6RG9gVd41SjAVPcjWdq9",
                "bc1psxyyfk9e4lykx7ghq4g44hunn75rmh3zvuxc5mud2tuae63yk3gqq2msra",
            ),
            DeriveInput(
                SCRIPT_TYPE_P2TR,
                2,
                "xpub6CPJGRoLhx72USPrYaGsiDZq6rBu6gzJruACdcyoRjffLGmecgW9rcojBmzQgw5Qsd8PN4KfSbiycVzDicvUW5m6RG9gVd41SjAVPcjWdq9",
                "bc1pfmkr97h8dy50srk4p5c9c3fxg8m2uum4mrrfears2c7pt2ljx2xqu4ddwk",
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

        // pledge excuse sunny slim laptop address deer arena six scene flavor kit
        context("wpkh-in-sh") {
            withData(
                DeriveInput(
                    SCRIPT_TYPE_WPKH_NESTED,
                    0,
                    "ypub6Y7oSCWrRaxYQNSndbanWMzVUg4KXNvsXamJoeK5dbCRF9fGFjKcCGYLzPShcoP1d5FCjK3ohZLa7EF6XGeVdK7VGGtNBReGJsLaWpj4wUD",
                    "39aZgVG6FjZJXQcB1WFZiU1JovGbbchBvK",
                ),
                DeriveInput(
                    SCRIPT_TYPE_WPKH_NESTED,
                    1,
                    "ypub6Y7oSCWrRaxYQNSndbanWMzVUg4KXNvsXamJoeK5dbCRF9fGFjKcCGYLzPShcoP1d5FCjK3ohZLa7EF6XGeVdK7VGGtNBReGJsLaWpj4wUD",
                    "3Ezc4TeDSTES491t7WDDRDsKrKJzhwYFnp",
                ),
                DeriveInput(
                    SCRIPT_TYPE_WPKH_NESTED,
                    2,
                    "ypub6Y7oSCWrRaxYQNSndbanWMzVUg4KXNvsXamJoeK5dbCRF9fGFjKcCGYLzPShcoP1d5FCjK3ohZLa7EF6XGeVdK7VGGtNBReGJsLaWpj4wUD",
                    "3GFNWYzm4xarKufpD42e7AsCSBWmJ4zoBH",
                ),
            ) { it.assert() }
        }
    }
})