package Model;

import javafx.collections.ObservableList;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


/**
 *
 */
public class Searcher {

    public Ranker ranker;
    public static Parse SearcherParser;
    public static HashMap<String, DictionaryDetailes> Loaded_Dictionary;
    public static HashMap<String, DocDetailes> Loaded_AllDocs;
    //public static HashMap<String, DocDetailes> Loaded_AllDocs;

    public static double AVGdl;
    public static double NumOfDocs;
    public static ObservableList<String> citiesToFilter;
    public static ArrayList<Pair> Results; //<<queryid,Docid>>
    public static String PathIN;


    /**
     * Constructor
     * @throws IOException
     */
    public Searcher(HashMap<String, DictionaryDetailes> dictionary,HashMap<String, DocDetailes> all_Docs,String PathOut,String PathIn) throws IOException {
        PathIN = PathIn;
        Results = new ArrayList<>();
        Loaded_Dictionary = dictionary;
        Loaded_AllDocs = all_Docs;
        NumOfDocs = Loaded_AllDocs.size();
        AVGdl = 0;
        ranker = new Ranker(PathOut);
    }

    public void setAVG(int avg){
        AVGdl = (avg/NumOfDocs);
    }


    /**
     * @param QueryPath
     * @throws IOException
     * @throws URISyntaxException
     */
    public void ProccesQueryFile(String QueryPath,boolean semanticNeeded,boolean Steemer) throws IOException, URISyntaxException {
        ArrayList<Pair> Queries = SplitQueriesFile(QueryPath);
        SearcherParser = new Parse(Steemer,PathIN + "\\stop_words.txt");
        for (int i = 0 ;i < Queries.size(); i++) {
            HashMap<String, TermDetailes> tmpQuery = SearcherParser.ParseDoc(Queries.get(i).getValue().toString(), "", "", "");
            HashSet<String> QueryWords = new HashSet<>(tmpQuery.keySet());
            try {
                ranker.InitializScores(QueryWords, Queries.get(i).getKey().toString(), semanticNeeded);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    /**
     * @param Query
     * @throws IOException
     * @throws URISyntaxException
     */
    public void ProccesSingleQuery(String Query,boolean semanticNeeded,boolean Steemer) throws IOException, URISyntaxException {
        SearcherParser = new Parse(Steemer,PathIN + "\\stop_words.txt");
        HashMap<String, TermDetailes> tmpQuery =  SearcherParser.ParseDoc(Query,"","","");
        HashSet<String> QueryWords = new HashSet<>(tmpQuery.keySet());
        ranker.InitializScores(QueryWords,"000",semanticNeeded);
    }


    /**
     * @param DocToSearch
     * @return
     */
    public static String EntityIdentification(String DocToSearch){
        String line = Loaded_AllDocs.get(DocToSearch).getDocSuspectedEntitys().toString();
        StringBuilder stb = new StringBuilder();
        stb.append("#### Doc Entitys ####\n");
        while(line.length() > 1){
            String term = line.substring(0,line.indexOf("-"));
            String tf = line.substring(line.indexOf("-")+1,line.indexOf(";"));
            stb.append(term + " " + tf + "\n");
            line = line.substring(line.indexOf(";")+1);
        }
        stb.append("#################\n");
        return stb.toString();
    }


    /**
     * @param QueriesDirectory
     * @return
     * @throws IOException
     */
    public ArrayList<Pair> SplitQueriesFile(String QueriesDirectory)throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader(QueriesDirectory));
        ArrayList<Pair> Result = new ArrayList<>(); // <Queryid,Query>
        StringBuilder stb = new StringBuilder();
        String line = bfr.readLine();int Q;
        while (line != null) {
            stb.append(" " + line);
            line = bfr.readLine();
        }
        String content = stb.toString();
        Document d = Jsoup.parse(content);
        Elements elements = d.getElementsByTag("top");
        for (Element element : elements) {
            StringBuilder QueryStb = new StringBuilder();
            String QueryID = element.getElementsByTag("num").toString();
            QueryID = QueryID.substring(QueryID.indexOf("Number:")+7,QueryID.indexOf("<title>"));
            QueryID = QueryID.trim();
            String QueryContent = element.getElementsByTag("title").text();
            String QueryDesc = element.getElementsByTag("desc").text();
            QueryDesc = QueryDesc.substring(QueryDesc.indexOf(":")+1,QueryDesc.indexOf("Narrative:"));
            QueryStb.append(QueryContent + QueryDesc);
            Pair p = new Pair(QueryID,QueryStb.toString());
            Result.add(p);
        }
        return Result;
    }

    public void AddCitiesToFilter(ObservableList<String> cities){
        citiesToFilter = cities;
    }



    /**
     * @param FileToSaveIn
     * @throws IOException
     */
    public static void WriteResults(File FileToSaveIn) throws IOException {
        FileWriter FW = new FileWriter(FileToSaveIn.getAbsolutePath()+"\\results.txt"); //todo
        //FileWriter FW = new FileWriter(FileToSaveIn.getAbsolutePath()+"/results.txt");
        for(int i = 0 ; i < Results.size();i++) {
            FW.write( Results.get(i).getKey()+ " 0" + " " + Results.get(i).getValue() + " 1" + " 00.00" + " test" + System.getProperty( "line.separator" ));
        }
        FW.close();
    }
}

