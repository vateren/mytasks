package by.terentyev;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerJsoup {

    private static final String keywords = new String("Банки, Курс, Ещё, торги, Беларусь, USD"); // Elon, Tesla, Elon Musk
    private static final List<String> listKeywords = Arrays.asList(keywords.split("\\s*,\\s*"));
    private static final String GRID = "#";
    private static final int LEVEL_DEPTH = 8;
    private static List<UrlInfo> urlInfos = new ArrayList<>();

    public static void main(String[] args) {
        String startUrl = new String("https://banki24.by/"); // вводим нужный адрес сайта

        Set<String> foundLinks = new HashSet<>();
        List<String> blackList = List.of("https://about.google/, https://about.google/intl/en/"); // список сайтов куда
        // не стоит заходить       // - ПРИКРУТИТЬ
        getLinks(startUrl, foundLinks, 0);


        //вывести urlInfos в файл
    }

    static void getLinks(String url, Set<String> foundUrls, int level) {
        if (level > LEVEL_DEPTH || foundUrls.contains(url)) {
            return;
        }
        foundUrls.add(url);
        System.out.println(url + " = " + level);

        try {
            Document doc = getPageDocument(url);

            urlInfos.add(keywordSearch(doc, listKeywords, url)); // вызов подсчёта ключевых слов
            Elements elements = doc.select("a");
            for (Element element : elements) {
                url = element.absUrl("href").replaceAll("#+", "");
                if (!foundUrls.contains(url)) { // проверка есть ли страница в посещённых
                    getLinks(url, foundUrls, level++);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Document getPageDocument(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent("Chrome/92.0.4515.159 (Windows NT 6.1; WOW64; rv:5.0) Gecko/20100101 Firefox/5.0")
                .timeout(10000).get();
    }

    private static UrlInfo keywordSearch(Document docHtml, List<String> tokens, String url) {
        UrlInfo urlInfo = new UrlInfo();
        urlInfo.setUrl(url);
        int count = 0;
        List<Integer> countKeywords = new ArrayList<>();
        for (String token : tokens) {
            Elements elements = docHtml.select("body"); // НУЖНО добавить поиск в Head HTML
            String html = elements.text();
            String patternString = "\\b" + token + "\\b";
            Pattern patternKeyWordInHtml = Pattern.compile(patternString);
            Matcher searchKeyword = patternKeyWordInHtml.matcher(html);
            while (searchKeyword.find()) {
                count++;
            }
            countKeywords.add(count);
            urlInfo.getKeywordCounter().put(token, count);
            count = 0;
        }
        System.out.println(url + " = " + countKeywords);
        return urlInfo;
    }

}