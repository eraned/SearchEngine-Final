package Controller;

import Model.Indexer;
import Model.SearchEngine;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Controller implements Initializable {

    public javafx.scene.control.TextField PathIN;
    public javafx.scene.control.Button PathINButton;
    public javafx.scene.control.TextField PathOUT;
    public javafx.scene.control.Button PathOUTButton;
    public javafx.scene.control.CheckBox Stemmer;
    public javafx.scene.control.ChoiceBox LangSelctor;
    public javafx.scene.control.Button StartEngine;
    public javafx.scene.control.Button resetEngine;
    public javafx.scene.control.Button LoadDic;
    public javafx.scene.control.Button ShoewDic;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Gets the path that we picked for the corpus and the stop words.
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
     * Gets the path which we will save the final posting file.
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
     * Function that gets the Path which we want to load the dictionary and the cache from,
     * and load the cache and dictionary into HashMaps.
     */
//todo - needed to fixxxxxxx
    public void LoadDicToMemory() {

        HashMap<String, StringBuilder> LoadedDictionary = new HashMap<>();
        try {
            LoadedDictionary = SearchEngine.indexer.ItsTimeToLoadDictionary();
        } catch (Exception e) {

        }
        if (LoadDic != null) {
            showAlert("Dic and cache successfully load");
        } else {
            showAlert("dic and cache failed to load");
        }

    }

    /**
     * This function is activated when the Start button is clicked,
     * we check the the three paths aren't empty and we start the whole process
     * parsing -> stemming if necessary -> create temp posting -> create dictionary and cache + save them -> create final posting File
     */
    public void startEngine() throws IOException, URISyntaxException {
        if (!PathIN.getText().trim().isEmpty() && !PathOUT.getText().trim().isEmpty()) {
            // String corpusPath = PathIN.getText()+"/corpus";  // lab path - "\\corpus"
            // String stopWordsPath=PathIN.getText()+"/stop_words.txt"; //lab path - "\\stop_words.txt"
            PathIN.setDisable(true);
            PathOUT.setDisable(true);
            String Pathout = PathOUT.getText();
            String Pathin = PathIN.getText();
            SearchEngine searchEngine;
            searchEngine = new SearchEngine(Pathin, Pathout, false);
            LoadLangugesToScroll();
            showAlert(getFinalDoc());  // to convert final doc to thus func
            PathIN.setDisable(false);
            PathOUT.setDisable(false);
            resetEngine.setDisable(false);
        } else {
            showAlert("alert");
        }
    }


    public String getFinalDoc ()
    {
        return SearchEngine.indexer.ItsTimeFor_FinalDoc();
    }

    /**
     * The function which delete all the files that we have created from the run,
     * The cache,dictionary,final posting file,all the temp postings, and the doctext.
     */
    public void resetAll() {
        File FileToReset;
        String Pathout = PathOUT.getText();
        if (Stemmer.isSelected()) {
            FileToReset = new File(Pathout + "/EngineOut_WithStemmer"); //lab path - "\\EngineOut_WithStemmer\\"
        } else {
            FileToReset = new File(Pathout + "/EngineOut"); //lab path - "\\EngineOut\\"
        }
        //  File tempPostingDir = new File("tempPosting");
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
        StartEngine.setDisable(false);
    }

    /**
     * This function open the notepad,
     * And show the dictionary inside it as the requested format.
     */
    public void ShowDictionary() throws IOException{
        if (!PathOUT.getText().trim().isEmpty()) {
            String Pathout = PathOUT.getText();
            File dictionary = new File(Pathout + "/Dictionary.txt"); //lab path - "\\Dictionary.txt"
            Desktop.getDesktop().edit(dictionary);
        }
        else {
            showAlert("choose dic and posting save path");
        }
    }

    /**
     * This function gets a String which is the content of the message
     * And open up an alert with this content
     *
     * @param str - The string that we want to show at the alert.
     */
    public static void showAlert(String str) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(str);
        a.show();
    }

    public void LoadLangugesToScroll(){
        for(String lang : SearchEngine.Languages) {
           // MenuItem newLang= new MenuItem(lang);
            LangSelctor.getItems().add(lang);
        }
    }
}
