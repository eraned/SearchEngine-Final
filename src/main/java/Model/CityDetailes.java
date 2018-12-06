package Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 */
public class CityDetailes {
    public String Country;
    public String Currency;
    public String PopulationSize;
    public HashMap<String,ArrayList<Integer>> CityInDoc;  // <DocID,PositionsInDoc>

    /**
     *
     */
    public CityDetailes(){CityInDoc = new HashMap<>();}

    /**
     * @param country
     * @param currency
     * @param populationSize
     */
    public void UpdateCityDetailes(String country,String currency, String populationSize) {
        Country = country;
        Currency = currency;
        PopulationSize = populationSize;
    }

    /**
     * @return
     */
    public String getCountry() {
        return Country;
    }

    /**
     * @param country
     */
    public void setCountry(String country) {
        Country = country;
    }

    /**
     * @return
     */
    public String getCurrency() {return Currency; }

    /**
     * @param currency
     */
    public void setCurrency(String currency) {Currency = currency; }

    /**
     * @return
     */
    public String getPopulationSize() {
        return PopulationSize;
    }

    /**
     * @param populationSize
     */
    public void setPopulationSize(String populationSize) {
        PopulationSize = populationSize;
    }

    /**
     * @param docid
     * @param positionsindoc
     */
    public void AddNewDoc(String docid,ArrayList<Integer> positionsindoc){
        CityInDoc.put(docid,positionsindoc);
    }
}
