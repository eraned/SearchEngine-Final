package Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * represent detailes on every city in the corpus
 * fields to save on every city by the work Requirements
 */
public class CityDetailes {
    public String Country;
    public String Currency;
    public String PopulationSize;
    public HashMap<String, ArrayList<Integer>> CityInDoc;  // <DocID,PositionsInDoc>

    /**
     * Constructor
     */
    public CityDetailes() {
        CityInDoc = new HashMap<>();
    }

    /**
     * @param country
     * @param currency
     * @param populationSize
     */
    public void UpdateCityDetailes(String country, String currency, String populationSize) {
        Country = country;
        Currency = currency;
        PopulationSize = populationSize;
    }

    public void AddNewDoc(String docid, ArrayList<Integer> positionsindoc) {
        CityInDoc.put(docid, positionsindoc);
    }

    public HashMap<String, ArrayList<Integer>> getCityInDoc() {
        return CityInDoc;
    }

}
//    public String getCountry() {
//        return Country;
//    }
//    public void setCountry(String country) {
//        Country = country;
//    }
//    public String getCurrency() {return Currency; }
//    public void setCurrency(String currency) {Currency = currency; }
//    public String getPopulationSize() {
//        return PopulationSize;
//    }
//    public void setPopulationSize(String populationSize) {
//        PopulationSize = populationSize;
//    }



