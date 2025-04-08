package site.neotrend

import androidx.compose.ui.graphics.Color

object R {
    object colors {
        val activeStar: Color = Color(0xff3EDC6D)
        val inactiveStar: Color = Color(0xffDBDADF)
    }
    object drawables {
        const val arrowsSvg: String = "arrows.svg"
        const val bookmarkSvg: String = "bookmark.svg"
        const val commentsSvg: String = "comments.svg"
        const val eyeSvg: String = "eye.svg"
        const val grayStarSvg: String = "gray_star.svg"
//        const val greenStarSvg: String = "green_star.svg"
    }
    object strings {
        const val bloggerRating: String = "Рейтинг блогера"
        const val blogger: String = "Блогер"
        const val gotIt: String = "Понятно"
        const val great: String = "Отлично!"
        const val makeOrder = "Заказать обзор у блогера"
        const val openVideo: String = "Открыть видео блогера"
        const val pay: String = "Оплатить"
        const val sendYourMessage: String = "Отправьте сообщение"
        const val rating: String = "Рейтинг"
        const val reviewPrice: String = "Стоимость обзора"
        const val sendMessage: String = "Отправить сообщение"
        const val welcome: String = "Доброго времени суток!\nХочу заказать у вас обзор на *(напишите название и подробности мероприятия или оставьте ссылку на карточку мероприятия)*"
        const val welcomeFocused: String = "Доброго времени суток!\nХочу заказать у вас обзор на"
        const val yourMessage = "ВАШЕ СООБЩЕНИЕ"
        val moneyFrozen: (author: Author) -> String get() = { "На вашем балансе заморожено 120 рублей, до момента одобрения вами обзора, присланного блогером **@${it.authorDto.name}**. Блогеру отправлен запрос." }
        val yourMessageSend: (author: Author) -> String get() = { "Ваше сообщение **@${it.authorDto.name}** отправлено." }
    }
}
