package org.pk.klient;

import static org.pk.util.StaleWartosci.NUMER_PORTU;
import static org.pk.util.StaleWartosci.TYTUL_APKI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

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
		@SuppressWarnings("unused")
		ExecutorService wykonawcaPolaczenia = null;

    	try {
    		serwer = new Socket("localhost",NUMER_PORTU);
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
			stage.setTitle(TYTUL_APKI);
			stage.show();
			stage.setOnCloseRequest((zdarzenie)->ConnectionBox.getInstance().zamknijPolaczenia());
		} catch (IOException wyjatekIO) {
			wyjatekIO.printStackTrace();
		}

    }

    public static void main(String[] args) {
        launch();
    }

}