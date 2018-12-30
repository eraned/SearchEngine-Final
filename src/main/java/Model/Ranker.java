package Model;

import javafx.util.Pair;
import org.apache.commons.io.FileUtils;
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
    HashMap<String, TermDetailes> Query;
    public String PostingPath;
    public HashMap<String, HashMap<String, Double>> PostingTFResult;
    public HashMap<String, Boolean> PostingTitelResult;
    public HashMap<Double, String> RankerResult;//HashMap<Rank,DocID>
    public HashMap<String, HashMap<String, Double>> CosSim_Matrix; //new--- <Docid,<term ,CosSim Score>>
    public HashMap<String, HashMap<String, Double>> BM25_Matrix;  //new--- <Docid,<term ,BM25 score>>
    public HashMap<String, Double> CosSimtmp;//for Docid
    public HashMap<String, Double> BM25tmp;//for Docid
    public HashMap<String, Double> Query_BM25;
    public HashMap<String, Double> Query_CosSim;
    public HashSet<String> SemanticsWords;
    public int Pointer;
    public double Wij;
    public double Cij;
    public double Wiq;
    public double Ciq;
    public double querylength;
    public double b = 0.75;
    public double k = 1.3;



    /**
     * Constructor
     *
     * @param pathforFindposting
     */
    public Ranker(String pathforFindposting) {
        Query = new HashMap<>();
        PostingPath = pathforFindposting;
        PostingTFResult = new HashMap<>();
        PostingTitelResult = new HashMap<>();
        RankerResult = new HashMap<>();
        CosSim_Matrix = new HashMap<>();
        BM25_Matrix = new HashMap<>();
        CosSimtmp = new HashMap<>();
        BM25tmp = new HashMap<>();
        Query_BM25 = new HashMap<>();
        Query_CosSim = new HashMap<>();
        SemanticsWords = new HashSet<>();
    }
//Falkland petroleum exploration
    /**
     * @param QueryAfterParse
     * @param Queryid
     * @param semanticNeeded
     * @throws IOException
     * @throws URISyntaxException
     */
    public void InitializScores(HashMap<String, TermDetailes> QueryAfterParse, String Queryid, boolean semanticNeeded) throws IOException, URISyntaxException { //Matrix of columns: d1,d2,d3....q     rows: t1,t2,t3....
        if (semanticNeeded)
            ProccesSemantic(QueryAfterParse);
        ProccesQuery(QueryAfterParse);
        ProccesReOrgnize();
        ProccesCompare();
        RankDocs(RankerResult, Queryid);
        ClearAll();
    }

    public void ProccesSemantic(HashMap<String, TermDetailes> QueryAfterParse) {
        int testline = 0;
        try {
            //part 1 - process query words for API
            HashSet<String> tmp = new HashSet<>();
            for (String term : QueryAfterParse.keySet()) {
                testline =1;
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
                testline =2;
                if (SemanticsWords != null) {
                    for (String word : SemanticsWords) {
                        if (Searcher.LoadedDictionary.get(word) != null) {
                            TermDetailes tmpTD = new TermDetailes("API");
                            tmpTD.setTF(1);
                            tmpTD.setInTitle(false);
                            Query.put(word, tmpTD);
                        }
                    }
                } else {
                    continue;
                }
            }
        } catch (Exception e) {
            System.out.println("Probellm in semantic ");
            System.out.println(testline);        }
    }

    public void ProccesQuery(HashMap<String, TermDetailes> QueryAfterParse) {
        int testline = 0;
        for(String term : QueryAfterParse.keySet()) {
            if (Searcher.LoadedDictionary.get(term) != null) {
                Query.put(term, QueryAfterParse.get(term));
            }
        }
        querylength = Query.size();
        for (String term : Query.keySet()){
            try {
                testline =1;
                double idf = (Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN() + 1);
                testline =2;
                Ciq = Query.get(term).getTF();
                Wiq = (Query.get(term).getTF() / querylength) * (Math.log((Searcher.NumOfDocs + 1) / idf) / Math.log(2));
                testline =3;
                Query_BM25.put(term, Ciq);
                Query_CosSim.put(term, Wiq);
            } catch (Exception e) {
                System.out.println("Probelem in Query section");
                System.out.println(term);
                System.out.println(testline);
            }
        }
    }

    public void ProccesReOrgnize() {
        int testline = 0;
        //part 1
        for (String term : Query.keySet()) {
            try {
                testline =1;
                Pointer = Searcher.LoadedDictionary.get(term).getPointer();
                testline =2;
                GetTF_InTitelFromPosting(Pointer, term);// <docid,tf> from posting
            } catch (Exception e) {
                System.out.println("Probellm in reorgnized part 1");
                System.out.println(term);
                System.out.println(testline);
                break;
            }
        }
        //part 2
        for (String Doc : PostingTFResult.keySet()) {
            if (Searcher.citiesToFilter != null) {
                if (!Searcher.citiesToFilter.contains(Searcher.DocsResultCITY.get(Doc)))
                    continue;
            }
            for (String term : PostingTFResult.get(Doc).keySet()) {
                try {
                    testline =3;
                    double idf = (Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN() + 1);
                    testline =4;
                    Wij = (PostingTFResult.get(Doc).get(term) / Searcher.DocsResultDL.get(Doc)) * (Math.log((Searcher.NumOfDocs + 1) / idf) / Math.log(2));
                    Cij = PostingTFResult.get(Doc).get(term);
                    CosSimtmp.put(term, Wij);
                    BM25tmp.put(term, Cij);
                } catch (Exception e) {
                    System.out.println("Probellm in reorgnized part 2");
                    System.out.println(Doc);
                    System.out.println(term);
                    System.out.println(testline);
                    break;
                }
            }
            testline =5;
            CosSim_Matrix.put(Doc, CosSimtmp);
            BM25_Matrix.put(Doc, BM25tmp);
        }

    }


    public void ProccesCompare() {
        int testline = 0;
        for (String Doc : PostingTFResult.keySet()) {
            double CosSimRankUP = 0;
            double CosSimRankDOWNdoc = 0;
            double CosSimRankDOWNquery = 0;
            double BM25UP = 0;
            double BM25DOWN = 0;
            double BM25Log = 0;
            double BM25Rank = 0;
            double CosSimRankDOWN = 0;
            double CosSimRank = 0;
            for (String term : PostingTFResult.get(Doc).keySet()) {
                try {
                    testline = 1;
                    double idf = (Searcher.LoadedDictionary.get(term).getNumOfDocsTermIN() + 1);
                    //calc CosSim
                    testline = 2;
                    CosSimRankUP += CosSim_Matrix.get(Doc).get(term) * Query_CosSim.get(term);
                    CosSimRankDOWNdoc += Math.pow(CosSim_Matrix.get(Doc).get(term), 2);
                    testline = 3;
                    CosSimRankDOWNquery += Math.pow(Query_CosSim.get(term), 2);
                    double x = CosSim_Matrix.get(Doc).get(term);
                    double y = Query_CosSim.get(term);
                    double z = Math.pow(CosSim_Matrix.get(Doc).get(term), 2);
                    //calc BM25
                    testline = 4;
                    BM25UP = BM25_Matrix.get(Doc).get(term) * (k + 1) * Query_BM25.get(term);
                    testline = 5;
                    BM25DOWN = BM25_Matrix.get(Doc).get(term) + k * (1 - b + b * (Searcher.DocsResultDL.get(Doc) / Searcher.AVGdl));
                    testline = 6;
                    BM25Log = (Math.log((Searcher.NumOfDocs + 1) / idf) / Math.log(2));
                    double a = BM25_Matrix.get(Doc).get(term);
                    double b = Query_BM25.get(term);
                    double d = Searcher.DocsResultDL.get(Doc);
                    double c = (Searcher.DocsResultDL.get(Doc) / Searcher.AVGdl);
                    BM25Rank += BM25UP * BM25DOWN * BM25Log;
                } catch (Exception e) {
                    System.out.println("Problem in Compare");
                    System.out.println(Doc);
                    System.out.println(term);
                    System.out.println(testline);
                    break;
                }
            }
            CosSimRankDOWN = Math.sqrt(CosSimRankDOWNdoc * CosSimRankDOWNquery);
            CosSimRank = CosSimRankUP / CosSimRankDOWN;
            RankerResult.put((0.5 * CosSimRank) + (0.5 * BM25Rank), Doc);
        }
    }

    public void ClearAll(){
        Query.clear();
        PostingTFResult.clear();
        RankerResult.clear();
        CosSim_Matrix.clear();
        BM25_Matrix.clear();
        CosSimtmp.clear();
        BM25tmp.clear();
        Query_BM25.clear();
        Query_CosSim.clear();
        SemanticsWords.clear();
    }


    /**
     * @param RankedQuery
     * @param queryID
     */
    public void RankDocs(HashMap<Double, String> RankedQuery, String queryID) { //HashMap<Rank,DocID> return only max 50 docs  ...final rank = 0.45 cosim + 0.45 BM25 + 0.1 InTitle
        ArrayList<Double> SortedRank = new ArrayList<>(RankedQuery.keySet());
        Collections.sort(SortedRank, Collections.reverseOrder());
        for (int i = 0; i < SortedRank.size() && i < 50; i++) {
            Searcher.Results.add(new Pair(queryID, RankedQuery.get(SortedRank.get(i))));
        }
    }

    /**
     * @param pointer
     * @param term
     * @throws IOException
     */
    public void GetTF_InTitelFromPosting(int pointer, String term) throws IOException {
        char tmpFirst = term.charAt(0);
        tmpFirst = Character.toLowerCase(tmpFirst);
        StringBuilder stb = new StringBuilder();
        if (tmpFirst >= 'a' && tmpFirst <= 'e') { //todo
            stb.append(PostingPath + "\\A_E.txt");
        } else if (tmpFirst >= 'f' && tmpFirst <= 'j') {
            stb.append(PostingPath + "\\F_J.txt");
        } else if (tmpFirst >= 'k' && tmpFirst <= 'p') {
            stb.append(PostingPath + "\\K_P.txt");
        } else if (tmpFirst >= 'q' && tmpFirst <= 'u') {
            stb.append(PostingPath + "\\Q_U.txt");
        } else if (tmpFirst >= 'v' && tmpFirst <= 'z') {
            stb.append(PostingPath + "\\V_Z.txt");
        } else {
            stb.append(PostingPath + "\\Numbers.txt");
        }
        File SelectedPosting = new File(stb.toString());
        String TermLIne = (String) FileUtils.readLines(SelectedPosting).get(pointer);
        stb.setLength(0);
        String docID;
        double tf = 0;
        boolean title;
        int index;
        index = TermLIne.indexOf(':');
        TermLIne = TermLIne.substring(index + 1);
        while (TermLIne.length() > 0) {
            try {
                index = TermLIne.indexOf("id:");
                docID = TermLIne.substring(index + 3, TermLIne.indexOf(';'));
                TermLIne = TermLIne.substring(TermLIne.indexOf(';')+1);
                index = TermLIne.indexOf("TF:");
                String TF = TermLIne.substring(index + 3,TermLIne.indexOf(';'));
                tf = Double.parseDouble(TF);
                TermLIne = TermLIne.substring(TermLIne.indexOf(';')+1);
                index = TermLIne.indexOf("InTitle:");
                String Title = TermLIne.substring(index + 8, TermLIne.indexOf("->"));
                title = Boolean.parseBoolean(Title);
                if (PostingTFResult.containsKey(docID)) {
                    PostingTFResult.get(docID).put(term, tf);
                } else {
                    HashMap<String, Double> tmp = new HashMap<>();
                    tmp.put(term, tf);
                    PostingTFResult.put(docID, tmp);
                }
                PostingTitelResult.put(docID, title);
                TermLIne = TermLIne.substring(TermLIne.indexOf("->"));
                if(TermLIne.length() == 3)
                    break;
            } catch (Exception e) {
                System.out.println("problem get tf!");
                break;
            }
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

/**
class Ranker
{
    string path;
    Parser p;
    bool isStem;
    ReadFile r;
    Dictionary<string, double> DocWeight;
    public Dictionary<string, int> Docsmaxtf;
    Dictionary<string, double> docslength;
    Indexer ind;
    double avgDL, k;
    public Ranker(string path,Parser p,ReadFile r,Indexer ind,bool isStem)
    {
        this.path = path;
        avgDL = 0;
        k = 1.3;
        this.isStem = isStem;
        this.r = r;
        this.p = p;
        this.ind = ind;
        DocWeight = new Dictionary<string, double>();
        Docsmaxtf = new Dictionary<string, int>();
        docslength = new Dictionary<string, double>();
        string file;
        //check if we can do fast loading
        if (isStem)
            file = path + @"\rankerStem";
            else
        file=path + @"\ranker";
        if (File.Exists(file))
        {
            //if File exiest loading dictionaries in order to run faster
            using (FileStream fs = new FileStream(file, FileMode.Open))
            {
                IFormatter bf = new BinaryFormatter();
                DocWeight = (Dictionary<string,double>)bf.Deserialize(fs);//read object
                Docsmaxtf = (Dictionary<string, int>)bf.Deserialize(fs);//read object
                docslength = (Dictionary<string, double>)bf.Deserialize(fs);//read object
                avgDL = (double)bf.Deserialize(fs);
            }
        }
        else
        {
            //calculate the dictionaries in order to run- long process
            calculateWeight();
            //after calculate save it for next time
            using (FileStream fs = new FileStream(file, FileMode.Create))
            {
                IFormatter bf = new BinaryFormatter();
                bf.Serialize(fs, DocWeight);//write object
                bf.Serialize(fs, Docsmaxtf);//write object
                bf.Serialize(fs, docslength);//write object
                bf.Serialize(fs, avgDL);//write object
            }

        }
    }
    //Calculate cosin and BM fields
    public void calculateWeight()
    {
        for (int i = 0; i < r.files.Count; i++)
        {
            Debug.WriteLine(i);
            //Run on all files again
            Dictionary<string, string> d = r.ProccessDocs(r.files[i]);
            foreach (string docNo in d.Keys)
            {
                //Parse DOC
                Dictionary<string, termInfo> docdic = p.Parse(d[docNo]);
                //length dictionary
                docslength[docNo.Trim(' ')] = docdic.Count;
                //Wight dictionary
                DocWeight.Add(docNo.Trim(' '), 0);
                //max tf dic
                Docsmaxtf.Add(docNo.Trim(' '), p.maxtf);
                foreach (string trm in docdic.Keys)
                {
                    if (docdic.ContainsKey(trm)&& ind.dic.ContainsKey(trm))
                    {  //For Cosin
                        double W = (((double)docdic[trm].tf /(double) p.maxtf) * (double)ind.dic[trm].idf);
                        DocWeight[docNo.Trim(' ')] += (W * W);
                    }
                }
                DocWeight[docNo.Trim(' ')] = Math.Sqrt(DocWeight[docNo.Trim(' ')]);
            }
        }
        //for BM Formula
        foreach(string s in docslength.Keys)
        {
            avgDL += docslength[s];
        }
        avgDL = avgDL / (double)docslength.Count;

    }
    //rate Docs of specific query, calculate cosin and bm25 and return the final list- 50 or 70 docs aprox.
    public List<string> rateDocs(Dictionary<string, Dictionary<string, int>> qTerms,int numOfTerms,int amount)
    {
        List<KeyValuePair<string, double>> temp = new List<KeyValuePair<string, double>>();

        foreach (string docNum in qTerms.Keys)
        {
            //cos_sim
            double upCos = 0;
            double downCos = 0;
            //BM25
            double upBM25 = 0;
            double downBM25 = 0;
            double bm25 = 0;
            double bmbcal = k * (0.25 + 0.75 * docslength[docNum] / avgDL);

            //all terms in query
            foreach (string query_term in qTerms[docNum].Keys)
            {
                //if term exsits in query but not in dictionary
                if (ind.dic.ContainsKey(query_term))
                {
                    //cosin
                    double Wij=((double)qTerms[docNum][query_term] / (double)Docsmaxtf[docNum]) * (double)ind.dic[query_term].idf;
                    upCos += Wij;

                    //BM25
                    upBM25 = (double)ind.dic[query_term].idf * (double)qTerms[docNum][query_term] * (k + 1);
                    downBM25 = (double)qTerms[docNum][query_term] + bmbcal ;
                    bm25 += upBM25 / downBM25;
                }
            }
            downCos = Math.Sqrt((double)numOfTerms)* (double)DocWeight[docNum];
            double cosin = upCos / downCos;
            //Final Formula
            double formul = 0.4 * (cosin) + 0.6 * (bm25);
            temp.Add(new KeyValuePair<string, double>(docNum, formul));
        }
        //Sort the temp
        temp.Sort((KeyValuePair<string,double> d1, KeyValuePair<string, double> d2) => d2.Value.CompareTo(d1.Value));
        List<string> ans = new List<string>();
        for (int i=0;i<temp.Count && i<amount;i++)
        {
            //list of the docs after rank
            ans.Add(temp[i].Key);
        }
        return ans;
    }
}
}
 */