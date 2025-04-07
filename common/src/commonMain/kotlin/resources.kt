package site.neotrend

import androidx.compose.ui.graphics.Color

object R {
    object colors {
        val activeStarColor: Color = Color(0xff3EDC6D)
        val inactiveStarColor: Color = Color(0xffDBDADF)
    }
    object strings {
        val MONEY_FREEZE: (author: Author) -> String get() = { "На вашем балансе заморожено 120 рублей, до момента одобрения вами обзора, присланного блогером **@${it.authorDto.name}**. Блогеру отправлен запрос." }
        const val YOUR_MESSAGE = "ВАШЕ СООБЩЕНИЕ"
        val YOUR_MESSAGE_SENT: (author: Author) -> String get() = {  "Ваше сообщение **@${it.authorDto.name}** отправлено." }
        const val MAKE_ORDER_TO_BLOGGER = "Заказать обзор у блогера"
        const val REVIEW_PRICE: String = "Стоимость обзора"
        const val RATING: String = "Рейтинг"
        const val BLOGGER: String = "Блогер"
        const val PAY: String = "Оплатить"
        const val BLOGGER_RATING: String = "Рейтинг блогера"
        const val SEND_MESSAGE: String = "Отправить сообщение"
        const val PLEASE_SEND_YOUR_MESSAGE: String = "Отправьте сообщение"
        const val GOT_IT: String = "Понятно"
        const val GREAT: String = "Отлично!"
        const val WELCOME: String = "Доброго времени суток!\nХочу заказать у вас обзор на *(напишите название и подробности мероприятия или оставьте ссылку на карточку мероприятия)*"
        const val WELCOME_FOCUSED: String = "Доброго времени суток!\nХочу заказать у вас обзор на"
    }
}
