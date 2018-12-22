package View;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;




public class GUI extends Application {
    public static Stage SearchEngineStage;
    @Override
    public void start(Stage primaryStage) throws Exception {
        SearchEngineStage = primaryStage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getClassLoader().getResource("GUIStructure.fxml")));
        SearchEngineStage.setTitle("Welcom to the best search engine in the world!!");
        SearchEngineStage.setScene(new Scene(root, 500, 700));
        SearchEngineStage.show();
    }
    public static void main(String[] args) throws IOException, URISyntaxException {
        launch(args);
    }
}


