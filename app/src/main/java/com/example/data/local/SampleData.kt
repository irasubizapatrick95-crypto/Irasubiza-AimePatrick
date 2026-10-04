package com.example.data.local

import com.example.data.model.CPDModule
import com.example.data.model.DentalClinic
import com.example.data.model.DentalService
import com.example.data.model.Dentist
import com.example.data.model.OutreachCampaign
import com.example.data.model.PatientMedicalRecord

object SampleData {

    val sampleDentists = listOf(
        Dentist(
            id = "d1",
            name = "Dr. Aimé Patrick Irasubiza",
            title = "Lead Dental Surgeon & Mobile Care Specialist",
            qualification = "BDS, MDent (Oral Surgery), University of Rwanda",
            specialty = "Oral Surgery & Atraumatic Restorative Treatment",
            experienceYears = 8,
            rating = 4.95f,
            bio = "Founder of Kalahari Dental Care. Dedicated to mobile preventive dentistry, community outreach across rural Rwanda, and digital health triage.",
            availableDays = "Mon, Tue, Wed, Fri, Sat",
            languages = listOf("Kinyarwanda", "English", "Français")
        ),
        Dentist(
            id = "d2",
            name = "Dr. Diane Umutoni",
            title = "Pediatric & Preventive Dentist",
            qualification = "BDS, MSc Pediatric Dentistry",
            specialty = "Pediatric Dentistry & Orthodontics",
            experienceYears = 6,
            rating = 4.88f,
            bio = "Passionate about children's oral health in schools, early fluoride treatment, and anxiety-free gentle dental care.",
            availableDays = "Mon, Wed, Thu, Sat",
            languages = listOf("Kinyarwanda", "English", "Français")
        ),
        Dentist(
            id = "d3",
            name = "Dr. Jean Claude Habimana",
            title = "Senior Dental Officer",
            qualification = "BDS, Diploma in Community Oral Health",
            specialty = "Endodontics, Fillings & Periodontal Care",
            experienceYears = 10,
            rating = 4.82f,
            bio = "Specialist in saving compromised teeth, painless root canals, and advanced scaling & gum rejuvenation.",
            availableDays = "Tue, Thu, Fri, Sun",
            languages = listOf("Kinyarwanda", "English", "Swahili")
        ),
        Dentist(
            id = "d4",
            name = "Dr. Sandrine Mukamana",
            title = "Prosthodontist & Aesthetic Specialist",
            qualification = "BDS, Cert. Aesthetic Dentistry",
            specialty = "Crowns, Bridges, Dentures & Smile Makeovers",
            experienceYears = 7,
            rating = 4.91f,
            bio = "Focused on functional oral rehabilitation, teeth whitening, and durable restorative prosthetics.",
            availableDays = "Mon, Tue, Thu, Fri",
            languages = listOf("Kinyarwanda", "English", "Français")
        )
    )

    val sampleServices = listOf(
        DentalService(
            id = "s_emergency",
            title = "Emergency Toothache Relief",
            titleRw = "Kuvura Uburibwe Bukomeje bw'Iryinyo",
            description = "Immediate evaluation, pain eradication, pulp soothing, or urgent drainage for acute odontogenic distress.",
            category = "Emergency Care",
            durationMinutes = 30,
            priceRwf = 12000,
            insuranceCoveredPercentage = 90,
            isEmergency = true
        ),
        DentalService(
            id = "s_consultation",
            title = "Comprehensive Oral Exam & Screening",
            titleRw = "Isuzuma Ryuzuye ry'Akanwa n'Amenyo",
            description = "Complete clinical examination, periodontal probe, digital shadow-mode screening, and treatment pathway planning.",
            category = "General Dentistry",
            durationMinutes = 25,
            priceRwf = 8000,
            insuranceCoveredPercentage = 95
        ),
        DentalService(
            id = "s_cleaning",
            title = "Deep Scaling & Ultrasonic Cleaning",
            titleRw = "Gukura Umwanda n'Urubobi ku Menyo",
            description = "Ultrasonic tartar removal, subgingival plaque debridement, and fluoride protective polishing.",
            category = "Preventive Care",
            durationMinutes = 40,
            priceRwf = 18000,
            insuranceCoveredPercentage = 85
        ),
        DentalService(
            id = "s_filling",
            title = "Composite Aesthetic Tooth Filling (ART)",
            titleRw = "Gusana Iryinyo Ryatobotse (Composite)",
            description = "Atraumatic restorative treatment with tooth-colored biomimetic composite resin to stop decay.",
            category = "Restorative Dentistry",
            durationMinutes = 45,
            priceRwf = 22000,
            insuranceCoveredPercentage = 90
        ),
        DentalService(
            id = "s_extraction",
            title = "Surgical / Atraumatic Extraction",
            titleRw = "Gukuramo Iryinyo ryangiritse bitababaje",
            description = "Gentle local anesthesia extraction for severely non-restorable, fractured, or impacted wisdom teeth.",
            category = "Oral Surgery",
            durationMinutes = 35,
            priceRwf = 15000,
            insuranceCoveredPercentage = 90
        ),
        DentalService(
            id = "s_rootcanal",
            title = "Painless Root Canal Therapy",
            titleRw = "Kuvura Imizi y'Iryinyo (Endodontics)",
            description = "Micro-endodontic cleaning, nerve disinfection, and hermetic obturation to rescue natural tooth.",
            category = "Endodontics",
            durationMinutes = 60,
            priceRwf = 45000,
            insuranceCoveredPercentage = 80
        ),
        DentalService(
            id = "s_pediatric",
            title = "Kids Dental Checkup & Fluoride Varnish",
            titleRw = "Isuzuma ry'Amenyo y'Abana n'Umuringa",
            description = "Child-friendly dental review, pit & fissure sealants, and cavity-preventing mineralizing varnish.",
            category = "Pediatric Care",
            durationMinutes = 30,
            priceRwf = 10000,
            insuranceCoveredPercentage = 95
        ),
        DentalService(
            id = "s_whitening",
            title = "Clinic Laser Teeth Whitening",
            titleRw = "Kweza Amenyo Akera Neza",
            description = "Medical-grade hydrogen peroxide activation removing stubborn coffee, tea, and tobacco stains safely.",
            category = "Cosmetic Dentistry",
            durationMinutes = 45,
            priceRwf = 55000,
            insuranceCoveredPercentage = 0
        )
    )

    val sampleClinics = listOf(
        DentalClinic(
            id = "c_kalahari_central",
            name = "Kalahari Dental Care Central",
            district = "Nyarugenge",
            province = "Kigali City",
            address = "KN 3 Ave, UTC Commercial Hub, Kigali",
            latitude = -1.9441,
            longitude = 30.0619,
            phoneNumber = "+250 788 123 456",
            emergencyPhone = "+250 735 204 584",
            rating = 4.96f,
            reviewsCount = 384,
            isMobileVan = false,
            openingHours = "Mon - Sat: 08:00 - 19:30 | Sun: 09:00 - 15:00",
            isEmergency24h = true,
            acceptsMutuelle = true,
            assignedDentists = listOf(sampleDentists[0], sampleDentists[1], sampleDentists[2]),
            availableServices = sampleServices
        ),
        DentalClinic(
            id = "c_mobile_van_1",
            name = "AI Mobile Dental Van Unit 1 (Gasabo & Kicukiro)",
            district = "Gasabo",
            province = "Kigali City",
            address = "Stationed today at Kimironko Market Square (Near Bus Terminal)",
            latitude = -1.9362,
            longitude = 30.1257,
            phoneNumber = "+250 795 352 654",
            emergencyPhone = "+250 735 204 584",
            rating = 4.91f,
            reviewsCount = 219,
            isMobileVan = true,
            openingHours = "Mon - Fri: 08:30 - 17:30 (On-site mobile unit)",
            isEmergency24h = false,
            acceptsMutuelle = true,
            currentVanLocation = "Kimironko Market / Inyange Sector",
            vanNextSchedule = "Tomorrow: Remera Stade Amahoro Sector",
            assignedDentists = listOf(sampleDentists[0], sampleDentists[1]),
            availableServices = listOf(sampleServices[0], sampleServices[1], sampleServices[2], sampleServices[3], sampleServices[4], sampleServices[6])
        ),
        DentalClinic(
            id = "c_mobile_van_2",
            name = "AI Mobile Dental Van Unit 2 (Southern Province)",
            district = "Huye (Butare)",
            province = "Southern Province",
            address = "Stationed at Huye District Hospital Outpost / Matyazo",
            latitude = -2.6006,
            longitude = 29.7424,
            phoneNumber = "+250 782 445 566",
            emergencyPhone = "+250 735 204 584",
            rating = 4.87f,
            reviewsCount = 143,
            isMobileVan = true,
            openingHours = "Mon - Sat: 09:00 - 17:00",
            acceptsMutuelle = true,
            isEmergency24h = false,
            currentVanLocation = "Huye Market & Matyazo Health Center",
            vanNextSchedule = "Next: Nyanza Heritage Park Community Grounds",
            assignedDentists = listOf(sampleDentists[2]),
            availableServices = listOf(sampleServices[0], sampleServices[1], sampleServices[2], sampleServices[3], sampleServices[4])
        ),
        DentalClinic(
            id = "c_kalahari_musanze",
            name = "Kalahari Northern Regional Clinic",
            district = "Musanze (Ruhengeri)",
            province = "Northern Province",
            address = "NM 20 St, Opposite Musanze District Council",
            latitude = -1.5002,
            longitude = 29.6349,
            phoneNumber = "+250 781 990 011",
            emergencyPhone = "+250 735 204 584",
            rating = 4.89f,
            reviewsCount = 176,
            isMobileVan = false,
            openingHours = "Mon - Sat: 08:00 - 18:00",
            acceptsMutuelle = true,
            isEmergency24h = true,
            assignedDentists = listOf(sampleDentists[1], sampleDentists[3]),
            availableServices = sampleServices
        ),
        DentalClinic(
            id = "c_kalahari_rubavu",
            name = "Kalahari Lake Kivu Dental Hub",
            district = "Rubavu (Gisenyi)",
            province = "Western Province",
            address = "Avenue de la Coopération, Rubavu Downtown",
            latitude = -1.6883,
            longitude = 29.2558,
            phoneNumber = "+250 783 777 222",
            emergencyPhone = "+250 735 204 584",
            rating = 4.85f,
            reviewsCount = 112,
            isMobileVan = false,
            openingHours = "Mon - Fri: 08:00 - 18:00 | Sat: 09:00 - 14:00",
            acceptsMutuelle = true,
            isEmergency24h = false,
            assignedDentists = listOf(sampleDentists[2], sampleDentists[3]),
            availableServices = sampleServices
        ),
        DentalClinic(
            id = "c_kalahari_rwamagana",
            name = "Kalahari Eastern Community Dental Center",
            district = "Rwamagana",
            province = "Eastern Province",
            address = "RN3 Main Highway, Near Rwamagana Modern Market",
            latitude = -1.9487,
            longitude = 30.4347,
            phoneNumber = "+250 784 112 334",
            emergencyPhone = "+250 735 204 584",
            rating = 4.88f,
            reviewsCount = 98,
            isMobileVan = false,
            acceptsMutuelle = true,
            openingHours = "Mon - Sat: 08:30 - 17:30",
            isEmergency24h = false,
            assignedDentists = listOf(sampleDentists[0], sampleDentists[3]),
            availableServices = sampleServices
        )
    )

    val sampleInitialMedicalRecord = PatientMedicalRecord(
        patientNationalId = "1 1996 8 0045231 1 82",
        patientName = "Patrick Irasubiza",
        bloodType = "O+",
        allergies = "None reported (Non-allergic to Penicillin & Lidocaine)",
        chronicConditions = "None (No hypertension or diabetes)",
        bleedingDisorders = false,
        dentalNotes = "Upper right molar #16 restored with composite filling in 2024. Routine preventative scaling performed. Slight cervical sensitivity on lower anterior incisors.",
        teethStatusJson = """{"18":"healthy","17":"healthy","16":"filled","15":"healthy","14":"healthy","13":"healthy","12":"healthy","11":"healthy","21":"healthy","22":"healthy","23":"healthy","24":"healthy","25":"healthy","26":"healthy","27":"healthy","28":"healthy","48":"healthy","47":"healthy","46":"healthy","45":"healthy","44":"healthy","43":"healthy","42":"healthy","41":"healthy","31":"healthy","32":"healthy","33":"healthy","34":"healthy","35":"healthy","36":"cleaned","37":"healthy","38":"healthy"}""",
        lastCleaningDate = "2026-06-15",
        emergencyContact = "Diane Umutoni (Spouse)",
        emergencyPhone = "+250 788 123 456"
    )

    val sampleCPDModules = listOf(
        CPDModule(
            id = "cpd_1",
            title = "Atraumatic Restorative Treatment (ART) in Mobile Outreach",
            category = "Clinical Technique",
            description = "Evidence-based minimal intervention dentistry using hand instruments and high-viscosity glass ionomer cements for rural Rwandan schools.",
            points = 5,
            durationMinutes = 45,
            lessons = 6,
            isCompleted = true,
            quizPassed = true
        ),
        CPDModule(
            id = "cpd_2",
            title = "Infection Prevention & Sterilization in Field Vans",
            category = "Safety & Quality",
            description = "Sterilization protocols, autoclave cycles in off-grid mobile clinics, solar-assisted sanitization, and biomedical waste disposal under MOH Rwanda standards.",
            points = 4,
            durationMinutes = 35,
            lessons = 4,
            isCompleted = false,
            quizPassed = false
        ),
        CPDModule(
            id = "cpd_3",
            title = "AI Screening Validation & Shadow-Mode Triage Protocol",
            category = "HealthTech & AI",
            description = "How AI diagnostic assistance flags early caries and mucosal abnormalities while maintaining dentist-in-the-loop clinical supervision.",
            points = 6,
            durationMinutes = 50,
            lessons = 7,
            isCompleted = false,
            quizPassed = false
        ),
        CPDModule(
            id = "cpd_4",
            title = "Pediatric Dental Trauma & Dental Emergency Triage",
            category = "Emergency Care",
            description = "Managing avulsions, luxation injuries, severe odontogenic abscesses, and emergency referrals in district community health centers.",
            points = 5,
            durationMinutes = 40,
            lessons = 5,
            isCompleted = false,
            quizPassed = false
        )
    )

    val sampleOutreachCampaigns = listOf(
        OutreachCampaign(
            id = "out_1",
            title = "Kigali Public School Oral Health Screening Campaign",
            location = "Groupe Scolaire Kimironko 1",
            district = "Gasabo, Kigali",
            date = "Oct 12 - 14, 2026",
            targetGroup = "Primary & Secondary Students",
            expectedBeneficiaries = 650,
            status = "Upcoming"
        ),
        OutreachCampaign(
            id = "out_2",
            title = "Northern Province Tea Workers Mobile Dental Clinic",
            location = "Mulindi Tea Plantation Health Post",
            district = "Gicumbi / Northern Province",
            date = "Oct 18 - 20, 2026",
            targetGroup = "Agricultural Workers & Families",
            expectedBeneficiaries = 420,
            status = "Upcoming"
        ),
        OutreachCampaign(
            id = "out_3",
            title = "Southern Rural Elders Tooth Replacement & Screening",
            location = "Matyazo Community Center",
            district = "Huye, Southern Province",
            date = "Oct 05, 2026",
            targetGroup = "Geriatric & Vulnerable Adults",
            expectedBeneficiaries = 300,
            status = "Active Today"
        )
    )
}
