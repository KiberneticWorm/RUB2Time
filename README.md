# RUB2Time
RUB2Time - это Android приложение, которое позволяет студентам Рубцовского Арарно-Промышленного техникума просматривать расписание.

<a href="https://play.google.com/store/apps/details?id=ru.rubt.rubttimetable&hl=ru&gl=US"><img alt="Google Play RUB2Time" src="https://github.com/KiberneticWorm/RUB2Time/blob/main/icons/google_play.png" width="300" /></a>

# История создания
Разработка приложения начилась в мае 2020 года на Java совсем незнакомым с Android разработчиком. И завершилась через месяц после старта.

Основные проблемы:
1. У сервера техникума нету API
2. Вторая проблема вытекает из первой: чтение и парсинг PDF и XLS файлов.
3. Синхронизация с сайтом техникума

В первой версии к сожалению не удалось решить все проблемы.

# Скриншоты

|   |   |   |
|---|---|---|
|![screenshot #1](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot1.jpg) | ![screenshot #2](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot2.jpg) | ![screenshot #3](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot3.jpg)|
|![screenshot #1](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot4.jpg) | ![screenshot #2](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot5.jpg) | ![screenshot #3](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot6.jpg)|
|![screenshot #1](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot7.jpg) | ![screenshot #2](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot8.jpg) | ![screenshot #3](https://github.com/KiberneticWorm/RUB2Time/blob/main/screenshots/screenshot9.jpg)|


# Дополнительные библиотеки и инструменты

Новая версия приложения была полностью переписана на Kotlin. А также были исправлены различные ошибки, связанные
с повторной загрузкой файлов, с отсуствием сети и другие.

Новая версия использует следующие библиотеки:

1. Material версии 1.3.0-alpha03
2. Android GIF Drawable версии 1.2.19 (Для вопроизведения .gif файлов)
3. MuPDF версии 1.16.1 (Для чтения PDF файлов)
4. Jsoup версии 1.13.1 (Для парсинга html страниц)
5. Kotlin corountines версии 1.4.0-M1 
6. Livedata ktx extensions версии 2.2.0-alpha01
7. Room версии 2.2.5
8. Apache POI ooxml версии 3.9 (с новыми версиями возникли проблемы)
9. OkHTTP версии 3.10.0 (Для загрузки файлов)

# Преимущества новой версии над старой

1. Приложение полностью переписано в соотвествии с android architecture components (Viewmodel, livedata, repository)
2. Используется современный материальный дизайн
3. Решены различные технические проблемы
4. Приложение переписано на новый язык Kotlin
