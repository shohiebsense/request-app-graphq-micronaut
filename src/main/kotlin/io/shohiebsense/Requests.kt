package io.shohiebsense


import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import jakarta.inject.Singleton
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Singleton
class Requests {
    private val requests = ConcurrentHashMap<Int, Request>()
    private val idCounter = AtomicInteger(1)

    fun addRequest(title: String, description: String): Request {
        val id = idCounter.getAndIncrement()
        val request = Request(id, title, description)
        requests[id] = request
        return request
    }

    fun editRequest(id: Int, title: String?, description: String?): Request? {
        val request = requests[id] ?: return null
        val updatedRequest = request.copy(
            title = title ?: request.title,
            description = description ?: request.description
        )
        requests[id] = updatedRequest
        return updatedRequest
    }

    fun getRequestById(id: Int): Request? {
        return requests[id]
    }

    fun getAllRequests(): List<Request> {
        return requests.values.toList()
    }
}

@Singleton
class GraphQLRequestDataFetchers(private val requests: Requests) {
    fun addRequestDataFetcher(): DataFetcher<Request> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val title = env.getArgument<String>("title")
            val description = env.getArgument<String>("description")
            if (title != null) {
                if (description != null) {
                    requests.addRequest(title, description)
                }
            }
            null
        }
    }

    fun editRequestDataFetcher(): DataFetcher<Request?> {
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

    fun getRequestByIdDataFetcher(): DataFetcher<Request?> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val id = env.getArgument<Int>("id")
            if (id != null) {
                requests.getRequestById(id)
            }
            null
        }
    }

    fun getAllRequestsDataFetcher(): DataFetcher<List<Request>> {
        return DataFetcher { requests.getAllRequests() }
    }
}

data class Request(val id: Int, val title: String, val description: String)
