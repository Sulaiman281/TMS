package views;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import models.Game;
import models.Tournament;
import sample.NumFieldFX;
import sample.Singleton;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class CreateTournament {
    private HBox root;

    private Integer value;
    private ArrayList<Game> gameModel;

    public CreateTournament(){
        initialize();
    }

    private void initialize() {
        // initializing main root.
        root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setPrefWidth(600);
        root.setPrefHeight(260);
        root.setStyle(
                "-fx-background-color: linear-gradient(#3C86B9,#0A0C0E);"
        );
        // adding labels
        root.getChildren().add(getLabels("Title","Game","Size"));
        //adding input fieds
        root.getChildren().add(getInputs());
    }

    private VBox getLabels(String... texts){
        VBox box = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        box.setSpacing(30);
        box.setPrefWidth(80);
        box.getChildren().add(new HBox());
        for (String text : texts) {
            Label label = Singleton.getInstance().getLabel(text,Color.web("#6aff4d"),18);
            label.setPrefWidth(80);
            label.setPrefHeight(30);
            box.getChildren().add(label);
        }
        return box;
    }
    private VBox getInputs(){
        VBox box = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        box.setSpacing(30);
        box.getChildren().add(new HBox());

        TextField title = new TextField();
        title.setFont(Font.font("Arial",16));
        title.setPrefWidth(420);
        title.setPrefHeight(30);
        box.getChildren().add(title);

        ComboBox<String> games = new ComboBox<>();
        games.setEditable(true);
//        games.valueProperty().addListener((observableValue, s, t1) -> {
//            System.out.println(t1);
//        });
        games.setPrefWidth(420);
        games.setPrefHeight(30);
        // load games from database. and add here.
        gameModel = Singleton.getInstance().sql.getGames();
        for (Game game : gameModel) {
            games.getItems().add(game.getName());
        }

        box.getChildren().add(games);

        value = 0;
        TextField size = new TextField();
        new NumFieldFX().numField(size);
        size.setText(value+"");
        size.setFont(Font.font("Arial",16));
        size.setPrefWidth(405);
        size.setPrefHeight(30);
        Button increase = new Button("+");
        increase.setFont(Font.font("Arial",6));
        increase.setPrefWidth(15);
        increase.setPrefHeight(15);
        increase.setOnAction(e->{
            value++;
            size.setText(value+"");
        });
        Button decrease = new Button("-");
        decrease.setFont(Font.font("Arial",6));
        decrease.setPrefWidth(15);
        decrease.setPrefHeight(15);
        decrease.setOnAction(e->{
            if(value <= 0) return;
            value--;
            size.setText(value+"");
        });
        VBox vBox = new VBox();
        vBox.getChildren().addAll(increase,decrease);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(size,vBox);
        box.getChildren().add(hBox);

        Button create = Singleton.getInstance().getButton("Create");
        create.setOnAction(e->{
            if(title.getText().isEmpty() || games.getValue().isEmpty() || size.getText().isEmpty()){
                // error box
                Singleton.getInstance().alertMsg("Empty Fields","Please Fill all the Fields.");
            }else{
                // add the data into database.
                String gName = games.getValue();
                int gameId;
                if(gameContained(gName)){
                    gameId = Singleton.getInstance().sql.getGame(gName);
                }else {
                    Game game = new Game(gName);
                    Singleton.getInstance().sql.addGame(game);
                    gameId = Singleton.getInstance().sql.getGame(gName);
                }

                String query = "INSERT INTO tournament (Title,GameID,Size,TournamentState) VALUES(?,?,?,?);";
                try {
                    PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                    statement.setString(1,title.getText());
                    statement.setInt(2,gameId);
                    statement.setInt(3, Integer.parseInt(size.getText()));
                    statement.setString(4,new Tournament().CREATED);
                    System.out.println(!statement.execute() ? "YES":"NO");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            Stage stage = (Stage) root.getScene().getWindow();
            stage.close();
        });
        HBox btnBox = new HBox();
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.getChildren().add(create);
        box.getChildren().add(btnBox);

        return box;
    }

    private boolean gameContained(String gName){
        boolean cond = false;
        for (Game game : gameModel) {
            if(game.getName().equals(gName)){
                cond = true;
                break;
            }
        }
        return cond;
    }

    public HBox getRoot() {
        return root;
    }
}
