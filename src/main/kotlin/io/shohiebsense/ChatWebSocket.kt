package io.shohiebsense


import io.micronaut.websocket.WebSocketBroadcaster
import io.micronaut.websocket.WebSocketSession
import io.micronaut.websocket.annotation.*
import java.util.concurrent.ConcurrentHashMap

@ServerWebSocket("/ws")
class ChatWebSocket(private val broadcaster: WebSocketBroadcaster) {

    private val sessions = ConcurrentHashMap.newKeySet<WebSocketSession>()

    @OnOpen
    fun onOpen(session: WebSocketSession) {
        sessions.add(session)
        session.sendSync("Welcome! Your session ID: ${session.id}")
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
        session.sendSync("Error: ${throwable.message}")
    }
}
