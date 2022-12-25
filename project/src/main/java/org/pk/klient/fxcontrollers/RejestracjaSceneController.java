package org.pk.klient.fxcontrollers;

import static org.pk.util.StaleWartosci.APP_VIEW_XML;
import static org.pk.util.StaleWartosci.bcrypt;

import java.io.IOException;

import org.pk.entity.Klient;
import org.pk.klient.util.ConnectionBox;

import com.password4j.Password;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RejestracjaSceneController {
	
	private Stage stage;
	private Scene scene;
	private Parent kontener;
	
	@FXML
	private TextField nameField;
	@FXML
	private TextField surnameField;
	@FXML
	private TextField emailField;
	@FXML
	private TextField passwdField;
	@FXML
	private TextField cPasswdField;
	@FXML
	private Label infoLabel;
	@FXML
	private Label nameLabel;
	@FXML
	private Label surnameLabel;
	@FXML
	private Label emailLabel;
	@FXML
	private Label passwdLabel;
	@FXML
	private Label cPasswdLabel;
	
	public void cofnijDoLogowania(ActionEvent zdarzenie) throws IOException {
		kontener = FXMLLoader.load(getClass().getResource(APP_VIEW_XML));
		stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
		scene = new Scene(kontener);
		stage.setScene(scene);
		stage.show();
	}
	
	public void wyczyscFieldy() {
		nameField.clear();
		surnameField.clear();
		emailField.clear();
		passwdField.clear();
		cPasswdField.clear();
	}
	
	public void wyczyscLabele() {
		infoLabel.setText(null);
		nameLabel.setText(null);
		surnameLabel.setText(null);
		emailLabel.setText(null);
		passwdLabel.setText(null);
		cPasswdLabel.setText(null);
	}
	
	@FXML
	public void wykonajRejestracje(ActionEvent zdarzenie) throws IOException, ClassNotFoundException, InterruptedException {
		Runnable watek = ()->{
			// Wstepny clear labelow w razie N-tej proby rejestracji
			Platform.runLater(()->wyczyscLabele());
			// Podstawowa walidacja
			if(!passwdField.getText().equals(cPasswdField.getText())) {
				Platform.runLater(()->{
					cPasswdLabel.setText("Podane hasła muszą być takie same!");
					wyczyscFieldy();
				});
				return;
			}
			if(nameField.getText().isEmpty() || surnameField.getText().isEmpty() || emailField.getText().isEmpty()
					|| passwdField.getText().isEmpty() || cPasswdField.getText().isEmpty()) {
				Platform.runLater(()->{
					infoLabel.setText("Wszystkie pola muszą być wypełnione!");
					wyczyscFieldy();
				});
				return;
			}
			if(!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
				Platform.runLater(()->{
					emailLabel.setText("Email musi być poprawny i dostępny!");
					wyczyscFieldy();
				});
				return;
			}
			// Walidacja, czy podany adres email istnieje w bazie
			try {
				ConnectionBox.getInstance().getDoSerwera().writeObject("pobierzEmail()");
				ConnectionBox.getInstance().getDoSerwera().writeObject(emailField.getText());
				ConnectionBox.getInstance().getDoSerwera().flush();
			} catch (IOException wyjatekIO) {
				wyjatekIO.printStackTrace();
			}
			
			String emailZSerwera="";
			try {
				emailZSerwera = (String)ConnectionBox.getInstance().getOdSerwera().readObject();
			}catch(Exception wyjatek) {
				wyjatek.printStackTrace();
			}
			
			if(emailZSerwera.equals(emailField.getText())) {
				Platform.runLater(()->{
					emailLabel.setText("Email musi być dostępny!");
					wyczyscFieldy();
				});
				return;
			}
			// Szyfrowanie zewnetrzna biblioteka
			String haslo = Password.hash(passwdField.getText()).with(bcrypt).getResult();
			// Operacja w bazie danych poprzez serwer
			Klient nowyKlient = new Klient(nameField.getText(),surnameField.getText(),emailField.getText(),haslo, 0.0);
			try {
				ConnectionBox.getInstance().getDoSerwera().writeObject("stworzKlienta()");
				ConnectionBox.getInstance().getDoSerwera().writeObject(nowyKlient);
				ConnectionBox.getInstance().getDoSerwera().flush();
			}catch(IOException wyjatekIO) {
				wyjatekIO.printStackTrace();
			}
			Platform.runLater(()->{
				// Stworzenie konta zakonczone sukcesem, przejscie na scene logowania i zaladowanie komunikatu
				FXMLLoader loader = new FXMLLoader(getClass().getResource(APP_VIEW_XML));
				try {
					kontener = loader.load();
				} catch (IOException wyjatekIO) {
					wyjatekIO.printStackTrace();
				}
				LoginSceneController menuLogowania = loader.getController();
				menuLogowania.udanaRejestracja("Rejestracja zakończona sukcesem!");
				
				stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
				scene = new Scene(kontener);
				stage.setScene(scene);
				stage.show();
			});
		};
		ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
	}
	
}
