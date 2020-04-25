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
import io.ktor.util.KtorExperimentalAPI
import io.ktor.webjars.Webjars
import io.ktor.websocket.webSocket
import io.ruban.entity.InstrumentEvent
import io.ruban.entity.QuoteEvent
import io.ruban.repository.Repository
import io.ruban.service.DataAggregator
import io.ruban.service.EventProcessor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.text.DateFormat
import java.time.Duration

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalAPI
@ExperimentalCoroutinesApi
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = true) {

    val kodein = Kodein {
        bind<HttpClient>(tag = "socketClient") with singleton { HttpClient(CIO).config { install(WebSockets) } }
        bind<Gson>(tag = "gson") with singleton { Gson() }

        bind<Repository>(tag = "repository") with singleton { Repository() }

        bind<EventProcessor>(tag = "processor") with singleton { EventProcessor(instance(tag = "repository")) }
        bind<DataAggregator>(tag = "aggregator") with singleton { DataAggregator(instance(tag = "repository")) }
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
    val processor by kodein.instance<EventProcessor>("processor")
    val aggregator by kodein.instance<DataAggregator>("aggregator")

    async {
        socketClient.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/instruments") {
            incoming.consumeAsFlow().map { it as? Frame.Text }.filterNotNull().collect { message ->
                if (testing) {
                    log.debug("Instruments channel received:\n${message.readText()}")
                }
                val event = gson.fromJson(message.readText(), InstrumentEvent::class.java)
                processor.consume(event)
            }
        }
    }

    async {
        socketClient.ws(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/quotes") {
            incoming.consumeAsFlow().map { it as? Frame.Text }.filterNotNull().collect { message ->
                if (testing) {
                    log.debug(("Quotes channel received:\n${message.readText()}"))
                }
                val event = gson.fromJson(message.readText(), QuoteEvent::class.java)
                processor.consume(event)
            }
        }
    }

    routing {
        get(path = "/instruments") {
            call.respond(status = HttpStatusCode.OK, message = aggregator.list())
        }

        get(path = "/candles") {
            val isin: String = call.parameters["isin"] ?: throw RuntimeException("ISIN not specified")
            val lastPeriod: Long = call.parameters["last_period"]?.toLong() ?: 30

            call.respond(status = HttpStatusCode.OK, message = aggregator.candlesFor(isin = isin, period = lastPeriod))
        }

        webSocket("/hotInstruments") {
            while (true) {
                // TODO delay ?
                // TODO Check Offers
                // TODO send

                outgoing.send(Frame.Text("Hot Push!"))
            }
        }
    }
}
