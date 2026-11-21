package org.example.novyru.ServiceTests;

import org.example.novyru.Application.Services.FavoriteRecipeService;
import org.example.novyru.Application.Services.ExternalRecipeService;

import org.example.novyru.Domain.Interfaces.Repositories.IFavoriteRecipeRepository;
import org.example.novyru.Domain.Interfaces.Rules.IFavoriteRecipeRules;

import org.example.novyru.Domain.Exceptions.InvalidIdException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeAlreadyExistsException;
import org.example.novyru.Domain.Exceptions.Recipe.RecipeNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavoriteRecipeServiceTest {

    @Mock
    private IFavoriteRecipeRepository favoriteRepository;

    @Mock
    private IFavoriteRecipeRules favoriteRules;

    @Mock
    private ExternalRecipeService externalRecipeService;

    private FavoriteRecipeService favoriteRecipeService;

    private static final int USER_ID = 1;
    private static final int RECIPE_ID = 10;

    private Map<String, Object> exampleRecipe;

    @BeforeEach
    void setUp() {
        favoriteRecipeService = new FavoriteRecipeService(
                favoriteRepository,
                favoriteRules,
                externalRecipeService
        );

        // Example recipe from the external API
        exampleRecipe = Map.of(
                "id", RECIPE_ID,
                "name", "Chicken Pasta",
                "description", "A delicious pasta recipe",
                "difficulty", "Easy",
                "prep_time", 25,
                "ingredients", List.of(
                        "Chicken",
                        "Pasta",
                        "Tomato sauce"
                ),
                "instructions", List.of(
                        "Cook the pasta",
                        "Cook the chicken",
                        "Mix everything together"
                )
        );
    }

    // ==========================================
    // ADD FAVORITE TESTS
    // ==========================================

    @Test
    void addFavorite_WithValidIds_ShouldAddSuccessfully() {

        when(externalRecipeService.getRecipeById(RECIPE_ID))
                .thenReturn(exampleRecipe);

        when(favoriteRepository.addFavorite(
                eq(USER_ID),
                eq(RECIPE_ID),
                anyString()
        )).thenReturn(true);

        assertDoesNotThrow(() ->
                favoriteRecipeService.addFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        InOrder order = inOrder(
                favoriteRules,
                externalRecipeService,
                favoriteRepository
        );

        order.verify(favoriteRules)
                .validateId(USER_ID, "User ID");

        order.verify(favoriteRules)
                .validateId(RECIPE_ID, "Recipe ID");

        order.verify(externalRecipeService)
                .getRecipeById(RECIPE_ID);

        order.verify(favoriteRepository)
                .addFavorite(
                        eq(USER_ID),
                        eq(RECIPE_ID),
                        anyString()
                );

        order.verify(favoriteRules)
                .requireAdded(
                        true,
                        USER_ID,
                        RECIPE_ID
                );
    }

    @Test
    void addFavorite_ShouldSaveCompleteRecipeAsJson() {

        when(externalRecipeService.getRecipeById(RECIPE_ID))
                .thenReturn(exampleRecipe);

        when(favoriteRepository.addFavorite(
                eq(USER_ID),
                eq(RECIPE_ID),
                anyString()
        )).thenReturn(true);

        favoriteRecipeService.addFavorite(
                USER_ID,
                RECIPE_ID
        );

        ArgumentCaptor<String> jsonCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(favoriteRepository).addFavorite(
                eq(USER_ID),
                eq(RECIPE_ID),
                jsonCaptor.capture()
        );

        String savedJson = jsonCaptor.getValue();

        assertNotNull(savedJson);

        assertTrue(savedJson.contains("\"id\":10"));
        assertTrue(savedJson.contains("\"Chicken Pasta\""));
        assertTrue(savedJson.contains("\"ingredients\""));
        assertTrue(savedJson.contains("\"instructions\""));
        assertTrue(savedJson.contains("\"prep_time\":25"));
    }

    @Test
    void addFavorite_WhenAlreadyExists_ShouldNotThrowIfUpsertSucceeds() {

        // The repository uses MySQL
        // ON DUPLICATE KEY UPDATE.
        // Saving an existing favorite returns true.

        when(externalRecipeService.getRecipeById(RECIPE_ID))
                .thenReturn(exampleRecipe);

        when(favoriteRepository.addFavorite(
                eq(USER_ID),
                eq(RECIPE_ID),
                anyString()
        )).thenReturn(true);

        assertDoesNotThrow(() ->
                favoriteRecipeService.addFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        verify(favoriteRules).requireAdded(
                true,
                USER_ID,
                RECIPE_ID
        );
    }

    @Test
    void addFavorite_WhenRepositoryReturnsFalse_ShouldThrowException() {

        when(externalRecipeService.getRecipeById(RECIPE_ID))
                .thenReturn(exampleRecipe);

        when(favoriteRepository.addFavorite(
                eq(USER_ID),
                eq(RECIPE_ID),
                anyString()
        )).thenReturn(false);

        doThrow(
                new RecipeAlreadyExistsException(
                        "Already favorited"
                )
        ).when(favoriteRules)
                .requireAdded(
                        false,
                        USER_ID,
                        RECIPE_ID
                );

        assertThrows(
                RecipeAlreadyExistsException.class,
                () -> favoriteRecipeService.addFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        verify(favoriteRules).requireAdded(
                false,
                USER_ID,
                RECIPE_ID
        );
    }

    @Test
    void addFavorite_WithInvalidUserId_ShouldThrowException() {

        doThrow(new InvalidIdException("User ID"))
                .when(favoriteRules)
                .validateId(0, "User ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.addFavorite(
                        0,
                        RECIPE_ID
                )
        );

        verifyNoInteractions(
                externalRecipeService,
                favoriteRepository
        );
    }

    @Test
    void addFavorite_WithInvalidRecipeId_ShouldThrowException() {

        // The service validates the user ID before the recipe ID.
        // Explicitly stub both calls to satisfy Mockito strict stubbing.
        doNothing()
                .when(favoriteRules)
                .validateId(USER_ID, "User ID");

        doThrow(new InvalidIdException("Recipe ID"))
                .when(favoriteRules)
                .validateId(0, "Recipe ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.addFavorite(
                        USER_ID,
                        0
                )
        );

        verify(favoriteRules)
                .validateId(USER_ID, "User ID");

        verify(favoriteRules)
                .validateId(0, "Recipe ID");

        verifyNoInteractions(
                externalRecipeService,
                favoriteRepository
        );
    }

    @Test
    void addFavorite_WhenExternalApiFails_ShouldPropagateException() {

        RuntimeException apiError =
                new RuntimeException(
                        "External recipe API unavailable"
                );

        when(externalRecipeService.getRecipeById(RECIPE_ID))
                .thenThrow(apiError);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> favoriteRecipeService.addFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        assertSame(apiError, exception);

        verify(externalRecipeService)
                .getRecipeById(RECIPE_ID);

        verifyNoInteractions(favoriteRepository);
    }

    // ==========================================
    // REMOVE FAVORITE TESTS
    // ==========================================

    @Test
    void removeFavorite_WhenExists_ShouldRemoveSuccessfully() {

        when(favoriteRepository.removeFavorite(
                USER_ID,
                RECIPE_ID
        )).thenReturn(true);

        assertDoesNotThrow(() ->
                favoriteRecipeService.removeFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        verify(favoriteRules)
                .validateId(USER_ID, "User ID");

        verify(favoriteRules)
                .validateId(RECIPE_ID, "Recipe ID");

        verify(favoriteRepository)
                .removeFavorite(USER_ID, RECIPE_ID);

        verify(favoriteRules)
                .requireRemoved(
                        true,
                        USER_ID,
                        RECIPE_ID
                );

        // Removing saved favorites does not
        // require contacting the external API.
        verifyNoInteractions(externalRecipeService);
    }

    @Test
    void removeFavorite_WhenNotFound_ShouldThrowException() {

        when(favoriteRepository.removeFavorite(
                USER_ID,
                RECIPE_ID
        )).thenReturn(false);

        doThrow(
                new RecipeNotFoundException(
                        "Favorite not found"
                )
        ).when(favoriteRules)
                .requireRemoved(
                        false,
                        USER_ID,
                        RECIPE_ID
                );

        assertThrows(
                RecipeNotFoundException.class,
                () -> favoriteRecipeService.removeFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        verify(favoriteRepository)
                .removeFavorite(USER_ID, RECIPE_ID);

        verify(favoriteRules)
                .requireRemoved(
                        false,
                        USER_ID,
                        RECIPE_ID
                );
    }

    @Test
    void removeFavorite_WithInvalidUserId_ShouldThrowException() {

        doThrow(new InvalidIdException("User ID"))
                .when(favoriteRules)
                .validateId(-1, "User ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.removeFavorite(
                        -1,
                        RECIPE_ID
                )
        );

        verifyNoInteractions(
                favoriteRepository,
                externalRecipeService
        );
    }

    // ==========================================
    // CHECK FAVORITE TESTS
    // ==========================================

    @Test
    void isFavorite_WhenFavorited_ShouldReturnTrue() {

        when(favoriteRepository.isFavorite(
                USER_ID,
                RECIPE_ID
        )).thenReturn(true);

        boolean result = favoriteRecipeService.isFavorite(
                USER_ID,
                RECIPE_ID
        );

        assertTrue(result);

        verify(favoriteRules)
                .validateId(USER_ID, "User ID");

        verify(favoriteRules)
                .validateId(RECIPE_ID, "Recipe ID");

        verify(favoriteRepository)
                .isFavorite(USER_ID, RECIPE_ID);
    }

    @Test
    void isFavorite_WhenNotFavorited_ShouldReturnFalse() {

        when(favoriteRepository.isFavorite(
                USER_ID,
                RECIPE_ID
        )).thenReturn(false);

        boolean result = favoriteRecipeService.isFavorite(
                USER_ID,
                RECIPE_ID
        );

        assertFalse(result);

        verify(favoriteRepository)
                .isFavorite(USER_ID, RECIPE_ID);
    }

    @Test
    void isFavorite_WithInvalidRecipeId_ShouldThrowException() {

        // The service validates the user ID before the recipe ID.
        // Explicitly stub both calls to satisfy Mockito strict stubbing.
        doNothing()
                .when(favoriteRules)
                .validateId(USER_ID, "User ID");

        doThrow(new InvalidIdException("Recipe ID"))
                .when(favoriteRules)
                .validateId(0, "Recipe ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.isFavorite(
                        USER_ID,
                        0
                )
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

        when(favoriteRepository.findFavoriteRecipeIdsByUserId(
                USER_ID
        )).thenReturn(expectedIds);

        List<Integer> result =
                favoriteRecipeService.getFavoriteRecipeIds(
                        USER_ID
                );

        assertEquals(expectedIds, result);
        assertEquals(3, result.size());

        verify(favoriteRules)
                .validateId(USER_ID, "User ID");

        verify(favoriteRepository)
                .findFavoriteRecipeIdsByUserId(USER_ID);
    }

    @Test
    void getFavoriteRecipeIds_WhenNoFavorites_ShouldReturnEmptyList() {

        when(favoriteRepository.findFavoriteRecipeIdsByUserId(
                USER_ID
        )).thenReturn(List.of());

        List<Integer> result =
                favoriteRecipeService.getFavoriteRecipeIds(
                        USER_ID
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(favoriteRepository)
                .findFavoriteRecipeIdsByUserId(USER_ID);
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
    // GET COMPLETE SAVED RECIPES TESTS
    // ==========================================

    @Test
    void getFavoriteRecipes_WhenFavoritesExist_ShouldReturnRecipes() {

        String firstRecipeJson = """
                {
                    "id": 10,
                    "name": "Chicken Pasta",
                    "prep_time": 25
                }
                """;

        String secondRecipeJson = """
                {
                    "id": 20,
                    "name": "Tomato Soup",
                    "prep_time": 15
                }
                """;

        when(favoriteRepository.findFavoriteRecipeJsonByUserId(
                USER_ID
        )).thenReturn(List.of(
                firstRecipeJson,
                secondRecipeJson
        ));

        List<Object> result =
                favoriteRecipeService.getFavoriteRecipes(
                        USER_ID
                );

        assertNotNull(result);
        assertEquals(2, result.size());

        assertInstanceOf(Map.class, result.get(0));
        assertInstanceOf(Map.class, result.get(1));

        Map<?, ?> firstRecipe =
                (Map<?, ?>) result.get(0);

        Map<?, ?> secondRecipe =
                (Map<?, ?>) result.get(1);

        assertEquals(10, firstRecipe.get("id"));
        assertEquals(
                "Chicken Pasta",
                firstRecipe.get("name")
        );

        assertEquals(20, secondRecipe.get("id"));
        assertEquals(
                "Tomato Soup",
                secondRecipe.get("name")
        );

        verify(favoriteRules)
                .validateId(USER_ID, "User ID");

        verify(favoriteRepository)
                .findFavoriteRecipeJsonByUserId(USER_ID);

        // Saved recipes must be loaded from MySQL,
        // not fetched again from the external API.
        verifyNoInteractions(externalRecipeService);
    }

    @Test
    void getFavoriteRecipes_WhenNoFavorites_ShouldReturnEmptyList() {

        when(favoriteRepository.findFavoriteRecipeJsonByUserId(
                USER_ID
        )).thenReturn(List.of());

        List<Object> result =
                favoriteRecipeService.getFavoriteRecipes(
                        USER_ID
                );

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(favoriteRepository)
                .findFavoriteRecipeJsonByUserId(USER_ID);
    }

    @Test
    void getFavoriteRecipes_WithInvalidUserId_ShouldThrowException() {

        doThrow(new InvalidIdException("User ID"))
                .when(favoriteRules)
                .validateId(0, "User ID");

        assertThrows(
                InvalidIdException.class,
                () -> favoriteRecipeService.getFavoriteRecipes(0)
        );

        verifyNoInteractions(
                favoriteRepository,
                externalRecipeService
        );
    }

    // ==========================================
    // DATABASE FAILURE TESTS
    // ==========================================

    @Test
    void addFavorite_WhenDatabaseFails_ShouldPropagateException() {

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(externalRecipeService.getRecipeById(RECIPE_ID))
                .thenReturn(exampleRecipe);

        when(favoriteRepository.addFavorite(
                eq(USER_ID),
                eq(RECIPE_ID),
                anyString()
        )).thenThrow(databaseError);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> favoriteRecipeService.addFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        assertSame(databaseError, exception);

        verify(externalRecipeService)
                .getRecipeById(RECIPE_ID);

        verify(favoriteRepository)
                .addFavorite(
                        eq(USER_ID),
                        eq(RECIPE_ID),
                        anyString()
                );
    }

    @Test
    void removeFavorite_WhenDatabaseFails_ShouldPropagateException() {

        RuntimeException databaseError =
                new RuntimeException("Database unavailable");

        when(favoriteRepository.removeFavorite(
                USER_ID,
                RECIPE_ID
        )).thenThrow(databaseError);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> favoriteRecipeService.removeFavorite(
                        USER_ID,
                        RECIPE_ID
                )
        );

        assertSame(databaseError, exception);
    }
}
