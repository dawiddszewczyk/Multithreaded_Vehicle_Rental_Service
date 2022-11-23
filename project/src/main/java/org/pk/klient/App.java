package org.pk.klient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.fxml.FXMLLoader;
import org.pk.entity.Klient;
import org.pk.klient.util.ConnectionBox;

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
		ExecutorService wykonawcaPolaczenia = null;
    	try {
    		serwer = new Socket("localhost",40000);
			doSerwera = new ObjectOutputStream(serwer.getOutputStream());
			odSerwera = new ObjectInputStream(serwer.getInputStream());
			ConnectionBox.getInstance(odSerwera,doSerwera,serwer); // instancjonowanie logiki polaczen w formie singletona

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