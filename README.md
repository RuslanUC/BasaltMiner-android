# BasaltMiner

Неофициальный андроид клиент игры BasaltMiner на твиче у [Заквиеля](https://www.twitch.tv/zakvielchannel).

Сейчас официальная версия BM недоступна, т.к. находится на обновлении. Поэтому сервер заменён на [неофициальный](https://github.com/RuslanUC/BasaltMiner-server).

Когда выйдет обновлённая версия BasaltMiner, скорее всего и для неё я сделаю андроид клиент.

Для того, чтобы заменить сервер на свой, нужно заменить значения переменных [host](https://github.com/RuslanUC/BasaltMiner-android/blob/master/app/src/main/java/com/rdev/basaltminer/CONFIG.java#L4) и [streamer_id](https://github.com/RuslanUC/BasaltMiner-android/blob/master/app/src/main/java/com/rdev/basaltminer/CONFIG.java#L5) в файле [app/src/main/java/com/rdev/basaltminer/CONFIG.java](https://github.com/RuslanUC/BasaltMiner-android/blob/master/app/src/main/java/com/rdev/basaltminer/CONFIG.java).

Для того, чтобы начать играть, скачайте [apk файл](https://github.com/RuslanUC/BasaltMiner-android/releases/download/1.6/BasaltMiner.apk), установите его и запустите. После необходимо авторизоваться на твиче. Данные (логин/пароль/куки) никуда не отправляются, только записываются в файл настроек, и больше нигде в коде не используются (кроме получения токена, который необходим для входа в игру).