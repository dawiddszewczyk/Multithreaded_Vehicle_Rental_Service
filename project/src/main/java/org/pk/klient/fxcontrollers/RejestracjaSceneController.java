package org.pk.klient.fxcontrollers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RejestracjaSceneController {
	
	private Stage stage;
	private Scene scene;
	private Parent kontener;
	
	public void cofnijDoLogowania(ActionEvent zdarzenie) throws IOException {
		kontener = FXMLLoader.load(getClass().getResource("/fxml_files/AppView.fxml"));
		stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
		scene = new Scene(kontener);
		stage.setScene(scene);
		stage.show();
	}
}
