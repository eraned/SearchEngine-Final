package Model;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import com.sun.deploy.util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * This class is responsible for reading all the corpus database,
 * first it reads all sub-folders and then every block sends a sub-folder to the parser to split to documents
 */
public class ReadFile {

    protected File MainPath;
    public static ArrayList<File> SubFilesPath;
    public StringBuilder stb;

    /**
     * @param path - to the corpuse directory
     */
    public ReadFile(String path) {
        this.MainPath = new File(path);
        SubFilesPath = new ArrayList<>();
    }

    /**
     * @throws IOException
     * read all the sub-folders Directories
     */
    public void ReadCorpus() throws IOException {
        if (MainPath.isDirectory() && MainPath != null) {
            ProccessSubFilesDirectories(MainPath.getAbsolutePath());
        }
    }

    /**
     * @param path - recursive Function to read all the sub-folders until you get to files
     * @throws IOException
     */
    public void ProccessSubFilesDirectories(String path) throws IOException {
        File file = new File(path);
        File[] SubDirectories = file.listFiles();
        for (File tmp : SubDirectories) {
            StringBuilder stop =new StringBuilder( MainPath.toString() + "/stop_words.txt");
            if (tmp.isFile() && !(tmp.toString().equals(stop.toString()))) {
                SubFilesPath.add(tmp);
            } else if (tmp != null && tmp.isDirectory()) {
                ProccessSubFilesDirectories(tmp.getAbsolutePath());
            }
        }
    }

    /**
     * @param subdirectory - a path to sub-folder and read the file and split it to docs and all the information needed to save for each doc
     * @return - a hashmap with all the docs and doc detailes.
     * @throws IOException
     */
    public HashMap<String, DocDetailes> ProccessSubFileToDocs(File subdirectory) throws IOException {
        BufferedReader bfr = new BufferedReader(new FileReader(subdirectory));
        HashMap<String, DocDetailes> tmpDocs = new HashMap<>();
        stb = new StringBuilder();
        String line = bfr.readLine();
        while (line != null) {
            stb.append(" " + line);
            line = bfr.readLine();
        }
        String content = stb.toString();
        Document d = Jsoup.parse(content);
        Elements elements = d.getElementsByTag("DOC"); //process doc
        for (Element element : elements) {
            boolean flag = false;
            String DocID = element.getElementsByTag("DOCNO").text();
            String DocText = element.getElementsByTag("TEXT").text();
            String DocDate = element.getElementsByTag("DATE1").text();
            String DocTitle = element.getElementsByTag("TI").text();
            String CitySection = element.getElementsByTag("F").toString();
            if (!CitySection.isEmpty()){
                String[] splited = StringUtils.splitString(CitySection, "[]}{(),<>%/:\"");
                for (int i = 0; i < splited.length; i++) {
                    if (splited[i].equals("104")) {
                        String[] finaleCity = StringUtils.splitString(splited[i + 1], " ");
                        if(finaleCity.length > 1) {
                            flag = true;
                            CitySection = finaleCity[1].toUpperCase();
                            if(CitySection.equals("BUENOS"))CitySection = "BUENOS AIRES";
                            if(CitySection.equals("TEL"))CitySection = "TEL AVIV";
                            if(CitySection.equals("HONG"))CitySection = "HONG KONG";
                            if(CitySection.equals("NEW"))CitySection = "NEW YORK";
                            if(CitySection.equals("RIO"))CitySection = "RIO DE JANEIRO";
                            if(CitySection.equals("MEXICO"))CitySection = "MEXICO CITY";
                            if(CitySection.equals("SAO"))CitySection = "SAO PAULO";
                            if(CitySection.equals("SANTIAGO"))CitySection = "SANTIAGO DE CHILE";
                            if(CitySection.equals("--"))CitySection = "";
                        }
                        else{
                            flag = true;
                            CitySection = "";
                        }
                    }
                    if(splited[i].equals("105")){
                        String[] finaleLanguage = StringUtils.splitString(splited[i + 1], " ");
                        if(finaleLanguage.length > 1) {
                            String Lang = finaleLanguage[1].toUpperCase();
                            if(!SearchEngine.Languages.contains(Lang)) {
                                SearchEngine.Languages.add(Lang);
                            }
                            break;
                        }
                    }

                }
                if(!flag)
                    CitySection = "";
            }
            tmpDocs.put(DocID, new DocDetailes(DocText, DocDate, DocTitle, CitySection));
        }
        return tmpDocs;
    }

    /**
     * @return - the size of the array list that save all the sub-folders directoris.
     */
    public int GetSubFilesSize() {
        return SubFilesPath.size();
    }

    /**
     * @param i - get the next sub-folder directory to process
     * @return - directory to the file to split.
     */
    public File GetSubFilesPath(int i) {
        return SubFilesPath.get(i);
    }
}
