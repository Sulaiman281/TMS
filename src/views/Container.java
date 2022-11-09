package views;

import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import sample.Singleton;

public class Container {
    private BorderPane root;

    public Container(){
        initialize();
    }
    private void initialize(){
        root = new BorderPane();
        root.setPrefWidth(1024);
        root.setPrefHeight(720);
        root.setStyle(
                "-fx-background-color: linear-gradient(from 20% 35% to 100% 100%,#3C86B9,#0A0C0E);"
        );
        Singleton.getInstance().mainRoot = root;
        top();
//        dashboard();

        root.setCenter(new TournamentList().getRoot());
    }

    private void top(){
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        ImageView imageView = new ImageView();
        imageView.setImage(Singleton.getInstance().loadImage("/sample/image.png"));
        box.getChildren().add(imageView);
        root.setTop(box);
        box.setOnMouseClicked(e->{
            root.setCenter(new TournamentList().getRoot());
        });
    }

//    private void dashboard(){
//        VBox box = new VBox();
//        box.setSpacing(10);
//        TextField game_name = new TextField();
//        game_name.setPromptText("Game Name");
//        game_name.setFont(Font.font("Arial",16));
//
//        Button addGame = new Button();
//        addGame.setFont(Font.font("Arial", FontWeight.BOLD,18));
//        addGame.setOnAction(e->{
//            if(game_name.getText().isEmpty()){
//                // return error dialog.
//                return;
//            }else{
//                // add the data into database.
//            }
//        });
//
//        box.getChildren().addAll(game_name,addGame);
//        root.setLeft(box);
//    }


    public BorderPane getRoot() {
        return root;
    }
}
