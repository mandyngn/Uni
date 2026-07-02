package com.example.demo.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"type", "external_id"})
})
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false)
    private String externalId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String type;

    @Column(length = 8)
    private String year;

    @Column(nullable = false)
    private int score;

    @Column(length = 1024)
    private String overview;

    @Column(length = 512)
    private String poster;

    @Column(length = 512)
    private String genres;

    @Column(nullable = false)
    private OffsetDateTime ratedAt;

    public Rating() {
        this.ratedAt = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public OffsetDateTime getRatedAt() {
        return ratedAt;
    }

    public void setRatedAt(OffsetDateTime ratedAt) {
        this.ratedAt = ratedAt;
    }
}
