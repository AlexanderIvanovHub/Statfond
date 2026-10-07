package ivanov.alex.statfond

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.mobile.ads.kmp.YandexAds
import com.yandex.mobile.ads.kmp.banner.Banner
import com.yandex.mobile.ads.kmp.banner.BannerAdSize
import com.yandex.mobile.ads.kmp.banner.BannerEvents
import com.yandex.mobile.ads.kmp.banner.rememberBannerAdState
import com.yandex.mobile.ads.kmp.common.AdRequest

@Composable
fun App1() {
    //Запускаем корутину, key1 = Unit гарантирует, что код выполняется один раз
    LaunchedEffect(Unit) {
        YandexAds.initialize()
    }
    // ...
}

@Composable
fun StickyBannerSlot(
    adUnitId: String = "demo-banner-yandex", // Передаем adUnitId параметром слота
    //width: Dp
) {
    // Получаем текущую ширину экрана устройства в dp
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp

    val bannerState = rememberBannerAdState(
        adSize = BannerAdSize.Sticky(screenWidthDp),
        events = BannerEvents(
            //Лямбда-выражение, которое сработает, когда реклама успешно загрузится. Сюда можно добавить логику (например, аналитику или логирование информации adInfo
            onAdLoaded = { adInfo -> /* ad loaded */ },
            //Срабатывает, если произошла ошибка загрузки (нет сети, не подобралось объявление). Здесь обрабатывают ошибки (например, скрывают слот или пробуют повторить запрос).
            onAdFailedToLoad = { error -> /* handle error */ },
            //Вызывается в момент, когда пользователь кликнул по баннеру.
            onAdClicked = { /* ad clicked */ },
            //Вызывается, когда SDK Яндекса засчитало показ рекламы (произошел фактический просмотр пользователем на экране).
            onImpression = { data -> /* impression tracked */ },
        ),
    )

    // Формируем AdRequest, привязывая adUnitId непосредственно к запросу
    //Создает объект запроса рекламы и «запоминает» его. Ключ adUnitId указывает, что код внутри фигурных скобок сработает повторно только в том случае, если изменится сам идентификатор рекламы. При обычных перерисовках экрана объект переиспользоваться не будет.
    val adRequest = remember(adUnitId) {
        //• Инициализирует сам объект AdRequest, связывая его с конкретным ID рекламного места. Этот объект содержит параметры для таргетинга.
        AdRequest(adUnitId = adUnitId)
        // Если AdRequest не имеет именованного аргумента, используйте: AdRequest(adUnitId)
        // Если вы настраиваете тему/таргетинг: AdRequest(adUnitId = adUnitId, preferredTheme = ...)
    }
//Запускает корутину для выполнения операции загрузки. Ключом выступает adRequest. Это значит: как только сформируется или изменится adRequest (например, при первом старте или смене ID баннера), этот блок выполнится
    LaunchedEffect(key1 = adRequest) {
        //Ключевая строчка: обращается к состоянию баннера и вызывает метод loadAd, передавая туда параметры запроса. Именно эта строчка дает команду SDK начать скачивание медиа-контента рекламы из сети.
        bannerState.loadAd(adRequest) // Метод теперь вызывается без ошибок компиляции
    }

//Специальный @Composable компонент из состава SDK Яндекса, который отвечает за непосредственное отображение баннера на экране Android-устройства.
    Banner(
        //• Связывает UI-элемент с ранее созданным и запущенным состоянием bannerState, чтобы компонент знал, что именно ему нужно визуализировать и какие колбэки вызывать.
        state = bannerState,
        //Задает правила разметки (модификатор) — в данном случае растягивает баннер на всю доступную ширину экрана.
        modifier = Modifier.fillMaxWidth(),
    )
}
