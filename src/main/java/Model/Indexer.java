package Model;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.*;
import java.util.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * This department is responsible for create InvertedIndex from all the Corpus,
 * it receives block from the Parser of parsed document and adds each term to pusting and the Dictionary.
 * In order to keep the memory free and not to crush it performs Flush every block of documents
 * and writes the posting to disk and starts new posting.
 * then merge all the files into one file and divide it into 6 ranges that the dictionary points to them by each term
 */
public class Indexer {

    public String CorpusPathOUT;
    public boolean StemmerNeeded;
    public HashMap<String, DictionaryDetailes> Dictionary;
    public HashMap<String, ArrayList<TermDetailes>> Posting;
    public StringBuilder stbOUT;
    public int PostingNumber;
    public int MergeNumber;
    public int BlockCounter;
    public int PostingDocIndex;
    public long PostingSize;
    public int NumOfTermsBeforeStemming;
    public int NumOfTermsAfterStemming;
    public int NumOfTerms_Numbers;
    public String DocMaxCity;
    public static HashSet<String> Entitys;
    public int atlanticCounter_Docs;
    public int atlanticCounter_TF;
    public int atlanticCounter_TotalTF;
    public int atlanticCounter_Idf;

    /**
     * Constructor
     * @param corpusPathOUT - where to save all the output of the indexer
     * @param isStemmer     - get from the user
     */
    public Indexer(String corpusPathOUT, boolean isStemmer) {
        atlanticCounter_Docs = 0;
        atlanticCounter_TF = 0;
        atlanticCounter_TotalTF = 0;
        atlanticCounter_Idf = 0;
        CorpusPathOUT = corpusPathOUT;
        StemmerNeeded = isStemmer;
        Dictionary = new HashMap<>();
        Posting = new HashMap<>();
        stbOUT = new StringBuilder();
        PostingNumber = 0;
        MergeNumber = 0;
        PostingSize = 0;
        BlockCounter = 0;
        PostingDocIndex = 0;
        NumOfTermsBeforeStemming = 0;
        NumOfTermsAfterStemming = 0;
        NumOfTerms_Numbers = 0;
        DocMaxCity = "";
        Entitys = new HashSet<>();
        if (StemmerNeeded)
            stbOUT.append(CorpusPathOUT + "\\EngineOut_WithStemmer\\"); //todo
        else
            stbOUT.append(CorpusPathOUT + "\\EngineOut\\"); //todo
        File folder = new File(stbOUT.toString());
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++)
                try {
                    Files.deleteIfExists(listOfFiles[i].toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } else {
            folder.mkdir();
        }
    }

    /**
     * method that hellpes the memory not to crush.
     * every bluck create tmp posting file into txt file and clear the hashmap of posting
     *
     * @param DocAfterParse - get from the parser parsed doc in hashmap
     * @param Docid         - the docid that parsed
     * @throws IOException
     */
    public void CreateMINI_Posting(HashMap<String, TermDetailes> DocAfterParse, String Docid) throws IOException {
        int MaxTermFreq = 0;
        StringBuilder Ent = new StringBuilder();
        //improve parser
        for (String tmpTerm : DocAfterParse.keySet()) {
            TermDetailes tmpTermDetailes = DocAfterParse.get(tmpTerm);
            try {
                if (StringUtils.contains(tmpTerm, '-')) {
                    if (StringUtils.containsOnly(tmpTerm, '-')) {
                        continue;
                    } else if (tmpTerm.charAt(0) == '-' || tmpTerm.endsWith("-")) {
                        tmpTerm = RegExUtils.removeAll(tmpTerm, "-");
                    } else if (StringUtils.contains(tmpTerm, '/'))
                        continue;
                    else {
                        if (StringUtils.contains(tmpTerm, "--"))
                            tmpTerm = RegExUtils.replaceAll(tmpTerm, "--", "-");
                        String[] splited = StringUtils.split(tmpTerm, "-");
                        for (int i = 0; i < splited.length; i++) {
                            if (!StringUtils.isAlphanumeric(splited[i])) {
                                continue;
                            }
                        }

                    }
                } else if (tmpTerm.length() <= 1 && StringUtils.contains(tmpTerm, '/'))
                    continue;
                else if (!ParserBooster(tmpTerm))
                    continue;
            } catch (Exception e) { //todo - delete before
                System.out.println(tmpTerm);
            }
            if (StringUtils.isAlpha(tmpTerm) && StringUtils.isAllUpperCase(tmpTerm)) {
                Ent.append(tmpTerm + ":" + tmpTermDetailes.getTF() + ";");
            }
            // not in Post
            if (!Posting.containsKey(tmpTerm)) {
                Posting.put(tmpTerm, new ArrayList<TermDetailes>());
                Posting.get(tmpTerm).add(tmpTermDetailes);
            }
            // in Post
            else {
                Posting.get(tmpTerm).add(tmpTermDetailes);
            }
            //in dic regular
            if (Dictionary.containsKey(tmpTerm)) {
                Dictionary.get(tmpTerm).UpdateNumOfDocsTermIN();
                Dictionary.get(tmpTerm).UpdateNumOfTermInCorpus(tmpTermDetailes.getTF());
            }
            //in dic with big letters
            else if (Dictionary.containsKey(tmpTerm.toUpperCase()) && Character.isLowerCase(tmpTerm.charAt(0))) {
                DictionaryDetailes tmp = Dictionary.get(tmpTerm.toUpperCase());
                tmp.UpdateNumOfDocsTermIN();
                tmp.UpdateNumOfTermInCorpus(tmpTermDetailes.getTF());
                Dictionary.remove(tmpTerm.toUpperCase());
                Dictionary.put(tmpTerm.toLowerCase(), tmp);
            }
            // not in Dic
            else {
                DictionaryDetailes tmpDictionaryDetailes = new DictionaryDetailes();
                tmpDictionaryDetailes.setNumOfTermInCorpus(tmpTermDetailes.getTF());
                Dictionary.put(tmpTerm, tmpDictionaryDetailes);
            }
            if (tmpTermDetailes.getTF() > MaxTermFreq) {
                MaxTermFreq = tmpTermDetailes.getTF();
            }
        }

        SearchEngine.All_Docs.get(Docid).setMaxTermFrequency(MaxTermFreq);
        SearchEngine.All_Docs.get(Docid).setDocLength(DocAfterParse.size());
        Ent.append("#");
        SearchEngine.All_Docs.get(Docid).setDocSuspectedEntitys(Ent);
        if (BlockCounter == 5000) {
            ItsTimeForFLUSH_POSTING();
            Posting.clear();
            BlockCounter = 0;
            PostingDocIndex = 0;
        }
        BlockCounter++;
    }


    /**
     * when you get the memory full its write all the hashmap of the posting to txt file for later use.
     *
     * @throws IOException
     */
    public void ItsTimeForFLUSH_POSTING() throws IOException {
        File tmpPost = new File(stbOUT.toString() + PostingNumber + ".txt");
        ArrayList<String> SortedPost = new ArrayList<>(Posting.keySet());
        Collections.sort(SortedPost);
        PostingNumber++;
        try {
            FileWriter FW = new FileWriter(tmpPost);
            BufferedWriter BW = new BufferedWriter(FW);
            for (String term : SortedPost) {
                BW.write(term + ":");
                for (TermDetailes TD : Posting.get(term)) {
                    BW.write("Docid:" + TD.getDocId() + ";TF:" + TD.getTF() + ";InTitle:" + TD.getInTitle() + "->");
                }
                BW.write("#");
                BW.newLine();
            }
            BW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * after all tmp posting files was created merge every 2 docs Lexicography for final posting file.
     *
     * @throws IOException
     */

    public void ItsTimeForMERGE_All_Postings() throws IOException {
        File file = new File(stbOUT.toString());
        File[] FilestoMerge = file.listFiles();
        //todo - corpus more then one block
        if (FilestoMerge.length >= 1) {
            while (FilestoMerge.length > 1) {
                for (int i = 0; i < FilestoMerge.length - 1; i += 2) {
                    EXTERNAL_SORT(FilestoMerge[i], FilestoMerge[i + 1], MergeNumber);
                    MergeNumber++;
                }
                FilestoMerge = file.listFiles();
            }
            //todo - corpus even posting files
            if (FilestoMerge.length == 1) {
                ItsTimeForFLUSH_POSTING();
                FilestoMerge = file.listFiles();
                EXTERNAL_SORT(FilestoMerge[0], FilestoMerge[1], MergeNumber);
                FilestoMerge = file.listFiles();
                PostingSize = FilestoMerge[0].length() / 1024;
                Posting.clear();
                ItsTimeForSPLIT_Final_Posting();
            }
            //todo - corpus odd posting files
            if (FilestoMerge.length == 2) {
                EXTERNAL_SORT(FilestoMerge[0], FilestoMerge[1], MergeNumber);
                ItsTimeForFLUSH_POSTING();
                FilestoMerge = file.listFiles();
                EXTERNAL_SORT(FilestoMerge[0], FilestoMerge[1], MergeNumber);
                FilestoMerge = file.listFiles();
                PostingSize = FilestoMerge[0].length() / 1024;
                Posting.clear();
                ItsTimeForSPLIT_Final_Posting();
            }
        }
        //todo - corpus less then one block
        else {
            ItsTimeForFLUSH_POSTING();
            FilestoMerge = file.listFiles();
            File filenewName = new File(stbOUT.toString() + MergeNumber + "tmp.txt");
            FilestoMerge[0].renameTo(filenewName);
            FilestoMerge = file.listFiles();
            PostingSize = FilestoMerge[0].length() / 1024;
            Posting.clear();
            ItsTimeForSPLIT_Final_Posting();
        }
    }


    /**
     * merge two file Lexicography implementaion line by line.
     *
     * @param F1 - tmp posting file to merge
     * @param F2 - tmp posting file to merge
     * @throws IOException
     */
    public void EXTERNAL_SORT(File F1, File F2, int merge) throws IOException {
        FileWriter FW = new FileWriter(new File(stbOUT.toString() + merge + "tmp.txt"));
        BufferedReader BR1 = new BufferedReader(new FileReader(F1));
        BufferedReader BR2 = new BufferedReader(new FileReader(F2));
        String S1 = BR1.readLine();
        String S2 = BR2.readLine();
        int Compare;
        while (S1 != null && S2 != null) {
            if (S1.length() == 0 || S2.length() == 0)
                break;
            String t1 = S1.substring(0, S1.indexOf(":"));
            String t2 = S2.substring(0, S2.indexOf(":"));
            Compare = t1.compareTo(t2);
            if (Compare > 0) {
                FW.write(S2 + System.getProperty("line.separator"));
                S2 = BR2.readLine();

            } else if (Compare < 0) {
                FW.write(S1 + System.getProperty("line.separator"));
                S1 = BR1.readLine();
            } else {
                StringBuilder stb = new StringBuilder();
                stb.append(t2 + ":");
                stb.append(S1.substring(S1.indexOf(":") + 1, S1.indexOf("#")));
                stb.append(S2.substring(S2.indexOf(":") + 1));
                FW.write(stb.toString() + System.getProperty("line.separator"));
                S1 = BR1.readLine();
                S2 = BR2.readLine();
            }
        }
        while (S1 != null) {
            FW.write(S1 + System.getProperty("line.separator"));
            S1 = BR1.readLine();
        }
        while (S2 != null) {
            FW.write(S2 + System.getProperty("line.separator"));
            S2 = BR2.readLine();
        }
        BR1.close();
        BR2.close();
        Files.delete(F1.toPath());
        Files.delete(F2.toPath());
        FW.flush();
        FW.close();
    }

    /**
     * split the final posting file to 6 ranges to improve to find doc for query
     */
    public void ItsTimeForSPLIT_Final_Posting() {
        File Numbers = new File(stbOUT + "\\Numbers.txt"); //todo
        File A_E = new File(stbOUT + "\\A_E.txt");
        File F_J = new File(stbOUT + "\\F_J.txt");
        File K_P = new File(stbOUT + "\\K_P.txt");
        File Q_U = new File(stbOUT + "\\Q_U.txt");
        File V_Z = new File(stbOUT + "\\V_Z.txt");
        File Final_Posting = new File(stbOUT.toString() + MergeNumber + "tmp.txt");
        int countNumber = 0;
        int countA_E = 0;
        int countF_J = 0;
        int countK_P = 0;
        int countQ_U = 0;
        int countV_Z = 0;
        try {
            BufferedReader BR_Final_Posting = new BufferedReader(new FileReader(Final_Posting));
            FileWriter Numbers_FW = new FileWriter(Numbers);
            FileWriter A_E_FW = new FileWriter(A_E);
            FileWriter F_J_FW = new FileWriter(F_J);
            FileWriter K_P_FW = new FileWriter(K_P);
            FileWriter Q_U_FW = new FileWriter(Q_U);
            FileWriter V_Z_FW = new FileWriter(V_Z);
            BufferedWriter Numbers_BW = new BufferedWriter(Numbers_FW);
            BufferedWriter A_E_BW = new BufferedWriter(A_E_FW);
            BufferedWriter F_J_BW = new BufferedWriter(F_J_FW);
            BufferedWriter K_P_BW = new BufferedWriter(K_P_FW);
            BufferedWriter Q_U_BW = new BufferedWriter(Q_U_FW);
            BufferedWriter V_Z_BW = new BufferedWriter(V_Z_FW);
            String S = BR_Final_Posting.readLine();
            StringBuilder stbTerm = new StringBuilder();

            while (S != null) {
                stbTerm.append(S.substring(0, S.indexOf(":")));
                char tmpFirst = stbTerm.charAt(0);
                tmpFirst = Character.toLowerCase(tmpFirst);
                //A-E
                if (tmpFirst >= 'a' && tmpFirst <= 'e') {
                    A_E_BW.write(S);
                    if (Dictionary.containsKey(stbTerm.toString())) {
                        Dictionary.get(stbTerm.toString()).setPointer(countA_E);
                    }
                    A_E_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countA_E++;
                    continue;
                }
                //F-J
                else if (tmpFirst >= 'f' && tmpFirst <= 'j') {
                    F_J_BW.write(S);
                    if (Dictionary.containsKey(stbTerm.toString())) {
                        Dictionary.get(stbTerm.toString()).setPointer(countF_J);
                    }
                    F_J_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countF_J++;
                    continue;
                }
                //K-P
                else if (tmpFirst >= 'k' && tmpFirst <= 'p') {
                    K_P_BW.write(S);
                    if (Dictionary.containsKey(stbTerm.toString())) {
                        Dictionary.get(stbTerm.toString()).setPointer(countK_P);
                    }
                    K_P_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countK_P++;
                    continue;
                }
                //Q-U
                else if (tmpFirst >= 'q' && tmpFirst <= 'u') {
                    Q_U_BW.write(S);
                    if (Dictionary.containsKey(stbTerm.toString())) {
                        Dictionary.get(stbTerm.toString()).setPointer(countQ_U);
                    }
                    Q_U_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countQ_U++;
                    continue;
                }
                //V-Z
                else if (tmpFirst >= 'v' && tmpFirst <= 'z') {
                    V_Z_BW.write(S);
                    if (Dictionary.containsKey(stbTerm.toString())) {
                        Dictionary.get(stbTerm.toString()).setPointer(countV_Z);
                    }
                    V_Z_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countV_Z++;
                    continue;
                }
                //Numbers
                else {
                    Numbers_BW.write(S);
                    if (Dictionary.containsKey(stbTerm.toString())) {
                        Dictionary.get(stbTerm.toString()).setPointer(countNumber);
                    }
                    Numbers_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countNumber++;
                    continue;
                }
            }
            BR_Final_Posting.close();
            Files.delete(Final_Posting.toPath());
            Numbers_BW.close();
            A_E_BW.close();
            F_J_BW.close();
            K_P_BW.close();
            Q_U_BW.close();
            V_Z_BW.close();
            NumOfTerms_Numbers = countNumber;
            if (StemmerNeeded) {
                NumOfTermsAfterStemming = Dictionary.size();
            } else {
                NumOfTermsBeforeStemming = Dictionary.size();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ItsTimeToWriteDictionary();
        LoadEntitiysToDocs();
        Dictionary.clear();
    }

    /**
     * after the inverted index was created write the Dictionary to the disk
     */
    public void ItsTimeToWriteDictionary() {
        File DictionaryDoc = new File(stbOUT + "\\Dictionary" + ".txt"); //todo
        ArrayList<String> SortedDic = new ArrayList<>(Dictionary.keySet());
        Collections.sort(SortedDic);
        try {
            FileWriter FW = new FileWriter(DictionaryDoc);
            BufferedWriter BW = new BufferedWriter(FW);
            for (String term : SortedDic) {
                if ((StringUtils.isAlpha(term)) && (StringUtils.isAllUpperCase(term))) {
                    Entitys.add(term);
                }
                BW.write(term + ":" + "TotalTF:" + Dictionary.get(term).getNumOfTermInCorpus() + ";DF:" + Dictionary.get(term).getNumOfDocsTermIN() + ";Pointer:" + Dictionary.get(term).getPointer() + "#");
                BW.newLine();
            }
            BW.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param term
     * @return
     */
    public boolean ParserBooster(String term) {
        if ((term.endsWith("K") || term.endsWith("M") || term.endsWith("B") || term.endsWith("%")) && (!StringUtils.isAlpha(term))) {
            if (!StringUtils.isNumeric(term.substring(0, term.length() - 1)) && !NumberUtils.isParsable(term.substring(0, term.length() - 1)))
                return false;
        } else if (term.endsWith("Dollars") && term.length() > 8) {
            int index = term.indexOf("Dollars");
            char c = term.charAt(index - 2);
            if (!Character.isDigit(c) && (c != 'M'))
                return false;
            if (term.length() > 10 && (c == 'M')) {
                char w = term.charAt(index - 4);
                if (!Character.isDigit(w))
                    return false;
            }
        } else if (!StringUtils.isAlpha(term) && !StringUtils.isNumeric(term))
            return false;
        else if (!NumberUtils.isParsable(term) && StringUtils.isNumeric(term))
            return false;
        return true;
    }


    // todo https://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
    public void LoadEntitiysToDocs() {
        try {
            HashMap<String, Integer> ans = new HashMap<>();
            HashMap<Integer, String> tmp = new HashMap<>();
            StringBuilder stb = new StringBuilder();
            String term;
            String tf;
            int TF;
            String Suspected;
            for (String Doc : SearchEngine.All_Docs.keySet()) {
                try {
                    Suspected = SearchEngine.All_Docs.get(Doc).getDocSuspectedEntitys().toString();
                    while (Suspected.length() > 1) {
                        term = Suspected.substring(0, Suspected.indexOf(":"));
                        tf = Suspected.substring(Suspected.indexOf(":") + 1, Suspected.indexOf(";"));
                        TF = Integer.parseInt(tf);
                        if (Entitys.contains(term)) {
                            ans.put(term, TF);
                        }
                        Suspected = Suspected.substring(Suspected.indexOf(";") + 1);
                    }
                    int counter = 0;
                    List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(ans.entrySet());
                    Collections.sort(list, new Comparator<Entry<String, Integer>>() {
                        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                            return o2.getValue().compareTo(o1.getValue());
                        }
                    });
                    Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
                    for (Entry<String, Integer> entry : list) {
                        if (counter < 5) {
                            stb.append(entry.getKey() + "-" + entry.getValue() + ";");
                            counter++;
                        } else
                            break;
                    }
                    stb.append("#");
                    SearchEngine.All_Docs.get(Doc).getDocSuspectedEntitys().setLength(0);
                    SearchEngine.All_Docs.get(Doc).getDocSuspectedEntitys().append(stb.toString());
                    stb.setLength(0);
                    ans.clear();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

