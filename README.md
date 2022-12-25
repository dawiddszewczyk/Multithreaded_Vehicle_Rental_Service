# Wstęp
Projekt uproszczonej wypożyczalni pojazdów zrealizowany z wykorzystaniem JDK 18.0.1, JavaFX, CSS, Hibernate (+ MySQL) oraz multithreadingu (architektura klient-serwer).
# Jak użyć?
Projekt należy pobrać i zaimportować do IDE z wykorzystaniem Mavena. Dodatkowo, należy ustanowić połączenie z serwerem bazodanowym MySQL (projekt był tworzony, z użyciem narzędzi Xampp oraz MYSQLWorkbench Community Edition). Po ustanowieniu połączenia, należy zaimportować bazę danych z wykorzystaniem plików **.sql** dołączonych do projektu. Przygotowane są dwie wersje, dla Xampp oraz MYSQLWorkbench. Pojazdy do użytku w aplikacji, należy dodać z poziomu bazy danych. W folderze z plikami .sql dołączony został plik **.txt** z komendą, do utworzenia trzech wstępnych pojazdów.
To już prawie koniec konfiguracji bazy danych. Na sam koniec należy zedytować w projekcie plik **hibernate.cfg.xml** w katalogu src/main/resources/hibernate_configs. Do edycji są pola "connection.url", "connection.username" oraz "connection.password".

![image](https://user-images.githubusercontent.com/106389146/209480738-342b1901-151c-4c42-ac2d-813ca748601b.png)

Należy tutaj podać ścieżkę do własnej instancji serwera bazy danych, oraz login/hasło do połączenia. URL należy zmienić o używany przez nas port (domyślnie jest to 3306, gdy posiadamy tylko jeden program do inicjacji serwera bazodanowego), oraz o nazwę bazy (domyślnie w projekcie "wypozyczalnia").
W przypadku Xampp'a, domyślnymi danymi do połączenia są root i brak hasła (tak jak na zrzucie ekranu).

W celu uruchomienia z poziomu IDE należy najpierw uruchomić serwer, jako zwykłą aplikację Javy (**MainServer.java**) a następnie dowolną ilość razy (na ile pozwolą zasoby) kliencką część (**App.java**) z interfejsem graficznym. App.java należy uruchomić z wykorzystaniem Mavena, dodając odpowiednie parametry w konfiguracji uruchomienia.

### Przykład konfiguracji uruchomienia dla Eclipse IDE for Enterprise Java and Web Developers:
![image](https://user-images.githubusercontent.com/106389146/209480588-8b104d1a-99a2-42d8-83c8-e321c2ba2f68.png)

Na zrzucie ekranu widoczne są parametry w polu "Goals": **clean javafx:run**

# Działanie
### Logowanie/ekran startowy
![image](https://user-images.githubusercontent.com/106389146/209480920-a8ccc4e4-5383-4261-b014-77c3589f7f80.png)

Po uruchomieniu aplikacji ukazane zostanie to okno. Posiadając już konto istnieje możliwość zalogowania po podaniu poprawnego emaila oraz hasła. W razie podania błędnych danych zostaną wyświetlone komunikaty. Istnieje także opcja wyjścia z aplikacji poprzez użycie przycisku „Wyjście”, lub przejście do rejestracji poprzez użycie przycisku „Rejestracja”.
### Rejestracja
![image](https://user-images.githubusercontent.com/106389146/209480952-be14ad9c-aefb-45b6-b730-c76c5077d69b.png)

W tym oknie istnieje możliwość rejestracji. Użytkownik ma możliwość podania wymaganych danych i dokonania rejestracji po wciśnięciu przycisku „Zarejestruj”, lub cofnięcia się do ekranu logowania po wciśnięciu „Cofnij”. W przypadku podania błędnych danych, użytkownik zostaje poinformowany. Po udanej rejestracji, następuje przekierowanie do okna logowania z komunikatem.
![image](https://user-images.githubusercontent.com/106389146/209480956-54be3b38-5642-4c30-8067-6106f825c7b5.png)

### Po zalogowaniu, lista pojazdów
![image](https://user-images.githubusercontent.com/106389146/209480971-f3322e2b-2c62-486f-8905-aefb85406ec3.png)

Użytkownik otrzymuje listę dostępnych (nieużywanych obecnie pojazdów). W rzeczywistości, pojazdy są oznakowane plakietkami z konkretnym numerem ID. Użytkownik wyszukuje więc konkretną hulajnogę wpisując jej id w pole tekstowe, i wciska przycisk wyszukaj.
Po wykonaniu tej czynności, należy wcisnąć dwukrotnie na rekord hulajnogi, i potwierdzić wypożyczenie.

![image](https://user-images.githubusercontent.com/106389146/209480974-4530d2e8-9067-48c9-923c-96159a137984.png)

Istnieje możliwość odświeżenia listy lub wyjścia z programu klikając odśwież/wyjdź.
Użytkownik ma także możliwość podglądu swojego imienia oraz aktualnego obciążenia wypożyczeniem:

![image](https://user-images.githubusercontent.com/106389146/209480981-32db5064-ca7c-4e2f-a4c4-cb3639809f4f.png)

### Wypożyczenie
Po wybraniu pojazdu oraz potwierdzeniu wypożyczenia, ukazuje się wybrany pojazd wraz z informacjami. Co sekundę wypożyczenia, użytkownik zostanie obciążony konkretną kwotą, oraz stan baterii/zasięgu ulegnie zmianie.

![image](https://user-images.githubusercontent.com/106389146/209480990-b05fed3a-8c95-449e-a0c3-e1a3c44536c5.png)

Wypożyczenie można zakończyć używając przycisku „Skończ” oraz poprzez następujące potwierdzenie zakończenia wypożyczenia. W razie niezidentyfikowanego wyłączenia aplikacji, stan hulajnogi zostanie zaktualizowany oraz użytkownik odpowiednio obciążony.






