package views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Tournament;
import sample.Singleton;

import java.util.ArrayList;

public class TournamentList {

    private VBox root;

    private TableView<Tournament> tournamentTableView;
    private TableColumn<Tournament, String> titleColumn;
    private TableColumn<Tournament, String> gameNameColumn;
    private TableColumn<Tournament, String> winnerNameColumn;

    public TournamentList(){
        initialize();
        updateTableView();
    }
    private void initialize(){
        root = new VBox();
        root.setAlignment(Pos.TOP_RIGHT);
        root.setSpacing(20);

        Button addT = Singleton.getInstance().getButton("Add Tournament");
        addT.setOnAction(e->{
            Stage stage = new Stage();
            stage.setTitle("Create Tournament");
            stage.setScene(new Scene(new CreateTournament().getRoot()));
            stage.showAndWait();
            updateTableView();
        });
        root.getChildren().add(addT);

        tournamentTableView = new TableView<>();
        titleColumn = new TableColumn<>();
        titleColumn.setText("Title");
        gameNameColumn = new TableColumn<>();
        gameNameColumn.setText("Game");
        winnerNameColumn = new TableColumn<>();
        winnerNameColumn.setText("Winner");
        setTableProperties();

        ContextMenu menu = new ContextMenu();
        MenuItem overView = new MenuItem("Overview");
        MenuItem remove = new MenuItem("Remove");
        overView.setOnAction(e->{
            Tournament tournament = tournamentTableView.getSelectionModel().getSelectedItem();
            if(tournament != null)
                Singleton.getInstance().mainRoot.setCenter(new Overview(tournament).getRoot());
        });
        remove.setOnAction(e->{

        });
        menu.getItems().addAll(overView,remove);
        tournamentTableView.setContextMenu(menu);

        root.getChildren().add(tournamentTableView);
    }
    private void setTableProperties(){
        tournamentTableView.getColumns().add(titleColumn);
        tournamentTableView.getColumns().add(gameNameColumn);
        tournamentTableView.getColumns().add(winnerNameColumn);
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        gameNameColumn.setCellValueFactory(new PropertyValueFactory<>("gameID"));
        winnerNameColumn.setCellValueFactory(new PropertyValueFactory<>("winnerID"));
    }

    public void updateTableView(){
        tournamentTableView.getItems().clear();
        tournamentTableView.getItems().addAll(Singleton.getInstance().sql.loadTournaments());
    }

    public VBox getRoot() {
        return root;
    }
}
