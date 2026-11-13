
package org.example.novyru.ApiTests;

import org.example.novyru.Application.Controllers.FavoriteController;
import org.example.novyru.Application.Services.FavoriteRecipeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FavoriteRecipeApiTest {

    @Mock
    private FavoriteRecipeService favoriteRecipeService;

    private MockMvc mockMvc;

    private static final int USER_ID = 1;
    private static final int RECIPE_ID = 10;

    private static final String BASE_URL =
            "/api/users/1/favorites";

    @BeforeEach
    void setUp() {
        FavoriteController controller =
                new FavoriteController(favoriteRecipeService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    // ==========================================
    // ADD FAVORITE TESTS
    // ==========================================

    @Test
    void addFavorite_WithValidRequest_ShouldReturn201()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "recipeId": 10
                                }
                                """))

                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "/api/users/1/favorites/10"
                ))
                .andExpect(content().string(""));

        verify(favoriteRecipeService)
                .addFavorite(USER_ID, RECIPE_ID);

        verifyNoMoreInteractions(favoriteRecipeService);
    }

    @Test
    void addFavorite_WithoutRequestBody_ShouldReturn400()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(favoriteRecipeService);
    }

    @Test
    void addFavorite_WithInvalidJson_ShouldReturn400()
            throws Exception {

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))

                .andExpect(status().isBadRequest());

        verifyNoInteractions(favoriteRecipeService);
    }

    // ==========================================
    // REMOVE FAVORITE TESTS
    // ==========================================

    @Test
    void removeFavorite_WithValidIds_ShouldReturn204()
            throws Exception {

        mockMvc.perform(delete(BASE_URL + "/10"))

                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(favoriteRecipeService)
                .removeFavorite(USER_ID, RECIPE_ID);

        verifyNoMoreInteractions(favoriteRecipeService);
    }

    // ==========================================
    // GET FAVORITE RECIPE IDS TESTS
    // ==========================================

    @Test
    void getFavoriteRecipeIds_WhenFavoritesExist_ShouldReturn200()
            throws Exception {

        List<Integer> favoriteIds = List.of(10, 20, 30);

        when(favoriteRecipeService.getFavoriteRecipeIds(USER_ID))
                .thenReturn(favoriteIds);

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())
                .andExpect(content().json(
                        "[10, 20, 30]"
                ))
                .andExpect(jsonPath("$[0]").value(10))
                .andExpect(jsonPath("$[1]").value(20))
                .andExpect(jsonPath("$[2]").value(30));

        verify(favoriteRecipeService)
                .getFavoriteRecipeIds(USER_ID);
    }

    @Test
    void getFavoriteRecipeIds_WhenNoFavorites_ShouldReturnEmptyList()
            throws Exception {

        when(favoriteRecipeService.getFavoriteRecipeIds(USER_ID))
                .thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))

                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(favoriteRecipeService)
                .getFavoriteRecipeIds(USER_ID);
    }

    // ==========================================
    // CHECK FAVORITE STATUS TESTS
    // ==========================================

    @Test
    void isFavorite_WhenFavorited_ShouldReturnTrue()
            throws Exception {

        when(favoriteRecipeService.isFavorite(USER_ID, RECIPE_ID))
                .thenReturn(true);

        mockMvc.perform(get(BASE_URL + "/10/status"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite")
                        .value(true));

        verify(favoriteRecipeService)
                .isFavorite(USER_ID, RECIPE_ID);
    }

    @Test
    void isFavorite_WhenNotFavorited_ShouldReturnFalse()
            throws Exception {

        when(favoriteRecipeService.isFavorite(USER_ID, RECIPE_ID))
                .thenReturn(false);

        mockMvc.perform(get(BASE_URL + "/10/status"))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.favorite")
                        .value(false));

        verify(favoriteRecipeService)
                .isFavorite(USER_ID, RECIPE_ID);
    }

    // ==========================================
    // INVALID PATH PARAMETER TESTS
    // ==========================================

    @Test
    void getFavoriteRecipeIds_WithNonNumericUserId_ShouldReturn400()
            throws Exception {

        mockMvc.perform(
                        get("/api/users/abc/favorites")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(favoriteRecipeService);
    }

    @Test
    void isFavorite_WithNonNumericRecipeId_ShouldReturn400()
            throws Exception {

        mockMvc.perform(
                        get(BASE_URL + "/abc/status")
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(favoriteRecipeService);
    }

    // ==========================================
    // HTTP METHOD TESTS
    // ==========================================

    @Test
    void addFavorite_WithUnsupportedMethod_ShouldReturn405()
            throws Exception {

        mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "recipeId": 10
                                }
                                """))

                .andExpect(status().isMethodNotAllowed());

        verifyNoInteractions(favoriteRecipeService);
    }
}
