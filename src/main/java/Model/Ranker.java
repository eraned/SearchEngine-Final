package Model;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.*;

public class Ranker {

    public String PostingPath;
    public double AVGdl;
    HashMap<String, Double> DocsResultDL;
    HashMap<String, Double> DocsResultMAX;

    public Ranker(String pathforFindposting) {
        PostingPath =  pathforFindposting;
        AVGdl = 0;
        DocsResultDL = new HashMap<>();
        DocsResultMAX = new HashMap<>();
    }

    public void InitializScores(HashMap<String, TermDetailes> QueryAfterParse,String Queryid) throws IOException { //Matrix of columns: d1,d2,d3....q     rows: t1,t2,t3....
        HashMap<Double,String> RankerResult = new HashMap<>();//HashMap<Rank,DocID>
        HashMap<String, HashMap<String, Double>> CosSim_Matrix = new HashMap<>(); //new--- <Docid,<term ,CosSim Score>>
        HashMap<String, HashMap<String, Double>> BM25_Matrix = new HashMap<>();  //new--- <Docid,<term ,BM25 score>>
        HashMap<String, Double> CosSimtmp = new HashMap<>();//for Docid
        HashMap<String, Double> BM25tmp = new HashMap<>();//for Docid
        GetfromAllDocs(Searcher.LoadedDocs);
        double NumOdDocs = Searcher.LoadedDocs.size()+1;
        int Pointer;
        double Wij;
        double Cij;
        double Wiq;
        double Ciq;
        double querylength = QueryAfterParse.size();
        double CosSimRank = 0;
        double CosSimRankDOWNdoc = 0;
        double CosSimRankDOWNquery = 0;
        double CosSimRankUP = 0;
        double CosSimRankDOWN = 0;
        double BM25Rank = 0;
        double b = 0.75;
        double k = 2;
        double BM25UP = 0;
        double BM25DOWN = 0;
        double BM25Log = 0;

        for (String term : QueryAfterParse.keySet()) {
            //if term not in corpus
            if (Searcher.LoadedDictionary.get(term) == null) {
                CosSim_Matrix.put(null, null);
                BM25_Matrix.put(null, null);
            }
            //term in corpus
            else {
                Pointer = Searcher.LoadedDictionary.get(term).getPointer();
                HashMap<String, Double> tmpTF = GetTFfromPosting(Pointer, term);// <docid,tf> from posting
                for (String docid : tmpTF.keySet()) {
                    CosSimtmp.put(term,tmpTF.get(docid));
                    BM25tmp.put(term,tmpTF.get(docid));
                    CosSim_Matrix.put(docid,CosSimtmp);
                    BM25_Matrix.put(docid,BM25tmp);
                }
            }
        }

        for (String Doc : CosSim_Matrix.keySet()) {
            if (Doc != null) {
                for (String term : CosSim_Matrix.get(Doc).keySet()) {
                    double idf = (Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN() + 1);
                    Wij = (CosSim_Matrix.get(Doc).get(term) / DocsResultDL.get(Doc)) * (Math.log(NumOdDocs / idf) / Math.log(2));
                    Cij = BM25_Matrix.get(Doc).get(term);
                    Ciq = QueryAfterParse.get(term).getTF();
                    Wiq = (QueryAfterParse.get(term).getTF() / querylength) * (Math.log(NumOdDocs / idf / Math.log(2)));

                    //calc CosSim
                    CosSimRankUP += Wij * Wiq;
                    CosSimRankDOWNdoc += Math.pow(Wij, 2);
                    CosSimRankDOWNquery += Math.pow(Wiq, 2);
                    //calc BM25
                    BM25UP = Cij * (k + 1) * Ciq;
                    BM25DOWN = Cij + (k * ((1 - b) + (b * (DocsResultDL.get(Doc) / AVGdl))));
                    BM25Log = (Math.log(NumOdDocs / idf) / Math.log(2));
                    BM25Rank += BM25UP * BM25DOWN * BM25Log;
                }
                CosSimRankDOWN = Math.sqrt(CosSimRankDOWNdoc * CosSimRankDOWNquery);
                CosSimRank = CosSimRankUP / CosSimRankDOWN;
                RankerResult.put(0.5 * CosSimRank + 0.5 * BM25Rank, Doc);
            }
            else{
                continue;
            }
            RankDocs(RankerResult,Queryid);
        }

    }


    public void RankDocs(HashMap<Double,String> RankedQuery,String queryID){ //HashMap<Rank,DocID> return only max 50 docs  ...final rank = 0.5 cosim + 0.5 BM25
        ArrayList<Double> SortedRank = new ArrayList<>(RankedQuery.keySet());
        Collections.sort(SortedRank, Collections.reverseOrder());
        for(int i = 0 ; i < SortedRank.size() && i < 50 ;i++){
            Searcher.Results.add(new Pair(queryID,RankedQuery.get(SortedRank.get(i)))); //Hashmap<queryid,Docid>
        }
    }


    private void GetfromAllDocs(HashMap<String,String> loadedDocs) {
        double Doclength;double Docmax_tf;
        for(String doc : loadedDocs.keySet()){
            try {
                String length = loadedDocs.get(doc).substring(0, loadedDocs.get(doc).indexOf(';'));
                String max_tf = loadedDocs.get(doc).substring(loadedDocs.get(doc).indexOf(';')+1);
                Docmax_tf = Integer.parseInt(max_tf);
                Doclength = Integer.parseInt(length);
                AVGdl += Doclength;
                DocsResultDL.put(doc, Doclength);
                DocsResultMAX.put(doc, Docmax_tf);
            }
            catch (Exception e){
                continue;
            }
        }
        AVGdl = AVGdl/loadedDocs.size();
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

}
