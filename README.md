# Itemszop Plugin 💸

Plugin stworzony dla projektu https://github.com/michaljaz/itemszop, plugin synchronizuje polecenia za pomocą WebSocketu. Plugin tworzy kolejkę poleceń w bazie, w przypadku braku połączenia plugin <-> sklep polecenia są przechowywane w bazie, do momentu, aż łączność z serwerem nie zostanie przywrócona.

### Konfiguracja pluginu:
Zdobądź wartość klucza wygenerowaną w edycji serwera w Twoim sklepie stworzonym z https://github.com/michaljaz/itemszop.

Zmień wartość `KEY` w pliku config.yml na wygenerowany klucz.


### Wydania deweloperskie:
Development builds (JDK11 & JDK17): https://github.com/ReferTV/itemszop-plugin-gamesmc/actions

### Wymagania: ⚠️
* Serwer spigot lub jego forki na 1.8-1.19

### Planowane funkcje 🧪🔜
* Działanie na Velocity/BungeeCord

### Znane błędy: ⚠️

* Brak

### Komendy:

| Polecenie     | Uprawnienie                 | Opis |
| ------------- |:-------------------:| -----:|
| itemszop    | default | Wyświetla informację o pluginie |
| itemszop reload   | itemszop.reload      |  Przeładowuje konfigurację pluginu |
| itemszop test   | itemszop.test       |  Testuje połączenie z bazą firebase |

### Użyte biblioteki:

* https://github.com/PaperMC/Paper
* https://github.com/TooTallNate/Java-WebSocket
* https://github.com/Elytrium/ElytriumJavaCommons
