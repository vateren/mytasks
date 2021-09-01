package by.terentyev;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class CrawlerConfig {

    private static Properties properties = new Properties();
    private List<String> listKeywords;
    private static List<String> listBlackList;

    public CrawlerConfig() { //getting properties
        try {
            this.properties.load(this.getClass().getResourceAsStream("data.properties")); //path to properties file
        } catch (IOException e) {
            System.err.println("ERROR: The configuration file is missing!");
        }
        //keywords properties
        this.listKeywords = Arrays.asList(this.properties.getProperty("keywords").split("\\s*,\\s*"));
        //blacklist properties
        this.listBlackList = Arrays.asList(this.properties.getProperty("blackList").split("\\s*,\\s*"));
    }

    public String getStartUrl() {
        return this.properties.getProperty("startUrl");
    } //read sead pages with properties


    public int getLevelDepth() {
        // level depth with properties
        int LEVEL_DEPTH = Integer.parseInt(properties.getProperty("levelDepth", "7"));
        return LEVEL_DEPTH;
    }

    public int getMaxVisitedPages() {
        // get max sisited pages with properties
        int maxVisitedPages = Integer.parseInt(properties.getProperty("maxVisitedPages", "10000"));
        return maxVisitedPages;
    }


    public boolean checkUrls(String url, int level) { //check url Level Depth, blacklist, null
        if ((url == null) || (url.trim() == "")) { //check url for content
            return false;
        }

        if (level > getLevelDepth()) { //check url for Level Depth
            return false;
        }

        for (String blackKey : getBlackList()) { //check url for blacklist
            if (url.contains(blackKey)) { //partial match is checked
                return false;
            }
        }
        return true;
    }

    public boolean checkSize(int size) {
        if (size >= getMaxVisitedPages()) { // check max visited pages
            return false;
        }
        return true;
    }

    public List<String> getListKeywords() {
        return listKeywords;
    }

    public List<String> getBlackList() {
        return listBlackList;
    }
}