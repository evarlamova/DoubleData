# DoubleData
Описание возможных реализаций дополнительных требований:

1) Вынести в отдельный микросервис вычисление хэш-суммы, остальные оперции выполнять отдельно, при необхожимости общаясь с сервисом вычислений по REST. Серверов для вычисления хэш-суммы может быть несколько, нагрузку между ними равномерно распределит балансировщик нагрузки (в архитектуру не заложено).

2) Как вариант будущей архитектуры: с помощью докера поднимаем несколько инстансов приложения, при запросе от клиента nginx 
отправляет на определенный сервер
(можно запустить тестовое приложение командами: 

(sudo) mvn package docker:build

(sudo) docker run -p 8080:8080 -t doubledata).

3) Сохранение в базу данных пользователя и его задач для восстановления данных. Использование персистентной очереди задач, например, Rabbit MQ.
