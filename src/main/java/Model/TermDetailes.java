package Model;


/**
 * represent detailes on every term in the posting file on each doc.
 */
public class TermDetailes {
    public String DocId;
    public int TF;
    public Boolean IsInTitle;

    /**
     * @param docid - constructor
     */
    public TermDetailes(String docid) {
        DocId = docid;
        TF = 0;
        IsInTitle = false;
    }

    public String getDocId() {
        return DocId;
    }
    public void setDocId(String docId) {
        DocId = docId;
    }
    public void UpdateTF(){this.TF = TF+1;}
    public int getTF() {
        return TF;
    }
    public void setTF(int TF) {
        this.TF = TF;
    }
    public void SumTF(int toAdd){ this.TF = this.TF + toAdd; }
    public Boolean getInTitle() {
        return IsInTitle;
    }
    public void setInTitle(Boolean inTitle) {
        IsInTitle = inTitle;
    }
}
