package Model;



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


    /**
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
    }

    /**
     * @return
     */
    public int getMaxTermFrequency() {return MaxTermFrequency;}

    /**
     * @param maxTermFrequency
     */
    public void setMaxTermFrequency(int maxTermFrequency) {
        MaxTermFrequency = maxTermFrequency;
    }

    /**
     * @return
     */
    public int getNumOfSpecialWords() {
        return NumOfSpecialWords;
    }

    /**
     * @param numOfSpecialWords
     */
    public void setNumOfSpecialWords(int numOfSpecialWords) {
        NumOfSpecialWords = numOfSpecialWords;
    }

    /**
     * @return
     */
    public int getDocLength() {
        return DocLength;
    }

    /**
     * @param docLength
     */
    public void setDocLength(int docLength) {
        DocLength = docLength;
    }

    /**
     * @return
     */
    public String getDocText() {
        return DocText;
    }

    /**
     * @param docText
     */
    public void setDocText(String docText) {
        DocText = docText;
    }

    /**
     * @return
     */
    public String getDocDate() {
        return DocDate;
    }

    /**
     * @param docDate
     */
    public void setDocDate(String docDate) {
        DocDate = docDate;
    }

    /**
     * @return
     */
    public String getDocTitle() {
        return DocTitle;
    }

    /**
     * @param docTitle
     */
    public void setDocTitle(String docTitle) {
        DocTitle = docTitle;
    }

    /**
     * @return
     */
    public String getDocCity() {return DocCity;}

    /**
     * @param docCity
     */
    public void setDocCity(String docCity) {DocCity = docCity; }

}
