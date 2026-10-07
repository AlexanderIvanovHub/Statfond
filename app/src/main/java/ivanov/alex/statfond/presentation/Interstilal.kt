package ivanov.alex.statfond.presentation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

// Правильный и полный импорт загрузчика для Compose
import com.yandex.mobile.ads.kmp.compose.rememberInterstitialAdLoader

// Общие классы для запросов и ошибок
import com.yandex.mobile.ads.kmp.common.AdRequest
import com.yandex.mobile.ads.kmp.common.AdError
import com.yandex.mobile.ads.kmp.common.ImpressionData

// Оставляем ТОЛЬКО ОДИН импорт для InterstitialAd и его слушателя.
// Согласно документации KMP SDK, они лежат в пакете .interstitial:
import com.yandex.mobile.ads.kmp.interstitial.InterstitialAd
import com.yandex.mobile.ads.kmp.interstitial.InterstitialAdEventListener

import kotlinx.coroutines.launch

@Composable
fun rememberInterstitialAdManager(
    adUnitId: String = "demo-interstitial-yandex"
): InterstitialAdManager {
    val loader = rememberInterstitialAdLoader()
    val scope = rememberCoroutineScope()

    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Suspend-загрузка рекламы через корутины KMP SDK
    val loadAd: () -> Unit = {
        if (!isLoading && interstitialAd == null) {
            isLoading = true
            scope.launch {
                try {
                    // Метод loadAd возвращает объект рекламы напрямую (suspend)
                    interstitialAd = loader.loadAd(AdRequest(adUnitId = adUnitId))
                } catch (e: Exception) {
                    // Обработка ошибки загрузки (AdLoadException)
                    // Частые повторные запросы без ограничений не рекомендуются
                } finally {
                    isLoading = false
                }
            }
        }
    }

    // Предзагрузка при первом старте экрана
    LaunchedEffect(Unit) {
        loadAd()
    }

    return remember(interstitialAd, isLoading) {
        InterstitialAdManager(
            isReady = interstitialAd != null,
            isLoading = isLoading,
            showAd = { onDismissed ->
                val ad = interstitialAd
                if (ad != null) {
                    ad.setAdEventListener(object : InterstitialAdEventListener {
                        override fun onAdShown() {
                            // Реклама открылась на весь экран
                        }

                        override fun onAdFailedToShow(adError: AdError) {
                            interstitialAd = null
                            onDismissed() // Возвращаем пользователя к игре/действию
                            loadAd()      // Пробуем загрузить следующую рекламу в фоне
                        }

                        override fun onAdDismissed() {
                            interstitialAd = null
                            onDismissed() // Пользователь закрыл рекламу — продолжаем логику приложения
                            loadAd()      // Заранее скачиваем следующий баннер
                        }

                        override fun onAdClicked() {
                            // Клик по рекламе
                        }

                        override fun onAdImpression(impressionData: ImpressionData?) {
                            // Показ засчитан в РСЯ
                        }
                    })
                    ad.show()
                } else {
                    onDismissed() // Если реклама не готова, сразу пропускаем пользователя
                }
            }
        )
    }
}

// Удобный UI-контейнер для стейта
class InterstitialAdManager(
    val isReady: Boolean,
    val isLoading: Boolean,
    val showAd: (onDismissed: () -> Unit) -> Unit
)


@Composable
fun MyGameScreen(onActionComplete: () -> Unit) {
    // Инициализируем KMP-менеджер
    val adManager = rememberInterstitialAdManager(adUnitId = "demo-interstitial-yandex")

    Button(
        onClick = {
            if (adManager.isReady) {
                // Если успела скачать — показываем, а по закрытию выполняем действие
                adManager.showAd { onActionComplete() }
            } else {
                // Если не готова — не задерживаем пользователя
                onActionComplete()
            }
        }
    ) {
        Text(if (adManager.isReady) "Показать рекламу" else "Пропустить паузу")
    }
}

@Composable
fun AdTestingScreen() {
    // Просто вызываем ваш экран и передаем пустую лямбду
    MyGameScreen(
        onActionComplete = {
            // Здесь ничего не происходит после закрытия рекламы
        }
    )
}