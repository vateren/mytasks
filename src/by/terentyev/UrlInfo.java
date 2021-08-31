package by.terentyev;

import java.util.Map;
import java.util.HashMap;

class UrlInfo {

    private String url;

    private Map<String, Integer> keywordCounter = new HashMap<String, Integer>();

    public UrlInfo() {

    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Integer> getKeywordCounter() {
        return keywordCounter;
    }

    public void setKeywordCounter(Map<String, Integer> keywordCounter) {
        this.keywordCounter = keywordCounter;
    }

}