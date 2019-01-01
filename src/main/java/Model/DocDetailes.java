package Model;

import java.util.HashSet;

/**
 *
 */
public class DocDetailes {
    private int MaxTermFrequency; //max_tf
    private int NumOfSpecialWords;
    private int DocLength; //doc size
    private String DocText;
    private String DocDate;
    private String DocTitle;
    private String DocCity;
    private StringBuilder DocSuspectedEntitys;


    /**
     * Constructor
     * @param docText
     * @param docDate
     * @param docTitle
     * @param city
     */
    public DocDetailes(String docText, String docDate, String docTitle, String city) {
        DocText = docText;
        DocDate = docDate;
        DocTitle = docTitle;
        DocCity = city;
        DocLength = 0;
        MaxTermFrequency = 0;
        DocSuspectedEntitys = new StringBuilder();
    }


    public int getMaxTermFrequency() {return MaxTermFrequency;}
    public void setMaxTermFrequency(int maxTermFrequency) {
        MaxTermFrequency = maxTermFrequency;
    }
    public int getNumOfSpecialWords() {
        return NumOfSpecialWords;
    }
    public void setNumOfSpecialWords(int numOfSpecialWords) {
        NumOfSpecialWords = numOfSpecialWords;
    }
    public int getDocLength() {
        return DocLength;
    }
    public void setDocLength(int docLength) {
        DocLength = docLength;
    }
    public String getDocText() {return DocText;}
    public void setDocText(String docText) {
        DocText = docText;
    }
    public String getDocDate() {
        return DocDate;
    }
    public void setDocDate(String docDate) {
        DocDate = docDate;
    }
    public String getDocTitle() {
        return DocTitle;
    }
    public void setDocTitle(String docTitle) {DocTitle = docTitle;}
    public String getDocCity() {return DocCity;}
    public void setDocCity(String docCity) {DocCity = docCity; }
    public StringBuilder getDocSuspectedEntitys() {return DocSuspectedEntitys;}
    public void setDocSuspectedEntitys(StringBuilder ent) {DocSuspectedEntitys = ent;}
}
