package Model;


public class TermDetailes {
    public String DocId; // needed for posting thats why i keep it
    public int TF; //tf
    public Boolean IsInTitle;

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
