package com.example.demo.controller;

import com.example.demo.model.Rating;
import com.example.demo.repository.RatingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*")
public class RatingController {

    private final RatingRepository repository;

    public RatingController(RatingRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Rating> findAll() {
        return repository.findAllByOrderByRatedAtDesc();
    }

    @PostMapping
    public ResponseEntity<Rating> createOrUpdate(@RequestBody Rating rating) {
        if (rating.getScore() < 1 || rating.getScore() > 5) {
            return ResponseEntity.badRequest().build();
        }
        if (rating.getExternalId() == null || rating.getExternalId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Rating savedRating = repository.findByTypeAndExternalId(rating.getType(), rating.getExternalId())
                .map(existing -> {
                    existing.setScore(rating.getScore());
                    existing.setOverview(rating.getOverview());
                    existing.setPoster(rating.getPoster());
                    existing.setGenres(rating.getGenres());
                    existing.setYear(rating.getYear());
                    existing.setTitle(rating.getTitle());
                    existing.setRatedAt(OffsetDateTime.now());
                    return repository.save(existing);
                })
                .orElseGet(() -> {
                    rating.setRatedAt(OffsetDateTime.now());
                    return repository.save(rating);
                });

        return ResponseEntity.ok(savedRating);
    }
}
