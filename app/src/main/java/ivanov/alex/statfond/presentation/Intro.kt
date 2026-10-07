package ivanov.alex.statfond.presentation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LogoLoadingScreen() {
    // --- КОНФИГУРАЦИЯ ЭКРАНА (УПРАВЛЯЮЩИЕ ПЕРЕМЕННЫЕ) ---
    val leftWord = "Стат"                   // Текст слева
    val rightWord = "фонд"                 // Текст справа (будет вращаться посимвольно)
    val slideDuration = 750                // Длительность выезда текста (в мс)
    val rotationDuration = 600             // Длительность переворота одной буквы (в мс)
    val waveDelayStep = 112L               // Интервал между запуском переворота следующей буквы (в мс)

    // ЦВЕТОВАЯ ПАЛИТРА

    val backgroundColor = Color.White // Цвет заднего фона экрана
    val textColor = Color.Gray             // Цвет текста (для обеих частей)

    //val backgroundColor = Color(0xFF121212) // Цвет заднего фона экрана
    //val textColor = Color.White             // Цвет текста (для обеих частей)
    // ----------------------------------------------------

    val animationPlayed = remember { mutableStateOf(false) }
    val startLetterRotation = remember { mutableStateOf(false) }
    val density = LocalDensity.current.density

    // Анимация смещения с использованием управляющей переменной скорости
    val slideOffset by animateDpAsState(
        targetValue = if (animationPlayed.value) 0.dp else 200.dp,
        animationSpec = tween(durationMillis = slideDuration, easing = FastOutSlowInEasing),
        label = "slide"
    )

    // Анимация прозрачности синхронно с выездом
    val textAlpha by animateFloatAsState(
        targetValue = if (animationPlayed.value) 1f else 0f,
        animationSpec = tween(durationMillis = slideDuration),
        label = "alpha"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed.value = true
        delay(slideDuration.toLong()) // Ждем ровно столько, сколько длится выезд текста
        startLetterRotation.value = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // ЛЕВАЯ ЧАСТЬ
            Text(
                text = leftWord,
                style = MaterialTheme.typography.headlineLarge,
                color = textColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .offset(x = -slideOffset)
                    .alpha(textAlpha)
            )

            // ПРАВАЯ ЧАСТЬ (посимвольная анимация)
            Row(
                modifier = Modifier
                    .offset(x = slideOffset)
                    .alpha(textAlpha)
            ) {
                rightWord.forEachIndexed { index, letter ->
                    val isRotated = remember { mutableStateOf(false) }
                    val rotationY by animateFloatAsState(
                        targetValue = if (isRotated.value) 360f else 0f,
                        animationSpec = tween(durationMillis = rotationDuration, easing = FastOutSlowInEasing),
                        label = "letterRotation_$index"
                    )

                    LaunchedEffect(key1 = startLetterRotation.value) {
                        if (startLetterRotation.value) {
                            delay(index * waveDelayStep) // Шаг волны переворота
                            isRotated.value = true
                        }
                    }

                    Text(
                        text = letter.toString(),
                        style = MaterialTheme.typography.headlineLarge,
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer(
                            rotationY = rotationY,
                            cameraDistance = 12f * density
                        )
                    )
                }
            }
        }
    }
}