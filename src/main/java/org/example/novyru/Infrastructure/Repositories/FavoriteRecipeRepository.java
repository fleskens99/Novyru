package org.example.novyru.Infrastructure.Repositories;

import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.util.List;

public class FavoriteRecipeRepository implements IFavoriteRecipeRepository {

    private final Jdbi jdbi;

    public FavoriteRecipeRepository(DataSource dataSource) {
        this.jdbi = Jdbi.create(dataSource);
    }

    // ADD A RECIPE TO FAVORITES
    @Override
    public boolean addFavorite(int userId, int recipeId) {

        int rows = jdbi.withHandle(handle ->
                handle.createUpdate("""
                        INSERT INTO favorite (user_id, recipe_id)
                        VALUES (:userId, :recipeId)
                        ON CONFLICT (user_id, recipe_id) DO NOTHING
                        """)
                        .bind("userId", userId)
                        .bind("recipeId", recipeId)
                        .execute()
        );

        return rows == 1;
    }

    // REMOVE A RECIPE FROM FAVORITES
    @Override
    public boolean removeFavorite(int userId, int recipeId) {

        int rows = jdbi.withHandle(handle ->
                handle.createUpdate("""
                        DELETE FROM favorite
                        WHERE user_id = :userId
                        AND recipe_id = :recipeId
                        """)
                        .bind("userId", userId)
                        .bind("recipeId", recipeId)
                        .execute()
        );

        return rows == 1;
    }

    // CHECK IF A RECIPE IS FAVORITED
    @Override
    public boolean isFavorite(int userId, int recipeId) {

        return jdbi.withHandle(handle ->
                handle.createQuery("""
                        SELECT EXISTS (
                            SELECT 1
                            FROM favorite
                            WHERE user_id = :userId
                            AND recipe_id = :recipeId
                        )
                        """)
                        .bind("userId", userId)
                        .bind("recipeId", recipeId)
                        .mapTo(Boolean.class)
                        .one()
        );
    }

    // GET ALL FAVORITE RECIPE IDS FOR A USER
    @Override
    public List<Integer> findFavoriteRecipeIdsByUserId(int userId) {

        return jdbi.withHandle(handle ->
                handle.createQuery("""
                        SELECT recipe_id
                        FROM favorite
                        WHERE user_id = :userId
                        ORDER BY recipe_id
                        """)
                        .bind("userId", userId)
                        .mapTo(Integer.class)
                        .list()
        );
    }
}
