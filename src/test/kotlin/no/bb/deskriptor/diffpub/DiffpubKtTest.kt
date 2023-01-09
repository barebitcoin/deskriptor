package no.bb.deskriptor.diffpub

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

data class Test(val input: String, val output: String)

class DiffpubTest : FunSpec({
    context("zpub") {
        withData(
            // rebel useless actual kit retreat unfair either beauty bracket quit marine squirrel
            Test(
                "zpub6qU2861n2op1mywoHyi1FchYrVFGf4g3oU1dcHCP3CGRtXLjMc27AV7rFyRsbaZYEzbG9trm6wZeHvcyRxfQ5RLadMJoM85U1VPB5bdVATP",
                "xpub6BoVWkfwjSj45PZZdG8kqSWYWYxNmph3yEyC3VQcHBWfnKiGrHgyvMoaDZWhbmFhRiMeewfeBcrYXMPqzZqNUwyNtfuxBJSVU3FtJRwzENi",

                ),
            // soccer cereal present flock simple uniform blind park jaguar session obvious industry
            Test(
                "zpub6rBwjtT6beigFq9XF9cng6nFFXwHekkia5m3GQTLntHz9ZfZ1BqfVsMdXPt5gFawuSEJNHBC3YU9y2P1XNoxVUTaYZfAu6rgFmWjzhjwfdf",
                "xpub6CXR8Z7GJHdiZEmHaS3YFvbEubePmWmijribhcfa2sYE3N36VsWYFk3MUyxugSH769zgsKz58Dm4CT9t5yyvu16NotGKjHDhiKPTDUvEqBP",

                )
        ) { t ->
            toXpub(t.input) shouldBe t.output
        }
    }
})
