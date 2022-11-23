package org.pk.klient.fxcontrollers;

import static org.pk.util.StaleWartosci.bcrypt;

import java.io.EOFException;
import java.io.IOException;

import org.pk.entity.Klient;
import org.pk.klient.util.ConnectionBox;
import org.pk.util.StaleWartosci;

import com.password4j.BcryptFunction;
import com.password4j.Password;
import com.password4j.types.Bcrypt;

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
	
	public void cofnijDoLogowania(ActionEvent zdarzenie) throws IOException {
		kontener = FXMLLoader.load(getClass().getResource("/fxml_files/AppView.fxml"));
		stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
		scene = new Scene(kontener);
		stage.setScene(scene);
		stage.show();
	}
	
	public void wyczyscLabele() {
		nameField.clear();
		surnameField.clear();
		emailField.clear();
		passwdField.clear();
		cPasswdField.clear();
	}
	
	public void wykonajRejestracje(ActionEvent zdarzenie) throws IOException, ClassNotFoundException, InterruptedException {
		Runnable watek = ()->{
		// Podstawowa walidacja
			Platform.runLater(()->{
				if(!passwdField.getText().equals(cPasswdField.getText())) {
					infoLabel.setText("Password fields must match!");
					wyczyscLabele();
					return;
				}
				if(nameField.getText().isEmpty() || surnameField.getText().isEmpty() || emailField.getText().isEmpty()
						|| passwdField.getText().isEmpty() || cPasswdField.getText().isEmpty()) {
					infoLabel.setText("All fields must be filled!");
					wyczyscLabele();
					return;
				}
				if(!emailField.getText().contains("@") || !emailField.getText().contains(".")) {
					infoLabel.setText("Email must be valid!");
					wyczyscLabele();
					return;
				}
			});
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
				System.out.println("Wyjatek podczas czytania emailu z bazy danych! - Register");
				wyjatek.printStackTrace();
			}
			
			if(emailZSerwera.equals(emailField.getText())) {
				Platform.runLater(()->infoLabel.setText("This email is already taken!"));
				wyczyscLabele();
				return;
			}
			// Szyfrowanie zewnetrzna biblioteka
			String haslo = Password.hash(passwdField.getText()).with(bcrypt).getResult();
			
			/*
			System.out.println("TEST BCRYPT: "+haslo);
			System.out.println("CZY ODSZYFROWANIE SIE ZGADZA: " + Password.check(passwdField.getText(), haslo).with(bcrypt));
			 */
			// Operacja w bazie danych poprzez serwer
			Klient nowyKlient = new Klient(nameField.getText(),surnameField.getText(),emailField.getText(),haslo);
			try {
				ConnectionBox.getInstance().getDoSerwera().writeObject("stworzKlienta()");
				ConnectionBox.getInstance().getDoSerwera().writeObject(nowyKlient);
				ConnectionBox.getInstance().getDoSerwera().flush();
			}catch(IOException wyjatekIO) {
				wyjatekIO.printStackTrace();
			}
			Platform.runLater(()->{
				// Stworzenie konta zakonczone sukcesem, przejscie na scene logowania i zaladowanie komunikatu
				FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml_files/AppView.fxml"));
				try {
					kontener = loader.load();
				} catch (IOException wyjatekIO) {
					wyjatekIO.printStackTrace();
				}
				LoginSceneController menuLogowania = loader.getController();
				menuLogowania.udanaRejestracja("Registration successful!");
				
				stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
				scene = new Scene(kontener);
				stage.setScene(scene);
				stage.show();
			});
		};
		ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
	}
	
}
