package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AIImageResult
import com.example.data.ai.GeminiDentalService
import com.example.data.ai.ScreeningAnalysisResult
import com.example.data.local.AppDatabase
import com.example.data.local.SampleData
import com.example.data.model.AIScreeningRecord
import com.example.data.model.Appointment
import com.example.data.model.CPDModule
import com.example.data.model.DentalClinic
import com.example.data.model.DentalService
import com.example.data.model.Dentist
import com.example.data.model.GeoPoint
import com.example.data.model.NavigationRoute
import com.example.data.model.OutreachCampaign
import com.example.data.model.PatientMedicalRecord
import com.example.data.repository.DentalRepository
import com.example.ui.map.RwandaMapHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

enum class AppScreen {
    HOME,
    MAP_LOCATOR,
    APPOINTMENTS,
    BOOKING,
    MEDICAL_HISTORY,
    AI_SCREENING,
    CLINICIAN_WORKSPACE
}

enum class ClinicFilter {
    ALL,
    MOBILE_VANS,
    FIXED_CLINICS,
    MUTUELLE_ACCEPTED
}

enum class AppLanguage {
    EN,
    RW,
    FR
}

class DentalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DentalRepository
    private val geminiService = GeminiDentalService()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = DentalRepository(database.dentalDao())
        viewModelScope.launch {
            repository.ensureInitialData()
        }
    }

    // Active Screen & Navigation
    private val _currentScreen = MutableStateFlow(AppScreen.HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.EN)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }

    // User GPS Location (Default: Kigali Center)
    private val _userLocation = MutableStateFlow(RwandaMapHelper.KIGALI_CENTER)
    val userLocation: StateFlow<GeoPoint> = _userLocation.asStateFlow()

    fun updateUserLocation(lat: Double, lon: Double, name: String = "My Current Location") {
        _userLocation.value = GeoPoint(lat, lon, name)
        // If a clinic is selected, re-calculate route
        _selectedClinic.value?.let { clinic ->
            calculateRouteTo(clinic)
        }
    }

    // Quick Rwanda Location Presets for easy testing
    val locationPresets = listOf(
        GeoPoint(-1.9441, 30.0619, "Kigali Downtown (UTC / KN 3)"),
        GeoPoint(-1.9362, 30.1257, "Kimironko / Remera (Gasabo)"),
        GeoPoint(-2.6006, 29.7424, "Huye (Southern Province)"),
        GeoPoint(-1.5002, 29.6349, "Musanze (Northern Province)"),
        GeoPoint(-1.6883, 29.2558, "Rubavu (Lake Kivu Western)"),
        GeoPoint(-1.9487, 30.4347, "Rwamagana (Eastern Province)")
    )

    // Clinics & Filtering
    val allClinics: List<DentalClinic> = repository.getAllClinics()
    val allDentists: List<Dentist> = repository.getAllDentists()
    val allServices: List<DentalService> = repository.getAllServices()
    val cpdModules: List<CPDModule> = repository.getAllCPDModules()
    val outreachCampaigns: List<OutreachCampaign> = repository.getAllOutreachCampaigns()

    private val _clinicFilter = MutableStateFlow(ClinicFilter.ALL)
    val clinicFilter: StateFlow<ClinicFilter> = _clinicFilter.asStateFlow()

    fun setClinicFilter(filter: ClinicFilter) {
        _clinicFilter.value = filter
    }

    val filteredClinics: List<DentalClinic>
        get() = when (_clinicFilter.value) {
            ClinicFilter.ALL -> allClinics
            ClinicFilter.MOBILE_VANS -> allClinics.filter { it.isMobileVan }
            ClinicFilter.FIXED_CLINICS -> allClinics.filter { !it.isMobileVan }
            ClinicFilter.MUTUELLE_ACCEPTED -> allClinics.filter { it.acceptsMutuelle }
        }

    // Selected Clinic & Navigation Route
    private val _selectedClinic = MutableStateFlow<DentalClinic?>(allClinics.firstOrNull())
    val selectedClinic: StateFlow<DentalClinic?> = _selectedClinic.asStateFlow()

    private val _activeRoute = MutableStateFlow<NavigationRoute?>(null)
    val activeRoute: StateFlow<NavigationRoute?> = _activeRoute.asStateFlow()

    private val _isNavigating = MutableStateFlow(false)
    val isNavigating: StateFlow<Boolean> = _isNavigating.asStateFlow()

    fun selectClinic(clinic: DentalClinic) {
        _selectedClinic.value = clinic
        calculateRouteTo(clinic)
    }

    fun calculateRouteTo(clinic: DentalClinic) {
        val dest = GeoPoint(clinic.latitude, clinic.longitude, clinic.name)
        val route = RwandaMapHelper.buildRoute(_userLocation.value, dest, clinic.name)
        _activeRoute.value = route
    }

    fun startLiveNavigation() {
        if (_activeRoute.value == null && _selectedClinic.value != null) {
            calculateRouteTo(_selectedClinic.value!!)
        }
        _isNavigating.value = true
    }

    fun stopLiveNavigation() {
        _isNavigating.value = false
    }

    // Room DB Flow Sources
    val appointments: StateFlow<List<Appointment>> = repository.appointments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val patientRecord: StateFlow<PatientMedicalRecord?> = repository.patientRecord
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val screenings: StateFlow<List<AIScreeningRecord>> = repository.screenings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Booking State
    private val _bookingClinic = MutableStateFlow<DentalClinic?>(allClinics.firstOrNull())
    val bookingClinic: StateFlow<DentalClinic?> = _bookingClinic.asStateFlow()

    private val _bookingDentist = MutableStateFlow<Dentist?>(allDentists.firstOrNull())
    val bookingDentist: StateFlow<Dentist?> = _bookingDentist.asStateFlow()

    private val _bookingService = MutableStateFlow<DentalService?>(allServices.firstOrNull())
    val bookingService: StateFlow<DentalService?> = _bookingService.asStateFlow()

    private val _bookingDate = MutableStateFlow("Oct 12, 2026")
    val bookingDate: StateFlow<String> = _bookingDate.asStateFlow()

    private val _bookingTimeSlot = MutableStateFlow("10:00 AM")
    val bookingTimeSlot: StateFlow<String> = _bookingTimeSlot.asStateFlow()

    private val _isEmergency = MutableStateFlow(false)
    val isEmergency: StateFlow<Boolean> = _isEmergency.asStateFlow()

    private val _patientName = MutableStateFlow("Patrick Irasubiza")
    val patientName: StateFlow<String> = _patientName.asStateFlow()

    private val _patientPhone = MutableStateFlow("+250 788 123 456")
    val patientPhone: StateFlow<String> = _patientPhone.asStateFlow()

    private val _nationalId = MutableStateFlow("1 1996 8 0045231 1 82")
    val nationalId: StateFlow<String> = _nationalId.asStateFlow()

    private val _symptomsNotes = MutableStateFlow("")
    val symptomsNotes: StateFlow<String> = _symptomsNotes.asStateFlow()

    private val _paymentMethod = MutableStateFlow("MTN_MOMO")
    val paymentMethod: StateFlow<String> = _paymentMethod.asStateFlow()

    // Payment Processing States
    private val _isProcessingPayment = MutableStateFlow(false)
    val isProcessingPayment: StateFlow<Boolean> = _isProcessingPayment.asStateFlow()

    private val _lastBookedAppointment = MutableStateFlow<Appointment?>(null)
    val lastBookedAppointment: StateFlow<Appointment?> = _lastBookedAppointment.asStateFlow()

    fun updateBookingClinic(clinic: DentalClinic) {
        _bookingClinic.value = clinic
        // Set first dentist of clinic if available
        if (clinic.assignedDentists.isNotEmpty()) {
            _bookingDentist.value = clinic.assignedDentists.first()
        }
    }

    fun updateBookingDentist(dentist: Dentist) { _bookingDentist.value = dentist }
    fun updateBookingService(service: DentalService) {
        _bookingService.value = service
        if (service.isEmergency) _isEmergency.value = true
    }
    fun updateBookingDate(date: String) { _bookingDate.value = date }
    fun updateBookingTimeSlot(slot: String) { _bookingTimeSlot.value = slot }
    fun updateIsEmergency(emergency: Boolean) { _isEmergency.value = emergency }
    fun updatePatientName(name: String) { _patientName.value = name }
    fun updatePatientPhone(phone: String) { _patientPhone.value = phone }
    fun updateNationalId(id: String) { _nationalId.value = id }
    fun updateSymptomsNotes(notes: String) { _symptomsNotes.value = notes }
    fun updatePaymentMethod(method: String) { _paymentMethod.value = method }

    fun bookAppointment(onSuccess: (Appointment) -> Unit) {
        viewModelScope.launch {
            _isProcessingPayment.value = true
            // Simulate digital payment gateway roundtrip (MoMo *182# / Mutuelle Verification)
            delay(1200)

            val clinic = _bookingClinic.value ?: allClinics.first()
            val dentist = _bookingDentist.value ?: allDentists.first()
            val service = _bookingService.value ?: allServices.first()

            val refPrefix = when (_paymentMethod.value) {
                "MTN_MOMO" -> "MOMO-RW-"
                "AIRTEL_MONEY" -> "AIRTEL-RW-"
                "MUTUELLE_CBHI" -> "RSSB-MUT-"
                "RAMA" -> "RAMA-MED-"
                else -> "CLINIC-CASH-"
            }
            val paymentRef = refPrefix + (100000..999999).random()

            val paymentStatus = when (_paymentMethod.value) {
                "MUTUELLE_CBHI", "RAMA" -> "MUTUELLE_VERIFIED"
                "CASH" -> "PENDING_AT_CLINIC"
                else -> "PAID"
            }

            val appointment = Appointment(
                patientName = _patientName.value.ifBlank { "Patrick Irasubiza" },
                patientPhone = _patientPhone.value.ifBlank { "+250 788 123 456" },
                clinicId = clinic.id,
                clinicName = clinic.name,
                isMobileVan = clinic.isMobileVan,
                dentistId = dentist.id,
                dentistName = dentist.name,
                serviceTitle = service.title,
                servicePriceRwf = service.priceRwf,
                date = _bookingDate.value,
                timeSlot = _bookingTimeSlot.value,
                isEmergency = _isEmergency.value,
                status = "CONFIRMED",
                symptomsNotes = _symptomsNotes.value.ifBlank { "Scheduled through AI Dental Mobile Clinic platform." },
                paymentStatus = paymentStatus,
                paymentMethod = _paymentMethod.value,
                paymentReference = paymentRef,
                nationalId = _nationalId.value
            )

            val insertedId = repository.saveAppointment(appointment)
            val finalAppointment = appointment.copy(id = insertedId)
            _lastBookedAppointment.value = finalAppointment
            _isProcessingPayment.value = false
            onSuccess(finalAppointment)
        }
    }

    fun cancelAppointment(id: Long) {
        viewModelScope.launch {
            repository.cancelAppointment(id)
        }
    }

    // Medical History update
    fun updateMedicalRecord(
        allergies: String,
        chronicConditions: String,
        dentalNotes: String
    ) {
        viewModelScope.launch {
            val current = patientRecord.value ?: SampleData.sampleInitialMedicalRecord
            val updated = current.copy(
                allergies = allergies,
                chronicConditions = chronicConditions,
                dentalNotes = dentalNotes,
                updatedAt = System.currentTimeMillis()
            )
            repository.savePatientRecord(updated)
        }
    }

    // AI Oral Health Screening State
    private val _isAnalyzingScreening = MutableStateFlow(false)
    val isAnalyzingScreening: StateFlow<Boolean> = _isAnalyzingScreening.asStateFlow()

    private val _lastScreeningResult = MutableStateFlow<ScreeningAnalysisResult?>(null)
    val lastScreeningResult: StateFlow<ScreeningAnalysisResult?> = _lastScreeningResult.asStateFlow()

    fun runAIScreening(
        complaint: String,
        painScore: Int,
        durationDays: Int,
        hasBleeding: Boolean,
        hasSwelling: Boolean,
        hasSensitivity: Boolean,
        hasDifficultyChewing: Boolean
    ) {
        viewModelScope.launch {
            _isAnalyzingScreening.value = true
            val analysis = geminiService.analyzeOralSymptoms(
                complaint = complaint,
                painScore = painScore,
                durationDays = durationDays,
                hasBleeding = hasBleeding,
                hasSwelling = hasSwelling,
                hasSensitivity = hasSensitivity,
                hasDifficultyChewing = hasDifficultyChewing
            )
            _lastScreeningResult.value = analysis

            val dateFormat = SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault())
            val record = AIScreeningRecord(
                date = dateFormat.format(Date()),
                chiefComplaint = complaint.ifBlank { "General oral discomfort & screening check" },
                painScore = painScore,
                durationDays = durationDays,
                hasBleeding = hasBleeding,
                hasSwelling = hasSwelling,
                sensitivityToColdHot = hasSensitivity,
                difficultyChewing = hasDifficultyChewing,
                riskLevel = analysis.riskLevel,
                probableCondition = analysis.probableCondition,
                aiRecommendation = analysis.recommendation,
                shadowModeTriageNotes = "Shadow-Mode Confidence: ${(analysis.shadowModeConfidence * 100).toInt()}% • Protocol: ${analysis.triageProtocol}",
                triageVerifiedByDentist = false
            )
            repository.saveScreening(record)
            _isAnalyzingScreening.value = false
        }
    }

    // AI Image Creation & High-Res Generation State
    private val _isGeneratingImage = MutableStateFlow(false)
    val isGeneratingImage: StateFlow<Boolean> = _isGeneratingImage.asStateFlow()

    private val _generatedImageResult = MutableStateFlow<AIImageResult?>(null)
    val generatedImageResult: StateFlow<AIImageResult?> = _generatedImageResult.asStateFlow()

    /**
     * Create or edit dental images using gemini-3.1-flash-image-preview
     */
    fun createOrEditDentalImage(prompt: String) {
        viewModelScope.launch {
            _isGeneratingImage.value = true
            val result = geminiService.generateOrEditImage(prompt)
            _generatedImageResult.value = result
            _isGeneratingImage.value = false
        }
    }

    /**
     * Generate high-quality dental visualization using gemini-3-pro-image-preview
     * with affordance for user-specified image sizes (1K, 2K, 4K)
     */
    fun generateHighResDentalImage(prompt: String, size: String = "2K") {
        viewModelScope.launch {
            _isGeneratingImage.value = true
            val result = geminiService.generateHighQualityImage(prompt, size)
            _generatedImageResult.value = result
            _isGeneratingImage.value = false
        }
    }

    // Clinician verification of AI screening
    fun verifyScreening(id: Long) {
        viewModelScope.launch {
            repository.verifyScreeningByDentist(id)
        }
    }
}
