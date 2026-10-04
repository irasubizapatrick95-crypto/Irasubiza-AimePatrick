package com.example.ui.map

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TurnRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DentalClinic
import com.example.data.model.GeoPoint
import com.example.data.model.NavigationRoute

@Composable
fun RwandaGeographyMap(
    userLocation: GeoPoint,
    clinics: List<DentalClinic>,
    selectedClinic: DentalClinic?,
    onSelectClinic: (DentalClinic) -> Unit,
    activeRoute: NavigationRoute?,
    isNavigating: Boolean,
    onStopNavigation: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Zoom and Pan state
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var panOffset by remember { mutableStateOf(Offset.Zero) }

    // Pulse animation for user GPS & Mobile clinic van
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        zoomScale = (zoomScale * zoomChange).coerceIn(0.8f, 3.5f)
        panOffset += offsetChange
    }

    Box(modifier = modifier.background(Color(0xFFE8F1F2))) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .transformable(transformState)
                .pointerInput(clinics, userLocation, zoomScale, panOffset) {
                    detectTapGestures { tapOffset ->
                        // Check if any clinic was tapped
                        val w = size.width
                        val h = size.height
                        val tappedClinic = clinics.minByOrNull { clinic ->
                            val pt = projectCoord(clinic.latitude, clinic.longitude, w.toFloat(), h.toFloat(), zoomScale, panOffset)
                            val dist = (pt - tapOffset).getDistance()
                            if (dist < 48f) dist else Float.MAX_VALUE
                        }
                        if (tappedClinic != null) {
                            val pt = projectCoord(tappedClinic.latitude, tappedClinic.longitude, w.toFloat(), h.toFloat(), zoomScale, panOffset)
                            if ((pt - tapOffset).getDistance() < 50f) {
                                onSelectClinic(tappedClinic)
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            // 1. Draw Country Geography Base (Land & Hills)
            drawRwandaCountryBase(width, height, zoomScale, panOffset)

            // 2. Draw Lake Kivu & Water Bodies
            drawWaterBodies(width, height, zoomScale, panOffset)

            // 3. Draw Major Road Network
            drawMajorRoads(width, height, zoomScale, panOffset)

            // 4. Draw Major Landmark Cities
            drawCityLandmarks(width, height, zoomScale, panOffset)

            // 5. Draw Active Route if Navigating
            if (activeRoute != null) {
                drawActiveRoute(activeRoute, width, height, zoomScale, panOffset)
            }

            // 6. Draw Clinic Pins
            clinics.forEach { clinic ->
                val isSelected = selectedClinic?.id == clinic.id
                drawClinicPin(
                    clinic = clinic,
                    isSelected = isSelected,
                    width = width,
                    height = height,
                    zoomScale = zoomScale,
                    panOffset = panOffset,
                    pulseRadius = if (clinic.isMobileVan) pulseRadius else 0f,
                    pulseAlpha = if (clinic.isMobileVan) pulseAlpha else 0f
                )
            }

            // 7. Draw User GPS Location Pin
            drawUserGpsPin(
                location = userLocation,
                width = width,
                height = height,
                zoomScale = zoomScale,
                panOffset = panOffset,
                pulseRadius = pulseRadius,
                pulseAlpha = pulseAlpha
            )
        }

        // Navigation Header Overlay if in Active Turn-by-Turn mode
        if (isNavigating && activeRoute != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopCenter),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TurnRight,
                                contentDescription = "Turn Instruction",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = activeRoute.steps.firstOrNull()?.instruction ?: "Heading to destination",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "${activeRoute.totalDistanceKm} km • ~${activeRoute.estimatedMinutes} mins • Real-time GPS",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFCCFBF1)
                        )
                    }
                    IconButton(onClick = onStopNavigation) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Exit Navigation",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Floating Map Controls (Zoom In, Zoom Out, Recenter on User)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp, top = if (isNavigating) 90.dp else 24.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                modifier = Modifier.size(44.dp)
            ) {
                IconButton(onClick = { zoomScale = (zoomScale * 1.3f).coerceAtMost(3.5f) }) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color(0xFF0F766E))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                modifier = Modifier.size(44.dp)
            ) {
                IconButton(onClick = { zoomScale = (zoomScale / 1.3f).coerceAtLeast(0.8f) }) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color(0xFF0F766E))
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                modifier = Modifier.size(44.dp)
            ) {
                IconButton(onClick = {
                    zoomScale = 1.6f
                    panOffset = Offset.Zero
                }) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter GPS", tint = Color(0xFF0284C7))
                }
            }
        }

        // Map Legend / Badge at bottom left
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
            shadowElevation = 4.dp,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFF0F766E), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Fixed Clinic", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(Color(0xFFF59E0B), CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Mobile Van", style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
            }
        }
    }
}

// Coordinate projection helper
private fun projectCoord(
    lat: Double,
    lon: Double,
    width: Float,
    height: Float,
    zoom: Float,
    pan: Offset
): Offset {
    val xNorm = (lon - RwandaMapHelper.MIN_LON) / (RwandaMapHelper.MAX_LON - RwandaMapHelper.MIN_LON)
    // Latitude decreases downwards in screen coordinates
    val yNorm = (RwandaMapHelper.MAX_LAT - lat) / (RwandaMapHelper.MAX_LAT - RwandaMapHelper.MIN_LAT)

    val cx = width * 0.5f
    val cy = height * 0.5f

    val basePx = (xNorm.toFloat() * width * 0.82f) + (width * 0.09f)
    val basePy = (yNorm.toFloat() * height * 0.78f) + (height * 0.11f)

    val zoomedX = cx + (basePx - cx) * zoom + pan.x
    val zoomedY = cy + (basePy - cy) * zoom + pan.y

    return Offset(zoomedX, zoomedY)
}

private fun DrawScope.drawRwandaCountryBase(width: Float, height: Float, zoom: Float, pan: Offset) {
    // Rwanda polygon outline representation
    val borderPoints = listOf(
        projectCoord(-1.05, 30.45, width, height, zoom, pan), // Northern corner Nyagatare
        projectCoord(-1.25, 30.80, width, height, zoom, pan), // Akagera East
        projectCoord(-1.95, 30.85, width, height, zoom, pan), // Eastern border
        projectCoord(-2.40, 30.50, width, height, zoom, pan), // South-East
        projectCoord(-2.82, 29.80, width, height, zoom, pan), // Southern border Butare/Huye
        projectCoord(-2.70, 29.20, width, height, zoom, pan), // South-West Nyungwe
        projectCoord(-2.48, 28.90, width, height, zoom, pan), // Rusizi / Lake Kivu south
        projectCoord(-2.00, 29.25, width, height, zoom, pan), // Karongi Lake Kivu
        projectCoord(-1.68, 29.26, width, height, zoom, pan), // Rubavu Gisenyi
        projectCoord(-1.45, 29.55, width, height, zoom, pan), // Musanze Volcanoes
        projectCoord(-1.20, 29.95, width, height, zoom, pan), // Gicumbi
        projectCoord(-1.05, 30.45, width, height, zoom, pan)
    )

    val path = Path()
    borderPoints.forEachIndexed { index, pt ->
        if (index == 0) path.moveTo(pt.x, pt.y) else path.lineTo(pt.x, pt.y)
    }
    path.close()

    // Landmass fill with lush Rwanda green/amber gradient
    drawPath(
        path = path,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFE2F0D9), Color(0xFFD3E7C9), Color(0xFFCBE3BE)),
            start = Offset(0f, 0f),
            end = Offset(width, height)
        )
    )

    // Border stroke
    drawPath(
        path = path,
        color = Color(0xFF86A778),
        style = Stroke(width = 3.5f * zoom)
    )

    // Province boundary dividers (dashed)
    val kigaliCenter = projectCoord(-1.9441, 30.0619, width, height, zoom, pan)
    val huye = projectCoord(-2.6006, 29.7424, width, height, zoom, pan)
    val musanze = projectCoord(-1.5002, 29.6349, width, height, zoom, pan)
    val rwamagana = projectCoord(-1.9487, 30.4347, width, height, zoom, pan)

    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
    drawLine(
        color = Color(0xFFA5C49A),
        start = kigaliCenter,
        end = huye,
        strokeWidth = 1.8f * zoom,
        pathEffect = dashEffect
    )
    drawLine(
        color = Color(0xFFA5C49A),
        start = kigaliCenter,
        end = musanze,
        strokeWidth = 1.8f * zoom,
        pathEffect = dashEffect
    )
    drawLine(
        color = Color(0xFFA5C49A),
        start = kigaliCenter,
        end = rwamagana,
        strokeWidth = 1.8f * zoom,
        pathEffect = dashEffect
    )
}

private fun DrawScope.drawWaterBodies(width: Float, height: Float, zoom: Float, pan: Offset) {
    // Lake Kivu on Western Border
    val lakeKivuPoints = listOf(
        projectCoord(-1.68, 29.26, width, height, zoom, pan),
        projectCoord(-1.85, 29.24, width, height, zoom, pan),
        projectCoord(-2.00, 29.25, width, height, zoom, pan),
        projectCoord(-2.25, 29.15, width, height, zoom, pan),
        projectCoord(-2.48, 28.90, width, height, zoom, pan),
        projectCoord(-2.48, 28.82, width, height, zoom, pan),
        projectCoord(-1.68, 28.82, width, height, zoom, pan)
    )
    val kivuPath = Path()
    lakeKivuPoints.forEachIndexed { i, pt ->
        if (i == 0) kivuPath.moveTo(pt.x, pt.y) else kivuPath.lineTo(pt.x, pt.y)
    }
    kivuPath.close()

    drawPath(
        path = kivuPath,
        color = Color(0xFFBAE6FD)
    )
    drawPath(
        path = kivuPath,
        color = Color(0xFF7DD3FC),
        style = Stroke(width = 2f)
    )

    // Lake Muhazi in Eastern Province
    val muhaziCenter = projectCoord(-1.88, 30.35, width, height, zoom, pan)
    drawOval(
        color = Color(0xFFBAE6FD),
        topLeft = Offset(muhaziCenter.x - 28f * zoom, muhaziCenter.y - 7f * zoom),
        size = Size(56f * zoom, 14f * zoom)
    )
}

private fun DrawScope.drawMajorRoads(width: Float, height: Float, zoom: Float, pan: Offset) {
    val kigali = projectCoord(-1.9441, 30.0619, width, height, zoom, pan)
    val musanze = projectCoord(-1.5002, 29.6349, width, height, zoom, pan)
    val huye = projectCoord(-2.6006, 29.7424, width, height, zoom, pan)
    val rubavu = projectCoord(-1.6883, 29.2558, width, height, zoom, pan)
    val rwamagana = projectCoord(-1.9487, 30.4347, width, height, zoom, pan)
    val nyagatare = projectCoord(-1.2982, 30.3243, width, height, zoom, pan)

    val roadColor = Color(0xFFE2E8F0)
    val roadStroke = Stroke(width = 4.5f * zoom, cap = StrokeCap.Round)

    // RN4: Kigali - Musanze - Rubavu
    drawLine(roadColor, kigali, musanze, strokeWidth = roadStroke.width, cap = roadStroke.cap)
    drawLine(roadColor, musanze, rubavu, strokeWidth = roadStroke.width, cap = roadStroke.cap)

    // RN1: Kigali - Muhanga - Huye
    drawLine(roadColor, kigali, huye, strokeWidth = roadStroke.width, cap = roadStroke.cap)

    // RN3: Kigali - Rwamagana - Nyagatare
    drawLine(roadColor, kigali, rwamagana, strokeWidth = roadStroke.width, cap = roadStroke.cap)
    drawLine(roadColor, rwamagana, nyagatare, strokeWidth = roadStroke.width, cap = roadStroke.cap)

    // Inner road lines
    val innerStroke = 2.2f * zoom
    val innerColor = Color(0xFFCBD5E1)
    drawLine(innerColor, kigali, musanze, strokeWidth = innerStroke)
    drawLine(innerColor, musanze, rubavu, strokeWidth = innerStroke)
    drawLine(innerColor, kigali, huye, strokeWidth = innerStroke)
    drawLine(innerColor, kigali, rwamagana, strokeWidth = innerStroke)
}

private fun DrawScope.drawCityLandmarks(width: Float, height: Float, zoom: Float, pan: Offset) {
    RwandaMapHelper.RWANDA_LANDMARKS.forEach { city ->
        val pt = projectCoord(city.latitude, city.longitude, width, height, zoom, pan)
        drawCircle(
            color = Color(0xFF475569),
            radius = if (city.name == "Kigali") 6f * zoom else 4f * zoom,
            center = pt
        )
        // City label text
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(51, 65, 85)
            textSize = (if (city.name == "Kigali") 13f else 11f) * zoom
            isFakeBoldText = city.name == "Kigali"
            textAlign = android.graphics.Paint.Align.CENTER
        }
        drawContext.canvas.nativeCanvas.drawText(
            city.name,
            pt.x,
            pt.y - (8f * zoom),
            paint
        )
    }
}

private fun DrawScope.drawActiveRoute(
    route: NavigationRoute,
    width: Float,
    height: Float,
    zoom: Float,
    pan: Offset
) {
    val originPt = projectCoord(route.origin.latitude, route.origin.longitude, width, height, zoom, pan)
    val destPt = projectCoord(route.destination.latitude, route.destination.longitude, width, height, zoom, pan)

    // Create intermediate path curve
    val midX = (originPt.x + destPt.x) * 0.5f + 18f
    val midY = (originPt.y + destPt.y) * 0.5f - 18f

    val routePath = Path().apply {
        moveTo(originPt.x, originPt.y)
        quadraticTo(midX, midY, destPt.x, destPt.y)
    }

    // Shadow outer stroke
    drawPath(
        path = routePath,
        color = Color(0xFF0284C7).copy(alpha = 0.35f),
        style = Stroke(width = 12f * zoom, cap = StrokeCap.Round)
    )

    // Route active gradient line
    drawPath(
        path = routePath,
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFF0284C7), Color(0xFF0D9488)),
            start = originPt,
            end = destPt
        ),
        style = Stroke(width = 6f * zoom, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawClinicPin(
    clinic: DentalClinic,
    isSelected: Boolean,
    width: Float,
    height: Float,
    zoomScale: Float,
    panOffset: Offset,
    pulseRadius: Float,
    pulseAlpha: Float
) {
    val pt = projectCoord(clinic.latitude, clinic.longitude, width, height, zoomScale, panOffset)
    val baseColor = if (clinic.isMobileVan) Color(0xFFF59E0B) else Color(0xFF0F766E)

    // Pulse wave for active mobile van
    if (clinic.isMobileVan && pulseRadius > 0f) {
        drawCircle(
            color = baseColor.copy(alpha = pulseAlpha),
            radius = (14f + pulseRadius) * zoomScale,
            center = pt
        )
    }

    // Selected glow ring
    if (isSelected) {
        drawCircle(
            color = Color(0xFF0284C7),
            radius = 22f * zoomScale,
            center = pt,
            style = Stroke(width = 4f * zoomScale)
        )
    }

    // Outer circle
    drawCircle(
        color = Color.White,
        radius = 16f * zoomScale,
        center = pt
    )

    // Inner circle
    drawCircle(
        color = baseColor,
        radius = 13f * zoomScale,
        center = pt
    )

    // Pin symbol (Cross or Van mark)
    drawCircle(
        color = Color.White,
        radius = 5f * zoomScale,
        center = pt
    )

    // Clinic name label if selected or zoomed
    if (isSelected || zoomScale > 1.4f) {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.rgb(15, 23, 42)
            textSize = 11f * zoomScale
            isFakeBoldText = isSelected
            textAlign = android.graphics.Paint.Align.CENTER
        }
        val label = if (clinic.isMobileVan) "🚐 ${clinic.name.take(20)}" else clinic.name.take(18)
        drawContext.canvas.nativeCanvas.drawText(
            label,
            pt.x,
            pt.y + (26f * zoomScale),
            paint
        )
    }
}

private fun DrawScope.drawUserGpsPin(
    location: GeoPoint,
    width: Float,
    height: Float,
    zoomScale: Float,
    panOffset: Offset,
    pulseRadius: Float,
    pulseAlpha: Float
) {
    val pt = projectCoord(location.latitude, location.longitude, width, height, zoomScale, panOffset)
    val blueGps = Color(0xFF0284C7)

    // Live GPS pulse
    drawCircle(
        color = blueGps.copy(alpha = pulseAlpha),
        radius = (12f + pulseRadius) * zoomScale,
        center = pt
    )

    // Outer white halo
    drawCircle(
        color = Color.White,
        radius = 11f * zoomScale,
        center = pt
    )

    // Solid blue core
    drawCircle(
        color = blueGps,
        radius = 8f * zoomScale,
        center = pt
    )

    // Center white dot
    drawCircle(
        color = Color.White,
        radius = 3f * zoomScale,
        center = pt
    )
}
