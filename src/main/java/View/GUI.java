package View;

import javafx.application.Application;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.stage.Stage;
        import java.io.IOException;
        import java.net.URISyntaxException;


public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("GUIStructure.fxml"));
        primaryStage.setTitle("Welcom to the best search engine in the world!!");
        primaryStage.setScene(new Scene(root, 500, 700));
        primaryStage.show();
    }
    public static void main(String[] args) throws IOException, URISyntaxException {
        launch(args);
    }
}


