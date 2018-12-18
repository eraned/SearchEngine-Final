package Model;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Ranker {


    public Ranker(){}


    public void InitializWeights(HashMap<String, TermDetailes> QueryAfterParse,String pathforFindposting,HashMap<String, DictionaryDetailes> LoadedDictionary,HashMap<String, String> LoadedDocs) throws IOException { //Matrix of columns: d1,d2,d3....q     rows: t1,t2,t3....
        HashMap<String, HashMap<String, Double>> CosSim_Matrix = new HashMap<>(); //<term,<Docid ,weight per Docid>>
        HashMap<String, HashMap<String, Integer>> BM25_Matrix = new HashMap<>();  //<term,<,Docid,TF per Docid>>
        int NumOdDocs = LoadedDocs.size();
        for (String term : QueryAfterParse.keySet()) {
            try {
                int Pointer = LoadedDictionary.get(term).getPointer();
                HashMap<String, Double> CosSimtmp = new HashMap<>();
                HashMap<String, Integer> BM25tmp = new HashMap<>();
                HashMap<String, Integer> tmp = GetTFfromPosting(Pointer, term, pathforFindposting);// <docid,tf> from posting
                for(String docid : tmp.keySet()){
                    String length = LoadedDocs.get(docid).substring(0,LoadedDocs.get(docid).indexOf(';'));
                    double doclength = Integer.parseInt(length);
                    double Wij = (tmp.get(docid)/doclength)*(Math.log(NumOdDocs/LoadedDictionary.get(term).getNumOfDocsTermIN())/Math.log(2));
                    int Cij = tmp.get(docid);
                    CosSimtmp.put(docid,Wij);
                    BM25tmp.put(docid,Cij);
                }
                CosSim_Matrix.put(term,CosSimtmp);
                BM25_Matrix.put(term,BM25tmp);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        RankDocs(CosSim_Matrix,BM25_Matrix);
    }





    public HashMap<Integer,String> RankDocs(HashMap<String, HashMap<String, Double>> CosSim_Matrix,HashMap<String, HashMap<String, Integer>> BM25_Matrix){ //HashMap<Rank,DocID> return only max 50 docs  ...final rank = 0.5 cosim + 0.5 BM25
        HashMap<Integer,String> RankerResult = new HashMap<>();

        //calc CosSim


        //calc BM25


        return RankerResult;
    }

    public HashMap<String, Integer> GetTFfromPosting(int pointer, String term,String Pathtosearch) throws IOException {
        HashMap<String, Integer> PostingResult = new HashMap<>();
        char tmpFirst = term.charAt(0);
        tmpFirst = Character.toLowerCase(tmpFirst);
        StringBuilder stb = new StringBuilder();
        if (tmpFirst >= 'a' && tmpFirst <= 'e') {
            stb.append(Pathtosearch + "/A_E.txt");
        }
        else if (tmpFirst >= 'f' && tmpFirst <= 'j') {
            stb.append(Pathtosearch + "/F_J.txt");
        }
        else if (tmpFirst >= 'k' && tmpFirst <= 'p') {
            stb.append(Pathtosearch + "/K_P.txt");
        }
        else if (tmpFirst >= 'q' && tmpFirst <= 'u') {
            stb.append(Pathtosearch + "/Q_U.txt");
        }
        else if (tmpFirst >= 'v' && tmpFirst <= 'z') {
            stb.append(Pathtosearch + "/V_Z.txt");
        }
        else {
            stb.append(Pathtosearch + "/Numbers.txt");
        }
        File SelectedPosting =  new File(stb.toString());
        String TermLIne  = (String) FileUtils.readLines(SelectedPosting).get(pointer);
        stb.setLength(0);
        String docID; int tf = 0;
        int index =  TermLIne.indexOf(':');
        TermLIne = TermLIne.substring(index + 1);
        while (TermLIne.length() > 0 ){
            docID = TermLIne.substring(8, TermLIne.indexOf(',')-1);
            TermLIne = TermLIne.substring(TermLIne.indexOf(',') + 1);
            String TF = TermLIne.substring(6, TermLIne.indexOf(')'));
            tf = Integer.parseInt(TF);
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




}
