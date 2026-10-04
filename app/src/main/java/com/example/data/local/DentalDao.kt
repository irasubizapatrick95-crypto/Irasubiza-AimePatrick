package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AIScreeningRecord
import com.example.data.model.Appointment
import com.example.data.model.PatientMedicalRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface DentalDao {
    // Appointments
    @Query("SELECT * FROM appointments ORDER BY id DESC")
    fun getAllAppointments(): Flow<List<Appointment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAppointment(appointment: Appointment): Long

    @Update
    suspend fun updateAppointment(appointment: Appointment)

    @Query("DELETE FROM appointments WHERE id = :id")
    suspend fun deleteAppointment(id: Long)

    @Query("UPDATE appointments SET status = :status WHERE id = :id")
    suspend fun updateAppointmentStatus(id: Long, status: String)

    // Patient Medical History
    @Query("SELECT * FROM medical_records LIMIT 1")
    fun getPatientRecord(): Flow<PatientMedicalRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePatientRecord(record: PatientMedicalRecord)

    // AI Screening Records
    @Query("SELECT * FROM screenings ORDER BY timestamp DESC")
    fun getAllScreenings(): Flow<List<AIScreeningRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScreening(screening: AIScreeningRecord): Long

    @Query("UPDATE screenings SET triageVerifiedByDentist = 1 WHERE id = :id")
    suspend fun verifyScreeningByDentist(id: Long)
}
