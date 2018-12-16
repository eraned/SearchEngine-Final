package Model;

/**
 * represent detailes on every record in the dictionary all over the corpus
 */
public class DictionaryDetailes {
    public int NumOfDocsTermIN;  //df
    public int NumOfTermInCorpus;
    public int Pointer;

    /**
     * constructor
     */
    public DictionaryDetailes() {
        NumOfTermInCorpus = 0;
        NumOfDocsTermIN = 1;
        Pointer = 0;
    }

    public int getNumOfDocsTermIN() {
        return NumOfDocsTermIN;
    }
    public void setNumOfDocsTermIN(int numOfDocsTermIN) {NumOfDocsTermIN = numOfDocsTermIN; }
    public int getNumOfTermInCorpus() {return NumOfTermInCorpus;}
    public void setNumOfTermInCorpus(int numOfTermInCorpus) {NumOfTermInCorpus = numOfTermInCorpus; }
    public int getPointer() {
        return Pointer;
    }
    public void setPointer(int pointer) {
        Pointer = pointer;
    }
    public void UpdateNumOfTermInCorpus(int update){this.NumOfTermInCorpus = NumOfTermInCorpus + update;}
    public void UpdateNumOfDocsTermIN(){NumOfDocsTermIN++;}
}
