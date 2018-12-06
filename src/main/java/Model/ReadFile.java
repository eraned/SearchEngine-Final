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
 *
 */
// read folder folder
public class ReadFile {

    protected File MainPath;
    public static ArrayList<File> SubFilesPath;
    public StringBuilder stb;

    /**
     * @param path
     */
    public ReadFile(String path) {
        this.MainPath = new File(path);
        SubFilesPath = new ArrayList<>();
    }

    /**
     * @throws IOException
     */
    public void ReadCorpus() throws IOException {
        if (MainPath.isDirectory() && MainPath != null) {
            ProccessSubFilesDirectories(MainPath.getAbsolutePath());
        }
    }

    /**
     * @param path
     * @throws IOException
     */
    public void ProccessSubFilesDirectories(String path) throws IOException {
        File file = new File(path);
        File[] SubDirectories = file.listFiles();
        for (File tmp : SubDirectories) {
            StringBuilder stop =new StringBuilder( MainPath.toString() + "/stop_words.txt"); // lab path - "\\stop_words.txt"
            if (tmp.isFile() && !(tmp.toString().equals(stop.toString()))) {
                SubFilesPath.add(tmp);
            } else if (tmp != null && tmp.isDirectory()) {
                ProccessSubFilesDirectories(tmp.getAbsolutePath());
            }
        }
    }

    /**
     * @param subdirectory
     * @return
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
            String DocID = element.getElementsByTag("DOCNO").text();
            String DocText = element.getElementsByTag("TEXT").text();
            String DocDate = element.getElementsByTag("DATE1").text();
            String DocTitle = element.getElementsByTag("TI").text();
            String CitySection = element.getElementsByTag("F").toString();
            int Length = DocText.length();
            if (!CitySection.isEmpty()){
                String[] splited = StringUtils.splitString(CitySection, "[]}{(),<>%/:\"");
                for (int i = 0; i < splited.length; i++) {
                    if (splited[i].equals("104")) {
                        String[] finaleCity = StringUtils.splitString(splited[i + 1], " ");
                        if(finaleCity.length > 1) {
                            CitySection = finaleCity[1].toUpperCase();
                        }
                        else{
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
            }
            tmpDocs.put(DocID, new DocDetailes(DocText, DocDate, DocTitle, CitySection, Length));
        }
        return tmpDocs;
    }

    /**
     * @return
     */
    public int GetSubFilesSize() {
        return SubFilesPath.size();
    }

    /**
     * @param i
     * @return
     */
    public File GetSubFilesPath(int i) {
        return SubFilesPath.get(i);
    }
}
