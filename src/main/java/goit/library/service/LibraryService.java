package goit.library.service;


import goit.library.book.dto.WorkInfoDTO;
import goit.library.book.dto.WorkResponse;

import goit.library.dto.AuthorInfoDTO;
import goit.library.dto.AuthorSearchResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import org.springframework.beans.factory.annotation.Autowired;


@Service
public class LibraryService {

    private final RestTemplate restTemplate;
    private static final String BASE_URL = "https://openlibrary.org/search/authors.json?q=";
    private static final String WORK_BASE_URL = "https://openlibrary.org/search.json?title=";

    @Autowired
    public LibraryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AuthorSearchResponse searchAuthorsByName(String name) {
        String url = BASE_URL + name;
        ResponseEntity<AuthorSearchResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, AuthorSearchResponse.class);
        return response.getBody();
    }

    public WorkResponse searchAuthorsByWorkTitle(String title) {
        String url = WORK_BASE_URL + title;
        ResponseEntity<WorkResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, WorkResponse.class);
        return response.getBody();
    }


    public AuthorInfoDTO getAuthorByKey(String key) {
        String url = "https://openlibrary.org/authors/" + key;
        ResponseEntity<String> htmlResponse = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

        if (htmlResponse.getStatusCode() == HttpStatus.OK) {
            AuthorInfoDTO author = new AuthorInfoDTO();
            String htmlContent = htmlResponse.getBody();

            author.setName(parseAuthorName(htmlContent));
            author.setDescription(parseDescription(htmlContent));
            author.setImageUrl(parseImageUrl(htmlContent));
            return author;
        } else {
            return null;
        }
    }

    public WorkInfoDTO getWorkByTitle(String title) {
        String url = WORK_BASE_URL + title;
        ResponseEntity<WorkResponse> response = restTemplate.exchange(url, HttpMethod.GET, null, WorkResponse.class);
        if (response.getBody() != null && !response.getBody().getDocs().isEmpty()) {
            WorkInfoDTO work = response.getBody().getDocs().get(0);

            String workUrl = "https://openlibrary.org" + work.getKey();
            ResponseEntity<String> htmlResponse = restTemplate.exchange(workUrl, HttpMethod.GET, null, String.class);

            if (htmlResponse.getStatusCode() == HttpStatus.OK) {
                String fullDescription = parseDescriptionFromHtml(htmlResponse.getBody());
                work.setDescription(fullDescription);
            } else {

                work.setDescription("Description not available.");
            }

            return work;
        }
        return null;
    }


    private String parseAuthorName(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        Element nameElement = doc.selectFirst("h1[itemprop=name]");
        if (nameElement != null) {
            return nameElement.text();
        }
        return "Name not available.";
    }

    private String parseDescription(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        Element descriptionContent = doc.selectFirst("div[itemprop=description]");
        if (descriptionContent != null) {
            StringBuilder description = new StringBuilder();
            for (Element p : descriptionContent.select("p")) {
                description.append(p.text()).append("\n");
            }
            return description.toString().trim();
        }
        return "Description not available.";
    }

    private String parseImageUrl(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        Element imageElement = doc.selectFirst("div.SRPCover.bookCover img[itemprop=image]");
        if (imageElement != null) {
            return "https:" + imageElement.attr("src");
        }
        return "Image not available.";
    }

    private String parseDescriptionFromHtml(String htmlContent) {
        Document doc = Jsoup.parse(htmlContent);
        Element descriptionContent = doc.selectFirst(".read-more__content");
        if (descriptionContent != null) {
            StringBuilder description = new StringBuilder();
            for (Element p : descriptionContent.select("p")) {
                description.append(p.text()).append("\n");
            }
            return description.toString().trim();
        }
        return "Description not available.";
    }

}
