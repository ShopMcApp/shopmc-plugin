# Itemszop Plugin 💸

Plugin stworzony dla projektu https://github.com/michaljaz/itemszop, plugin synchronizuje polecenia za pomocą WebSocketu, tworzy on również kolejkę poleceń w bazie, w przypadku braku połączenia plugin <-> sklep polecenia są przechowywane w bazie, do momentu, aż łączność z serwerem nie zostanie przywrócona.

### Konfiguracja pluginu:
Zdobądź wartość klucza wygenerowaną w edycji serwera w Twoim sklepie stworzonym z https://github.com/michaljaz/itemszop.

Zmień wartość `KEY` w pliku config.yml na wygenerowany klucz.

### Przedwczesne wersje deweloperskie:
Development builds (Tylko te oznaczone jako Pre-release): https://github.com/michaljaz/itemszop-plugin/releases

### Aby plugin działał, musisz posiadać: ⚠️
* Kompatybilny silnik
* Sklep Itemszop

### Kompatybilne silniki: 🚚
* PaperMC na wersji 1.8-1.19.2 lub jego forki takie jak Purpur czy Pufferfish (Wersje ItemszopSpigot-xx.jar)
* Velocity w wersji 3.1 (Wersje ItemszopVelocity-xx.jar)

### Dostępne polecenia: ⌨️
* W tej chwili działają tylko na wersji Spigot.

| Polecenie     | Uprawnienie                 | Opis |
| ------------- |:-------------------:| -----:|
| itemszop    | default | Wyświetla informację o pluginie |
| itemszop reload   | itemszop.reload      |  Przeładowuje konfigurację pluginu |
| itemszop reconnect   | itemszop.reconnect       |  Ponawia połączenie z bazą |
| itemszop test   | itemszop.test       |  Testuje połączenie z bazą firebase |

### Użyte biblioteki:

* https://github.com/PaperMC/Paper
* https://github.com/KyoriPowered/adventure
* https://github.com/TooTallNate/Java-WebSocket
* https://github.com/Elytrium/ElytriumJavaCommons
