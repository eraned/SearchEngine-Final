package Model;


import java.io.*;
import java.net.URL;
import java.util.*;
import java.net.*;
import java.net.URISyntaxException;

/**
 * This class manages the entire search engine,
 * it initializes all the documents from the corpus and sends each document to the parser and then to the  Indexer to create an inverted index.
 */
public class SearchEngine {
    public static String CorpusPathIN;
    public static String CorpusPathOUT;
    public static StringBuilder StopWordsPath;
    public static boolean StemmerNeeded;
    private ReadFile readFile;
    private Parse parse;
    public static Indexer indexer;
    private HashMap<String, DocDetailes> DocsPerBlock;
    private HashMap<String, TermDetailes> TermsPerDoc;
    private NumberToken NT;
    public static HashMap<String, DocDetailes> All_Docs;
    public static HashMap<String, CityDetailes> Cities;
    public static HashSet<String> Countries;
    public static HashSet<String> Languages;
    public static int NumOfCitysNotCapital;
    public static long TotalTime;

    /**
     * @param corpusPathIN - path in that the user enter
     * @param corpusPathOUT - path out that the user enter
     * @param Steemer - user choise
     * @throws IOException
     * @throws URISyntaxException
     */
    public SearchEngine(String corpusPathIN, String corpusPathOUT, boolean Steemer) throws IOException, URISyntaxException {
        long StartTime = System.nanoTime();
        CorpusPathIN = corpusPathIN;
        CorpusPathOUT = corpusPathOUT;
        StopWordsPath = new StringBuilder(corpusPathIN + "/stop_words.txt");
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
        TotalTime = 0;
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
        long FinishTime = System.nanoTime();
        TotalTime = FinishTime - StartTime;

    }

    /**
     * This function gets the city from each document and adds its to the data structure of the cities.
     * all the information that required get it from the API by the requirements of the work.
     * @param CitySection - city to find detailes on her in the api
     * @throws IOException
     * @throws URISyntaxException
     */
    //if term not in Cities database
    public void ProcessCity(String CitySection) throws IOException, URISyntaxException {  // API brings :String cityName, String country, String crrency, String populationSize
        Cities.put(CitySection, CityDetailesAPI(CitySection));
    }

    /**
     * @param city - city to find in the API
     * @return - new City detailes for the cities data structure
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



    public static StringBuilder Prepare_DocMaxCity_ToFinalDoc(){
        StringBuilder Final = new StringBuilder();
        CityDetailes tmpD = Cities.get(All_Docs.get(indexer.DocMaxCity).getDocCity());
        ArrayList<Integer> tmpP = tmpD.getCityInDoc().get(indexer.DocMaxCity);
        Final.append(" Doc with Max City Freq :" + indexer.DocMaxCity);
        Final.append(" City :" + All_Docs.get(indexer.DocMaxCity).getDocCity());
        Final.append(" Positiones :" + Arrays.toString(tmpP.toArray()));
        return Final;
    }

    /**
     * print all the detailes that needed by the work Requirements.
     */
    public static String ItsTimeFor_FinalDoc(){
        StringBuilder stb = new StringBuilder();
        stb.append("##############  Finale Doc  ##############.\n");
        stb.append(" Nummber of Terms Without Stamming :" + indexer.NumOfTermsBeforeStemming + "\n");
        stb.append(" Nummber of Terms With Stamming :" + indexer.NumOfTermsAfterStemming + "\n");
        stb.append(" Nummber of Terms Only Numbers :" + indexer.NumOfTerms_Numbers + "\n");
        stb.append(" Nummber of Different Countries :" + Countries.size() + "\n");
        stb.append(" Nummber of Different Cities :" + Cities.size() +"\n");
        stb.append(" Nummber of Different Cities not Capital :" + SearchEngine.NumOfCitysNotCapital + "\n");
        if(!indexer.DocMaxCity.isEmpty()) {
            stb.append(Prepare_DocMaxCity_ToFinalDoc() + "\n");
        }
        else {
            stb.append("No Cities where found!\n");
        }
        stb.append(" Posting Size :" + indexer.PostingSize + "KBs.\n");
        stb.append(" Total time foe engine : " + (double)SearchEngine.TotalTime / 1000000.0 +" Seconds.\n");
        stb.append("##############  Finished  ##############.\n");
        return stb.toString();
    }
}

