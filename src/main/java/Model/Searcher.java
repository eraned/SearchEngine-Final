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

public class Searcher {

    public Ranker ranker;
    public Indexer RankerIndexer;
    public Parse RankerParser;
    public ReadFile RankerReadfile;
    public boolean SemanticNeeded;
    public boolean ResultByCityNeeded;
    public boolean StemmerNeeded;
    public StringBuilder stbResult;

    public Searcher(Indexer indexer, Parse parser, ReadFile readFile, boolean semanticNeeded, boolean resultByCityNeeded, boolean stemmerNeeded) {
        RankerIndexer = indexer;
        RankerParser = parser;
        RankerReadfile = readFile;
        SemanticNeeded = semanticNeeded;
        ResultByCityNeeded = resultByCityNeeded;
        StemmerNeeded = stemmerNeeded;
        ranker = new Ranker(RankerIndexer,RankerParser,RankerReadfile,StemmerNeeded);
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
    }


    public void ProccesQuery(String QueryPath) throws IOException {
        ArrayList<String> Queries = SplitQueriesFile(QueryPath);








    }

    public void EntityIdentification(){

    }

    public void ReturnResults(){

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
}
