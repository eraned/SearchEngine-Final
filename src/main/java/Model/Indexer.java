package Model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.*;
import java.util.*;


/**
 *
 */
public class Indexer {

    public String CorpusPathOUT;
    public boolean StemmerNeeded;
    public static HashMap<String, DictionaryDetailes> Dictionary; //<Term,DictionaryDetailes>
    public static HashMap<String, ArrayList<TermDetailes>> Posting; //<Term,ArrayList<TermDetailes>>
    public StringBuilder stbOUT;
    public int PostingNumber;
    public int BlockCounter;
    public int PostingDocIndex;
    public  long PostingSize;
    public  int NumOfTermsBeforeStemming;// Dictionary size
    public  int NumOfTermsAfterStemming;// Dictionary size
    public  int NumOfTerms_Numbers;
    public boolean FinalLap;
    public String DocMacCity;


    /**
     * @param corpusPathOUT
     * @param isStemmer
     */
    public Indexer(String corpusPathOUT,Boolean isStemmer) {
        CorpusPathOUT = corpusPathOUT;
        StemmerNeeded = isStemmer;
        Dictionary = new HashMap<>();
        Posting = new HashMap<>();
        stbOUT = new StringBuilder();
        PostingNumber = 0;
        PostingSize = 0;
        BlockCounter = 0;
        PostingDocIndex = 0;
        NumOfTermsBeforeStemming = 0;
        NumOfTermsAfterStemming = 0;
        NumOfTerms_Numbers = 0;
        FinalLap = false;
        DocMacCity = "";

        if(StemmerNeeded){
            stbOUT.append(CorpusPathOUT + "/EngineOut_WithStemmer/"); //lab path - "\\EngineOut_WithStemmer\\"
        }
        else
            stbOUT.append(CorpusPathOUT + "/EngineOut/"); //lab path - "\\EngineOut\\"

        File folder = new File(stbOUT.toString());
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

    /**
     * @param DocAfterParse
     * @param Docid
     * @throws IOException
     */
    public void CreateMINI_Posting(HashMap<String, TermDetailes> DocAfterParse,String Docid) throws IOException {
        int MaxTermFreq = 0;
        for (String tmpTerm : DocAfterParse.keySet()) {
            if (tmpTerm.length() > 15 || tmpTerm.length() <= 1) { continue;}
            // in Post
            if (!Posting.containsKey(tmpTerm)) {
                Posting.put(tmpTerm,new ArrayList<TermDetailes>());
                Posting.get(tmpTerm).add(DocAfterParse.get(tmpTerm));
            }
            //not in Post
            else {
                Posting.get(tmpTerm).add(DocAfterParse.get(tmpTerm));
            }
            //in dic
            if (Dictionary.containsKey(tmpTerm)) {
                if (Dictionary.containsKey(tmpTerm.toUpperCase())&& Character.isLowerCase(tmpTerm.charAt(0))){
                    Dictionary.put(tmpTerm.toLowerCase(), Dictionary.get(tmpTerm.toUpperCase()));
                    Dictionary.remove(tmpTerm.toUpperCase());
                    Dictionary.get(tmpTerm.toLowerCase()).setNumOfDocsTermIN(Posting.get(tmpTerm).size());
                    Dictionary.get(tmpTerm.toLowerCase()).UpdateNumOfTermInCorpus(DocAfterParse.get(tmpTerm).getTF());
                }
                else{
                    Dictionary.get(tmpTerm).setNumOfDocsTermIN(Posting.get(tmpTerm).size());
                    Dictionary.get(tmpTerm).UpdateNumOfTermInCorpus(DocAfterParse.get(tmpTerm).getTF());
                }
            }
            // not in Dic
            else {
                DictionaryDetailes tmpDictionaryDetailes = new DictionaryDetailes();
                tmpDictionaryDetailes.UpdateNumOfTermInCorpus(DocAfterParse.get(tmpTerm).getTF());
                tmpDictionaryDetailes.setNumOfDocsTermIN(Posting.get(tmpTerm).size());
                Dictionary.put(tmpTerm, tmpDictionaryDetailes);
            }
            if (DocAfterParse.get(tmpTerm).getTF() > MaxTermFreq) {
                MaxTermFreq = DocAfterParse.get(tmpTerm).getTF();
            }
        }
        SearchEngine.All_Docs.get(Docid).setMaxTermFrequency(MaxTermFreq);
        if(BlockCounter == 5000) {
            ItsTimeForFLUSH_POSTING();
            Posting.clear();
            BlockCounter = 0;
            PostingDocIndex = 0;
        }
        BlockCounter++;
    }


    /**
     * @throws IOException
     */
    //copy to disk
    public void ItsTimeForFLUSH_POSTING()throws IOException{
        File tmpPost = new File(stbOUT.toString() + PostingNumber + ".txt");
        ArrayList<String> SortedPost = new ArrayList<>(Posting.keySet());
        Collections.sort(SortedPost);
        PostingNumber++;

        try {
            FileWriter FW = new FileWriter(tmpPost);
            BufferedWriter BW = new BufferedWriter(FW);
            for(String term : SortedPost){
                BW.write(term + ":");
                for(TermDetailes TD : Posting.get(term)) {
                    BW.write("(Docid :" + TD.getDocId() + "  ,  TF  :" + TD.getTF() + ")");
                }
                BW.newLine();
            }
            BW.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @throws IOException
     */
    public void ItsTimeForMERGE_All_Postings() throws IOException {
        File file = new File(stbOUT.toString());
        File[] FilestoMerge = file.listFiles();
        while (FilestoMerge.length > 1) {
            for (int i = 0; i < FilestoMerge.length - 1; i += 2) {
                EXTERNAL_SORT(FilestoMerge[i], FilestoMerge[i + 1]);
            }
            FilestoMerge = file.listFiles();
        }
        ItsTimeForFinalLap(file);
        ItsTImeToBoostDic();
        ItsTimeForSPLIT_Final_Posting();
    }

    /**
     * @param F1
     * @param F2
     * @throws IOException
     */
    public void EXTERNAL_SORT(File F1 , File F2) throws IOException{
        FileWriter FW = new FileWriter(new File(stbOUT + "/tmpMerge" + ".txt")); //lab path - "\\tmpMerge" + ".txt"
        BufferedReader BR1 = new BufferedReader(new FileReader(F1));
        BufferedReader BR2 = new BufferedReader(new FileReader(F2));
        String S1 = BR1.readLine();
        String S2 = BR2.readLine();
        int Compare;
        while (S1 != null && S2 != null){
            if(S1.length()==0 || S2.length()==0)
                break;
            String t1 = S1.substring(0,S1.indexOf(":"));
            String t2 = S2.substring(0,S2.indexOf(":"));
            Compare = t1.compareTo(t2);

            if(Compare > 0){
                FW.write( S2 +  System.getProperty( "line.separator" ) );
                S2 = BR2.readLine();

            }
            else if(Compare < 0){
                FW.write(S1 + System.getProperty( "line.separator" ));
                S1 = BR1.readLine();
            }
            else{
                StringBuilder stb = new StringBuilder();
                stb.append(S1.substring(S1.indexOf(":")+2));
                stb.append(S2.substring(S2.indexOf(":")+2));
                FW.write(stb.toString() + System.getProperty( "line.separator" ));
                S1 = BR1.readLine();
                S2 = BR2.readLine();
            }
        }
        while (S1 != null){
            if(S1.length()==0)
                break;
            FW.write(S1 + System.getProperty( "line.separator" ));
            S1 = BR1.readLine();
        }
        while(S2 != null){
            if(S2.length()==0)
                break;
            FW.write(S2 + System.getProperty( "line.separator" ));
            S2 = BR2.readLine();
        }
        FW.close();
        BR1.close();
        BR2.close();
        Files.delete(F1.toPath());
    }

    /**
     *
     */
    //for showing the dic sorted
    public void ItsTimeForSPLIT_Final_Posting(){
        File Numbers = new File(stbOUT+"/Numbers.txt"); //lab path - \\Numbers.txt"
        File A_E = new File(stbOUT+"/A_E.txt");  //lab path - "\\A_E.txt"
        File F_J = new File(stbOUT+"/F_J.txt");  //lab path - "\\F_J.txt"
        File K_P= new File(stbOUT+"/K_O.txt" );  //lab path - "\\K_O.txt"
        File Q_U = new File(stbOUT+"/Q_U.txt");  //lab path - "\\Q_U.txt"
        File V_Z = new File(stbOUT+"/V_Z.txt");  //lab path -   "\\V_Z.txt"
        File Final_Posting =  new File(stbOUT + "/tmpMerge" + ".txt");  //lab path - "\\tmpMerge" + ".txt"
        int countNumber = 0 ;
        int countA_E = 0 ;
        int countF_J = 0 ;
        int countK_P = 0 ;
        int countQ_U = 0 ;
        int countV_Z = 0 ;

        try{
            BufferedReader BR_Final_Posting = new BufferedReader(new FileReader(Final_Posting));
            FileWriter Numbers_FW = new FileWriter(Numbers);
            FileWriter A_E_FW= new FileWriter(A_E);
            FileWriter F_J_FW= new FileWriter(F_J);
            FileWriter K_P_FW= new FileWriter(K_P);
            FileWriter Q_U_FW= new FileWriter(Q_U);
            FileWriter V_Z_FW= new FileWriter(V_Z);
            BufferedWriter Numbers_BW = new BufferedWriter(Numbers_FW);
            BufferedWriter A_E_BW = new BufferedWriter(A_E_FW);
            BufferedWriter F_J_BW = new BufferedWriter(F_J_FW);
            BufferedWriter K_P_BW = new BufferedWriter(K_P_FW);
            BufferedWriter Q_U_BW = new BufferedWriter(Q_U_FW);
            BufferedWriter V_Z_BW = new BufferedWriter(V_Z_FW);
            String S = BR_Final_Posting.readLine();
            StringBuilder stbTerm = new StringBuilder();
            //if(!stbTerm.toString().isEmpty()){
            while(S != null) {
                stbTerm.append(S.substring(0, S.indexOf(":")));
                //A-E
                if (S.charAt(0) >= 'a' && S.charAt(0) <= 'e') {
                    A_E_BW.write(S);
                    if(Dictionary.containsKey(S)){
                        Dictionary.get(S).setPointer(countA_E);
                    }
                    A_E_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countA_E++;
                    continue;
                }
                //F-J
                else if (S.charAt(0) >= 'f' && S.charAt(0) <= 'j') {
                    F_J_BW.write(S);
                    if(Dictionary.containsKey(S)){
                        Dictionary.get(S).setPointer(countF_J);
                    }
                    F_J_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countF_J++;
                    continue;
                }
                //K-P
                else if (S.charAt(0) >= 'k' && S.charAt(0) <= 'p') {
                    K_P_BW.write(S);
                    if(Dictionary.containsKey(S)){
                        Dictionary.get(S).setPointer(countK_P);
                    }
                    K_P_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countK_P++;
                    continue;
                }
                //Q-U
                else if (S.charAt(0) >= 'q' && S.charAt(0) <= 'u') {
                    Q_U_BW.write(S);
                    if(Dictionary.containsKey(S)){
                        Dictionary.get(S).setPointer(countQ_U);
                    }
                    Q_U_BW.newLine();
                    S = BR_Final_Posting.readLine();
                    stbTerm.setLength(0);
                    countQ_U++;
                    continue;
                }
                //V-Z
                else if (S.charAt(0) >= 'v' && S.charAt(0) <= 'z') {
                    V_Z_BW.write(S);
                    if(Dictionary.containsKey(S)){
                        Dictionary.get(S).setPointer(countV_Z);
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
                    if(Dictionary.containsKey(S)){
                        Dictionary.get(S).setPointer(countNumber);
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
            if(StemmerNeeded) {
                NumOfTermsAfterStemming = countA_E + countF_J + countK_P + countNumber + countQ_U + countV_Z;
            }
            else {
                NumOfTermsBeforeStemming = countA_E + countF_J + countK_P + countNumber + countQ_U + countV_Z;
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        ItsTimeToWriteDictionary();
        Dictionary.clear();
    }

    /**
     * @param file
     * @throws IOException
     */
    public void ItsTimeForFinalLap(File file)throws IOException{
        ItsTimeForFLUSH_POSTING();
        File[] FilestoMerge = file.listFiles();
        EXTERNAL_SORT(FilestoMerge[0],FilestoMerge[1]);
        FilestoMerge = file.listFiles();
        PostingSize = FilestoMerge[0].length()/1024;
    }

    /**
     *
     */
    public void ItsTImeToBoostDic(){
        HashSet<String> Garbage = new HashSet<>();
        for (Map.Entry<String,DictionaryDetailes> term : Dictionary.entrySet()) {
            String tmpTerm = term.getKey();
            if(term.getValue().getNumOfTermInCorpus() < 8 || tmpTerm.equals(" ") || tmpTerm.equals("") || tmpTerm.isEmpty() || tmpTerm.length()<= 1){
                Garbage.add(tmpTerm);
            }
        }
        for(String t : Garbage){
            Dictionary.remove(t);
        }
    }

    /**
     *
     */
    public void ItsTimeToWriteDictionary(){
        File DictionaryDoc = new File(stbOUT + "/Dictionary" + ".txt");
        ArrayList<String> SortedDic = new ArrayList<>(Dictionary.keySet());
        Collections.sort(SortedDic);

        try {
            FileWriter FW = new FileWriter(DictionaryDoc);
            BufferedWriter BW = new BufferedWriter(FW);
            for(String term : SortedDic){
                // BW.write(  term + " :  ( Total TF  : " + Dictionary.get(term).getNumOfTermInCorpus() +")" );
                BW.write(  term + " : " + "Total Freq:"+Dictionary.get(term).getNumOfTermInCorpus() + ";DF:" + Dictionary.get(term).getNumOfDocsTermIN() + ";Pointer:" + Dictionary.get(term).getPointer());

                BW.newLine();
            }
            BW.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * @param Path
     * @return
     */
    public HashMap<String,DictionaryDetailes> ItsTimeToLoadDictionary(String Path){
        HashMap<String,DictionaryDetailes> LoadedDic = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(Path))) {
            String line = br.readLine();
            int totalfreq = 0,df = 0,pointer = 0;
            while (line != null ){
                int index = line.indexOf(':');
                String term = line.substring(0,index);
                line = line.substring(index + 1);
                if (!term.isEmpty()){
                    String TFreq = line.substring(12, line.indexOf(';'));
                    totalfreq = Integer.parseInt(TFreq);
                    line = line.substring(line.indexOf(';') + 1);
                    index = line.indexOf("DF:");
                    String DF = line.substring(index + 3, line.indexOf(';'));
                    df = Integer.parseInt(DF);
                    line = line.substring(line.indexOf(';') + 1);
                    index = line.indexOf("Pointer:");
                    String point = line.substring(index + 8);
                    pointer = Integer.parseInt(point);
                    DictionaryDetailes DD = new DictionaryDetailes();
                    DD.setNumOfTermInCorpus(totalfreq);
                    DD.setNumOfDocsTermIN(df);
                    DD.setPointer(pointer);
                    LoadedDic.put(term,DD);
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return LoadedDic;
    }
}
