package org.pk.klient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


import javafx.fxml.FXMLLoader;
import org.pk.entity.Klient;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {

    	Socket serwer = null;
		ObjectOutputStream doSerwera = null;
		ObjectInputStream odSerwera= null;
    	try {
    		serwer = new Socket("localhost",40000);
			doSerwera = new ObjectOutputStream(serwer.getOutputStream());
			odSerwera = new ObjectInputStream(serwer.getInputStream());
			String polecenie = "stworzKlienta()";
			if(polecenie.equals("stworzKlienta()")) {
				System.out.println("Wysylam polecenie do serwera");
				doSerwera.writeObject(polecenie);
				doSerwera.writeObject(new Klient("Testowy", "Klient", "doSerwera","haslopotezne"));
				doSerwera.flush();
			}
    	}catch (Exception wyjatek) {
			System.out.println("Wyjatek od strony klienta!");
			wyjatek.printStackTrace();
		}

		try {
			Parent root = FXMLLoader.load(getClass().getResource("/fxml_files/AppView.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("App");
			stage.show();


		} catch (IOException e) {
			e.printStackTrace();
		}

    }

    public static void main(String[] args) {
        launch();
    }

}