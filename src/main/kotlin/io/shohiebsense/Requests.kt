package io.shohiebsense

import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import io.micronaut.websocket.WebSocketBroadcaster
import io.shohiebsense.models.MaintenanceRequest
import jakarta.inject.Inject
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Singleton
class Requests {
    private val requests = ConcurrentHashMap<Int, MaintenanceRequest>()
    private val idCounter = AtomicInteger(1)

    fun addRequest(title: String, status: String, date: String, urgentLevel: String, type: String): MaintenanceRequest {
        val id = idCounter.getAndIncrement()
        val request = MaintenanceRequest(id, title = title, status = status, date = date, urgentLevel = urgentLevel, type = type)
        requests[id] = request
        return request
    }

    fun editRequest(id: Int, title: String?, status: String?): MaintenanceRequest? {
        val request = requests[id] ?: return null
        val updatedRequest = request.copy(
            title = title ?: request.title,
            status = status ?: request.status
        )
        requests[id] = updatedRequest
        return updatedRequest
    }

    fun getRequestById(id: Int): MaintenanceRequest? {
        return requests[id]
    }

    fun getAllRequests(): List<MaintenanceRequest> {
        return requests.values.toList()
    }
}

@Singleton
class GraphQLRequestDataFetchers(private val requests: Requests, private val broadcaster: WebSocketBroadcaster) {

    @Inject
    lateinit var objectMapper: ObjectMapper

    fun addRequestDataFetcher(): DataFetcher<MaintenanceRequest> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val title = env.getArgument<String>("title")
            val date = env.getArgument<String>("date")
            val status = env.getArgument<String>("status")
            val urgentLevel = env.getArgument<String>("urgent_level")
            val type = env.getArgument<String>("type")

            if (title.isNullOrBlank() || date.isNullOrBlank() || status.isNullOrBlank() || urgentLevel.isNullOrBlank() || type.isNullOrBlank()) {
                throw IllegalArgumentException("All required fields must be provided.")
            }

            val newRequest = requests.addRequest(
                title = title,
                date = date,
                status = status,
                urgentLevel = urgentLevel,
                type = type)
            val jsonMessage = objectMapper.writeValueAsString(requests.getAllRequests())

            broadcaster.broadcastSync(jsonMessage)
            newRequest
        }
    }


    fun editRequestDataFetcher(): DataFetcher<MaintenanceRequest?> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val id = env.getArgument<Int>("id")
            val title = env.getArgument<String>("title")
            val description = env.getArgument<String>("description")
            if (id != null) {
                requests.editRequest(id, title, description)
            }
            null
        }
    }

    fun getRequestByIdDataFetcher(): DataFetcher<MaintenanceRequest?> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val id = env.getArgument<Int>("id")
            if (id != null) {
                requests.getRequestById(id)
            }
            null
        }
    }

    fun getAllRequestsDataFetcher(): DataFetcher<List<MaintenanceRequest>> {
        return DataFetcher { requests.getAllRequests() }
    }
}
