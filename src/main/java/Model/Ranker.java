package Model;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Ranker {

    public String PostingPath;

    public Ranker(String pathforFindposting) {
        PostingPath =  pathforFindposting;
    }

    public void InitializScores(HashMap<String, TermDetailes> QueryAfterParse) throws IOException { //Matrix of columns: d1,d2,d3....q     rows: t1,t2,t3....
        HashMap<String, HashMap<String, Double>> CosSim_Matrix = new HashMap<>(); //new--- <Docid,<term ,CosSim Score>>
        HashMap<String, HashMap<String, Double>> BM25_Matrix = new HashMap<>();  //new--- <Docid,<term ,BM25 score>>
        HashMap<String,Double> QueryTF = QueryCountTF(QueryAfterParse);
        HashMap<String, Double> CosSimtmp = new HashMap<>();//for Docid
        HashMap<String, Double> BM25tmp = new HashMap<>();//for Docid
        int NumOdDocs = Searcher.LoadedDocs.size()+1;
        int Pointer;
        double doclength;
        double Wij;
        double Cij;
        double Wiq;
        double querylength = QueryAfterParse.size();
        double AVGdl = 0;
        double CosSimRank;
        double BM25Rank;
        double b = 0.75;
        double k = 2;
        double CosSimRankUP = 0;
        double CosSimRankDOWN = 0;
        double BM25RankUP = 0;
        double BM25DOWN = 0;
        double CosSimRankDOWNd = 0;
        double CosSimRankDOWNq = 0;


        for (String term : QueryAfterParse.keySet()) {
            //if term not in corpus
            if (Searcher.LoadedDictionary.get(term) == null) {
                CosSim_Matrix.put(null, null);
                BM25_Matrix.put(null,null);
            }
            //term in corpus
            else {
                Pointer = Searcher.LoadedDictionary.get(term).getPointer();
                HashMap<String, Double> tmp = GetTFfromPosting(Pointer, term);// <docid,tf> from posting
                for (String docid : tmp.keySet()) {
                    try {
                        String length = Searcher.LoadedDocs.get(docid).substring(0, Searcher.LoadedDocs.get(docid).indexOf(';'));
                        doclength = Integer.parseInt(length);
                        Wij = (tmp.get(docid) / doclength) * (Math.log(NumOdDocs / Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN()) / Math.log(2));
                        Cij = tmp.get(docid);
                        Wiq = (QueryTF.get(term) / querylength) * (Math.log(NumOdDocs / Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN()+1) / Math.log(2)); //todo - how to get q tf??
                        AVGdl += doclength;


                        //calc CosSim
                        CosSimRankUP += Wij * Wiq;
                        CosSimRankDOWNd += Math.pow(Wij,2);
                        CosSimRankDOWNq += Math.pow(Wiq,2);


                        //calc BM25
                        BM25RankUP = (k+1)*Cij;


              //          BM25DOWN  = Cij + (k(1-b+b*(doclength/AVGdl)));


                    }
                    catch (NullPointerException e){
                        continue;
                    }
                }
            }

        }
//        System.out.println("CosMatrix:");
//        for (String CosKey: CosSim_Matrix.keySet()){
//
//            String key =CosKey.toString();
//            String value = CosSim_Matrix.get(CosKey).toString();
//            System.out.println(key + " " + value);
//
//
//        }
//        System.out.println("BM25Matrix:");
//        for (String BMkey: BM25_Matrix.keySet()){
//
//            String key = BMkey.toString();
//            String value = BM25_Matrix.get(BMkey).toString();
//            System.out.println(key + " " + value);
//
//
//        }
//        System.out.println("finish query!");
        // AVGdl = AVGdl/NumOdDocs;

        RankDocs(CosSim_Matrix, BM25_Matrix);
    }




    public HashMap<Integer,String> RankDocs(HashMap<String, HashMap<String, Double>> CosSim_Matrix,HashMap<String, HashMap<String, Double>> BM25_Matrix){ //HashMap<Rank,DocID> return only max 50 docs  ...final rank = 0.5 cosim + 0.5 BM25
        HashMap<Integer,String> RankerResult = new HashMap<>();

        return RankerResult;
    }


    public void PrintRankedResults(){

    }

    public HashMap<String, Double> GetTFfromPosting(int pointer, String term) throws IOException {
        HashMap<String, Double> PostingResult = new HashMap<>();
        char tmpFirst = term.charAt(0);
        tmpFirst = Character.toLowerCase(tmpFirst);
        StringBuilder stb = new StringBuilder();
        if (tmpFirst >= 'a' && tmpFirst <= 'e') {
            stb.append(PostingPath + "/A_E.txt");
        }
        else if (tmpFirst >= 'f' && tmpFirst <= 'j') {
            stb.append(PostingPath + "/F_J.txt");
        }
        else if (tmpFirst >= 'k' && tmpFirst <= 'p') {
            stb.append(PostingPath + "/K_P.txt");
        }
        else if (tmpFirst >= 'q' && tmpFirst <= 'u') {
            stb.append(PostingPath + "/Q_U.txt");
        }
        else if (tmpFirst >= 'v' && tmpFirst <= 'z') {
            stb.append(PostingPath + "/V_Z.txt");
        }
        else {
            stb.append(PostingPath + "/Numbers.txt");
        }
        File SelectedPosting =  new File(stb.toString());
        String TermLIne  = (String) FileUtils.readLines(SelectedPosting).get(pointer);
        stb.setLength(0);
        String docID; double tf = 0;
        int index =  TermLIne.indexOf(':');
        TermLIne = TermLIne.substring(index + 1);
        while (TermLIne.length() > 0 ){
            docID = TermLIne.substring(8, TermLIne.indexOf(',')-1);
            TermLIne = TermLIne.substring(TermLIne.indexOf(',') + 1);
            String TF = TermLIne.substring(6, TermLIne.indexOf(')'));
            tf = Double.parseDouble(TF);
            try {
                TermLIne = TermLIne.substring(TermLIne.indexOf("("));
            }
            catch (Exception e){
                break;
            }
            PostingResult.put(docID,tf);
        }
        return PostingResult;
    }

    private HashMap<String,Double>  QueryCountTF(HashMap<String, TermDetailes> QueryAfterParse) {
        HashMap<String,Double> QueryTF = new HashMap<>();
        for (String term : QueryAfterParse.keySet()) {  // count TF for Query Matrix
            //in query matrix
            if (QueryTF.containsKey(term)) {
                QueryTF.put(term, QueryTF.get(term) + 1);
            }
            //not in query matrix
            else {
                QueryTF.put(term,1.0);
            }
        }
        return QueryTF;
    }

}
