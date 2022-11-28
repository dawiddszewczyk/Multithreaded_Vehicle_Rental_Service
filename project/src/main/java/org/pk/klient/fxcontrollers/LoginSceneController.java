package org.pk.klient.fxcontrollers;


import static org.pk.util.StaleWartosci.REGISTER_VIEW_XML;

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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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
	
	public void przejdzDoRejestracji(ActionEvent zdarzenie) throws IOException {
		kontener = FXMLLoader.load(getClass().getResource(REGISTER_VIEW_XML));
		stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
		scene = new Scene(kontener);
		stage.setScene(scene);
		stage.show();
	}
	public void wyczyscFieldy() {
		emailField.clear();
		passwdField.clear();
	}
	public void wyczyscLabele() {
		emailLabel.setText(null);
		passwdLabel.setText(null);
	}
	public void udanaRejestracja(String wiadomosc){
		regInfoLabel.setText(wiadomosc);
	}
	public void logowanie(ActionEvent zdarzenie) {
		
		Runnable watek = ()->{
			// Wstepny clear labelow w razie N-tej proby logowania
			Platform.runLater(()->wyczyscLabele());
			// Walidacja czy pola puste
			if(emailField.getText().isEmpty()) {
				Platform.runLater(()->{
					emailLabel.setText("Email field is empty!");
				});
				return;
			}
			if(passwdField.getText().isEmpty()) {
				Platform.runLater(()->{
					passwdLabel.setText("Password field is empty!");
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
					regInfoLabel.setText("Wrong email or password");
					wyczyscFieldy();
				});
				return;
			}
			
			// Ustawienie w connectionBox idKlienta (jezeli logowanie powiodlo sie)
			ConnectionBox.getInstance().setIdKlienta(klientZSerwera.getId());
			
			// --------- TO DO ---------
			// do wyrzucenia po zaimplementowaniu przejscia do innej sceny (ten platform run later nizej)
			Platform.runLater(()->regInfoLabel.setText("Login successful"));
			// odkomentuj i wstaw w sciezke swoj fxml
			/*
			Platform.runLater(()->{
				try {
					kontener = FXMLLoader.load(getClass().getResource("/fxml_files/PlikAdrianowy.xml" Lub daj stałą tak jak ja));
				} catch (IOException wyjatekIo) {
					wyjatekIo.printStackTrace();
				}
				stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
				scene = new Scene(kontener);
				stage.setScene(scene);
				stage.show();
			});
		*/
		};
		ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
	}
}
