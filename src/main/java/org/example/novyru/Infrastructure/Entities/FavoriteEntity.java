package org.example.novyru.Infrastructure.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "favorite")
public class FavoriteEntity {

    @EmbeddedId
    private FavoriteIdEntity id;

    @Column(name = "recipe_json")
    private String recipeJson;

    public FavoriteEntity() {}

    public FavoriteEntity(
            int userId,
            int recipeId,
            String recipeJson
    ) {
        this.id = new FavoriteIdEntity(userId, recipeId);
        this.recipeJson = recipeJson;
    }

    public FavoriteIdEntity getId() {
        return id;
    }

    public String getRecipeJson() {
        return recipeJson;
    }

    public void setRecipeJson(String recipeJson) {
        this.recipeJson = recipeJson;
    }
}