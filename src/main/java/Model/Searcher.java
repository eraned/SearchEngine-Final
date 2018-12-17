package Model;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Searcher {


    public String Query;
    public Ranker ranker;
    public Indexer RankerIndexer;
    public Parse RankerParser;
    public ReadFile RankerReadfile;
    public StringBuilder stbResult;

    public Searcher(String query, Indexer indexer, Parse parser, ReadFile readFile, boolean semanticNeeded, boolean resultByCityNeeded, boolean stemmerNeeded) {
        Query = query;
        RankerIndexer = indexer;
        RankerParser = parser;
        RankerReadfile = readFile;
        ranker = new Ranker(RankerIndexer,RankerParser,RankerReadfile,stemmerNeeded);
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


    public void ProccesQuery(){

    }

    public void EntityIdentification(){

    }
}
