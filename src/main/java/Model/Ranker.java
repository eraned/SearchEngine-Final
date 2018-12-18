package Model;

import java.lang.reflect.Array;
import java.util.HashMap;

public class Ranker {





    public Ranker(String pathforposting) {







    }

    public void InitializWeights(String Query){ //Matrix of columns: d1,d2,d3....q     rows: t1,t2,t3....
        double CosSim_Matrix[][];
        double BM25_Matrix[][];






    }

    public HashMap<Integer,String> RankDocs(){ //HashMap<Rank,DocID> return only max 50 docs  ...final rank = 0.5 cosim + 0.5 BM25
        HashMap<Integer,String> RankerResult = new HashMap<>();











        return RankerResult;
    }

    public void GetTFfromPosting(){

    }




}
