package org.example.novyru.DomainRulesTests;

import org.example.novyru.Domain.Rules.FavoriteRecipeRules;

import org.example.novyru.Domain.Exceptions.InvalidIdException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeAlreadyExistsException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FavoriteRecipeRuleTests {

    private FavoriteRecipeRules favoriteRules;

    private static final int USER_ID = 1;
    private static final int RECIPE_ID = 10;

    @BeforeEach
    void setUp() {
        favoriteRules = new FavoriteRecipeRules();
    }

    // ==========================================
    // ID VALIDATION TESTS
    // ==========================================

    @Test
    void validateId_WithPositiveId_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                favoriteRules.validateId(USER_ID, "User ID")
        );
    }

    @Test
    void validateId_WithZero_ShouldThrowInvalidIdException() {

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRules.validateId(0, "User ID")
        );
    }

    @Test
    void validateId_WithNegativeId_ShouldThrowInvalidIdException() {

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRules.validateId(-1, "Recipe ID")
        );
    }

    @Test
    void validateId_WithLargePositiveId_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                favoriteRules.validateId(Integer.MAX_VALUE, "Recipe ID")
        );
    }

    // ==========================================
    // ADD FAVORITE RULE TESTS
    // ==========================================

    @Test
    void requireAdded_WhenAdded_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                favoriteRules.requireAdded(
                        true,
                        USER_ID,
                        RECIPE_ID
                )
        );
    }

    @Test
    void requireAdded_WhenAlreadyExists_ShouldThrowException() {

        assertThrows(
                RecipeAlreadyExistsException.class,
                () -> favoriteRules.requireAdded(
                        false,
                        USER_ID,
                        RECIPE_ID
                )
        );
    }

    // ==========================================
    // REMOVE FAVORITE RULE TESTS
    // ==========================================

    @Test
    void requireRemoved_WhenRemoved_ShouldNotThrow() {

        assertDoesNotThrow(() ->
                favoriteRules.requireRemoved(
                        true,
                        USER_ID,
                        RECIPE_ID
                )
        );
    }

    @Test
    void requireRemoved_WhenNotFound_ShouldThrowException() {

        assertThrows(
                RecipeNotFoundException.class,
                () -> favoriteRules.requireRemoved(
                        false,
                        USER_ID,
                        RECIPE_ID
                )
        );
    }
}
