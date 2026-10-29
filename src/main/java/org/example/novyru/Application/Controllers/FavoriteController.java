
package org.example.novyru.Application.Controllers;

import lombok.RequiredArgsConstructor;

import org.example.novyru.Application.Services.FavoriteRecipeService;
import org.example.novyru.Application.DTOs.AddFavoriteRequestDto;
import org.example.novyru.Application.DTOs.FavoriteStatusResponseDto;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteRecipeService favoriteRecipeService;

    // ADD FAVORITE
    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @PathVariable int userId,
            @RequestBody AddFavoriteRequestDto dto
    ) {
        favoriteRecipeService.addFavorite(
                userId,
                dto.recipeId()
        );

        URI location = URI.create(
                "/api/users/" + userId
                        + "/favorites/" + dto.recipeId()
        );

        return ResponseEntity.created(location).build();
    }

    // REMOVE FAVORITE
    @DeleteMapping("/{recipeId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable int userId,
            @PathVariable int recipeId
    ) {
        favoriteRecipeService.removeFavorite(userId, recipeId);

        return ResponseEntity.noContent().build();
    }

    // GET ALL FAVORITE RECIPE IDS
    @GetMapping
    public ResponseEntity<List<Integer>> getFavoriteRecipeIds(
            @PathVariable int userId
    ) {
        List<Integer> favorites =
                favoriteRecipeService.getFavoriteRecipeIds(userId);

        return ResponseEntity.ok(favorites);
    }

    // CHECK IF A RECIPE IS FAVORITED
    @GetMapping("/{recipeId}/status")
    public ResponseEntity<FavoriteStatusResponseDto> isFavorite(
            @PathVariable int userId,
            @PathVariable int recipeId
    ) {
        boolean favorite =
                favoriteRecipeService.isFavorite(userId, recipeId);

        return ResponseEntity.ok(
                new FavoriteStatusResponseDto(favorite)
        );
    }
}
