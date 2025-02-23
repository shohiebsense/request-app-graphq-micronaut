package io.shohiebsense


import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import jakarta.inject.Singleton
import java.util.*

@Singleton
class MaintenanceRequestDataFetcher(private val repository: MaintenanceRequestService) {

    fun getAllRequestsFetcher(): DataFetcher<List<MaintenanceRequest>> {
        return DataFetcher { _: DataFetchingEnvironment ->
            val requests = repository.getAllRequests()
            println("Fetched Requests: $requests")
            requests
        }
    }

    fun addRequestFetcher(): DataFetcher<MaintenanceRequest> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val title: String = env.getArgument<String?>("title") ?: throw IllegalArgumentException("Title is required")
            val date: String = env.getArgument<String?>("date") ?: throw IllegalArgumentException("Date is required")
            val status: String = env.getArgument<String?>("status") ?: "Pending"  // Default value if null
            val info: String = env.getArgument<String?>("info") ?: ""
            val type: String? = env.getArgument("type")  // Nullable field, no need to force unwrap

            val newRequest = MaintenanceRequest(
                title = title, date = date, status = status, info = info, type = type
            )
            repository.repository.save(newRequest)
        }
    }

    fun updateRequestStatusFetcher(): DataFetcher<MaintenanceRequest?> {
        return DataFetcher { env: DataFetchingEnvironment ->
            val id: String = env.getArgument<String?>("id") ?: throw IllegalArgumentException("ID is required")
            val status: String = env.getArgument<String?>("status") ?: "Pending"

            val uuid = try {
                UUID.fromString(id)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException("Invalid UUID format")
            }

            val request = repository.repository.findById(uuid).orElse(null)
            request?.let {
                it.status = status
                repository.repository.update(it)
            }
        }
    }

}
