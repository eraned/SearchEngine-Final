package Controller;

import Model.SearchEngine;
import Model.DictionaryDetailes;
import Model.Searcher;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.io.*;

/**
 * This class manages all the interaction between the View package and  Model package,
 * linking each button that the user clicks in the GUI and translate to the action that the engine needs to do or display.
 */
public class Controller{

    public javafx.scene.control.TextField PathIN;
    public javafx.scene.control.Button PathINButton;
    public javafx.scene.control.TextField PathOUT;
    public javafx.scene.control.Button PathOUTButton;
    public javafx.scene.control.CheckBox Stemmer;
    public javafx.scene.control.CheckBox Semantic;
    public javafx.scene.control.ChoiceBox LangSelctor;
    public javafx.scene.control.Button StartEngine;
    public javafx.scene.control.Button resetEngine;
    public javafx.scene.control.Button LoadDic;
    public javafx.scene.control.Button ShoewDic;
    public javafx.scene.control.Button ShowEntitys;
    public javafx.scene.control.TextField SingleQuery;
    public javafx.scene.control.TextField PathQueriesFile;
    public javafx.scene.control.Button RunSingleQueryButton;
    public javafx.scene.control.Button BrowseQueryButton;
    public javafx.scene.control.Button runQueryFileButton;
    public javafx.scene.control.Button newSearchButton;
    public javafx.scene.control.ChoiceBox CitySelctor;



    private SearchEngine searchEngine;
    private Searcher searcher;

    /**
     * translate the user choise to string for input to the search engine
     */
    public void GetCorpusDirectoryIN() {
        DirectoryChooser DC = new DirectoryChooser();
        DC.setTitle("Pick Directory IN");
        File file = DC.showDialog(null);
        if (file != null) {
            String Path = file.getAbsolutePath();
            PathIN.setText(Path);
        }
    }

    /**
     * translate the user choise to string for output to the search engine output
     */
    public void GetCorpusDirectoryOUT() {
        DirectoryChooser DC = new DirectoryChooser();
        DC.setTitle("Pick Directory OUT");
        File file = DC.showDialog(null);
        if (file != null) {
            String name = file.getAbsolutePath();
            PathOUT.setText(name);
        }
    }

    /**
     * when the user want to start the engine this  function run
     * its initiate new show of search engine and start to indexing the corpuse
     *
     */
    public void startEngine() throws IOException, URISyntaxException {
        if (!PathIN.getText().trim().isEmpty() && !PathOUT.getText().trim().isEmpty()) {
            PathIN.setDisable(true);
            PathOUT.setDisable(true);
            String Pathout = PathOUT.getText();
            String Pathin = PathIN.getText();
            SearchEngine searchEngine;
            searchEngine = new SearchEngine(Pathin, Pathout,Stemmer.isSelected());
            LoadLangugesToScroll();
            LoadCitiesToScroll();
            showAlert(getFinalDoc());
            PathIN.setDisable(false);
            PathOUT.setDisable(false);
            resetEngine.setDisable(false);
            LoadDic.setDisable(false);
            ShoewDic.setDisable(false);
            SingleQuery.setDisable(false);
            PathQueriesFile.setDisable(false);
            BrowseQueryButton.setDisable(false);
            RunSingleQueryButton.setDisable(false);
            runQueryFileButton.setDisable(false);
        } else {
            showAlert("Please enter Path's!");
        }
    }

    /**
     * return from the search engine the finale doc to show in new GUI Window.
     */
    public String getFinalDoc ()
    {
        return SearchEngine.ItsTimeFor_FinalDoc();
    }

    /**
     * gets string to pop alert for the user/
     * @param str - String to pop
     */
    public static void showAlert(String str) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(str);
        a.show();
    }


    /**
     * after the search engine run first time its initiate all the languages that he fing to new menu item
     */
    public void LoadLangugesToScroll(){
        for(String lang : SearchEngine.Languages) {
            // MenuItem newLang= new MenuItem(lang);
            LangSelctor.getItems().add(lang);
        }
    }

    /**
     * this function after the user click the reset button delete all the search engine output.
     */
    public void resetAll() {
        File FileToReset;
        String Pathout = PathOUT.getText();
        if (Stemmer.isSelected()) {
            FileToReset = new File(Pathout + "/EngineOut_WithStemmer"); //lab path - "\\EngineOut_WithStemmer\\"
        } else {
            FileToReset = new File(Pathout + "/EngineOut"); //lab path - "\\EngineOut\\"
        }
        if (FileToReset.exists()) {
            File[] fileList = FileToReset.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                File textFileDirectory = fileList[i];
                if (textFileDirectory.exists()) {
                    textFileDirectory.delete();
                }
            }
            FileToReset.delete();
        }
        PathIN.setText("");
        PathOUT.setText("");
        resetEngine.setDisable(true);
        ShoewDic.setDisable(true);
        LoadDic.setDisable(true);
        StartEngine.setDisable(false);
    }

    /**
     * open new window and display the Dictionary from the disk.
     */
    public void ShowDictionary() throws IOException{
        if (!PathOUT.getText().trim().isEmpty()) {
            String Pathout = PathOUT.getText();
            if(!Stemmer.isSelected()) {
                File dictionary = new File(Pathout + "/EngineOut/Dictionary.txt"); //lab path - "\\Dictionary.txt"
                Desktop.getDesktop().edit(dictionary);
            }
            else{
                File dictionary = new File(Pathout + "/EngineOut_WithStemmer/Dictionary.txt"); //lab path - "\\Dictionary.txt"
                Desktop.getDesktop().edit(dictionary);
            }
        }
        else {
            showAlert("Please make sure that the Path out is valid");
        }
    }

    /**
     * read the dic from the disk and initialized it to new data structure that save in the memory.
     */
    public void LoadDicToMemory() throws IOException {
        if(!Stemmer.isSelected()) {
            HashMap<String, DictionaryDetailes> LoadedDictionary = new HashMap<>();
            LoadedDictionary = SearchEngine.indexer.ItsTimeToLoadDictionary(  PathOUT.getText() + "/EngineOut/Dictionary.txt");
        }
        else{
            HashMap<String, DictionaryDetailes> LoadedDictionary = new HashMap<>();
            LoadedDictionary = SearchEngine.indexer.ItsTimeToLoadDictionary( PathOUT.getText()+ "/EngineOut_WithStemmer/Dictionary.txt");
        }
        if (LoadDic != null) {
            showAlert("Dictionary successfully loaded to Memory!");
        } else {
            showAlert("Dictionry failed to load in to the Memory!");
        }
    }

    public void LoadCitiesToScroll(){
        for(String city : SearchEngine.Cities.keySet()) {
            // MenuItem newLang= new MenuItem(lang);
            CitySelctor.getItems().add(city);
        }
    }


    public void RunSingle() {


    }

    public void QueriesInput() {
        FileChooser FC = new FileChooser();
        FC.setTitle("Pick Directory for Queries!");
        File file = FC.showOpenDialog(null);
        if (file != null) {
            String Path = file.getAbsolutePath();
            PathQueriesFile.setText(Path);
        }
    }

    public void RunAll() throws IOException {
        searcher = new Searcher(searchEngine.indexer,searchEngine.parser,searchEngine.readFile,Semantic.isSelected(),CitySelctor.isShowing(),Stemmer.isSelected());
        searcher.ProccesQuery(PathQueriesFile.getText());
        searcher.EntityIdentification();
        searcher.ReturnResults();
        newSearchButton.setDisable(false);
    }

    public void NewSearch() {
        File ResultsToReset;
        String ResultPath = searcher.stbResult.toString();
//        if (Stemmer.isSelected()) {
            ResultsToReset = new File(ResultPath); //lab path - "\\EngineOut_WithStemmer\\"
//        } else {
//            ResultsToReset = new File(ResultPath); //lab path - "\\EngineOut\\"
//        }
        if (ResultsToReset.exists()) {
            File[] fileList = ResultsToReset.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                File textFileDirectory = fileList[i];
                if (textFileDirectory.exists()) {
                    textFileDirectory.delete();
                }
            }
            ResultsToReset.delete();
        }
    }
}
