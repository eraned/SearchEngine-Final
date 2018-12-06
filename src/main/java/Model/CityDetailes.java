package Model;

import java.util.ArrayList;
import java.util.HashMap;

public class CityDetailes {
    public String Country;
    public String Currency;
    public String PopulationSize;
    public HashMap<String,ArrayList<Integer>> CityInDoc;  // <DocID,PositionsInDoc>

    public CityDetailes(){CityInDoc = new HashMap<>();}

    public void UpdateCityDetailes(String country,String currency, String populationSize) {
        Country = country;
        Currency = currency;
        PopulationSize = populationSize;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public String getCurrency() {return Currency; }

    public void setCurrency(String currency) {Currency = currency; }

    public String getPopulationSize() {
        return PopulationSize;
    }

    public void setPopulationSize(String populationSize) {
        PopulationSize = populationSize;
    }

    public void AddNewDoc(String docid,ArrayList<Integer> positionsindoc){
        CityInDoc.put(docid,positionsindoc);
    }
}
