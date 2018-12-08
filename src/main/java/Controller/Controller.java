package Controller;

import Model.SearchEngine;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class Controller {

    public javafx.scene.control.TextField PathIN;
    public javafx.scene.control.Button PathINButton;
    public javafx.scene.control.TextField PathOUT;
    public javafx.scene.control.Button PathOUTButton;
    public javafx.scene.control.CheckBox Stemmer;
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
            ShoewDic.setDisable(false);
        } else {
            ShoewDic.setDisable(false);
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
            //showAlert(getFinalAlert(sizeDoc,t-f,finalPosting,cachePath));  // to convert final doc to thus func
            PathIN.setDisable(false);
            PathOUT.setDisable(false);
            resetEngine.setDisable(false);
        } else {
            showAlert("alert");
        }
    }

    /**
     * This function return the String of the alert which will be showed after we finish the index process.
     * @param sizeDocs - The number of all the docs that we have indexed during our process.
     * @param time - The time in milliseconds which took the process to finish.
     * @param finalPostringPath - The path to the final posting file.
     * @param finalCachePath - The path to the cache file.
     * @return the String message.
     */
    public String getFinalAlert (int sizeDocs, long time,String finalPostringPath,String finalCachePath)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Time to run: "+time/1000 +" seconds.\n");
        File postingFile = new File(finalPostringPath);
        sb.append("The size of the posting file is "+postingFile.length()+" bytes.\n"); // .length()
        File cacheFile = new File (finalCachePath);
        sb.append("The size of the cache file is "+cacheFile.length()+" bytes.\n");
        sb.append("Number of docs indexed is "+sizeDocs+".\n");
        return sb.toString();
    }

    /**
     * The function which delete all the files that we have created from the run,
     * The cache,dictionary,final posting file,all the temp postings, and the doctext.
     */
    public void resetAll() {
        File postingFile;
        String Pathout = PathOUT.getText();
        File dictionary_posting = new File(Pathout + "/EngineOut"); // lab path - "\\EngineOut"
        if (Stemmer.isSelected()) {
            postingFile = new File(Pathout + "/EngineOut_WithStemmer"); //lab path - "\\EngineOut_WithStemmer\\"
        } else {
            postingFile = new File(Pathout + "/EngineOut"); //lab path - "\\EngineOut\\"
        }
        File tempPostingDir = new File("tempPosting");
        if (tempPostingDir.exists()) {
            File[] fileList = tempPostingDir.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                File textFileDirectory = fileList[i];
                if (textFileDirectory.exists()) {
                    textFileDirectory.delete();
                }
            }
            tempPostingDir.delete();
        }
//        if(dictionary.exists())
//        {
//            dictionary.delete();
//        }
//        if(postingFile.exists())
//        {
//            postingFile.delete();
//        }
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
    public void ShowDictionary() {
        if (!PathOUT.getText().trim().isEmpty()) {
            String postSaveStr = PathOUT.getText();
            File dictionary = new File(postSaveStr + "\\dictionaryText.txt");
            try {
                Desktop.getDesktop().edit(dictionary);
            } catch (Exception e) {

            }
        } else {
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

}
