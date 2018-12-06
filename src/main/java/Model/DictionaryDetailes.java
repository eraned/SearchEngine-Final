package Model;


/**
 *
 */
public class DictionaryDetailes {
    public int NumOfDocsTermIN;  //df
    public int NumOfTermInCorpus;
    public int Pointer;

    /**
     *
     */
    public DictionaryDetailes() {
        NumOfTermInCorpus = 0;
        NumOfDocsTermIN = 1;
    }

    /**
     * @return
     */
    public int getNumOfDocsTermIN() {
        return NumOfDocsTermIN;
    }

    /**
     * @param numOfDocsTermIN
     */
    public void setNumOfDocsTermIN(int numOfDocsTermIN) {NumOfDocsTermIN = numOfDocsTermIN; }

    /**
     * @return
     */
    public int getNumOfTermInCorpus() {return NumOfTermInCorpus;}

    /**
     * @param numOfTermInCorpus
     */
    public void setNumOfTermInCorpus(int numOfTermInCorpus) {NumOfTermInCorpus = numOfTermInCorpus; }

    /**
     * @return
     */
    public int getPointer() {
        return Pointer;
    }

    /**
     * @param pointer
     */
    public void setPointer(int pointer) {
        Pointer = pointer;
    }

    /**
     * @param update
     */
    public void UpdateNumOfTermInCorpus(int update){this.NumOfTermInCorpus = NumOfTermInCorpus + update;}

    /**
     *
     */
    public void UpdateNumOfDocsTermIN(){NumOfDocsTermIN++;}
}
