package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import views.Container;
import views.CreateTournament;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Singleton.getInstance().sql.authorize();
        primaryStage.setScene(new Scene(new Container().getRoot()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
