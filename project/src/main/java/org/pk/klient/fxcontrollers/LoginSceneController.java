package org.pk.klient.fxcontrollers;


import java.io.IOException;


import org.pk.entity.Klient;
import org.pk.klient.util.ConnectionBox;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import static org.pk.util.StaleWartosci.*;

/**
 * Klasa będąca łącznikiem między interfejsem javafx (tutaj: AppView.fxml -
 * okno z logowaniem/startowe) a programem
 */
public class LoginSceneController {	
	
	private Stage stage;
	private Scene scene;
	private Parent kontener;
	
	@FXML
	private Label regInfoLabel;
	@FXML
	private TextField emailField;
	@FXML
	private TextField passwdField;
	@FXML
	private Label emailLabel;
	@FXML
	private Label passwdLabel;
    @FXML
    private Button wyjscieButton;
    
	/**
	 * Metoda oddelegowywująca do rejestracji
	 * @param zdarzenie przyjmuje obiekt klasy ActionEvent zawierający informacje o akcjach użytkownika
	 */
	public void przejdzDoRejestracji(ActionEvent zdarzenie){
		try {
			kontener = FXMLLoader.load(getClass().getResource(REGISTER_VIEW_XML));
			stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
			scene = new Scene(kontener);
			stage.setScene(scene);
			stage.show();
		} catch (Exception wyjatek) {
			System.out.println("Wyjatek podczas przejscia do rejestracji");
		}
	}
	/**
	 * Metoda służąca do wyczyszczenia pól z emailem i hasłem
	 */
	public void wyczyscFieldy() {
		emailField.clear();
		passwdField.clear();
	}
	/**
	 * Metoda służąca do wyczyszczenia labeli (pól informacyjnych) obok inputów emaila i hasła
	 */
	public void wyczyscLabele() {
		emailLabel.setText(null);
		passwdLabel.setText(null);
	}
	/**
	 * Metoda służąca do wyświetlenia wiadomości po udanej rejestracji
	 * @param wiadomosc
	 */
	public void udanaRejestracja(String wiadomosc){
		regInfoLabel.setText(wiadomosc);
	}
	
	/**
	 * Metoda, która obsługuje proces logowania. Dokonuje ona walidacji danych wprowadzonych przez użytkownika, i przeprowadza
	 * komunikację z serwerem w tym celu
	 * @param zdarzenie przyjmuje obiekt klasy ActionEvent zawierający informacje o akcjach użytkownika
	 */
	@FXML
	public void logowanie(ActionEvent zdarzenie) {
		
		Runnable watek = ()->{
			// Wstepny clear labelow w razie N-tej proby logowania
			Platform.runLater(()->wyczyscLabele());
			// Walidacja czy pola puste
			if(emailField.getText().isEmpty()) {
				Platform.runLater(()->{
					emailLabel.setText("Pole z emailem jest puste!");
				});
				return;
			}
			if(passwdField.getText().isEmpty()) {
				Platform.runLater(()->{
					passwdLabel.setText("Pole z hasłem jest puste!");
				});
				return;
			}
			// Pobranie uzytkownika z bazy
			try {
				ConnectionBox.getInstance().getDoSerwera().writeObject("logowanie()");
				ConnectionBox.getInstance().getDoSerwera().writeObject(emailField.getText());
				ConnectionBox.getInstance().getDoSerwera().writeObject(passwdField.getText());
				ConnectionBox.getInstance().getDoSerwera().flush();
			}catch(Exception wyjatek) {
				wyjatek.printStackTrace();
			}
			
			// Walidacja pobranego klienta
			Klient klientZSerwera = null;
			try {
				klientZSerwera = (Klient)ConnectionBox.getInstance().getOdSerwera().readObject();
			}catch(Exception wyjatek) {
				wyjatek.printStackTrace();
			}
			
			if(klientZSerwera==null) {
				Platform.runLater(()->{
					regInfoLabel.setText("Zly email lub haslo");
					wyczyscFieldy();
				});
				return;
			}
			
			// Ustawienie w connectionBox idKlienta (jezeli logowanie powiodlo sie)

			ConnectionBox.getInstance().setKlientIIdKlienta(klientZSerwera.getId(),klientZSerwera);
			
			Platform.runLater(()->regInfoLabel.setText("Pomyslnie zalogowano"));

			// inicjalizacja listy z dostepnymi pojazdami
			FXMLLoader loader = new FXMLLoader(getClass().getResource(LIST_VIEW_XML));
			Platform.runLater(()->{
				try {
					kontener = loader.load();
				} catch (IOException wyjatekIo) {
					wyjatekIo.printStackTrace();
				}
				stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
				scene = new Scene(kontener);
				stage.setScene(scene);

				ListController listController = loader.getController();

				listController.pobierzListe(zdarzenie);

				stage.show();
			});
		};
		ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
	}
	
	/**
	 * Metoda służąca do obsługi procesu zamknięcia aplikacji, po wciśnięciu przycisku wyjścia z ekranu logowania
	 * @param zdarzenie przyjmuje obiekt klasy ActionEvent zawierający informacje o akcjach użytkownika
	 */
    @FXML
    public void wyjscieLogowanie(ActionEvent zdarzenie) {
    	
		Alert alert = new Alert(AlertType.CONFIRMATION, "Czy na pewno chcesz wyjść z aplikacji?",
				ButtonType.OK, ButtonType.CANCEL);
		alert.setTitle("Potwierdzenie");
		alert.setHeaderText("Potwierdz wyjście");
		
		((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Potwierdz");
		((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
		alert.showAndWait();
		
		if(alert.getResult() == ButtonType.CANCEL)
			return;
    	
    	ConnectionBox.getInstance().zamknijPolaczenia();
        Stage stage = (Stage) wyjscieButton.getScene().getWindow();
        stage.close();
        
    }
}
