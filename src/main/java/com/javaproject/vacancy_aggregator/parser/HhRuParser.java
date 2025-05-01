package com.javaproject.vacancy_aggregator.parser;

import com.javaproject.vacancy_aggregator.domain.RawVacancy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class HhRuParser implements VacancyParser {

    private static final String SEARCH_URL = "https://hh.ru/search/vacancy?text=Java+Developer";

    @Override
    public List<RawVacancy> parse(String text) {
        List<RawVacancy> vacancies = new ArrayList<>();

        try {
            Document document = Jsoup.connect(SEARCH_URL) //запускает HTTP-запрос по указанному URL
                    .userAgent("Mozilla/5.0")
                    .get(); //выполняет запрос и парсит тело ответа в объект org.jsoup.nodes.Document (внутри — DOM-дерево страницы)
            Elements items = document.select(".vacancy-serp-item"); //возвращает коллекцию Elements, где каждый элемент — это обёртка над узлом DOM, соответствующим указанному селектору

            for (Element item : items) {
                String title = item.select(".vacancy-serp-item__title").text();
                String company = item.select(".vacancy-serp-item__meta-info a").text();
                String city = item.select(".vacancy-serp-item__meta-info").get(1).text();
                String salary = item.select(".vacancy-serp-item__sidebar").text();
                String url = item.select(".vacancy-serp-item__title a").attr("href");
                String desc = item.select(".g-user-content").text();
                LocalDateTime pubDate = LocalDateTime.now();

                RawVacancy rv = new RawVacancy(
                        title,
                        company,
                        city,
                        salary,
                        desc,
                        pubDate,
                        url,
                        "hh.ru",
                        "https://hh.ru"
                );
                vacancies.add(rv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return vacancies;
    }
}
