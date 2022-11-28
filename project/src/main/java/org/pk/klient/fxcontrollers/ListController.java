package org.pk.klient.fxcontrollers;

        import javafx.collections.ObservableList;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.scene.control.TableColumn;
        import javafx.scene.control.TableView;
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
    @FXML
    void pobierzListe(ActionEvent event) throws IOException, ClassNotFoundException {
        ConnectionBox.getInstance().getDoSerwera().writeObject("getList()");

        listaHulajnog = (List<Pojazd>) ConnectionBox.getInstance().getOdSerwera().readObject();

        ConnectionBox.getInstance().getDoSerwera().flush();
        for(Pojazd temp:listaHulajnog){
            System.out.println("Debug Eventu");
            System.out.print(temp.toString());
            //tv.getItems().add(temp);
        }
    }
}
