package io.shohiebsense


import io.micronaut.core.annotation.Introspected
import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "maintenance_requests")
@Introspected
data class MaintenanceRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: UUID? = null,
    val title: String,
    val date: String,
    var status: String,
    val info: String,
    val type: String? = null
)
