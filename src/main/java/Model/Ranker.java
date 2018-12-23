package Model;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.util.*;

public class Ranker {

    public String PostingPath;
    public HashMap<String, Double> PostingTFResult;
    public HashMap<String, Boolean> PostingTitelResult;

    public Ranker(String pathforFindposting) {
        PostingPath =  pathforFindposting;
        PostingTFResult = new HashMap<>();
        PostingTitelResult = new HashMap<>();
    }

    public void InitializScores(HashMap<String, TermDetailes> QueryAfterParse,String Queryid) throws IOException { //Matrix of columns: d1,d2,d3....q     rows: t1,t2,t3....
        HashMap<Double,String> RankerResult = new HashMap<>();//HashMap<Rank,DocID>
        HashMap<String, HashMap<String, Double>> CosSim_Matrix = new HashMap<>(); //new--- <Docid,<term ,CosSim Score>>
        HashMap<String, HashMap<String, Double>> BM25_Matrix = new HashMap<>();  //new--- <Docid,<term ,BM25 score>>
        HashMap<String, Double> CosSimtmp = new HashMap<>();//for Docid
        HashMap<String, Double> BM25tmp = new HashMap<>();//for Docid
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
        double TitleRank = 0;

        for (String term : QueryAfterParse.keySet()) {
            //if term not in corpus
            if (Searcher.LoadedDictionary.get(term) == null) {
                CosSim_Matrix.put(null, null);
                BM25_Matrix.put(null, null);
            }
            //term in corpus
            else {
                Pointer = Searcher.LoadedDictionary.get(term).getPointer();
                GetTF_InTitelFromPosting(Pointer, term);// <docid,tf> from posting
                for (String docid : PostingTFResult.keySet()) {
                    CosSimtmp.put(term,PostingTFResult.get(docid));
                    BM25tmp.put(term,PostingTFResult.get(docid));
                    CosSim_Matrix.put(docid,CosSimtmp);
                    BM25_Matrix.put(docid,BM25tmp);
                }
            }
        }

        for (String Doc : CosSim_Matrix.keySet()) {
            if (Doc != null) {
                for (String term : CosSim_Matrix.get(Doc).keySet()) {
                    try {
                        double idf = (Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN() + 1);
                        Wij = (CosSim_Matrix.get(Doc).get(term) / Searcher.DocsResultDL.get(Doc)) * (Math.log(Searcher.NumOdDocs / idf) / Math.log(2));
                        Cij = BM25_Matrix.get(Doc).get(term);
                        Ciq = QueryAfterParse.get(term).getTF();
                        Wiq = (QueryAfterParse.get(term).getTF() / querylength) * (Math.log(Searcher.NumOdDocs / idf / Math.log(2)));

                        //calc CosSim
                        CosSimRankUP += Wij * Wiq;
                        CosSimRankDOWNdoc += Math.pow(Wij, 2);
                        CosSimRankDOWNquery += Math.pow(Wiq, 2);
                        //calc BM25
                        BM25UP = Cij * (k + 1) * Ciq;
                        BM25DOWN = Cij + (k * ((1 - b) + (b * (Searcher.DocsResultDL.get(Doc) / Searcher.AVGdl))));
                        BM25Log = (Math.log(Searcher.NumOdDocs / idf) / Math.log(2));
                        BM25Rank += BM25UP * BM25DOWN * BM25Log;
                    } catch (Exception e) {
                        System.out.println(Doc);
                        System.out.println(term);
                    }
                }
                CosSimRankDOWN = Math.sqrt(CosSimRankDOWNdoc * CosSimRankDOWNquery);
                CosSimRank = CosSimRankUP / CosSimRankDOWN;
                if(PostingTitelResult.get(Doc))
                    TitleRank = 1.0;
                RankerResult.put((0.45 * CosSimRank) + (0.45 * BM25Rank) + (0.1 * TitleRank), Doc);
            }
            else {continue;}
        }
        RankDocs(RankerResult, Queryid);
    }


    public void RankDocs(HashMap<Double,String> RankedQuery,String queryID){ //HashMap<Rank,DocID> return only max 50 docs  ...final rank = 0.45 cosim + 0.45 BM25 + 0.1 InTitle
        ArrayList<Double> SortedRank = new ArrayList<>(RankedQuery.keySet());
        Collections.sort(SortedRank, Collections.reverseOrder());
        for(int i = 0 ; i < SortedRank.size() && i < 50 ;i++){
            Searcher.Results.add(new Pair(queryID,RankedQuery.get(SortedRank.get(i)))); //Hashmap<queryid,Docid>
        }
    }

    public void GetTF_InTitelFromPosting(int pointer, String term) throws IOException {
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
        String docID; double tf = 0;boolean title;
        int index =  TermLIne.indexOf(':');
        TermLIne = TermLIne.substring(index + 1);
        while (TermLIne.length() > 0 ){
            docID = TermLIne.substring(TermLIne.indexOf(':')+1, TermLIne.indexOf(',')-1);
            TermLIne = TermLIne.substring(TermLIne.indexOf(',') + 1);
            String TF = TermLIne.substring(TermLIne.indexOf(':')+1, TermLIne.indexOf(',')-1);
            tf = Double.parseDouble(TF);
            TermLIne = TermLIne.substring(TermLIne.indexOf(',') + 1);
            String Title = TermLIne.substring(TermLIne.indexOf(':')+1, TermLIne.indexOf(')'));
            title = Boolean.parseBoolean(Title);
            try {
                TermLIne = TermLIne.substring(TermLIne.indexOf("("));
            }
            catch (Exception e){
                break;
            }
            PostingTFResult.put(docID,tf);
            PostingTitelResult.put(docID,title);
        }
    }
}
