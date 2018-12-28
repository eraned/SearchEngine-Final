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
    public static Indexer SearcherIndexer;
    public static Parse SearcherParser;
    public boolean SemanticNeeded;
    public boolean StemmerNeeded;
    public StringBuilder stbResult;
    public static HashMap<String, DictionaryDetailes> LoadedDictionary;
    public static double AVGdl;
    public static double NumOdDocs;
    public static HashMap<String, Double> DocsResultDL;
    public static HashMap<String, Double> DocsResultMax;
    public static HashMap<String, String> DocsResultCITY;
    public static HashMap<String,HashMap<String, Double>> DocsResultEntitys;
    public static ObservableList<String> citiesToFilter;

    public static ArrayList<Pair> Results; //<<queryid,Docid>>


    /**
     * Constructor
     * @param indexer
     * @param parser
     * @param semanticNeeded
     * @param stemmerNeeded
     * @param cities
     * @throws IOException
     */
    public Searcher(Indexer indexer, Parse parser, boolean semanticNeeded,boolean stemmerNeeded,ObservableList<String> cities) throws IOException {
        SearcherIndexer = indexer;
        SearcherParser = parser;
        SemanticNeeded = semanticNeeded;
        StemmerNeeded = stemmerNeeded;
        AVGdl = 0;
        NumOdDocs = 0;
        if(cities != null)
            citiesToFilter = cities;
        DocsResultDL = new HashMap<>();
        DocsResultCITY = new HashMap<>();
        DocsResultMax = new HashMap<>();
        DocsResultEntitys = new HashMap<>();
        stbResult = new StringBuilder();
        Results = new ArrayList<>();
        if(indexer.Dictionary.isEmpty())
        LoadedDictionary = SearchEngine.ItsTimeToLoadDictionary(SearcherIndexer.stbOUT.toString() + "Dictionary.txt");
        else
            LoadedDictionary = indexer.Dictionary;
        SearchEngine.ItsTimeToLoadAllDocs(  SearcherIndexer.stbOUT.toString() + "Docs.txt");
        ranker = new Ranker(SearcherIndexer.stbOUT.toString());
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
            ranker.InitializScores(tmpQuery, Queries.get(i).getKey().toString(), SemanticNeeded);
        }

    }

    /**
     * @param Query
     * @throws IOException
     * @throws URISyntaxException
     */
    public void ProccesSingleQuery(String Query) throws IOException, URISyntaxException {
        HashMap<String, TermDetailes> tmpQuery =  SearcherParser.ParseDoc(Query,"","","");
        ranker.InitializScores(tmpQuery,"000",SemanticNeeded);
    }


    /**
     * @param Entitys
     * @param DocToSearch
     * @return
     */
    public static String EntityIdentification(HashSet<String> Entitys,String DocToSearch){
        HashMap<String,Double> tmp = new HashMap<>();
        HashMap<Double,String> ans = new HashMap<>();
        StringBuilder stb = new StringBuilder().append("#### Entitys Result ####\n");
        for(String term : DocsResultEntitys.get(DocToSearch).keySet()){
            if(Entitys.contains(term)){
                tmp.put(term.toUpperCase(),DocsResultEntitys.get(DocToSearch).get(term));
            }
        }
        for(String term : tmp.keySet()){
            ans.put(tmp.get(term),term);
        }
        ArrayList<Double> SortedEntitys = new ArrayList<>(ans.keySet());
        Collections.sort(SortedEntitys, Collections.reverseOrder());
        for(int i = 0 ; i < SortedEntitys.size() && i < 5  ;i++){
            stb.append(ans.get(SortedEntitys.get(i)) + " " + SortedEntitys.get(i) +"\n");
        }
        stb.append("###################\n");
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
            String QueryID = element.getElementsByTag("num").toString();
            QueryID = QueryID.substring(QueryID.indexOf("Number:")+7,QueryID.indexOf("<title>"));
            QueryID = QueryID.trim();
            String QueryContent = element.getElementsByTag("title").text();
            Pair p = new Pair(QueryID,QueryContent);
            Result.add(p);
        }
        return Result;
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

}

