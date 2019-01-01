package Model;

import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;


/**
 *
 */
public class Ranker {
    public boolean Steemerneeded;
    HashSet<String> Query;
    public String PostingPath;
    public HashMap<String, HashMap<String, Double>> PostingTFResult;
    public HashMap<String, HashMap<String, Boolean>> PostingTitelResult;
    public HashMap<Double, String> RankerResult;//HashMap<Rank,DocID>
    public HashMap<String, HashMap<String, Double>> BM25_Matrix;  //new--- <Docid,<term ,BM25 score>>
    public HashMap<String, Double> BM25tmp;//for Docid
    public HashSet<String> SemanticsWords;
    public int Pointer;
    public double Cij;
    public double querylength;
    public double b = 0.75;
    public double k = 1.3;



    /**
     * Constructor
     *
     * @param pathforFindposting
     */
    public Ranker(String pathforFindposting,boolean StemmingNeeded) {
        Steemerneeded = StemmingNeeded;
        Query = new HashSet<>();
        PostingPath = pathforFindposting;
        PostingTFResult = new HashMap<>();
        PostingTitelResult = new HashMap<>();
        RankerResult = new HashMap<>();
        BM25_Matrix = new HashMap<>();
        //Query_BM25 = new HashMap<>();
        BM25tmp = new HashMap<>();
        SemanticsWords = new HashSet<>();
    }
    /**
     * @param Queryid
     * @param semanticNeeded
     * @throws IOException
     * @throws URISyntaxException
     */
    public void InitializScores(HashSet<String> querywords,String Queryid, boolean semanticNeeded) throws IOException, URISyntaxException { //Matrix of columns: d1,d2,d3....q     rows: t1,t2,t3....
        if (semanticNeeded)
            ProccesSemantic(querywords,Steemerneeded);
        ProccesQuery(querywords,Steemerneeded);
        ProccesReOrgnize();
        ProccesCompare();
        RankDocs(RankerResult, Queryid);
        ClearAll();
    }

    public void ProccesSemantic(HashSet<String> querywords,boolean Steemer) {
        try {
            //part 1 - process query words for API
            HashSet<String> tmp = new HashSet<>();
            for (String term : querywords) {
                if (term.contains("-")) {
                    String[] splited = StringUtils.split(term, "-");
                    for (int i = 0; i < splited.length; i++) {
                        if (StringUtils.isAlpha(splited[i])) {
                            tmp.add(splited[i]);
                        }
                    }
                } else if (!StringUtils.isAlpha(term))
                    continue;
                else
                    tmp.add(term);
            }
            //part 2 - add semantics words to query
            for (String term : tmp) {
                SemanticsWords = GetSemanticFromAPI(term);
                if (SemanticsWords != null) {
                    for (String word : SemanticsWords) {
                        if (Searcher.Loaded_Dictionary.get(word) != null)
                            Query.add(word);
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("Probellm in semantic ");
        }
    }

    public void ProccesQuery(HashSet<String> querywords,boolean Steemer) {
        for(String term : querywords) {
            if(StringUtils.isAllUpperCase(term) && Searcher.Loaded_Dictionary.get(term) != null) {
                Query.add(term);
                if(Searcher.Loaded_Dictionary.get(term.toLowerCase()) != null)
                Query.add(term.toLowerCase());
            }
            else if(StringUtils.isAllLowerCase(term) && Searcher.Loaded_Dictionary.get(term) != null){
                Query.add(term);
                if(Searcher.Loaded_Dictionary.get(term.toUpperCase()) != null)
                    Query.add(term.toUpperCase());
            }
                else
                    continue;
        }
        querylength = Query.size();
    }

    public void ProccesReOrgnize() {
        //part 1
        for (String term : Query) {
            try {
                Pointer = Searcher.Loaded_Dictionary.get(term).getPointer();
                GetTF_InTitelFromPosting(Pointer, term);// <docid,tf> from posting
            } catch (Exception e) {
                System.out.println("Probellm in reorgnized part 1 reg");
                System.out.println(term);
                break;
            }
        }
        //part 2
        for (String Doc : PostingTFResult.keySet()) {
            if (Searcher.citiesToFilter != null) {
                if (!Searcher.citiesToFilter.contains(Searcher.Loaded_AllDocs.get(Doc).getDocCity()))
                    continue;
            }
            for (String term : PostingTFResult.get(Doc).keySet()) {
                try {
                    Cij = PostingTFResult.get(Doc).get(term);
                    BM25tmp.put(term, Cij);
                } catch (Exception e) {
                    System.out.println("Probellm in reorgnized part 2");
                    System.out.println(Doc);
                    System.out.println(term);
                    break;
                }
            }
            BM25_Matrix.put(Doc, BM25tmp);
        }
    }


    public void ProccesCompare() {
        for (String Doc : PostingTFResult.keySet()) {
            double BM25Rank = 0;
            boolean T = false;
            for (String term : PostingTFResult.get(Doc).keySet()) {
                try {
                    double idf = (Searcher.Loaded_Dictionary.get(term).getNumOfDocsTermIN() + 1);
                    BM25Rank += ((((k + 1) * BM25_Matrix.get(Doc).get(term)) / (BM25_Matrix.get(Doc).get(term) + k * (1 - b + b * Searcher.Loaded_AllDocs.get(Doc).getDocLength() / Searcher.AVGdl))) * Math.log((Searcher.NumOfDocs + 1) / idf));
                    T = PostingTitelResult.get(Doc).get(term);
                } catch (Exception e) {
                    System.out.println("Problem in Compare");
                    System.out.println(Doc);
                    System.out.println(term);
                    break;
                }
            }
           // System.out.println(Doc + "###" + BM25Rank);
            if(T)
            RankerResult.put(BM25Rank + 0.05, Doc);
            else
                RankerResult.put((BM25Rank), Doc);
        }
    }

    public void ClearAll(){
        Query.clear();
        PostingTFResult.clear();
        RankerResult.clear();
        BM25_Matrix.clear();
        BM25tmp.clear();
        //Query_BM25.clear();
        SemanticsWords.clear();
    }


    /**
     * @param RankedQuery
     * @param queryID
     */
    public void RankDocs(HashMap<Double, String> RankedQuery, String queryID) { //HashMap<Rank,DocID> return only max 50 docs  ...final rank = 0.45 cosim + 0.45 BM25 + 0.1 InTitle
        ArrayList<Double> SortedRank = new ArrayList<>(RankedQuery.keySet());
        Collections.sort(SortedRank,Collections.<Double>reverseOrder());
        for (int i = 0; i < SortedRank.size() && i < 50; i++) {
            Searcher.Results.add(new Pair(queryID, RankedQuery.get(SortedRank.get(i))));
         //   System.out.println(SortedRank.get(i) + "##" + RankedQuery.get(SortedRank.get(i)));
        }
    //    System.out.println("finish rank!");
    }

    /**
     * @param pointer
     * @param term
     * @throws IOException
     */
    public void GetTF_InTitelFromPosting(int pointer, String term) throws IOException {
        char tmpFirst = term.charAt(0);
        String docID;
        double tf;
        boolean title;
        int index;
        tmpFirst = Character.toLowerCase(tmpFirst);
        StringBuilder stb = new StringBuilder();
//        if (tmpFirst >= 'a' && tmpFirst <= 'e') { //todo
//            stb.append(PostingPath + "\\A_E.txt");
//        } else if (tmpFirst >= 'f' && tmpFirst <= 'j') {
//            stb.append(PostingPath + "\\F_J.txt");
//        } else if (tmpFirst >= 'k' && tmpFirst <= 'p') {
//            stb.append(PostingPath + "\\K_P.txt");
//        } else if (tmpFirst >= 'q' && tmpFirst <= 'u') {
//            stb.append(PostingPath + "\\Q_U.txt");
//        } else if (tmpFirst >= 'v' && tmpFirst <= 'z') {
//            stb.append(PostingPath + "\\V_Z.txt");
//        } else {
//            stb.append(PostingPath + "\\Numbers.txt");
//        }
        if (tmpFirst >= 'a' && tmpFirst <= 'e') { //todo
            stb.append(PostingPath + "/A_E.txt");
        } else if (tmpFirst >= 'f' && tmpFirst <= 'j') {
            stb.append(PostingPath + "/F_J.txt");
        } else if (tmpFirst >= 'k' && tmpFirst <= 'p') {
            stb.append(PostingPath + "/K_P.txt");
        } else if (tmpFirst >= 'q' && tmpFirst <= 'u') {
            stb.append(PostingPath + "/Q_U.txt");
        } else if (tmpFirst >= 'v' && tmpFirst <= 'z') {
            stb.append(PostingPath + "/V_Z.txt");
        } else {
            stb.append(PostingPath + "/Numbers.txt");
        }
        File SelectedPosting = new File(stb.toString());
        try (BufferedReader br = new BufferedReader(new FileReader(SelectedPosting))) {
            for(int i =0;i<pointer;i++){
                br.readLine();
            }
            String TermLIne = br.readLine();
            stb.setLength(0);
            index = TermLIne.indexOf(':');
            TermLIne = TermLIne.substring(index + 1);
            while (TermLIne.length() > 0) {
                try {
                    index = TermLIne.indexOf("id:");
                    docID = TermLIne.substring(index + 3, TermLIne.indexOf(';'));
                    TermLIne = TermLIne.substring(TermLIne.indexOf(';') + 1);
                    index = TermLIne.indexOf("TF:");
                    String TF = TermLIne.substring(index + 3, TermLIne.indexOf(';'));
                    tf = Double.parseDouble(TF);
                    TermLIne = TermLIne.substring(TermLIne.indexOf(';') + 1);
                    index = TermLIne.indexOf("InTitle:");
                    String Title = TermLIne.substring(index + 8, TermLIne.indexOf("->"));
                    title = Boolean.parseBoolean(Title);
                    if (PostingTFResult.containsKey(docID)) {
                        PostingTFResult.get(docID).put(term, tf);
                        PostingTitelResult.get(docID).put(term,title);
                    } else {
                        HashMap<String, Double> tmpTF = new HashMap<>();
                        tmpTF.put(term, tf);
                        HashMap<String, Boolean> tmpTitl = new HashMap<>();
                        tmpTitl.put(term, title);
                        PostingTFResult.put(docID, tmpTF);
                        PostingTitelResult.put(docID,tmpTitl);
                    }
                    TermLIne = TermLIne.substring(TermLIne.indexOf("->"));
                    if (TermLIne.length() == 3)
                        break;
                } catch (Exception e) {
                    System.out.println("problem get tf!");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param term
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    public HashSet<String> GetSemanticFromAPI(String term) throws IOException, URISyntaxException {
        HashSet<String> result = new HashSet<>();
        try {
            URI CityUrl = new URI("https://api.datamuse.com/words?ml=" + term);
            URL AfterCheck = CityUrl.toURL();
            BufferedReader Input = new BufferedReader(new InputStreamReader(AfterCheck.openStream()));
            String Line = Input.readLine();
            int counter = 0;
            if (!Line.equals("[]")) {
                while (counter < 3) {
                    String WordToAdd = Line.substring(Line.indexOf("\"word\"") + 8, Line.indexOf("\"score\"") - 2);
                    Line = Line.substring(Line.indexOf("}") + 1);
                    result.add(WordToAdd);
                    counter++;
                }
                Input.close();
                return result;
            } else {
                return null;
            }
        } catch (URISyntaxException e) {
            System.out.println("API");
            return null;
        }
    }
}


//
//    public void ProccesCompare() {
//        for (String Doc : PostingTFResult.keySet()) {
//            double CosSimRankUP = 0;
//            double CosSimRankDOWNdoc = 0;
//            double CosSimRankDOWNquery = 0;
//            double BM25UP = 0;
//            double BM25DOWN = 0;
//            double BM25Log = 0;
//            double BM25Rank = 0;
//            double CosSimRankDOWN = 0;
//            double CosSimRank = 0;
//            for (String term : PostingTFResult.get(Doc).keySet()) {
//                try {
//                    double idf = (Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN() + 1);
//                    //calc CosSim
//                    testline = 2;
//                    CosSimRankUP += CosSim_Matrix.get(Doc).get(term) * Query_CosSim.get(term);
//                    // System.out.println(CosSimRankUP);
//                    CosSimRankDOWNdoc += Math.pow(CosSim_Matrix.get(Doc).get(term), 2);
//                    testline = 3;
//                    CosSimRankDOWNquery += Math.pow(Query_CosSim.get(term), 2);
//                    double x = CosSim_Matrix.get(Doc).get(term);
//                    double y = Query_CosSim.get(term);
//                    double z = Math.pow(CosSim_Matrix.get(Doc).get(term), 2);
//                    //calc BM25
//                    testline = 4;
//                    BM25UP = BM25_Matrix.get(Doc).get(term) * (k + 1) * Query_BM25.get(term);
//                    // System.out.println(BM25UP);
//                    testline = 5;
//                    BM25DOWN = BM25_Matrix.get(Doc).get(term) + k * (1 - b + b * (Searcher.DocsResultDL.get(Doc) / Searcher.AVGdl));
//                    // System.out.println(BM25DOWN);
//                    testline = 6;
//                    BM25Log = (Math.log((Searcher.NumOfDocs + 1) / idf) / Math.log(2));
//                    //System.out.println(BM25Log);
//                    double a = BM25_Matrix.get(Doc).get(term);
//                    double b = Query_BM25.get(term);
//                    double d = Searcher.DocsResultDL.get(Doc);
//                    double c = (Searcher.DocsResultDL.get(Doc) / Searcher.AVGdl);
//                    BM25Rank += BM25UP * BM25DOWN * BM25Log;
//                    BM25Rank += ((((k+1)*BM25_Matrix.get(Doc).get(term))/(BM25_Matrix.get(Doc).get(term) + k * (1-b+b*Searcher.DocsResultDL.get(Doc)/Searcher.AVGdl)))*Math.log((Searcher.NumOfDocs + 1)/idf));
//                    //  System.out.println(BM25Rank);
//                } catch (Exception e) {
//                    System.out.println("Problem in Compare");
//                    System.out.println(Doc);
//                    System.out.println(term);
//                    break;
//                }
//            }
//            CosSimRankDOWN = Math.sqrt(CosSimRankDOWNdoc * CosSimRankDOWNquery);
//            CosSimRank = CosSimRankUP / CosSimRankDOWN;
//            System.out.println(Doc + "###" +BM25Rank + "###"+ CosSimRank);
//            RankerResult.put((0.5 * CosSimRank) + (0.5 * BM25Rank), Doc);
//            RankerResult.put(BM25Rank, Doc);
//
//        }
//    }
