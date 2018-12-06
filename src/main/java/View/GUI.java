package View;

import Model.SearchEngine;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URISyntaxException;


// lab path - "d:\\documents\\users\\eraned\\Downloads\\Completecorpus"

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("main.View.GUI.fxml"));
//        primaryStage.setTitle("Hello World");
//        primaryStage.setScene(new Scene(root, 300, 275));
//        primaryStage.show();
        SearchEngine searchEngine;
        searchEngine = new SearchEngine( "/Users/eranedri/Documents/SearchEngineCorpuse/Postingcorpus","/Users/eranedri/Documents/SearchEngineCorpuse/Postingcorpus",false);
        System.exit(0);

    }
    public static void main(String[] args) throws IOException, URISyntaxException {
        launch(args);
    }
}
