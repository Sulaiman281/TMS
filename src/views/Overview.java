package views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import models.Tournament;
import sample.Singleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Overview {

    private VBox root;

    private TabPane tabPane;
    private Tab participant;
    private Tab matches;

    private Tournament tournament;

    public Overview(Tournament tournament){
        this.tournament = tournament;
        initialize();
    }
    private void initialize(){
        root = new VBox();
        root.setAlignment(Pos.TOP_RIGHT);
        root.setSpacing(20);

        tabPane = new TabPane();
        participant = new Tab();
        participant.setText("Participants");
        matches = new Tab();
        matches.setText("Matches");
        tabPane.getTabs().addAll(participant,matches);

        participant.setContent(new Participants(tournament).getRoot());
        matches.setContent(new Matches(tournament).getRoot());

        Button export = Singleton.getInstance().getButton("Export Data");
        export.setOnAction(e->{
            // export the data in csv file if the tournament is finished
            if(tournament.getState().equals(tournament.FINISHED)) {
                String query = "SELECT * FROM matches WHERE tournamentID = " + tournament.getId();
                try {
                    PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                    ResultSet result = statement.executeQuery();
                    String data = "Stage,Order,Winner,Loser\n";
                    while (result.next()) {
                        int stage = result.getInt(5);
                        int order = result.getInt(6);
                        int winnerId = result.getInt(3) == result.getInt(7) ? result.getInt(3) : result.getInt(4);
                        int loserID = result.getInt(3) != result.getInt(7) ? result.getInt(3) : result.getInt(4);
                        String winner = Singleton.getInstance().sql.getParticipantName(winnerId);
                        String loser = Singleton.getInstance().sql.getParticipantName(loserID);
                        data = data.concat(stage+","+order+","+winner+","+loser+"\n");
                    }
                    FileChooser chooser = new FileChooser();
                    chooser.setTitle("Choose Directory to save the file");
                    chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv"));
                    File file = chooser.showSaveDialog(null);
                    if(!file.exists()){
                        file.createNewFile();
                        FileWriter writer = new FileWriter(file);
                        writer.write(data);
                        writer.flush();
                        writer.close();
                    }
                } catch (SQLException | IOException throwables) {
                    throwables.printStackTrace();
                }
            }else{
                Singleton.getInstance().alertMsg("Tournament is not Finished.","You can not export the result until tournament is finished.");
            }
        });
        root.getChildren().add(export);
        root.getChildren().add(tabPane);
    }

    public VBox getRoot() {
        return root;
    }
}
