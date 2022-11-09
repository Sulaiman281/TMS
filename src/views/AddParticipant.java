package views;

import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Tournament;
import sample.Singleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddParticipant {

    private AnchorPane root;

    private Tournament tournament;
    public AddParticipant(Tournament tournament){
        this.tournament = tournament;
        initialize();
    }
    private boolean editMode = false;
    private int pID;
    public void editParticipant(int pID){
        editMode = true;
        this.pID = pID;
    }

    private void initialize(){
        root = new AnchorPane();
        root.setStyle(
                "-fx-background-color: linear-gradient(from 20% 35% to 100% 100%,#3C86B9,#0A0C0E);"
        );
        root.setPrefWidth(600);
        root.setPrefHeight(250);

        Label name = Singleton.getInstance().getLabel("Name: ", Color.web("#8df547"),18);
        name.setLayoutX(50);
        name.setLayoutY(30);
        TextField nameInput = new TextField();
        nameInput.setFont(Font.font("Arial",16));
        nameInput.setLayoutX(200);
        nameInput.setLayoutY(30);
        if(editMode)
            nameInput.setText(Singleton.getInstance().sql.getParticipantName(pID));
        else
            nameInput.setPromptText("participant name");

        Label type = Singleton.getInstance().getLabel("Type: ", Color.web("#8df547"),18);
        type.setLayoutX(50);
        type.setLayoutY(90);

        RadioButton playerRadio = new RadioButton("Player");
        playerRadio.setTextFill(Color.web("#f5b847"));
        playerRadio.setFont(Font.font("Arial",18));
        playerRadio.setLayoutX(200);
        playerRadio.setLayoutY(90);

        RadioButton teamRadio = new RadioButton("Team");
        teamRadio.setTextFill(Color.web("#f5b847"));
        teamRadio.setFont(Font.font("Arial",18));
        teamRadio.setLayoutX(320);
        teamRadio.setLayoutY(90);

        playerRadio.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1) if(teamRadio.isSelected()) teamRadio.setSelected(false);
        });
        teamRadio.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if(t1) if(playerRadio.isSelected()) playerRadio.setSelected(false);
        });

        CheckBox temp = new CheckBox("Temporary");
        temp.setFont(Font.font("Arial",18));
        temp.setTextFill(Color.web("#8df547"));
        temp.setLayoutX(50);
        temp.setLayoutY(150);

        Button save_btn = Singleton.getInstance().getButton("Save");
        save_btn.setOnAction(e->{
            if(!nameInput.getText().isEmpty() && playerRadio.isSelected() || teamRadio.isSelected()) {
                // add the participant into database.
                if(!editMode) {
                    String query = "INSERT INTO participant (Name,IsTemporary,IsTeam) VALUES(?,?,?);";
                    try {
                        PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                        statement.setString(1, nameInput.getText());
                        statement.setBoolean(2, temp.isSelected());
                        statement.setBoolean(3, teamRadio.isSelected());
                        System.out.println(!statement.execute() ? "YES" : "");

                        int id;
                        query = "SELECT LAST_INSERT_ID();";
                        statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                        ResultSet resultSet = statement.executeQuery();
                        resultSet.next();
                        id = resultSet.getInt(1);
                        query = "INSERT INTO participantintournament (TournamentID,ParticipantID) VALUES (?,?);";
                        statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                        statement.setInt(1, tournament.getId());
                        statement.setInt(2, id);
                        System.out.println(!statement.execute() ? "Added Participant in Tournament" : "");

                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }else{
                    String query = "UPDATE participant SET Name = '"+nameInput.getText()+"',IsTemporary="+temp.isSelected()+",IsTeam = "+teamRadio.isSelected()+
                            " WHERE ID = "+pID+";";
                    try {
                        Singleton.getInstance().sql.connection.prepareStatement(query).execute();
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                }
            }else{
                Singleton.getInstance().alertMsg("Missing Fields","Please Fill all the given fields.");
            }
            Stage stage = (Stage)root.getScene().getWindow();
            stage.close();
        });
        save_btn.setLayoutX(500);
        save_btn.setLayoutY(200);
        Button cancel_btn = Singleton.getInstance().getButton("Cancel");
        cancel_btn.setOnAction(e->{
            Stage stage = (Stage)root.getScene().getWindow();
            stage.close();
        });
        cancel_btn.setLayoutX(400);
        cancel_btn.setLayoutY(200);

        root.getChildren().addAll(name,nameInput,type,playerRadio,teamRadio,temp,save_btn,cancel_btn);
    }

    public AnchorPane getRoot() {
        return root;
    }
}
