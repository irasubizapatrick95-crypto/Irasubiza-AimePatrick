package com.example.data.repository

import com.example.data.local.DentalDao
import com.example.data.local.SampleData
import com.example.data.model.AIScreeningRecord
import com.example.data.model.Appointment
import com.example.data.model.CPDModule
import com.example.data.model.DentalClinic
import com.example.data.model.DentalService
import com.example.data.model.Dentist
import com.example.data.model.OutreachCampaign
import com.example.data.model.PatientMedicalRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class DentalRepository(private val dao: DentalDao) {

    val appointments: Flow<List<Appointment>> = dao.getAllAppointments()
    val patientRecord: Flow<PatientMedicalRecord?> = dao.getPatientRecord()
    val screenings: Flow<List<AIScreeningRecord>> = dao.getAllScreenings()

    suspend fun ensureInitialData() {
        val existingRecord = dao.getPatientRecord().firstOrNull()
        if (existingRecord == null) {
            dao.insertOrUpdatePatientRecord(SampleData.sampleInitialMedicalRecord)
        }
        val existingAppointments = dao.getAllAppointments().firstOrNull()
        if (existingAppointments.isNullOrEmpty()) {
            dao.insertAppointment(
                Appointment(
                    patientName = "Patrick Irasubiza",
                    patientPhone = "+250 788 123 456",
                    clinicId = "c_kalahari_central",
                    clinicName = "Kalahari Dental Care Central",
                    isMobileVan = false,
                    dentistId = "d1",
                    dentistName = "Dr. Aimé Patrick Irasubiza",
                    serviceTitle = "Comprehensive Oral Exam & Screening",
                    servicePriceRwf = 8000,
                    date = "Oct 10, 2026",
                    timeSlot = "10:30 AM",
                    isEmergency = false,
                    status = "CONFIRMED",
                    symptomsNotes = "Routine bi-annual checkup & dental cleaning check.",
                    paymentStatus = "MUTUELLE_VERIFIED",
                    paymentMethod = "MUTUELLE_CBHI",
                    paymentReference = "RSSB-883921-RW",
                    nationalId = "1 1996 8 0045231 1 82"
                )
            )
        }
    }

    fun getAllClinics(): List<DentalClinic> = SampleData.sampleClinics

    fun getClinicById(id: String): DentalClinic? =
        SampleData.sampleClinics.find { it.id == id }

    fun getAllDentists(): List<Dentist> = SampleData.sampleDentists

    fun getAllServices(): List<DentalService> = SampleData.sampleServices

    fun getAllCPDModules(): List<CPDModule> = SampleData.sampleCPDModules

    fun getAllOutreachCampaigns(): List<OutreachCampaign> = SampleData.sampleOutreachCampaigns

    suspend fun saveAppointment(appointment: Appointment): Long {
        return dao.insertAppointment(appointment)
    }

    suspend fun updateAppointmentStatus(id: Long, status: String) {
        dao.updateAppointmentStatus(id, status)
    }

    suspend fun cancelAppointment(id: Long) {
        dao.updateAppointmentStatus(id, "CANCELLED")
    }

    suspend fun savePatientRecord(record: PatientMedicalRecord) {
        dao.insertOrUpdatePatientRecord(record)
    }

    suspend fun saveScreening(record: AIScreeningRecord): Long {
        return dao.insertScreening(record)
    }

    suspend fun verifyScreeningByDentist(id: Long) {
        dao.verifyScreeningByDentist(id)
    }
}
