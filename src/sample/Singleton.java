package sample;

import controller.MySQL;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class Singleton {

    public MySQL sql = new MySQL();

    private static final Singleton singleton = new Singleton();
    public BorderPane mainRoot;

    private Singleton(){

    }

    public static Singleton getInstance(){
        return singleton;
    }


    public Button getButton(String txt){
        Button btn = new Button(txt);
        btn.setFont(Font.font("Arial", FontWeight.BOLD,18));
        btn.setStyle(
                "-fx-background-color: linear-gradient(#62D966,#B2E6B4);"
        );
        return btn;
    }
    public Label getLabel(String text, Color color,int font_size){
        Label label = new Label(text);
        label.setTextFill(color);
        label.setFont(Font.font("Arial", FontWeight.BOLD,font_size));
        return label;
    }

    public Image loadImage(String _file){
        return new Image(getClass().getResource(_file).toString());
    }

    public void alertMsg(String title, String msg){
        Stage stage = new Stage();
        stage.setTitle(title);

        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(30);
        box.setStyle(
                "-fx-background-color: linear-gradient(from 20% 35% to 100% 100%,#3C86B9,#0A0C0E);"
        );
        box.setPrefWidth(300);
        box.setPrefHeight(150);
        Label label = getLabel(msg,Color.RED,18);
        label.setMinWidth(Region.USE_COMPUTED_SIZE);
        label.setMinHeight(Region.USE_COMPUTED_SIZE);
        label.setPrefWidth(Region.USE_COMPUTED_SIZE);
        label.setPrefHeight(Region.USE_COMPUTED_SIZE);
        box.getChildren().add(label);

        Button btn = getButton("Close");
        btn.setOnAction(e->{
            stage.close();
        });
        box.getChildren().add(btn);

        stage.setScene(new Scene(box));
        stage.showAndWait();
    }
}
