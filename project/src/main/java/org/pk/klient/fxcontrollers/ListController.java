package org.pk.klient.fxcontrollers;

        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.TableColumn;
        import javafx.scene.control.TableView;
        import javafx.scene.input.MouseEvent;
        import org.pk.entity.Pojazd;
        import org.pk.klient.util.ConnectionBox;

        import java.io.IOException;
        import java.util.List;

public class ListController {

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
        for(Pojazd temp:listaHulajnog){
            tv.getItems().add(temp);
        }
    }
    @FXML
    public void wybierzPojazd(MouseEvent event) {
        if (event.getClickCount() == 2)
        {
            if(tv.getSelectionModel().getSelectedItem().getClass()==Pojazd.class){
                wybranyPojazd=(Pojazd)tv.getSelectionModel().getSelectedItem();
                System.out.println(wybranyPojazd.toString());
            }
        }
    }
    @FXML
    public void wypozyczPojazd(MouseEvent event) {

    }
}
