package Model;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Searcher {


    public String Query;
    public Indexer RankerIndexer;
    public Parse RankerParser;
    public Ranker ranker;
    public StringBuilder stbResult;

    public Searcher(String query, Indexer indexer, Parse parser, boolean semanticNeeded, boolean resultByCityNeeded) {
        Query = query;
        RankerIndexer = indexer;
        RankerParser = parser;
        ranker = new Ranker();
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
