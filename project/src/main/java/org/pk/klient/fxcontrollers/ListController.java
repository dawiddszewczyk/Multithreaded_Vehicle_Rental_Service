package org.pk.klient.fxcontrollers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.klient.util.ConnectionBox;
import java.io.IOException;
import java.util.List;

import static org.pk.util.StaleWartosci.SCOOTER_VIEW_XML;

/**
 * Klasa będąca łącznikiem między interfejsem javafx (tutaj: ListView.fxml -
 * okno z listą pojazdów) a programem
 */
public class ListController {
	
    private Stage stage;
    private Scene scene;
    private Parent kontener;
	
    @FXML
    private TableColumn<Pojazd, Integer> tA;

    @FXML
    private TableColumn<Pojazd, String> tB;

    @FXML
    private TableColumn<Pojazd, Double> tC;

    @FXML
    private TableColumn<Pojazd, Double> tD;

    @FXML
    private TableView<Pojazd> tv;
    
    @FXML
    private TextField idField;
    
    @FXML
    private Label infoLabel;
    
    @FXML
    private Label uzytkownikLabel;
    
    @FXML
    private Label zadluzenieLabel;
    
    @FXML
    private Button wyjscieButton;
    
    private List<Pojazd> listaHulajnog;
    private Pojazd wybranyPojazd;
    
    /**
     * Metoda służąca do pobierania listy wszystkich dostępnych pojazdów oraz inicjalizacji
     * statystyk użytkownika (pokazywanie ich w interfejsie). Używana także podczas odświeżania po kliknięciu
     * przycisku "odśwież"
     * @param zdarzenie przyjmuje obiekt klasy ActionEvent zawierający informacje o akcjach użytkownika
     */
    @SuppressWarnings("unchecked")
	@FXML
    public void pobierzListe(ActionEvent zdarzenie) {
    	
    	Runnable watek = () ->{
    		try {
    			
    			Platform.runLater(()->{
    				infoLabel.setText("");
    				uzytkownikLabel.setText("Witaj, " + ConnectionBox.getInstance()
    													.getKlient().getImie());
    				zadluzenieLabel.setText("Twoje zadluzenie: " + ConnectionBox.getInstance()
    															   .getKlient().getZadluzenie()
    															 + " PLN");
    			});
    			
    			ConnectionBox.getInstance().getDoSerwera().flush();
    			ConnectionBox.getInstance().getDoSerwera().reset();
    			ConnectionBox.getInstance().getDoSerwera().writeObject("getListaPojazdow(false)");
    			ConnectionBox.getInstance().getDoSerwera().flush();
    			ConnectionBox.getInstance().getDoSerwera().reset();
    			
    			listaHulajnog = (List<Pojazd>) ConnectionBox.getInstance().getOdSerwera().readObject();
    			tv.getItems().clear();
    		
    			for(Pojazd temp:listaHulajnog){
    				tv.getItems().add(temp);
    			}
    			tv.refresh();
    			
    		}catch (Exception wyjatek) {
				wyjatek.printStackTrace();
			}
    	};
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
    }
    
    /**
     * Metoda służąca do pobierania na listView konkretnego pojazdu z użyciem wyszukania (po ID)
     * @param zdarzenie przyjmuje obiekt klasy ActionEvent zawierający informacje o akcjach użytkownika
     */
    @FXML
	@SuppressWarnings("unchecked")
    public void pobierzListeZId(ActionEvent zdarzenie) {
		Runnable watek = () ->{
    		
    		String id = idField.getText();
    		
    		// usun wszystko co nie jest liczba z inputu
    		if(!id.matches("[0-9]*"))
    			id = id.replaceAll("[^0-9]", "");

    		try {
    			
    			if(id.isBlank()) {
    				tv.getItems().clear();
    				tv.refresh();
    				return;
    			}
    			
    			ConnectionBox.getInstance().getDoSerwera().flush();
    			ConnectionBox.getInstance().getDoSerwera().reset();
    			ConnectionBox.getInstance().getDoSerwera().writeObject("getListaPojazdow(true)");
    			ConnectionBox.getInstance().getDoSerwera().writeObject(Integer.parseInt(id));
    			ConnectionBox.getInstance().getDoSerwera().flush();
    			ConnectionBox.getInstance().getDoSerwera().reset();
    			
    			// czytaj strumien dopoki nie zostanie uzyskany tylko jeden rekord lub brak wyniku
    			do {
    				listaHulajnog = (List<Pojazd>) ConnectionBox.getInstance().getOdSerwera().readObject();
    			}while(listaHulajnog.size()!=1 && !listaHulajnog.isEmpty() && listaHulajnog!=null);
    			
    			if(listaHulajnog.isEmpty() || listaHulajnog==null) {
    				tv.getItems().clear();
    				tv.refresh();
    				return;
    			}else {
        			tv.getItems().clear();
        			for(Pojazd temp:listaHulajnog){
        				tv.getItems().add(temp);
        			}
        			tv.refresh();
    			}
    			
    		}catch(Exception wyjatek) {
    			wyjatek.printStackTrace();
    		}
    		
    	};
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
    }
    
    /**
     * Metoda służąca do wybrania konkretnego pojazdu z listy w celu wypożyczenia. Posiada także pop-up sprawdzający,
     * czy użytkownik jest pewien co do swojego wyboru oraz sprawdzenie, czy ktoś już nie wypożyczył danego pojazdu.
     * @param zdarzenie przyjmuje obiekt klasy MouseEvent zawierający informacje o akcjach użytkownika
     */
    @FXML
    public void wybierzPojazd(MouseEvent zdarzenie) {
    	
    	Runnable watek = () ->{
    		if(zdarzenie.getClickCount() == 2) {
    			if(tv.getSelectionModel().getSelectedItem().getClass()==Pojazd.class){
    				
    				Platform.runLater(()->{
    					
    					// popup w celu potwierdzenia wyboru
    					Alert alert = new Alert(AlertType.CONFIRMATION, "Czy na pewno chcesz wypożyczyć pojazd?",
    							ButtonType.OK, ButtonType.CANCEL);
    					alert.setTitle("Potwierdzenie");
    					alert.setHeaderText("Potwierdz wybor");
    					
    					((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Potwierdz");
    					((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
    					alert.showAndWait();
    					
    					if(alert.getResult() == ButtonType.CANCEL)
    						return;
    					
    					// po zaakceptowaniu wybor pojazdu
        				wybranyPojazd=(Pojazd)tv.getSelectionModel().getSelectedItem();
    					
    					// sprawdzenie czy pojazd nie zostal w miedzyczasie zajety przez innego uzytkownika
        				try {
        					ConnectionBox.getInstance().getDoSerwera().flush();
            				ConnectionBox.getInstance().getDoSerwera().reset();
                			ConnectionBox.getInstance().getDoSerwera().writeObject("sprawdzDostepnosc()");
                			ConnectionBox.getInstance().getDoSerwera().writeObject(wybranyPojazd);
                			ConnectionBox.getInstance().getDoSerwera().flush();
                			ConnectionBox.getInstance().getDoSerwera().reset();
                			
                    		Object potwierdzenie = null;
                    		do {
                    			potwierdzenie = ConnectionBox.getInstance().getOdSerwera().readObject();
                    		}while(!(potwierdzenie instanceof Boolean));
                    		
                    		if(!(Boolean)potwierdzenie) {
                    			infoLabel.setText("Pojazd jest już zajęty, proszę o odświeżenie!");
                    			return;
                    		}
                    		
        				}catch (Exception wyjatek) {
							wyjatek.printStackTrace();
						}
        				
        				// inicjalizacja widoku ze statystykami pojazdu
    					FXMLLoader loader = new FXMLLoader(getClass().getResource(SCOOTER_VIEW_XML));
    				
    					try {
    						kontener = loader.load();
    					}catch (IOException wyjatekIO) {
    						wyjatekIO.printStackTrace();
    					}
    					stage = (Stage) ((Node)zdarzenie.getSource()).getScene().getWindow();
    					scene = new Scene(kontener);
    					stage.setScene(scene);
    				
    					// stworzenie nowego wypozyczenia i dodanie go do watku
    					HulajnogaController scooterController=loader.getController();
    					scooterController.ustawWartosci(wybranyPojazd);
                    	Klient tempKlient=ConnectionBox.getInstance().getKlient();
                    	Wypozyczenie tempWypozyczenie = new Wypozyczenie(tempKlient,wybranyPojazd);
                    	tempKlient.dodajWypozyczenie2S(tempWypozyczenie);
                    	
                    	try {
                    		// inicjalizacja wypozyczenia z baza
                			ConnectionBox.getInstance().getDoSerwera().flush();
                			ConnectionBox.getInstance().getDoSerwera().reset();
                    		ConnectionBox.getInstance().getDoSerwera().writeObject("stworzWypozyczenie()");
                    		ConnectionBox.getInstance().getDoSerwera().writeObject(tempWypozyczenie);
                    		ConnectionBox.getInstance().getDoSerwera().flush();
                    		ConnectionBox.getInstance().getDoSerwera().reset();

                    		Object odbiorZeStrumienia = null;
                    		do {
                    			odbiorZeStrumienia = ConnectionBox.getInstance().getOdSerwera().readObject();
                    			if(odbiorZeStrumienia instanceof Wypozyczenie)
                    			tempWypozyczenie.setId(((Wypozyczenie)odbiorZeStrumienia).getId());
                    		}while(!(odbiorZeStrumienia instanceof Wypozyczenie));
                    		
                    	}catch (IOException | ClassNotFoundException wyjatek) {
                    		wyjatek.printStackTrace();
                    	}
    					try {
    						scooterController.procesWypozyczenia(tempWypozyczenie);
    					}catch(InterruptedException wyjatekPrzerwaniaWatku) {
    						wyjatekPrzerwaniaWatku.printStackTrace(); 
    					}
    					stage.show();
    				});
    			}
    		}
    	};
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
    }
	
    /**
     * Metoda obsługująca proces wyjścia z aplikacji po naciśnięciu przycisku
     * @param zdarzenie przyjmuje obiekt klasy ActionEvent zawierający informacje o akcjach użytkownika
     */
    @FXML
    public void wyjscieList(ActionEvent zdarzenie) {
    	
		Alert alert = new Alert(AlertType.CONFIRMATION, "Czy na pewno chcesz wyjść z aplikacji?",
				ButtonType.OK, ButtonType.CANCEL);
		alert.setTitle("Potwierdzenie");
		alert.setHeaderText("Potwierdz wyjście");
		
		((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Potwierdz");
		((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Anuluj");
		alert.showAndWait();
		
		if(alert.getResult() == ButtonType.CANCEL)
			return;
    	
    	ConnectionBox.getInstance().zamknijPolaczenia();
        Stage stage = (Stage) wyjscieButton.getScene().getWindow();
        stage.close();
    }
}
