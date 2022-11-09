package views;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Tournament;
import sample.Singleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Participants {
    private VBox root;

    private ListView<String> participants;
    private Label p_size;

    private Tournament tournament;

    public Participants(Tournament tournament){
        this.tournament = tournament;
        initialize();
        bottom();
    }
    private void initialize(){
        root = new VBox();
        root.setAlignment(Pos.TOP_CENTER);
        root.setSpacing(20);

        Label heading = Singleton.getInstance().getLabel("Participants of \""+tournament.getTitle()+"\"", Color.YELLOWGREEN,18);
        heading.setPrefWidth(Region.USE_COMPUTED_SIZE);
        heading.setPrefHeight(Region.USE_COMPUTED_SIZE);
        root.getChildren().add(heading);

        HBox box = new HBox();
        box.setAlignment(Pos.TOP_CENTER);
        box.setSpacing(60);

        p_size = new Label("4"); // size of participant
        p_size.setFont(Font.font("Arial",24));
        p_size.setTextFill(Color.ORANGE);
        Label p = new Label("Participants"); // size of participant
        p.setFont(Font.font("Arial",12));
        p.setTextFill(Color.WHITE);

        VBox pBox = new VBox();
        pBox.getChildren().addAll(p_size,p);
        pBox.setAlignment(Pos.CENTER);
        pBox.setSpacing(10);

        Label t_size = new Label(tournament.getSize()+""); // size of participant
        t_size.setFont(Font.font("Arial",24));
        t_size.setTextFill(Color.ORANGE);
        Label t = new Label("Tournaments Size"); // size of participant
        t.setFont(Font.font("Arial",12));
        t.setTextFill(Color.WHITE);

        VBox tBox = new VBox();
        tBox.setAlignment(Pos.CENTER);
        tBox.setSpacing(10);
        tBox.getChildren().addAll(t_size,t);
        box.getChildren().addAll(pBox,tBox);

        root.getChildren().add(box);

        participants = new ListView<>();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem edit = new MenuItem("Edit");
        edit.setOnAction(e->{
            // edit the selected participant
            System.out.println(participants.getSelectionModel().getSelectedItem());
            String query = "SELECT * FROM participant WHERE name = '"+participants.getSelectionModel().getSelectedItem()+"';";
            try {
                PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                ResultSet set = statement.executeQuery();
                if(set.next()){
                    Stage stage = new Stage();
                    stage.setTitle("Edit Participant");
                    AddParticipant participant = new AddParticipant(tournament);
                    participant.editParticipant(set.getInt(1));
                    stage.setScene(new Scene(participant.getRoot()));
                    stage.showAndWait();
                    updateListView();
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(e->{
            // remove the selected participant.
            if(tournament.getState().equals(tournament.CREATED)) {
                String query = "DELETE FROM participant WHERE name = '" + participants.getSelectionModel().getSelectedItem() + "';";
                try {
                    Singleton.getInstance().sql.connection.prepareStatement(query).execute();
                    ArrayList<Integer> ids = Singleton.getInstance().sql.getParticipantFromTournament(tournament.getId());
                    int pId = 0;
                    for (Integer id : ids) {
                        String name = Singleton.getInstance().sql.getParticipantName(id);
                        if (name.equals(participants.getSelectionModel().getSelectedItem())) {
                            pId = id;
                        }
                    }
                    query = "DELETE FROM participantintournament WHERE TournamentID = " + tournament.getId() + " AND ParticipantID = " + pId + ";";
                    Singleton.getInstance().sql.connection.prepareStatement(query).execute();
                    query = "DELETE FROM matches WHERE participant1 = " + pId + " OR participant2 = " + pId + ";";
                    Singleton.getInstance().sql.connection.prepareStatement(query).execute();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                updateListView();
            }else{
                Singleton.getInstance().alertMsg("Warning","After the tournament started you can not remove any member.");
            }
        });
        contextMenu.getItems().addAll(edit,remove);
        participants.setContextMenu(contextMenu);
        updateListView();
        root.getChildren().add(participants);
    }

    private void bottom(){
        Button addParticipant = Singleton.getInstance().getButton("+ Participant");
        addParticipant.setOnAction(e->{
            // add new Participant in the tournament.
            if(participants.getItems().size() == tournament.getSize()){
                Singleton.getInstance().alertMsg("Sorry","No Vacancy to Add more Participants.");
                return;
            }
            Stage stage = new Stage();
            stage.setTitle("Participant");
            stage.setScene(new Scene(new AddParticipant(tournament).getRoot()));
            stage.showAndWait();
            updateListView();
        });
        Button fillRandom = Singleton.getInstance().getButton("Fill Random");
        fillRandom.setOnAction(e->{
            // just fill the random participant according to the size of tournament.
            for(int i = participants.getItems().size(); i< tournament.getSize(); i++){
                String query = "INSERT INTO participant (Name,IsTemporary,IsTeam) VALUES(?,?,?);";
                try {
                    PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                    statement.setString(1, "Random"+i);
                    statement.setBoolean(2,true);
                    statement.setBoolean(3,false);
                    System.out.println(!statement.execute() ? "YES":"NO");

                    int id;
                    query = "SELECT LAST_INSERT_ID();";
                    statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                    ResultSet resultSet = statement.executeQuery();
                    resultSet.next();
                    id = resultSet.getInt(1);
                    query = "INSERT INTO participantintournament (TournamentID,ParticipantID) VALUES (?,?);";
                    statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                    statement.setInt(1,tournament.getId());
                    statement.setInt(2,id);
                    System.out.println(!statement.execute() ? "Added Participant in Tournament":"");

                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            updateListView();
        });
        HBox box = new HBox();
        box.setAlignment(Pos.BOTTOM_LEFT);
        box.setSpacing(20);
        box.getChildren().addAll(addParticipant,fillRandom);
        root.getChildren().add(box);
    }

    private void updateListView(){
        participants.getItems().clear();
        ArrayList<Integer> ids = Singleton.getInstance().sql.getParticipantFromTournament(tournament.getId());
        for (Integer id : ids) {
            participants.getItems().add(Singleton.getInstance().sql.getParticipantName(id));
        }
        p_size.setText(participants.getItems().size()+"");
    }

    public VBox getRoot() {
        return root;
    }
}
