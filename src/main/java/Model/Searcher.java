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
import java.util.*;


/**
 *
 */
public class Searcher {

    public Ranker ranker;
    public static Parse SearcherParser;
    public boolean SemanticNeeded;
    public boolean Steemerneeded;
    public static HashMap<String, DictionaryDetailes> Loaded_Dictionary;
    public static HashMap<String, DocDetailes> Loaded_AllDocs;
    //public static HashMap<String,String> DocsResultEntitys;
    public static double AVGdl;
    public static double NumOfDocs;
    public int counter;
    public static ObservableList<String> citiesToFilter;
    public static ArrayList<Pair> Results; //<<queryid,Docid>>





    /**
     * Constructor
     * @param semanticNeeded
     * @throws IOException
     */
    public Searcher(HashMap<String, DictionaryDetailes> dictionary,HashMap<String, DocDetailes> all_Docs,boolean semanticNeeded,boolean Steemer,String PathOut,String PathIn) throws IOException {
        SearcherParser = new Parse(Steemer,PathIn);
        SemanticNeeded = semanticNeeded;
        Steemerneeded = Steemer;
        //DocsResultEntitys = new HashMap<>();
        Results = new ArrayList<>();
        Loaded_Dictionary = dictionary;
        Loaded_AllDocs = all_Docs;
        NumOfDocs = Loaded_AllDocs.size();
        counter = 0;
        for(String doc : Loaded_AllDocs.keySet()){
            counter += Loaded_AllDocs.get(doc).getDocLength();
        }
        AVGdl = counter/NumOfDocs;
        ranker = new Ranker(PathOut,Steemerneeded);
    }


    /**
     * @param QueryPath
     * @throws IOException
     * @throws URISyntaxException
     */
    public void ProccesQueryFile(String QueryPath) throws IOException, URISyntaxException {
        ArrayList<Pair> Queries = SplitQueriesFile(QueryPath);
        for (int i = 0 ;i < Queries.size(); i++) {
            HashMap<String, TermDetailes> tmpQuery = SearcherParser.ParseDoc(Queries.get(i).getValue().toString(), "", "", "");
            HashSet<String> QueryWords = new HashSet<>(tmpQuery.keySet());
            ranker.InitializScores(QueryWords, Queries.get(i).getKey().toString(), SemanticNeeded);
        }

    }

    /**
     * @param Query
     * @throws IOException
     * @throws URISyntaxException
     */
    public void ProccesSingleQuery(String Query) throws IOException, URISyntaxException {
        HashMap<String, TermDetailes> tmpQuery =  SearcherParser.ParseDoc(Query,"","","");
        HashSet<String> QueryWords = new HashSet<>(tmpQuery.keySet());
        ranker.InitializScores(QueryWords,"000",SemanticNeeded);
    }


    /**
     * @param DocToSearch
     * @return
     */
    public static String EntityIdentification(String DocToSearch){
        return Loaded_AllDocs.get(DocToSearch).getDocSuspectedEntitys().toString();
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
            QueryStb.append(element.getElementsByTag("title").text());
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
        //FileWriter FW = new FileWriter(FileToSaveIn.getAbsolutePath()+"\\results.txt"); //todo
        FileWriter FW = new FileWriter(FileToSaveIn.getAbsolutePath()+"/results.txt");
        for(int i = 0 ; i < Results.size();i++) {
            FW.write( Results.get(i).getKey()+ " 0" + " " + Results.get(i).getValue() + " 1" + " 00.00" + " test" + System.getProperty( "line.separator" ));
        }
        FW.close();
    }


    //    public StringBuilder ProccessNarrative(String Nar){
//        StringBuilder ans = new StringBuilder();
//
//
//
//
//
//
//        return ans;
//    }

}

