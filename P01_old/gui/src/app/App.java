package app;

import app.controllers.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        MainWindow main = new MainWindow();
        double width = main.getPrefWidth(), height = main.getPrefHeight();

        Font.loadFont(getClass().getResource("/fonts/Poppins/Poppins-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(getClass().getResource("/fonts/MaterialIcons/MaterialIcons-Regular.ttf").toExternalForm(), 10);

        Scene scene = new Scene(main, main.getPrefWidth(), main.getPrefHeight());
        scene.getStylesheets().add(getClass().getResource("/css/app.css").toExternalForm());

        stage.setScene(scene);
        stage.setTitle("Practica 1 | Redes");
        stage.setMinWidth(width);
        stage.setMinHeight(height);

        stage.show();

    }

    public static void main(String[] args){ launch(); }

}
