# BasaltMiner

Неофициальный андроид клиент игры BasaltMiner на твиче у [Заквиеля](https://www.twitch.tv/zakvielchannel).

Сейчас официальная версия BM недоступна, т.к. находится на обновлении. Поэтому сервер заменён на [неофициальный](https://github.com/RuslanUC/BasaltMiner-server) (сейчас исходный код недоступен).

Когда выйдет обновлённая версия BasaltMiner, скорее всего и для неё я сделаю андроид клиент.

Для того, чтобы заменить сервер на свой, нужно заменить значение переменной HOST в файле app/src/main/java/com/rdev/basaltminer/CONFIG.java.

Для того, чтобы начать играть, скачайте [apk файл](https://github.com/RuslanUC/BasaltMiner-android/releases/download/1.0/BasaltMiner.apk), установить его и запустите. После необходимо авторизоваться на твиче. Данные (логин/пароль/куки) никуда не отправляются, только записываются в файл настроек, и больше нигде в коде не используются (кроме получения токена, который необходим для входа в игру). Так же нужно дать приложению доступ к вашему айди (перейдите на [твич Заквиеля](https://www.twitch.tv/zakvielchannel), и нажмите на такой значок ![...](https://raw.githubusercontent.com/RuslanUC/BasaltMiner-android/master/images/permission_icon.png?token=AK3WJPVTNJLNPSP7ZHTGQO3BW5RP2)).