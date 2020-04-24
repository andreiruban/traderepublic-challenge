package io.ruban

import com.google.gson.Gson
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.features.websocket.ws
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.pingPeriod
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.timeout
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.webjars.Webjars
import io.ktor.websocket.webSocket
import io.ruban.entity.Instrument
import io.ruban.entity.InstrumentEvent
import io.ruban.entity.QuoteEvent
import io.ruban.service.InstrumentService
import io.ruban.util.view
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.filterNotNull
import kotlinx.coroutines.channels.map
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.text.DateFormat
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    val kodein = Kodein {
        bind<HttpClient>(tag = "socketClient") with singleton { HttpClient(CIO).config { install(WebSockets) } }
        bind<Gson>(tag = "gson") with singleton { Gson() }
        bind<InstrumentService>(tag = "service") with singleton { InstrumentService() }
    }

    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.FULL)
            setPrettyPrinting()
        }
    }

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    install(Webjars) { path = "/webjars" }

    val socketClient by kodein.instance<HttpClient>(tag = "socketClient")
    val gson by kodein.instance<Gson>(tag = "gson")
    val service by kodein.instance<InstrumentService>("service")

    async {
        socketClient.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/instruments") {
            for (message in incoming.map { it as? Frame.Text }.filterNotNull()) {
                if (testing) {
                    log.debug("Instruments channel received:\n${message.readText()}")
                }
                val event = gson.fromJson(message.readText(), InstrumentEvent::class.java)
                service.save(
                    Instrument(isin = event.data.isin, description = event.data.description)
                )
            }
        }
    }

    async {
        socketClient.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/quotes") {
            for (message in incoming.map { it as? Frame.Text }.filterNotNull()) {
                if (testing) {
                    log.debug(("Quotes channel received:\n${message.readText()}"))
                }
                val event = gson.fromJson(message.readText(), QuoteEvent::class.java)
                // TODO: persist them
            }
        }
    }


    routing {
        get(path = "/instruments") {
            val instruments = service.getAll().map { it.view() }
            call.respond(status = HttpStatusCode.OK, message = instruments)
        }

        webSocket("/channel") {
            while (true) {
                val frame = incoming.receive()
                if (frame is Frame.Text) {
                    send(Frame.Text("Client said: " + frame.readText()))
                }
            }
        }
    }
}
