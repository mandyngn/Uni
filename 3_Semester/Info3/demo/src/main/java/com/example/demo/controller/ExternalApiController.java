package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ExternalApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalApiController.class);
    private static final String IMDB_BASE_URL = "https://api.imdbapi.dev";
    private static final String JIKAN_BASE_URL = "https://api.jikan.moe/v4";
    private static final String ANILIST_GRAPHQL_URL = "https://graphql.anilist.co";
    private static final String ANILIST_SEARCH_QUERY = "query ($search: String) { Page(page: 1, perPage: 16) { media(search: $search, type: ANIME) { id title { romaji english native } coverImage { large } description format episodes duration genres averageScore startDate { year } } } }";

    private final WebClient webClient = WebClient.builder().build();

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam String type,
            @RequestParam String query
    ) {
        try {
            if ("anime".equalsIgnoreCase(type)) {
                return ResponseEntity.ok(searchAnime(query));
            }
            return ResponseEntity.ok(searchTitles(type, query));
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    @GetMapping("/title-details")
    public ResponseEntity<Map<String, Object>> getTitleDetails(@RequestParam String id) {
        try {
            Map<String, Object> details = webClient.get()
                    .uri(IMDB_BASE_URL + "/titles/{id}", id)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (details == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(createTitleDetails(details));
        } catch (WebClientResponseException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
    }

    private List<Map<String, Object>> searchAnime(String query) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(JIKAN_BASE_URL + "/anime?q={query}&limit=16", query)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response != null) {
                LOGGER.info("Jikan anime search returned {} entries", getList(response.get("data")).size());
                return getList(response.get("data")).stream().map(this::createAnimeItem).collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.warn("Jikan anime search failed for query='{}'. Falling back to AniList.", query, e);
        }

        return searchAnimeWithAniList(query);
    }

    private List<Map<String, Object>> searchAnimeWithAniList(String query) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("query", ANILIST_SEARCH_QUERY);
            Map<String, Object> variables = new HashMap<>();
            variables.put("search", query);
            payload.put("variables", variables);

            Map<String, Object> response = webClient.post()
                    .uri(ANILIST_GRAPHQL_URL)
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null) {
                return Collections.emptyList();
            }
            Map<String, Object> data = getMap(response.get("data"));
            Map<String, Object> page = getMap(data.get("Page"));
            List<Map<String, Object>> media = getList(page.get("media"));
            return media.stream().map(this::createAniListAnimeItem).collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("AniList anime search failed for query='{}'", query, e);
            return Collections.emptyList();
        }
    }

    private List<Map<String, Object>> searchTitles(String type, String query) {
        try {
            Map<String, Object> response = webClient.get()
                    .uri(IMDB_BASE_URL + "/search/titles?query={query}&limit=16", query)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            if (response == null) {
                return Collections.emptyList();
            }
            List<Map<String, Object>> titles = getList(response.get("titles"));
            return titles.stream()
                    .filter(entry -> filterTitle(type, entry))
                    .map(this::createTitleItem)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            LOGGER.error("Title search failed for type='{}', query='{}'", type, query, e);
            throw e;
        }
    }

    private boolean filterTitle(String type, Map<String, Object> entry) {
        if (entry == null) {
            return false;
        }
        String entryType = String.valueOf(entry.getOrDefault("type", ""));
        if ("movie".equalsIgnoreCase(type)) {
            return "movie".equalsIgnoreCase(entryType);
        }
        if ("tv".equalsIgnoreCase(type)) {
            return "tvSeries".equalsIgnoreCase(entryType) || "tvMiniSeries".equalsIgnoreCase(entryType);
        }
        return true;
    }

    private Map<String, Object> createAnimeItem(Map<String, Object> entry) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", entry.get("mal_id"));
        item.put("type", "anime");
        item.put("title", entry.getOrDefault("title", "Unbekannt"));
        item.put("year", Optional.ofNullable(entry.get("year")).map(Object::toString).orElse("n/a"));
        item.put("subtitle", entry.getOrDefault("type", ""));
        item.put("overview", Optional.ofNullable(entry.get("synopsis")).orElse("Keine Beschreibung verfügbar."));
        item.put("poster", Optional.ofNullable(entry.get("images")).map(images -> {
            if (images instanceof Map<?, ?> imageMap) {
                return Optional.ofNullable(imageMap.get("jpg")).filter(Map.class::isInstance)
                        .map(Map.class::cast)
                        .map(inner -> inner.get("image_url"))
                        .orElse("");
            }
            return "";
        }).orElse(""));
        item.put("score", entry.get("score"));
        item.put("runtimeSeconds", entry.get("duration") instanceof String duration ? parseDuration(duration) : null);
        item.put("genres", Optional.ofNullable(entry.get("genres")).filter(List.class::isInstance).map(List.class::cast).orElse(Collections.emptyList()));
        return item;
    }

    private Map<String, Object> createTitleItem(Map<String, Object> entry) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", entry.get("id"));
        item.put("type", entry.getOrDefault("type", ""));
        item.put("title", Optional.ofNullable(entry.get("primaryTitle")).orElse(entry.getOrDefault("originalTitle", "Unbekannt")));
        item.put("year", Optional.ofNullable(entry.get("startYear")).map(Object::toString).orElse("n/a"));
        item.put("subtitle", Optional.ofNullable(entry.get("originalTitle")).orElse(entry.getOrDefault("type", "")));
        item.put("overview", Optional.ofNullable(entry.get("plot")).orElse("Keine Beschreibung verfügbar."));
        item.put("poster", Optional.ofNullable(entry.get("primaryImage")).filter(Map.class::isInstance).map(Map.class::cast).map(img -> img.get("url")).orElse(""));
        item.put("imdbRating", Optional.ofNullable(entry.get("rating")).filter(Map.class::isInstance).map(Map.class::cast).map(r -> r.get("aggregateRating")).orElse(null));
        item.put("runtimeSeconds", null);
        item.put("genres", Optional.ofNullable(entry.get("genres")).filter(List.class::isInstance).map(List.class::cast).orElse(Collections.emptyList()));
        item.put("plotFetched", entry.containsKey("plot"));
        return item;
    }

    private Map<String, Object> createAniListAnimeItem(Map<String, Object> entry) {
        Map<String, Object> title = getMap(entry.get("title"));
        Map<String, Object> coverImage = getMap(entry.get("coverImage"));
        Map<String, Object> startDate = getMap(entry.get("startDate"));

        Map<String, Object> item = new HashMap<>();
        item.put("id", entry.get("id"));
        item.put("type", "anime");
        item.put("title", Optional.ofNullable(title.get("english")).orElse(Optional.ofNullable(title.get("romaji")).orElse(Optional.ofNullable(title.get("native")).orElse("Unbekannt"))));
        item.put("year", Optional.ofNullable(startDate.get("year")).map(Object::toString).orElse("n/a"));
        item.put("subtitle", Optional.ofNullable(entry.get("format")).orElse(""));
        item.put("overview", Optional.ofNullable(entry.get("description")).orElse("Keine Beschreibung verfügbar."));
        item.put("poster", Optional.ofNullable(coverImage.get("large")).orElse(""));
        item.put("score", Optional.ofNullable(entry.get("averageScore")).orElse(null));
        item.put("runtimeSeconds", Optional.ofNullable(entry.get("duration")).filter(Number.class::isInstance).map(Number.class::cast).map(Number::intValue).orElse(null));
        item.put("genres", Optional.ofNullable(entry.get("genres")).filter(List.class::isInstance).map(List.class::cast).orElse(Collections.emptyList()));
        item.put("plotFetched", Boolean.TRUE);
        return item;
    }

    private Map<String, Object> createTitleDetails(Map<String, Object> details) {
        Map<String, Object> item = new HashMap<>();
        item.put("plot", details.getOrDefault("plot", "Keine Beschreibung verfügbar."));
        item.put("primaryImage", details.getOrDefault("primaryImage", Collections.emptyMap()));
        item.put("rating", details.getOrDefault("rating", Collections.emptyMap()));
        item.put("runtimeSeconds", details.getOrDefault("runtimeSeconds", null));
        item.put("genres", details.getOrDefault("genres", Collections.emptyList()));
        return item;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getList(Object value) {
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(item -> item instanceof Map)
                    .map(item -> (Map<String, Object>) item)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return Collections.emptyMap();
    }

    private Integer parseDuration(String duration) {
        try {
            String digits = duration.replaceAll("\\D+", "");
            return digits.isEmpty() ? null : Integer.parseInt(digits) * 60;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
