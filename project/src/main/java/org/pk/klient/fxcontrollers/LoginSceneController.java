package org.pk.klient.fxcontrollers;


import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class LoginSceneController {	
	
	private Stage stage;
	private Scene scene;
	private Parent kontener;
	
	@FXML
	private Label regInfoLabel;
	
	public void przejdzDoRejestracji(ActionEvent zdarzenie) throws IOException {
		kontener = FXMLLoader.load(getClass().getResource("/fxml_files/RegisterView.fxml"));
		stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
		scene = new Scene(kontener);
		stage.setScene(scene);
		stage.show();
	}
	
	public void udanaRejestracja(String wiadomosc){
		regInfoLabel.setText(wiadomosc);
	}
	
}
