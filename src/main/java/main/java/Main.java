package main.java;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main  extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/visu/main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 860);
        stage.setTitle("Главное меню");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/image/icon.png")));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
