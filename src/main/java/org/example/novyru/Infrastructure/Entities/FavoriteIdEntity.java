package org.example.novyru.Infrastructure.Entities;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FavoriteIdEntity implements Serializable {

    @Column(name = "user_id")
    private int userId;

    @Column(name = "recipe_id")
    private int recipeId;

    public FavoriteIdEntity() {}

    public FavoriteIdEntity(int userId, int recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    public int getUserId() {
        return userId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavoriteIdEntity that)) return false;

        return userId == that.userId
                && recipeId == that.recipeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, recipeId);
    }
}