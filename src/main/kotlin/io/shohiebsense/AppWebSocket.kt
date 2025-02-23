package io.shohiebsense


import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.websocket.WebSocketBroadcaster
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.*
import io.micronaut.websocket.exceptions.WebSocketSessionException
import jakarta.inject.Inject
import java.util.concurrent.ConcurrentHashMap

@ServerWebSocket("/ws")
class AppWebSocket(private val broadcaster: WebSocketBroadcaster, private val requests: Requests) {

    private val sessions = ConcurrentHashMap.newKeySet<WebSocketSession>()

    @Inject
    lateinit var objectMapper: ObjectMapper

    @OnOpen
    fun onOpen(session: WebSocketSession) {
        sessions.add(session)
        val jsonMessage = objectMapper.writeValueAsString(requests.getAllRequests())
        session.sendSync(jsonMessage)
    }

    @OnMessage
    fun onMessage(message: String, session: WebSocketSession) {
        val formattedMessage = "Session ${session.id}: $message"
        broadcaster.broadcastSync(formattedMessage)
    }

    @OnClose
    fun onClose(session: WebSocketSession) {
        sessions.remove(session)
    }

    @OnError
    fun onError(session: WebSocketSession, throwable: Throwable) {
        if (!session.isOpen) {
            println("Error occurred, but session is already closed: ${throwable.message}")
            return
        }

        try {
            session.sendSync("Error: ${throwable.message}")
        } catch (e: WebSocketSessionException) {
            println("Failed to send error message: ${e.message}")
        }
    }
}
