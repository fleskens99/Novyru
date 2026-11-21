package org.example.novyru.Infrastructure.Repositories;

import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.example.novyru.Infrastructure.Entities.FavoriteEntity;
import org.example.novyru.Infrastructure.Entities.FavoriteIdEntity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class FavoriteRecipeRepository implements IFavoriteRecipeRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public boolean addFavorite(
            int userId,
            int recipeId,
            String recipeJson
    ) {
        FavoriteIdEntity id = new FavoriteIdEntity(userId, recipeId);

        FavoriteEntity favorite = entityManager.find(FavoriteEntity.class, id);

        if (favorite == null) {
            entityManager.persist(
                    new FavoriteEntity(userId, recipeId, recipeJson)
            );
        } else {
            favorite.setRecipeJson(recipeJson);
        }

        return true;
    }

    @Override
    public boolean removeFavorite(int userId, int recipeId) {
        FavoriteEntity favorite = entityManager.find(
                FavoriteEntity.class,
                new FavoriteIdEntity(userId, recipeId)
        );

        if (favorite == null) {
            return false;
        }

        entityManager.remove(favorite);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(int userId, int recipeId) {
        return entityManager.find(
                FavoriteEntity.class,
                new FavoriteIdEntity(userId, recipeId)
        ) != null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> findFavoriteRecipeIdsByUserId(int userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        Root<FavoriteEntity> favorite = query.from(FavoriteEntity.class);

        query.select(favorite.get("id").get("recipeId"))
                .where(cb.equal(
                        favorite.get("id").get("userId"),
                        userId
                ))
                .orderBy(cb.asc(
                        favorite.get("id").get("recipeId")
                ));

        return entityManager.createQuery(query).getResultList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> findFavoriteRecipeJsonByUserId(int userId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<FavoriteEntity> favorite = query.from(FavoriteEntity.class);

        query.select(favorite.get("recipeJson"))
                .where(
                        cb.equal(
                                favorite.get("id").get("userId"),
                                userId
                        ),
                        cb.isNotNull(favorite.get("recipeJson"))
                )
                .orderBy(cb.asc(
                        favorite.get("id").get("recipeId")
                ));

        return entityManager.createQuery(query).getResultList();
    }
}