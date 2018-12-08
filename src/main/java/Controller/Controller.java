package Controller;

import Model.SearchEngine;
import Model.Indexer;
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


    public javafx.scene.control.TextField MainPath;
    public javafx.scene.control.Button MainPathBrowse;

    public javafx.scene.control.TextField PostSavePath;
    public javafx.scene.control.Button PostSavePathBrowse;

    public javafx.scene.control.CheckBox Stemmer;

    public javafx.scene.control.TextField CacheDicPathSave;
    public javafx.scene.control.Button CacheDicPathSavePath;

    public javafx.scene.control.TextField LoadCacheDic;
    public javafx.scene.control.Button LoadCacheDicPath;

    public javafx.scene.control.Button start;
    public javafx.scene.control.Button reset;
    public javafx.scene.control.Button cache;
    public javafx.scene.control.Button dic;

    public javafx.scene.control.Button loadSearchFiles;
    public javafx.scene.control.TextField singleQuery;
    public javafx.scene.control.Button runSingleQuery;
    public javafx.scene.control.TextField pathForQueries;
    public javafx.scene.control.Button browseQuery;
    public javafx.scene.control.Button runAllQuery;
    public javafx.scene.control.Button resetSearch;
    public javafx.scene.control.CheckBox top5;

    public javafx.scene.control.TextField preSearchPath;
    public javafx.scene.control.CheckBox StemmerPartB;
    public javafx.scene.control.Button PreButtom;

    public javafx.scene.control.TextField SavePathB;
    public javafx.scene.control.Button BrowseSaveB;
    public javafx.scene.control.Button SaveSave;
    public javafx.scene.control.TextField SavePathBFileName;

    public javafx.scene.control.CheckBox extend;

    private HashMap<String,String> docHashObject;
    private HashMap<String,StringBuilder> dicObject;
    private HashMap<String,Double> docAllWijObject;
    private HashMap<String,String> cacheObject;
    private HashMap<String,String> docToFileObject;

    /**
     * The array which we write to the file at save button.
     */
    public ArrayList<String> ansTrec;

    /**
     *  Gets the path that we picked for the corpus and the stop words.
     */
    public void GetCorpus(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Pick corpus directory");
        File f = dc.showDialog(null);
        if(f!=null)
        {
            String name = f.getAbsolutePath();
            MainPath.setText(name);
        }
    }

    /**
     * Gets the path which we will save the final posting file.
     */
    public void GetPostSavePath(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Pick corpus directory");
        File f = dc.showDialog(null);
        if(f!=null)
        {
            String name = f.getAbsolutePath();
            PostSavePath.setText(name);
            dic.setDisable(false);
        }
        else {
            dic.setDisable(false);
        }

    }

    /**
     *  Gets the path which we will save the cache and the dictionary.
     */
    public void GetCacheDicBrowse(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Pick cache and dic directory");
        File f = dc.showDialog(null);
        if(f!=null)
        {
            String name = f.getAbsolutePath();
            CacheDicPathSave.setText(name);
            cache.setDisable(false);
        }
    }

    /**
     * Function that gets the Path which we want to load the dictionary and the cache from,
     * and load the cache and dictionary into HashMaps.
     */

    public void LoadCacheDic(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("dictionary");
        File f = dc.showDialog(null);
        if(f!=null)
        {
            String name = f.getAbsolutePath();
            LoadCacheDic.setText(name);
        }

        HashMap<String,StringBuilder> LoadDic = new HashMap<>();
        HashMap<String,String> LoadCache = new HashMap<>();
        try {
            LoadDic = Indexer.getDictionry(LoadCacheDic.getText());
        }
        catch (Exception e){

        }
        if(LoadDic != null){
            showAlert("Dic and cache successfully load");
        }
        else {
            showAlert("dic and cache failed to load");
        }

    }

    /**
     * This function is activated when the Start button is clicked,
     * we check the the three paths aren't empty and we start the whole process
     * parsing -> stemming if necessary -> create temp posting -> create dictionary and cache + save them -> create final posting File
     */
    public void startEngine() throws IOException ,URISyntaxException {

        SearchEngine searchEngine;
        searchEngine = new SearchEngine( "/Users/eranedri/Documents/SearchEngineCorpuse/Postingcorpus","/Users/eranedri/Documents/SearchEngineCorpuse/Postingcorpus",false);
        System.exit(0);
        showAlert("please choose corpus and stop words path\nand Posting file save path\nand save cache and dic path");

//        int sizeDoc;
//        long f = System.currentTimeMillis();
//        if(!MainPath.getText().trim().isEmpty() && !PostSavePath.getText().trim().isEmpty() && !CacheDicPathSave.getText().trim().isEmpty()) {
//            String corpusPath = MainPath.getText()+"\\corpus";
//            String stopWordsPath=MainPath.getText()+"\\stop_words.txt";
            MainPathBrowse.setDisable(true);
            PostSavePathBrowse.setDisable(true);
            String postSaveStr = PostSavePath.getText();
            String mainPathStr = MainPath.getText();
//            Indexer i = new Indexer();
//            sizeDoc = i.createPostingFile(corpusPath,stopWordsPath,Stemmer.isSelected());
//            //long u = System.currentTimeMillis();
//            //System.out.println(u-f);
//            try {
//                Indexer.ExternalSort(postSaveStr,Stemmer.isSelected(),CacheDicPathSave.getText(),sizeDoc);
//                HashMap<String,Double> DocAllWijTable = Indexer.getDocAllWij(Stemmer.isSelected(),postSaveStr);
//                FileOutputStream fout;
//                if(Stemmer.isSelected())
//                {
//                    fout = new FileOutputStream("DocAllWijTableWithStemmer");
//                }
//                else
//                {
//                    fout = new FileOutputStream("DocAllWijTableWithOutStemmer");
//                }
//                // FileOutputStream fout = new FileOutputStream("DocAllWijTable");
//                ObjectOutputStream oos = new ObjectOutputStream(fout);
//                oos.writeObject(DocAllWijTable);
//                oos.close();
//                start.setDisable(true);
//            } catch (Exception e) {
//
//            }
//            String finalPosting;
//            if(Stemmer.isSelected()) {
//                finalPosting = PostSavePath.getText() + "\\postingTableWithStemmer";//postingTable the name of the posting file.
//            }
//            else
//            {
//                finalPosting = PostSavePath.getText() + "\\postingTableWithoutStemmer";//postingTable the name of the posting file.
//            }
//            String cachePath="";
//            if(Stemmer.isSelected())
//            {
//                cachePath = PostSavePath.getText()+"\\finalCacheWithStemmer";
//            }
//            else
//            {
//                cachePath = PostSavePath.getText()+"\\finalCacheWithOutStemmer";
//            }
//            // cachePath = PostSavePath.getText()+"\\finalCache";
//            long t = System.currentTimeMillis();
//            //System.out.println(t - f);
//            showAlert(getFinalAlert(sizeDoc,t-f,finalPosting,cachePath));
//            MainPathBrowse.setDisable(false);
//            PostSavePathBrowse.setDisable(false);
//            reset.setDisable(false);
//        }
//        else{
      //      showAlert("please choose corpus and stop words path\nand Posting file save path\nand save cache and dic path");
  //      }

    }

    /**
     * This function return the String of the alert which will be showed after we finish the index process.
     * @param sizeDocs - The number of all the docs that we have indexed during our process.
     * @param time - The time in milliseconds which took the process to finish.
     * @param finalPostringPath - The path to the final posting file.
     * @param finalCachePath - The path to the cache file.
     * @return the String message.
     */
//    public String getFinalAlert (int sizeDocs, long time,String finalPostringPath,String finalCachePath)
//    {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Time to run: "+time/1000 +" seconds.\n");
//        File postingFile = new File(finalPostringPath);
//        sb.append("The size of the posting file is "+postingFile.length()+" bytes.\n"); // .length()
//        File cacheFile = new File (finalCachePath);
//        sb.append("The size of the cache file is "+cacheFile.length()+" bytes.\n");
//        sb.append("Number of docs indexed is "+sizeDocs+".\n");
//        return sb.toString();
//    }

    /**
     * The function which delete all the files that we have created from the run,
     * The cache,dictionary,final posting file,all the temp postings, and the doctext.
     */
    public void reset(){
        // need to add cache delete!
        File postingFile;
        String postSaveStr = PostSavePath.getText();
        String cacheDicPath= CacheDicPathSave.getText();
        File dictionaryText = new File(cacheDicPath+"\\dictionaryText.txt");
        File dictionary = new File (cacheDicPath +"\\Dictionary");
        if(Stemmer.isSelected()) {
            postingFile = new File(postSaveStr + "\\postingTableWithStemmer");
        }
        else
        {
            postingFile = new File(postSaveStr + "\\postingTableWithutStemmer");
        }
        File tempPostingDir = new File("tempPostingDir");
//        File docsTextFile = new File("DocsText");
//        File cache = new File(cacheDicPath+"\\cache.txt");
//        File cacheText = new File(cacheDicPath+"\\finalCache");
        if(tempPostingDir.exists())
        {
            File [] fileList =tempPostingDir.listFiles();
            for (int i = 0 ; i <fileList.length;i++) {
                File textFileDirectory = fileList[i];
                if(textFileDirectory.exists())
                {
                    textFileDirectory.delete();
                }
            }
            tempPostingDir.delete();
        }
//        if(cache.exists())
//        {
//            cache.delete();
//        }
//        if(cacheText.exists())
//        {
//            cacheText.delete();
//        }
//        if(docsTextFile.exists())
//        {
//            docsTextFile.delete();
//        }
        if(dictionaryText.exists())
        {
            dictionaryText.delete();
        }
        if(dictionary.exists())
        {
            dictionary.delete();
        }
        if(postingFile.exists())
        {
            postingFile.delete();
        }
        MainPath.setText("");
        PostSavePath.setText("");
        CacheDicPathSave.setText("");
        LoadCacheDic.setText("");
        reset.setDisable(true);
        dic.setDisable(true);
        start.setDisable(false);
    }

    /**
     * This function open the notepad,
     * And show the cache inside it.
     */
//    public void showCache(){
//        if(!PostSavePath.getText().trim().isEmpty()) {
//            String postSaveStr = PostSavePath.getText();
//            File cache = new File(postSaveStr + "\\cache.txt");
//            try {
//                Desktop.getDesktop().edit(cache);
//            } catch (Exception e) {
//
//            }
//        }
//        else{
//            showAlert("choose dic and posting save path");
//        }
//    }

    /**
     * This function open the notepad,
     * And show the dictionary inside it as the requested format.
     */
    public void showDic(){
        if(!PostSavePath.getText().trim().isEmpty()) {
            String postSaveStr = PostSavePath.getText();
            File dictionary = new File(postSaveStr + "\\dictionaryText.txt");
            try {
                Desktop.getDesktop().edit(dictionary);
            } catch (Exception e) {

            }
        }
        else{
            showAlert("choose dic and posting save path");
        }
    }

    /**
     * This function gets a String which is the content of the message
     * And open up an alert with this content
     * @param str - The string that we want to show at the alert.
     */
    public static void showAlert(String str){
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setContentText(str);
        a.show();
    }

    /**
     * this function load all the files : Dictionary, cache.
     */
    public void searchLoad(){
        // we load here all the files for search: dic cache docAllWijTable docToMaxTf
        if(!preSearchPath.getText().trim().isEmpty()) {
            String path = preSearchPath.getText().trim();
            try {
                FileInputStream fin;
                FileInputStream fin1;
                FileInputStream fin2;
                FileInputStream fin3;
                if(StemmerPartB.isSelected()) {
//                    fin = new FileInputStream("DocToMaxTfWithStemmer");
//                    ObjectInputStream ois = new ObjectInputStream(fin);
//                    this.docHashObject = (HashMap<String, String>) ois.readObject();

                    fin1 = new FileInputStream(path + "\\DictionaryWithStemmer");
                    ObjectInputStream ois1 = new ObjectInputStream(fin1);
                    this.dicObject = (HashMap<String, StringBuilder>) ois1.readObject();


//                    fin2 = new FileInputStream("DocAllWijTableWithStemmer");
//                    ObjectInputStream ois2 = new ObjectInputStream(fin2);
//                    this.docAllWijObject = (HashMap<String, Double>) ois2.readObject();
//
//
//                    fin3 = new FileInputStream(path + "\\finalCacheWithStemmer");
//                    ObjectInputStream ois3 = new ObjectInputStream(fin3);
//                    this.cacheObject = (HashMap<String, String>) ois3.readObject();
                }
                else
                {
//                    fin = new FileInputStream("DocToMaxTfWithOutStemmer");
//                    ObjectInputStream ois = new ObjectInputStream(fin);
//                    this.docHashObject = (HashMap<String, String>) ois.readObject();

                    fin1 = new FileInputStream(path + "\\DictionaryWithOutStemmer");
                    ObjectInputStream ois1 = new ObjectInputStream(fin1);
                    this.dicObject = (HashMap<String, StringBuilder>) ois1.readObject();

//
//                    fin2 = new FileInputStream("DocAllWijTableWithOutStemmer");
//                    ObjectInputStream ois2 = new ObjectInputStream(fin2);
//                    this.docAllWijObject = (HashMap<String, Double>) ois2.readObject();
//
//
//                    fin3 = new FileInputStream(path + "\\finalCacheWithOutStemmer");
//                    ObjectInputStream ois3 = new ObjectInputStream(fin3);
//                    this.cacheObject = (HashMap<String, String>) ois3.readObject();
                }

                FileInputStream fin4 = new FileInputStream("DocToFile");
                ObjectInputStream ois4 = new ObjectInputStream(fin4);
                this.docToFileObject = (HashMap<String, String>) ois4.readObject();

                showAlert("all files successfully loaded");

                //open all the buttoms and fileds//////////////////////////
                loadSearchFiles.setDisable(true);
 //               singleQuery.setDisable(false);
  //              runSingleQuery.setDisable(false);
 //               pathForQueries.setDisable(false);
  //              browseQuery.setDisable(false);
 //               runAllQuery.setDisable(false);
                resetSearch.setDisable(false);
                top5.setDisable(false);
                extend.setDisable(false);


                //now power off the indexer buttoms and fileds/////////////
                MainPath.setDisable(true);
                MainPathBrowse.setDisable(true);
                PostSavePath.setDisable(true);
                PostSavePathBrowse.setDisable(true);
                Stemmer.setDisable(true);
                CacheDicPathSave.setDisable(true);
                CacheDicPathSavePath.setDisable(true);
                LoadCacheDic.setDisable(true);
                LoadCacheDicPath.setDisable(true);
                start.setDisable(true);
                reset.setDisable(true);
                cache.setDisable(true);
                dic.setDisable(true);
                preSearchPath.setDisable(true);
                StemmerPartB.setDisable(true);
                PreButtom.setDisable(true);


            } catch (Exception e) {
                showAlert("problem in loading files");
            }
        }
        else
        {
            showAlert("Fill the load path");
        }
    }



      /**
     * this function reset all the data structers and the file that we saved
     */
    public void resetSearch(){
        MainPath.setDisable(false);
        MainPathBrowse.setDisable(false);
        PostSavePath.setDisable(false);
        PostSavePathBrowse.setDisable(false);
        Stemmer.setDisable(false);
        CacheDicPathSave.setDisable(false);
        CacheDicPathSavePath.setDisable(false);
        LoadCacheDic.setDisable(false);
        LoadCacheDicPath.setDisable(false);
        start.setDisable(false);
        ////////////////////////////////////////////
        loadSearchFiles.setDisable(false);
        singleQuery.setDisable(true);
        runSingleQuery.setDisable(true);
        pathForQueries.setDisable(true);
        browseQuery.setDisable(true);
        runAllQuery.setDisable(true);
        resetSearch.setDisable(true);
        top5.setDisable(true);
        preSearchPath.setDisable(false);
        StemmerPartB.setDisable(false);
        PreButtom.setDisable(false);
        extend.setDisable(true);

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        SavePathB.setDisable(true);
        BrowseSaveB.setDisable(true);
        SaveSave.setDisable(true);
        SavePathBFileName.setDisable(true);
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if(!SavePathB.getText().trim().isEmpty() && !SavePathBFileName.getText().trim().isEmpty()) {
            String Folderpath = SavePathB.getText().trim();
            String FileName = SavePathBFileName.getText().trim();
            String path = Folderpath + "\\" + FileName + ".txt";
            File f = new File(path);
            if(f.exists())
            {
                f.delete();
            }
            File result = new File("results.txt");
            if(result.exists())
            {
                result.delete();
            }
            showAlert("Files have delted successfully");
        }
        else
        {
            showAlert("Please fill all the required fields.");
        }
    }

    /**
     * this function gets the PreFile directory path
     */
    public void browsePreSearch(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Pick PreFiles directory");
        File f = dc.showDialog(null);
        if(f!=null)
        {
            String name = f.getAbsolutePath();
            preSearchPath.setText(name);
        }
    }

    /**
     * this function gets the SaveFile directory path
     */
    public void BrowseB(){
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Pick SaveFile Path");
        File f = dc.showDialog(null);
        if(f!=null)
        {
            String name = f.getAbsolutePath();
            SavePathB.setText(name);
        }
    }
}
