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
    public Indexer RankerIndexer;
    public Parse RankerParser;
    public ReadFile RankerReadfile;
    public boolean SemanticNeeded;
    public boolean ResultByCityNeeded;
    public boolean StemmerNeeded;
    public StringBuilder stbResult;
    public HashMap<String, DictionaryDetailes> LoadedDictionary;
    public HashMap<String, StringBuilder> LoadedDocs;


    public Searcher(Indexer indexer, Parse parser, ReadFile readFile, boolean semanticNeeded, boolean resultByCityNeeded, boolean stemmerNeeded) throws IOException {
        RankerIndexer = indexer;
        RankerParser = parser;
        RankerReadfile = readFile;
        SemanticNeeded = semanticNeeded;
        ResultByCityNeeded = resultByCityNeeded;
        StemmerNeeded = stemmerNeeded;
        stbResult = new StringBuilder();

        if(semanticNeeded){
            stbResult.append(RankerIndexer.stbOUT.toString() + "/Results_WithSemantic/");
        }
        else
            stbResult.append(RankerIndexer.stbOUT.toString() + "/Results/");

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

        ranker = new Ranker(RankerIndexer.stbOUT.toString());
        LoadedDictionary = LoadDicToMemory();
        LoadedDocs = LoadDocsToMemory();
    }


    public void ProccesQuery(String QueryPath) throws IOException {
        ArrayList<String> Queries = SplitQueriesFile(QueryPath);
        for(String query : Queries) {
            ranker.InitializWeights(query);
            ranker.RankDocs();
        }
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
        return SearchEngine.indexer.ItsTimeToLoadDictionary(  RankerIndexer.stbOUT.toString() + "Dictionary.txt");
    }

    public HashMap<String, StringBuilder> LoadDocsToMemory() throws IOException {
        return SearchEngine.ItsTimeToLoadAllDocs(  RankerIndexer.stbOUT.toString() + "Docs.txt");
    }
}
