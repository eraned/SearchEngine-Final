package Model;


import java.io.*;
import java.net.URL;
import java.util.*;
import java.net.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 */
public class SearchEngine {
    public static String CorpusPathIN;
    public static String CorpusPathOUT;
    public static StringBuilder StopWordsPath;
    public static boolean StemmerNeeded;
    private ReadFile readFile;
    private Parse parse;
    private Indexer indexer;
    private HashMap<String, DocDetailes> DocsPerBlock;
    private HashMap<String, TermDetailes> TermsPerDoc;
    private NumberToken NT;
    public static HashMap<String, DocDetailes> All_Docs;  //<DocId,Model.DocDetailes>
    public static HashMap<String, CityDetailes> Cities;     //<Cities,CityDetailes>
    public static HashSet<String> Countries;
    public static HashSet<String> Languages;
    public static int NumOfCitysNotCapital;

    /**
     * @param corpusPathIN
     * @param corpusPathOUT
     * @param Steemer
     * @throws IOException
     * @throws URISyntaxException
     */
    public SearchEngine(String corpusPathIN, String corpusPathOUT, boolean Steemer) throws IOException, URISyntaxException {
        long StartTime = System.nanoTime();
        CorpusPathIN = corpusPathIN;
        CorpusPathOUT = corpusPathOUT;
        StopWordsPath = new StringBuilder(corpusPathIN + "/stop_words.txt"); // lab path - "\\stop_words.txt"
        StemmerNeeded = Steemer;
        readFile = new ReadFile(CorpusPathIN);
        parse = new Parse(StemmerNeeded, StopWordsPath.toString());
        indexer = new Indexer(CorpusPathOUT, StemmerNeeded);
        NT = new NumberToken();
        All_Docs = new HashMap<>();
        Cities = new HashMap<>();
        Countries = new HashSet<>();
        Languages = new HashSet<>();
        NumOfCitysNotCapital = 0;
        System.out.println("Start your Engines!!!");
        // Starts threads
        readFile.ReadCorpus();
        for (int i = 0; i < readFile.GetSubFilesSize(); i++) {
            DocsPerBlock = readFile.ProccessSubFileToDocs(readFile.GetSubFilesPath(i));
            for (Map.Entry<String, DocDetailes> Docid : DocsPerBlock.entrySet()) {
                if (!Docid.getValue().getDocText().isEmpty() && !Docid.getKey().isEmpty()) {
                    if (!Cities.containsKey(Docid.getValue().getDocCity()) && Docid.getValue().getDocCity().length() > 0) {
                        ProcessCity(Docid.getValue().getDocCity());
                    }
                    All_Docs.put(Docid.getKey(), Docid.getValue());
                    TermsPerDoc = parse.ParseDoc(Docid.getValue().getDocText(), Docid.getKey(), Docid.getValue().getDocCity(), Docid.getValue().getDocTitle());
                    All_Docs.get(Docid.getKey()).setNumOfSpecialWords(TermsPerDoc.size());
                    indexer.CreateMINI_Posting(TermsPerDoc, Docid.getKey()); // update - tNumOfSpecialWords , DocLength , MaxTermFrequency
                }
            }
        }
        //finish threads
        indexer.ItsTimeForMERGE_All_Postings();
        indexer.ItsTimeFor_FinalDoc();
        long FinishTime = System.nanoTime();
        long TotalTime = FinishTime - StartTime;
        System.out.println("Out of fuel... lets do Summery :");
        System.out.println("Total time foe engine : " + TotalTime / 1000000);
    }

    /**
     * @param CitySection
     * @throws IOException
     * @throws URISyntaxException
     */
    //if term not in Cities database
    public void ProcessCity(String CitySection) throws IOException, URISyntaxException {  // API brings :String cityName, String country, String crrency, String populationSize
        Cities.put(CitySection, CityDetailesAPI(CitySection));
    }

    /**
     * @param city
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    //https://docs.oracle.com/javase/tutorial/networking/urls/readingURL.html
    public CityDetailes CityDetailesAPI(String city) throws IOException, URISyntaxException {
        try {
            URI CityUrl = new URI("http://getcitydetails.geobytes.com/GetCityDetails?fqcn=" + city);
            URL AfterCheck = CityUrl.toURL();
            BufferedReader Input = new BufferedReader(new InputStreamReader(AfterCheck.openStream()));
            CityDetailes tmp = new CityDetailes();
            String CityData;
            while ((CityData = Input.readLine()) != null) {
                String Country = CityData.substring(CityData.indexOf("\"geobytescountry\"") + 19, CityData.indexOf("\"geobytesregionlocationcode\"") - 2);
                String Capital = CityData.substring(CityData.indexOf("\"geobytescapital\"") + 19, CityData.indexOf("\"geobytestimezone\"") - 2);
                String Currency = CityData.substring(CityData.indexOf("\"geobytescurrency\"") + 20, CityData.indexOf("\"geobytescurrencycode\"") - 2);
                String PopulationSize = CityData.substring(CityData.indexOf("\"geobytespopulation\"") + 22, CityData.indexOf("\"geobytesnationalityplural\"") - 2);
                if (!Capital.isEmpty() && !PopulationSize.isEmpty() && !Currency.isEmpty() && !Country.isEmpty()) {
                    if (!Capital.toUpperCase().equals(city)) {
                        NumOfCitysNotCapital++;
                    }
                    if (!Countries.contains(Country.toUpperCase())) {
                        Countries.add(Country.toUpperCase());
                    }
                    List<String> L = Arrays.asList(PopulationSize);
                    ParsedResult PR = NT.TryParse(L);
                    tmp.UpdateCityDetailes(Country, Currency, PR.ParsedSentence.toString());
                }
            }
            Input.close();
            return tmp;
        }
        catch (URISyntaxException e) {
            return new CityDetailes();
        }
    }
}

