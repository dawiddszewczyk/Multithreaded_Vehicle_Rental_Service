package org.pk.klient.fxcontrollers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.pk.entity.Klient;
import org.pk.entity.Pojazd;
import org.pk.entity.Wypozyczenie;
import org.pk.klient.util.ConnectionBox;
import java.io.IOException;
import java.util.List;
import static org.pk.util.StaleWartosci.SCOOTER_VIEW_XML;

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
    
    private List<Pojazd> listaHulajnog;
    private Pojazd wybranyPojazd;
    
    @SuppressWarnings("unchecked")
	@FXML
    void pobierzListe(ActionEvent zdarzenie) {
    	
    	Runnable watek = () ->{
    		try {
    			ConnectionBox.getInstance().getDoSerwera().writeObject("getListaPojazdow()");
    			ConnectionBox.getInstance().getDoSerwera().flush();
    			
    			listaHulajnog = (List<Pojazd>) ConnectionBox.getInstance().getOdSerwera().readObject();
    			tv.getItems().clear();
    		
    			for(Pojazd temp:listaHulajnog){
    				tv.getItems().add(temp);
    			}
    		}catch (Exception wyjatek) {
				wyjatek.printStackTrace();
			}
    	};
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
    }
    
    @FXML
    public void wybierzPojazd(MouseEvent zdarzenie) {
    	
    	Runnable watek = () ->{
    		if(zdarzenie.getClickCount() == 2) {
    			if(tv.getSelectionModel().getSelectedItem().getClass()==Pojazd.class){
    				Platform.runLater(()->{
        				wybranyPojazd=(Pojazd)tv.getSelectionModel().getSelectedItem();
        				
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
                    		ConnectionBox.getInstance().getDoSerwera().writeObject("stworzWypozyczenie()");
                    		ConnectionBox.getInstance().getDoSerwera().writeObject(tempWypozyczenie);
                    		ConnectionBox.getInstance().getDoSerwera().flush();

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
    						scooterController.zmienStan(tempWypozyczenie);
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
}
