package by.terentyev;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WebCrawlerJsoup {

    private static HashMap<String, UrlInfo> foundUrl = new HashMap<>();
    private static final CrawlerConfig curConfig = new CrawlerConfig();
    private static String baseUrl;
    private static Pattern patternHref = Pattern.compile("<a href=\"[:\\w.\\/\\-^s%]+"); //create pattern
    private static final String END_TAG = "\">";                        //declare regular expression
    private static final String EMPTY_STRING = "";
    private static final String START_HREF = "<a href=\"";
    private static final String HTTP_HREF = "http";

    public static void main(String[] args) {

        followLinks(curConfig.getStartUrl(), 0); //starting recursion
        try {
            saveToCsv("output.csv"); // call void to write the result to a file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveToCsv(String filename) throws IOException { //void for write csv file. Input String for name file
        File file = new File(filename); //declare file
        FileWriter fileWriter = new FileWriter(file, false);
        boolean isFirst = true;

        for (Map.Entry<String, UrlInfo> element : foundUrl.entrySet()) { //excluding reordering of keywords and write
            if (isFirst) {
                fileWriter.append(";");
                fileWriter.append(element.getValue().getKeywordCounter().keySet().stream().map(String::toString).collect(Collectors.joining(";")));
                fileWriter.append(System.lineSeparator()); //write new line
            }
            isFirst = false;
            fileWriter.append(element.getValue().getUrl() + ";");
            fileWriter.append(element.getValue().getKeywordCounter().values().stream().map(Object::toString).collect(Collectors.joining(";"))); //write csv output
            fileWriter.append(System.lineSeparator()); //new line
        }

        fileWriter.close();
    }


    static boolean followLinks(String url, int level) {

        if (!curConfig.checkSize(foundUrl.size())) { //check max Visited Pages
            return false;
        }
        if (!curConfig.checkUrls(url, level)) { //check level depth
            return true;
        }

        try {
            String htmlString = getPageDocument(url); //getting the page code through the method
            Matcher foundHrefMatches = patternHref.matcher(htmlString); //search for tags containing links
            Document doc = new Document(htmlString); //
            if (doc == null) { //check for content
                return true;
            }

            //заполняем информацию о URL для добавления в список найденных
            UrlInfo tmpUrlInfo = new UrlInfo();
            tmpUrlInfo.setUrl(url);
            //running method for search keywords
            tmpUrlInfo.setKeywordCounter(keywordSearch(htmlString, curConfig.getListKeywords()));

            if (!addUrl(tmpUrlInfo, level)) {
                return true;
            }

            List<String> allMatches = new ArrayList<String>(); //declaring a list to store found matches
            while (foundHrefMatches.find()) { //add to list allMatches results
                allMatches.add(foundHrefMatches.group());
            }

            URL tmpBaseUrl = new URL(url); //declare tmp variable for base URL
            baseUrl = tmpBaseUrl.getProtocol() + "://" + tmpBaseUrl.getHost(); //base url search for relative links

            List<String> allUrls = allMatches.stream() //collection and normalization of all links on the page
                    .map(match -> match.replaceAll(START_HREF, EMPTY_STRING)) //clearing links from start tags
                    .map(match -> match.replaceAll(END_TAG, EMPTY_STRING)) //clearing links from end tags
                    .map(str -> (str.startsWith(HTTP_HREF)) ? str : baseUrl + str) //converting relative references to absolute
                    .collect(Collectors.toList());

            for (String tmpUrl : allUrls) {
                if (!foundUrl.containsKey(tmpUrl)) { // checking if there is a page in visited
                    //Recursion. Function launch for all new links. level for control level depth
                    if (!followLinks(tmpUrl, level++)) {
                        return false;
                    }
                }

            }

        } catch (IOException e) {
            e.getMessage();
        }
        return true;
    }

    private static boolean addUrl(UrlInfo url, int level) {
        if (foundUrl.containsKey(url.getUrl())) { //check. If the link is in the collection then return
            return false;
        }

        foundUrl.put(url.getUrl(), url);


        return true;
    }

    private static String getPageDocument(String newUrl) throws IOException { //getting a document by url
        try {
            URL url = new URL(newUrl); //converting String to URL
            InputStream input = url.openStream(); //receiving a response to a request by URL
            byte[] buffer = input.readAllBytes(); //byte read into buffer
            return new String(buffer); //return the received string
        } catch (IllegalArgumentException e) {
            e.getMessage();
            return null;
        }
    }

    private static Map<String, Integer> keywordSearch(String html, List<String> tokens) { //
        Map<String, Integer> keyWords = new HashMap<>(); // //new collection for storing the count of matches found
        int count;

        for (String token : tokens) {
            count = 0;
            Pattern patternKeyWordInHtml = Pattern.compile(token); //creating a pattern by keyword
            Matcher searchKeyword = patternKeyWordInHtml.matcher(html); //search for matches by keyword
            while (searchKeyword.find()) {
                count++; //counting found matches on the page
            }

            keyWords.put(token, count); //collection for storing the count of matches found
        }
        return keyWords;
    }

}