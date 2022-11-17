package org.pk.klient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import org.pk.entity.Klient;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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
    	
        var label = new Label("Hello, JavaFX ");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}