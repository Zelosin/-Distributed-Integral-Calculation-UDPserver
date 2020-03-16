package zelosin.pack.base;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import zelosin.pack.Services.UDP.EchoServer;

public class Main extends Application {
    public static Stage primaryStage;
    public static FXMLLoader mFXMLLoader = new FXMLLoader();

    @Override
    public void start(Stage primaryStage) throws Exception{
        new EchoServer().start();
        Main.primaryStage = primaryStage;
        mFXMLLoader.setLocation(getClass().getClassLoader().getResource("zelosin/pack/FXML's/sample.fxml"));
        mFXMLLoader.load();
        primaryStage.setScene(new Scene(mFXMLLoader.getRoot()));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
