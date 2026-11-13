package org.example.novyru.ServiceTests;

import org.example.novyru.Application.Services.FavoriteRecipeService;

import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.example.novyru.Domain.Interfaces.Rules.IFavoriteRecipeRules;

import org.example.novyru.Domain.Exceptions.InvalidIdException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeAlreadyExistsException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteRecipeServiceTest {

    @Mock
    private IFavoriteRecipeRepository favoriteRepository;

    @Mock
    private IFavoriteRecipeRules favoriteRules;

    private FavoriteRecipeService favoriteRecipeService;

    private static final int USER_ID = 1;
    private static final int RECIPE_ID = 10;

    @BeforeEach
    void setUp() {
        favoriteRecipeService = new FavoriteRecipeService(
                favoriteRepository,
                favoriteRules
        );
    }

    // ==========================================
    // ADD FAVORITE TESTS
    // ==========================================

    @Test
    void addFavorite_WithValidIds_ShouldAddSuccessfully() {

        when(favoriteRepository.addFavorite(USER_ID, RECIPE_ID))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                favoriteRecipeService.addFavorite(USER_ID, RECIPE_ID)
        );

        InOrder order = inOrder(favoriteRules, favoriteRepository);

        order.verify(favoriteRules).validateId(USER_ID, "User ID");
        order.verify(favoriteRules).validateId(RECIPE_ID, "Recipe ID");
        order.verify(favoriteRepository).addFavorite(USER_ID, RECIPE_ID);
        order.verify(favoriteRules).requireAdded(true, USER_ID, RECIPE_ID);
    }

    @Test
    void addFavorite_WhenAlreadyExists_ShouldThrowException() {

        when(favoriteRepository.addFavorite(USER_ID, RECIPE_ID))
                .thenReturn(false);

        doThrow(new RecipeAlreadyExistsException("Already favorited"))
                .when(favoriteRules)
                .requireAdded(false, USER_ID, RECIPE_ID);

        assertThrows(
                RecipeAlreadyExistsException.class,
                () -> favoriteRecipeService.addFavorite(USER_ID, RECIPE_ID)
        );

        verify(favoriteRepository).addFavorite(USER_ID, RECIPE_ID);
        verify(favoriteRules).requireAdded(false, USER_ID, RECIPE_ID);
    }

    @Test
    void addFavorite_WithInvalidUserId_ShouldThrowException() {

        doThrow(new InvalidIdException("User ID"))
                .when(favoriteRules)
                .validateId(0, "User ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.addFavorite(0, RECIPE_ID)
        );

        verifyNoInteractions(favoriteRepository);
    }

    @Test
    void addFavorite_WithInvalidRecipeId_ShouldThrowException() {

        doAnswer(invocation -> {
            int id = invocation.getArgument(0);
            String label = invocation.getArgument(1);

            if (id == 0 && "Recipe ID".equals(label)) {
                throw new InvalidIdException("Recipe ID");
            }

            return null;
        }).when(favoriteRules)
                .validateId(anyInt(), anyString());

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.addFavorite(USER_ID, 0)
        );

        verify(favoriteRules).validateId(USER_ID, "User ID");
        verify(favoriteRules).validateId(0, "Recipe ID");
        verifyNoInteractions(favoriteRepository);
    }

    // ==========================================
    // REMOVE FAVORITE TESTS
    // ==========================================

    @Test
    void removeFavorite_WhenExists_ShouldRemoveSuccessfully() {

        when(favoriteRepository.removeFavorite(USER_ID, RECIPE_ID))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                favoriteRecipeService.removeFavorite(USER_ID, RECIPE_ID)
        );

        verify(favoriteRules).validateId(USER_ID, "User ID");
        verify(favoriteRules).validateId(RECIPE_ID, "Recipe ID");
        verify(favoriteRepository).removeFavorite(USER_ID, RECIPE_ID);
        verify(favoriteRules).requireRemoved(true, USER_ID, RECIPE_ID);
    }

    @Test
    void removeFavorite_WhenNotFound_ShouldThrowException() {

        when(favoriteRepository.removeFavorite(USER_ID, RECIPE_ID))
                .thenReturn(false);

        doThrow(new RecipeNotFoundException("Favorite not found"))
                .when(favoriteRules)
                .requireRemoved(false, USER_ID, RECIPE_ID);

        assertThrows(
                RecipeNotFoundException.class,
                () -> favoriteRecipeService.removeFavorite(USER_ID, RECIPE_ID)
        );

        verify(favoriteRepository).removeFavorite(USER_ID, RECIPE_ID);
        verify(favoriteRules).requireRemoved(false, USER_ID, RECIPE_ID);
    }

    @Test
    void removeFavorite_WithInvalidUserId_ShouldThrowException() {

        doThrow(new InvalidIdException("User ID"))
                .when(favoriteRules)
                .validateId(-1, "User ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.removeFavorite(-1, RECIPE_ID)
        );

        verifyNoInteractions(favoriteRepository);
    }

    // ==========================================
    // CHECK FAVORITE TESTS
    // ==========================================

    @Test
    void isFavorite_WhenFavorited_ShouldReturnTrue() {

        when(favoriteRepository.isFavorite(USER_ID, RECIPE_ID))
                .thenReturn(true);

        boolean result = favoriteRecipeService.isFavorite(
                USER_ID, RECIPE_ID
        );

        assertTrue(result);

        verify(favoriteRules).validateId(USER_ID, "User ID");
        verify(favoriteRules).validateId(RECIPE_ID, "Recipe ID");
        verify(favoriteRepository).isFavorite(USER_ID, RECIPE_ID);
    }

    @Test
    void isFavorite_WhenNotFavorited_ShouldReturnFalse() {

        when(favoriteRepository.isFavorite(USER_ID, RECIPE_ID))
                .thenReturn(false);

        boolean result = favoriteRecipeService.isFavorite(
                USER_ID, RECIPE_ID
        );

        assertFalse(result);

        verify(favoriteRepository).isFavorite(USER_ID, RECIPE_ID);
    }

    @Test
    void isFavorite_WithInvalidRecipeId_ShouldThrowException() {

        doAnswer(invocation -> {
            int id = invocation.getArgument(0);
            String label = invocation.getArgument(1);

            if (id == 0 && label.equals("Recipe ID")) {
                throw new InvalidIdException("Recipe ID");
            }

            return null;
        }).when(favoriteRules)
                .validateId(anyInt(), anyString());

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.isFavorite(USER_ID, 0)
        );

        verify(favoriteRules)
                .validateId(USER_ID, "User ID");

        verify(favoriteRules)
                .validateId(0, "Recipe ID");

        verifyNoInteractions(favoriteRepository);
    }

    // ==========================================
    // GET FAVORITE RECIPE IDS TESTS
    // ==========================================

    @Test
    void getFavoriteRecipeIds_WhenFavoritesExist_ShouldReturnIds() {

        List<Integer> expectedIds = List.of(10, 20, 30);

        when(favoriteRepository.findFavoriteRecipeIdsByUserId(USER_ID))
                .thenReturn(expectedIds);

        List<Integer> result =
                favoriteRecipeService.getFavoriteRecipeIds(USER_ID);

        assertEquals(expectedIds, result);
        assertEquals(3, result.size());

        verify(favoriteRules).validateId(USER_ID, "User ID");
        verify(favoriteRepository).findFavoriteRecipeIdsByUserId(USER_ID);
    }

    @Test
    void getFavoriteRecipeIds_WhenNoFavorites_ShouldReturnEmptyList() {

        when(favoriteRepository.findFavoriteRecipeIdsByUserId(USER_ID))
                .thenReturn(List.of());

        List<Integer> result =
                favoriteRecipeService.getFavoriteRecipeIds(USER_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(favoriteRepository).findFavoriteRecipeIdsByUserId(USER_ID);
    }

    @Test
    void getFavoriteRecipeIds_WithInvalidUserId_ShouldThrowException() {

        doThrow(new InvalidIdException("User ID"))
                .when(favoriteRules)
                .validateId(0, "User ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.getFavoriteRecipeIds(0)
        );

        verifyNoInteractions(favoriteRepository);
    }

    // ==========================================
    // REPOSITORY FAILURE TESTS
    // ==========================================

    @Test
    void addFavorite_WhenDatabaseFails_ShouldPropagateException() {

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(favoriteRepository.addFavorite(USER_ID, RECIPE_ID))
                .thenThrow(databaseError);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> favoriteRecipeService.addFavorite(USER_ID, RECIPE_ID)
        );

        assertSame(databaseError, exception);
    }

    @Test
    void removeFavorite_WhenDatabaseFails_ShouldPropagateException() {

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(favoriteRepository.removeFavorite(USER_ID, RECIPE_ID))
                .thenThrow(databaseError);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> favoriteRecipeService.removeFavorite(USER_ID, RECIPE_ID)
        );

        assertSame(databaseError, exception);
    }
}
