package Controller;

import Model.SearchEngine;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.io.*;

/**
 *
 */
public class Controller{

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
     * This function is activated when the Start button is clicked,
     * we check the the three paths aren't empty and we start the whole process
     * parsing -> stemming if necessary -> create temp posting -> create dictionary and cache + save them -> create final posting File
     */
    public void startEngine() throws IOException, URISyntaxException {
        if (!PathIN.getText().trim().isEmpty() && !PathOUT.getText().trim().isEmpty()) {
            PathIN.setDisable(true);
            PathOUT.setDisable(true);
            String Pathout = PathOUT.getText();
            String Pathin = PathIN.getText();
            SearchEngine searchEngine;
            searchEngine = new SearchEngine(Pathin, Pathout, Stemmer.isSelected());
            LoadLangugesToScroll();
            showAlert(getFinalDoc());
            PathIN.setDisable(false);
            PathOUT.setDisable(false);
            resetEngine.setDisable(false);
            LoadDic.setDisable(false);
            ShoewDic.setDisable(false);
        } else {
            showAlert("Please enter Path's!");
        }
    }


    public String getFinalDoc ()
    {
        return SearchEngine.ItsTimeFor_FinalDoc();
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


    /**
     *
     */
    public void LoadLangugesToScroll(){
        for(String lang : SearchEngine.Languages) {
            // MenuItem newLang= new MenuItem(lang);
            LangSelctor.getItems().add(lang);
        }
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
     * This function open the notepad,
     * And show the dictionary inside it as the requested format.
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
     * Function that gets the Path which we want to load the dictionary and the cache from,
     * and load the cache and dictionary into HashMaps.
     */
//todo - needed to fixxxxxxx
    public void LoadDicToMemory() throws IOException {
        if(!Stemmer.isSelected()) {
            HashMap<String, Model.DictionaryDetailes> LoadedDictionary = new HashMap<>();
            LoadedDictionary = SearchEngine.indexer.ItsTimeToLoadDictionary(  PathOUT.getText() + "/EngineOut/Dictionary.txt");
        }
        else{
            HashMap<String, Model.DictionaryDetailes> LoadedDictionary = new HashMap<>();
            LoadedDictionary = SearchEngine.indexer.ItsTimeToLoadDictionary( PathOUT.getText()+ "/EngineOut_WithStemmer/Dictionary.txt");
        }
        if (LoadDic != null) {
            showAlert("Dictionary successfully loaded to Memory!");
        } else {
            showAlert("Dictionry failed to load in to the Memory!");
        }
    }
}
