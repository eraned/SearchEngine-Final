package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;


public class GUI extends Application {
    public static Stage MainStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        MainStage = primaryStage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("GUIStructure.fxml")));
        primaryStage.setTitle("Welcom to the best search engine in the world!!");
        primaryStage.setScene(new Scene(root, 500, 700));
        primaryStage.show();
    }
    public static void main(String[] args) throws IOException, URISyntaxException {
        launch(args);
    }
}


