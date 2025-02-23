package io.shohiebsense

import jakarta.inject.Singleton
import java.util.*

@Singleton
class MaintenanceRequestService(val repository: MaintenanceRequestRepository) {

    fun getAllRequests(): List<MaintenanceRequest> = repository.findAll()

    fun addRequest(title: String, date: String, status: String, info: String, type: String?): MaintenanceRequest {
        val request = MaintenanceRequest(title = title, date = date, status = status, info = info, type = type)
        return repository.save(request)
    }

    fun updateRequestStatus(id: UUID, status: String): MaintenanceRequest? {
        val request = repository.findById(id).orElse(null) ?: return null
        request.status = status
        return repository.update(request)
    }
}
