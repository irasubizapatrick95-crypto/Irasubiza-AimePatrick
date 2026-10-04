package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val name: String
)

data class DentalClinic(
    val id: String,
    val name: String,
    val district: String,
    val province: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val phoneNumber: String,
    val emergencyPhone: String,
    val rating: Float,
    val reviewsCount: Int,
    val isMobileVan: Boolean,
    val openingHours: String,
    val isEmergency24h: Boolean,
    val acceptsMutuelle: Boolean,
    val currentVanLocation: String? = null,
    val vanNextSchedule: String? = null,
    val assignedDentists: List<Dentist> = emptyList(),
    val availableServices: List<DentalService> = emptyList()
)

data class Dentist(
    val id: String,
    val name: String,
    val title: String,
    val qualification: String,
    val specialty: String,
    val experienceYears: Int,
    val rating: Float,
    val bio: String,
    val availableDays: String,
    val languages: List<String>
)

data class DentalService(
    val id: String,
    val title: String,
    val titleRw: String,
    val description: String,
    val category: String,
    val durationMinutes: Int,
    val priceRwf: Int,
    val insuranceCoveredPercentage: Int, // e.g. 90% with Mutuelle de Santé
    val isEmergency: Boolean = false
)

@Entity(tableName = "appointments")
data class Appointment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientName: String,
    val patientPhone: String,
    val clinicId: String,
    val clinicName: String,
    val isMobileVan: Boolean,
    val dentistId: String,
    val dentistName: String,
    val serviceTitle: String,
    val servicePriceRwf: Int,
    val date: String,
    val timeSlot: String,
    val isEmergency: Boolean,
    val status: String, // "CONFIRMED", "PENDING", "COMPLETED", "CANCELLED"
    val symptomsNotes: String,
    val paymentStatus: String, // "PAID", "MUTUELLE_VERIFIED", "PENDING_AT_CLINIC"
    val paymentMethod: String, // "MTN_MOMO", "AIRTEL_MONEY", "MUTUELLE_CBHI", "RAMA", "CASH"
    val paymentReference: String,
    val nationalId: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "medical_records")
data class PatientMedicalRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val patientNationalId: String,
    val patientName: String,
    val bloodType: String,
    val allergies: String, // e.g., "Penicillin, Latex, Local Anesthetics"
    val chronicConditions: String, // e.g., "Hypertension, Diabetes Type 2"
    val bleedingDisorders: Boolean,
    val dentalNotes: String,
    val teethStatusJson: String, // simple tooth status map
    val lastCleaningDate: String,
    val emergencyContact: String,
    val emergencyPhone: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "screenings")
data class AIScreeningRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String,
    val chiefComplaint: String,
    val painScore: Int, // 1 - 10
    val durationDays: Int,
    val hasBleeding: Boolean,
    val hasSwelling: Boolean,
    val sensitivityToColdHot: Boolean,
    val difficultyChewing: Boolean,
    val riskLevel: String, // "LOW", "MODERATE", "HIGH", "URGENT"
    val probableCondition: String,
    val aiRecommendation: String,
    val shadowModeTriageNotes: String,
    val triageVerifiedByDentist: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class CPDModule(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val points: Int,
    val durationMinutes: Int,
    val lessons: Int,
    val isCompleted: Boolean = false,
    val quizPassed: Boolean = false
)

data class OutreachCampaign(
    val id: String,
    val title: String,
    val location: String,
    val district: String,
    val date: String,
    val targetGroup: String, // "Primary Schools", "Tea Factory Workers", "Rural Community"
    val expectedBeneficiaries: Int,
    val status: String // "Upcoming", "Active Today", "Completed"
)

data class NavigationRoute(
    val origin: GeoPoint,
    val destination: GeoPoint,
    val totalDistanceKm: Float,
    val estimatedMinutes: Int,
    val steps: List<RouteStep>
)

data class RouteStep(
    val instruction: String,
    val distanceMeters: Int,
    val streetName: String
)
