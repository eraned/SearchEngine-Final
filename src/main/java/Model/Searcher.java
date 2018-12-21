package Model;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

public class Searcher {

    public Ranker ranker;
    public Indexer SearcherIndexer;
    public Parse SearcherParser;
    // public ReadFile RankerReadfile;
    public boolean SemanticNeeded;
    public boolean ResultByCityNeeded;
    public boolean StemmerNeeded;
    public StringBuilder stbResult;
    public static HashMap<String, DictionaryDetailes> LoadedDictionary;
    public static HashMap<String, String> LoadedDocs;
    public static ArrayList<String> Results;


    public Searcher(Indexer indexer, Parse parser, ReadFile readFile, boolean semanticNeeded, boolean resultByCityNeeded, boolean stemmerNeeded) throws IOException {
        SearcherIndexer = indexer;
        SearcherParser = parser;
        //RankerReadfile = readFile;
        SemanticNeeded = semanticNeeded;
        ResultByCityNeeded = resultByCityNeeded;
        StemmerNeeded = stemmerNeeded;
        stbResult = new StringBuilder();
        Results = new ArrayList<>();

        if(semanticNeeded){
            stbResult.append(SearcherIndexer.stbOUT.toString() + "/Results_WithSemantic/");
        }
        else
            stbResult.append(SearcherIndexer.stbOUT.toString() + "/Results/");

        File folder = new File(stbResult.toString());
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles!=null) {
            for (int i = 0; i < listOfFiles.length; i++)
                try {
                    Files.deleteIfExists(listOfFiles[i].toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        else{
            folder.mkdir();
        }

        LoadedDictionary = LoadDicToMemory();
        LoadedDocs = LoadDocsToMemory();
        ranker = new Ranker(SearcherIndexer.stbOUT.toString());
    }


    public void ProccesQueryFile(String QueryPath) throws IOException {
        ArrayList<String> Queries = SplitQueriesFile(QueryPath);
        for(String query : Queries) {
            HashMap<String, TermDetailes> tmpQuery =  SearcherParser.ParseDoc(query,"Q","","");
            ranker.InitializScores(tmpQuery);
        }
    }

    public void ProccesSingleQuery(String Query) throws IOException {
        HashMap<String, TermDetailes> tmpQuery =  SearcherParser.ParseDoc(Query,"Q","","");
        ranker.InitializScores(tmpQuery);
    }



    public void EntityIdentification(){




    }

    public ArrayList<String> SplitQueriesFile(String QueriesDirectory)throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader(QueriesDirectory));
        ArrayList<String> Result = new ArrayList<>();
        StringBuilder stb = new StringBuilder();
        String line = bfr.readLine();
        while (line != null) {
            stb.append(" " + line);
            line = bfr.readLine();
        }
        String content = stb.toString();
        Document d = Jsoup.parse(content);
        Elements elements = d.getElementsByTag("top");
        for (Element element : elements) {
            //     String QueryID = element.getElementsByTag("num").text(); //todo - check how to get Query id only..dont have </num>
            String QueryContent = element.getElementsByTag("title").text();
            Result.add(QueryContent);
        }
        return Result;
    }

    public HashMap<String, DictionaryDetailes> LoadDicToMemory() throws IOException {
        return SearchEngine.indexer.ItsTimeToLoadDictionary(  SearcherIndexer.stbOUT.toString() + "Dictionary.txt");
    }

    public HashMap<String, String> LoadDocsToMemory() throws IOException {
        return SearchEngine.ItsTimeToLoadAllDocs(  SearcherIndexer.stbOUT.toString() + "Docs.txt");
    }


    public static String ItsTimeFor_Results() {

        StringBuilder stb = new StringBuilder();
        stb.append("##############  Final Results  ##############.\n");
        for(int i = 0 ; i < Results.size();i++) {
            //  System.out.println(SortedRank.get(i) + "<->" +RankedQuery.get(SortedRank.get(i)));
            stb.append(Results.get(i)+"\n");
        }
        stb.append("##############  Finished  ##############.\n");
        return stb.toString();
    }

}
