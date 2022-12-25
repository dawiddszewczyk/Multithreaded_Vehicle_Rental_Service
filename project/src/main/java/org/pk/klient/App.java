package org.pk.klient;

import static org.pk.util.StaleWartosci.NUMER_PORTU;
import static org.pk.util.StaleWartosci.TYTUL_APKI;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javafx.fxml.FXMLLoader;
import org.pk.entity.Klient;
import org.pk.klient.util.ConnectionBox;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Klasa z metodą main (klient) połączona z javafx. Wywołanie maina z tej klasy spowoduje
 * włączenie instancji klienta. W IDE należy uruchamiać z wykorzystaniem maven'a/javafx.
 * Można uruchamiać wielokrotnie
 */
public class App extends Application {

	/**
	 * Metoda inicjująca javafx. Łączy się ona z serwerem i ukazuje pierwszą scenę logowania.
	 */
    @Override
    public void start(Stage stage) {

    	Socket serwer = null;
		ObjectOutputStream doSerwera = null;
		ObjectInputStream odSerwera= null;

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