package org.pk.klient.fxcontrollers;

        import javafx.application.Platform;
        import javafx.collections.ObservableList;
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
        import org.pk.serwer.dao.KlientDao;

        import java.io.IOException;
        import java.util.List;

        import static org.pk.util.StaleWartosci.APP_VIEW_XML;
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
    private TableView tv;
    private List<Pojazd> listaHulajnog;
    private Pojazd wybranyPojazd;
    @FXML

    void pobierzListe(ActionEvent event) throws IOException, ClassNotFoundException {
        ConnectionBox.getInstance().getDoSerwera().writeObject("getList()");

        listaHulajnog = (List<Pojazd>) ConnectionBox.getInstance().getOdSerwera().readObject();

        ConnectionBox.getInstance().getDoSerwera().flush();
        tv.getItems().clear();
        for(Pojazd temp:listaHulajnog){
            tv.getItems().add(temp);
        }
    }
    @FXML
    public void wybierzPojazd(MouseEvent event) {
    	Runnable watek = () ->{
    		if (event.getClickCount() == 2)
    		{
    			if(tv.getSelectionModel().getSelectedItem().getClass()==Pojazd.class){
    				wybranyPojazd=(Pojazd)tv.getSelectionModel().getSelectedItem();
    				Platform.runLater(()->{
    					// Stworzenie konta zakonczone sukcesem, przejscie na scene logowania i zaladowanie komunikatu
    					FXMLLoader loader = new FXMLLoader(getClass().getResource(SCOOTER_VIEW_XML));
    					try {
    						kontener = loader.load();
    					} catch (IOException wyjatekIO) {
    						wyjatekIO.printStackTrace();
    					}
    					
    					stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
    					scene = new Scene(kontener);
    					stage.setScene(scene);
    					
    					ScooterController scooterController=loader.getController();
    					scooterController.ustawWartosci(wybranyPojazd);

                        Klient tempKlient=ConnectionBox.getInstance().getKlient();
                        Wypozyczenie tempWypozyczenie = new Wypozyczenie(tempKlient,wybranyPojazd);
                        tempKlient.dodajWypozyczenie2S(tempWypozyczenie);
                        //stage.setUserData(tempWypozyczenie);
                        
                        try {
                        	
                            ConnectionBox.getInstance().getDoSerwera().writeObject("stworzWypozyczenie()");
                            ConnectionBox.getInstance().getDoSerwera().writeObject(tempWypozyczenie);
                            ConnectionBox.getInstance().getDoSerwera().flush();
                            
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        
    					try {
    						scooterController.zmienStan(wybranyPojazd);
    					} catch (InterruptedException e) {
    						throw new RuntimeException(e);
    					}
    					
    					stage.show();
    				});
    			}

    		}
    	};
    	ConnectionBox.getInstance().getWykonawcaGlobalny().execute(watek);
    }
}
