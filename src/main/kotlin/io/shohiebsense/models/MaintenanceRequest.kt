package io.shohiebsense.models


import io.micronaut.core.annotation.Introspected
import jakarta.persistence.*

@Entity
@Table(name = "maintenance_requests")
@Introspected
data class MaintenanceRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Int,
    val title: String,
    val date: String,
    var status: String,
    val urgentLevel: String,
    val type: String
)
