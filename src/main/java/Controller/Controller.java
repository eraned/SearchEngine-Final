package Controller;

import Model.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.controlsfx.control.CheckListView;
import java.awt.*;
import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.*;
import java.util.HashSet;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    public javafx.scene.control.TextField SingleQuery;
    public javafx.scene.control.TextField PathQueriesFile;
    public javafx.scene.control.Button RunSingleQueryButton;
    public javafx.scene.control.Button BrowseQueryButton;
    public javafx.scene.control.Button runQueryFileButton;
    public javafx.scene.control.Button newSearchButton;
    public javafx.scene.control.ChoiceBox DocSelctor;
    public javafx.scene.control.TextField PathForResults;
    public javafx.scene.control.Button BrowseSaveResults;
    public javafx.scene.control.Button SaveResults;
    public javafx.scene.control.Button ShowIdentityForDoc;
    public CheckListView CitySelctor;


    private SearchEngine searchEngine;
    private Searcher searcher;
    public static HashSet<String> Controller_Languages;
    public static HashSet<String> Controller_Cities;
    public static int AVGdl;

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
            searchEngine = new SearchEngine(Pathin, Pathout,Stemmer.isSelected());
            LoadLangugesToScroll_Engine();
            LoadCitiesToScroll_Engine();
            showFirstRunMessage(getFinalDoc(),getFinalPositions());
            searchEngine.GetAllDocs().clear();
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

    private ArrayList<Integer> getFinalPositions() {
        return SearchEngine.ItsTimeFor_FinalPos();
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
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Message");
        alert.setHeaderText(null);
        alert.setContentText(str);
        alert.show();
    }

    public static void showFirstRunMessage(String str,ArrayList<Integer> Pos) {
        List<String> ans = new ArrayList<>();
        if(Pos != null) {
            for (Integer P : Pos) {
                ans.add(P.toString());
            }
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null,ans);
        dialog.setTitle("Search Engine Finished!");
        dialog.setHeaderText(str);
        dialog.setContentText("City Positions:");
        dialog.show();
    }


    /**
     * after the search engine run first time its initiate all the languages that he fing to new menu item
     */
    public void LoadLangugesToScroll_Engine(){
        for(String lang : SearchEngine.Languages) {
            LangSelctor.getItems().add(lang);
        }
    }



    /**
     * after the search engine run first time its initiate all the languages that he fing to new menu item
     */
    public void LoadLangugesToScroll_Disk(){
        for(String lang : Controller_Languages) {
            LangSelctor.getItems().add(lang);
        }
    }

    /**
     *
     */
    public void LoadCitiesToScroll_Engine(){
        for(String city : SearchEngine.Cities.keySet()) {
            CitySelctor.getItems().add(city);
        }
    }

    /**
     *
     */
    public void LoadCitiesToScroll_Disk(){
        for(String city : Controller_Cities) {
            CitySelctor.getItems().add(city);
        }
    }



    /**
     * this function after the user click the reset button delete all the search engine output.
     */
    public void resetAll() {
        File FileToReset;
        String Pathout = PathOUT.getText();
        if (Stemmer.isSelected())
            FileToReset = new File(Pathout + "\\EngineOut_WithStemmer\\"); //todo
            //FileToReset = new File(Pathout + "/EngineOut_WithStemmer/");

        else
            FileToReset = new File(Pathout + "\\EngineOut\\"); //todo
            //FileToReset = new File(Pathout + "/EngineOut/");


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
        SingleQuery.clear();
        PathQueriesFile.clear();
        PathForResults.clear();
        DocSelctor.getItems().clear();
        showAlert("Deleting files is complete!");
    }

    /**
     * open new window and display the Dictionary from the disk.
     */
    public void ShowDictionary() throws IOException{
        if (!PathOUT.getText().trim().isEmpty()) {
            String Pathout = PathOUT.getText();
            if(!Stemmer.isSelected()) {
                File dictionary = new File(Pathout + "\\EngineOut\\Dictionary.txt"); //todo
                //File dictionary = new File(Pathout + "/EngineOut/Dictionary.txt");
                Desktop.getDesktop().edit(dictionary);
            }
            else{
                File dictionary = new File(Pathout + "\\EngineOut_WithStemmer\\Dictionary.txt"); //todo
                //File dictionary = new File(Pathout + "/EngineOut_WithStemmer/Dictionary.txt");
                Desktop.getDesktop().edit(dictionary);
            }
        }
        else {
            showAlert("Please make sure that the Path out is valid!");
        }
    }

    /**
     * read the dic from the disk and initialized it to new data structure that save in the memory.
     */
    public void LoadDicToMemory() throws IOException {
        if (!Stemmer.isSelected()) {
            HashMap dic = ItsTimeToLoadDictionary(PathOUT.getText() + "/EngineOut/Dictionary.txt");
            HashMap docs = ItsTimeToLoadAllDocs(PathOUT.getText() + "/EngineOut/Docs.txt");
            searcher = new Searcher(dic, docs, PathOUT.getText() + "\\EngineOut\\", PathIN.getText());
            LoadCitiesToScroll_Disk();
            LoadLangugesToScroll_Disk();
            searcher.setAVG(AVGdl);
        } else {
            HashMap dic = ItsTimeToLoadDictionary(PathOUT.getText() + "/EngineOut/Dictionary.txt");
            HashMap docs = ItsTimeToLoadAllDocs(PathOUT.getText() + "/EngineOut/Docs.txt");
            searcher = new Searcher(dic,docs, PathOUT.getText() + "\\EngineOut_WithStemmer\\", PathIN.getText());
            LoadCitiesToScroll_Disk();
            LoadLangugesToScroll_Disk();
            searcher.setAVG(AVGdl);
        }
        PathIN.setDisable(false);
        PathOUT.setDisable(false);
        LoadDic.setDisable(false);
        ShoewDic.setDisable(false);
        SingleQuery.setDisable(false);
        PathQueriesFile.setDisable(false);
        BrowseQueryButton.setDisable(false);
        RunSingleQueryButton.setDisable(false);
        runQueryFileButton.setDisable(false);
        if (searcher != null)
            showAlert("Dictionary successfully loaded to Memory!");
        else
            showAlert("Dictionry failed to load in to the Memory!");
    }
    /**
     *
     */
    public void LoadDocsToScroll(){
        for(Pair pair : Searcher.Results) {
            DocSelctor.getItems().add(pair.getKey().toString() + " - " + pair.getValue().toString());
        }
    }


    /**
     * @throws IOException
     * @throws URISyntaxException
     */
    public void RunQuery() throws IOException, URISyntaxException {
        if(searcher == null) {
            if (!Stemmer.isSelected()) {
                HashMap dic = ItsTimeToLoadDictionary(PathOUT.getText() + "/EngineOut/Dictionary.txt");
                HashMap docs = ItsTimeToLoadAllDocs(PathOUT.getText() + "/EngineOut/Docs.txt");
                searcher = new Searcher(dic,docs , PathOUT.getText() + "/EngineOut/", PathIN.getText());
                LoadCitiesToScroll_Disk();
                LoadLangugesToScroll_Disk();
                searcher.setAVG(AVGdl);
            } else {
                HashMap dic = ItsTimeToLoadDictionary(PathOUT.getText() + "/EngineOut/Dictionary.txt");
                HashMap docs = ItsTimeToLoadAllDocs(PathOUT.getText() + "/EngineOut/Docs.txt");
                searcher = new Searcher(dic,docs, PathOUT.getText() + "/EngineOut_WithStemmer/", PathIN.getText());
                LoadCitiesToScroll_Disk();
                LoadLangugesToScroll_Disk();
                searcher.setAVG(AVGdl);
            }

        }
        if (CitySelctor.getCheckModel().getCheckedItems().size() > 1 || (CitySelctor.getCheckModel().getCheckedItems().size() == 1 && !CitySelctor.getCheckModel().getCheckedItems().equals("None"))) {
            ObservableList<String> cities = FXCollections.observableArrayList();
            cities.addAll(CitySelctor.getCheckModel().getCheckedItems());
            searcher.AddCitiesToFilter(cities);
        }
        if(!SingleQuery.getText().isEmpty())
            searcher.ProccesSingleQuery(SingleQuery.getText(),Semantic.isSelected(),Stemmer.isSelected());
        else if(!PathQueriesFile.getText().isEmpty())
            searcher.ProccesQueryFile(PathQueriesFile.getText(),Semantic.isSelected(),Stemmer.isSelected());


        else {
            showAlert("You must enter a Querry in the right place!");
            return;
        }
        LoadDocsToScroll();
        ShowIdentityForDoc.setDisable(false);
        BrowseSaveResults.setDisable(false);
        SaveResults.setDisable(false);
        PathForResults.setDisable(false);
        showAlert("Query search completed successfully! , The search results you'll see in Returned Documents. for new Search first click on the 'new Search' Button! " );
        newSearchButton.setDisable(false);
    }

    /**
     *
     */
    public void QueriesInput() {
        FileChooser FC = new FileChooser();
        FC.setTitle("Pick Directory for Queries!");
        File file = FC.showOpenDialog(null);
        if (file != null) {
            String Path = file.getAbsolutePath();
            PathQueriesFile.setText(Path);
        }
    }

    /**
     *
     */
    public void NewSearch() {
        File ResultsToReset;
        String ResultPath = PathForResults.getText() + "\\Results"; //todo
        //String ResultPath = PathForResults.getText() + "/Results";
        ResultsToReset = new File(ResultPath);
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
        SingleQuery.clear();
        PathQueriesFile.clear();
        PathForResults.clear();
        DocSelctor.getItems().clear();
        showAlert("ready for new Search!" );
    }

    /**
     *
     */
    public void ResultsInput() {
        DirectoryChooser DC = new DirectoryChooser();
        DC.setTitle("Pick Directory For saving Results!");
        File file = DC.showDialog(null);
        if (file != null) {
            String Path = file.getAbsolutePath();
            PathForResults.setText(Path);
        }
    }

    /**
     * @throws IOException
     */
    public void SaveResults() throws IOException {
        File SavedResultsFile = new File(PathForResults.getText() + "\\Results"); //todo
        //File SavedResultsFile = new File(PathForResults.getText() + "/Results");
        SavedResultsFile.mkdir();
        Searcher.WriteResults(SavedResultsFile);
        showAlert("Results Saved successfully!");
    }

    /**
     *
     */
    public void RunSearchIdentitis() {
        String docTosearch = DocSelctor.getSelectionModel().getSelectedItem().toString();
        docTosearch = docTosearch.substring(docTosearch.indexOf("-")+2);
        showAlert(searcher.EntityIdentification(docTosearch));
    }

    /**
     * reading line by line from disk and create new data structue that represent the Dictionary.
     *
     * @param Path - where from to load the Dictionary from disk to memory
     * @return
     */
    public static HashMap<String, DictionaryDetailes> ItsTimeToLoadDictionary(String Path) {
        HashMap<String, DictionaryDetailes> LoadedDic = new HashMap<>(); int index;
        try (BufferedReader br = new BufferedReader(new FileReader(Path))) {
            String line = br.readLine();
            int totalfreq = 0, df = 0, pointer = 0;
            while (line != null) {
                try {
                    index = line.indexOf(':');
                    String term = line.substring(0, index);
                    line = line.substring(index + 1);
                    if (!term.isEmpty()) {
                        index = line.indexOf("TotalTF:");
                        String TFreq = line.substring(index+8,line.indexOf(';'));
                        totalfreq = Integer.parseInt(TFreq);
                        line = line.substring(line.indexOf(';') + 1);
                        index = line.indexOf("DF:");
                        String DF = line.substring(index + 3, line.indexOf(';'));
                        df = Integer.parseInt(DF);
                        line = line.substring(line.indexOf(';') + 1);
                        index = line.indexOf("Pointer:");
                        String point = line.substring(index + 8,line.indexOf("#"));
                        pointer = Integer.parseInt(point);
                        DictionaryDetailes DD = new DictionaryDetailes();
                        DD.setNumOfTermInCorpus(totalfreq);
                        DD.setNumOfDocsTermIN(df);
                        DD.setPointer(pointer);
                        LoadedDic.put(term, DD);
                    }
                    line = br.readLine();
                } catch (Exception e) {
                    System.out.println("problem!");
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return LoadedDic;
    }

    /**
     * @param Path
     */
    public static HashMap<String, DocDetailes> ItsTimeToLoadAllDocs(String Path) {
        HashMap<String, DocDetailes> Ans = new HashMap<>();
        int index;
        int Doclength;
        String DocCity;
        String DocEntitys;
        double tmp = 0;
        double counter = 0;
        int max_tf;
        String term;
        String tf;
        int DocsLengthCounter = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(Path))) {
            //upload languges
            Controller_Languages = new HashSet<>();
            String languuges = br.readLine();
            index = languuges.indexOf(':');
            languuges = languuges.substring(index + 1);
            while(languuges.length() > 1) {
                String l = languuges.substring(0,languuges.indexOf(";"));
                Controller_Languages.add(l);
                languuges = languuges.substring(languuges.indexOf(";")+1);
            }
            //upload cities
            Controller_Cities = new HashSet<>();
            String cities = br.readLine();
            index = cities.indexOf(':');
            cities = cities.substring(index + 1);
            while(cities.length() > 1){
                String c = cities.substring(0,cities.indexOf(";"));
                Controller_Cities.add(c);
                cities = cities.substring(cities.indexOf(";")+1);
            }
            //upload alldocs
            String line = br.readLine();
            while (line != null) {
                try {
                    index = line.indexOf(':');
                    String doc = line.substring(0, index);
                    line = line.substring(index + 1);
                    if (!doc.isEmpty()) {
                        index = line.indexOf("DocLength:");
                        String Length = line.substring(index+ 10, line.indexOf(';'));
                        Doclength = Integer.parseInt(Length);
                        DocsLengthCounter += Doclength;
                        line = line.substring(line.indexOf(';') + 1);
                        index = line.indexOf("MaxTermFrequency:");
                        String max = line.substring(index+ 17, line.indexOf(';'));
                        max_tf = Integer.parseInt(max);
                        line = line.substring(line.indexOf(';') + 1);
                        index = line.indexOf("City:");
                        DocCity = line.substring(index + 5,line.indexOf(';')+1);
                        if(DocCity.length() == 1)
                            DocCity = "";
                        DocEntitys = line.substring(line.indexOf(';') + 1);
                        DocDetailes DD = new DocDetailes(null,null,null,DocCity);
                        DD.setDocLength(Doclength);
                        DD.setMaxTermFrequency(max_tf);
                        DD.getDocSuspectedEntitys().append(DocEntitys);
                        Ans.put(doc,DD);
                    }
                    line = br.readLine();
                } catch (Exception e) {
                    System.out.println("problem load docs!");
                    break;
                }
            }
            AVGdl = DocsLengthCounter;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Ans;
    }
}
