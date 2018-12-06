package Model;


/**
 *
 */
public class TermDetailes {
    public String DocId; // needed for posting thats why i keep it
    public int TF; //tf
    public Boolean IsInTitle;

    /**
     * @param docid
     */
    public TermDetailes(String docid) {
        DocId = docid;
        TF = 0;
        IsInTitle = false;
    }

    /**
     * @return
     */
    public String getDocId() {
        return DocId;
    }

    /**
     * @param docId
     */
    public void setDocId(String docId) {
        DocId = docId;
    }

    /**
     *
     */
    public void UpdateTF(){this.TF = TF+1;}

    public int getTF() {
        return TF;
    }

    /**
     * @param TF
     */
    public void setTF(int TF) {
        this.TF = TF;
    }

    /**
     * @param toAdd
     */
    public void SumTF(int toAdd){ this.TF = this.TF + toAdd; }

    /**
     * @return
     */
    public Boolean getInTitle() {
        return IsInTitle;
    }

    /**
     * @param inTitle
     */
    public void setInTitle(Boolean inTitle) {
        IsInTitle = inTitle;
    }
}
