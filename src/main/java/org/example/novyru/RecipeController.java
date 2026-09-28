package org.example.novyru;

import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RecipeController {

    @GetMapping("/recipe")
    public List<Recipe> retrieveRecipe() {
        return Arrays.asList(
                new Recipe(
                        1,
                        "Pasta with Tomato Sauce",
                        "A quick and easy pasta dish.",
                        "Pasta, tomato sauce, olive oil, salt, pepper",
                        "1. Boil the pasta in salted water.\n" +
                                "2. Heat the tomato sauce with olive oil.\n" +
                                "3. Drain the pasta and mix with the sauce.\n" +
                                "4. Serve and enjoy!",
                        20
                ),
                new Recipe(
                        2,
                        "Scrambled Eggs",
                        "Simple scrambled eggs for breakfast.",
                        "2 eggs, butter, salt, pepper",
                        "1. Crack the eggs into a bowl.\n" +
                                "2. Beat the eggs with salt and pepper.\n" +
                                "3. Melt butter in a pan.\n" +
                                "4. Add the eggs and stir until cooked.",
                        10
                ),
                new Recipe(
                        3,
                        "Cheese Toast",
                        "Crispy toast with melted cheese.",
                        "2 slices of bread, cheese, butter",
                        "1. Butter the bread slices.\n" +
                                "2. Add cheese between the slices.\n" +
                                "3. Toast in a pan until golden brown.\n" +
                                "4. Serve while warm.",
                        10
                ),
                new Recipe(
                        4,
                        "Banana Pancakes",
                        "Easy pancakes made with banana.",
                        "1 banana, 2 eggs, 50g flour, 50ml milk",
                        "1. Mash the banana in a bowl.\n" +
                                "2. Add eggs, flour, and milk.\n" +
                                "3. Mix until smooth.\n" +
                                "4. Cook small pancakes in a pan until golden.",
                        15
                ),
                new Recipe(
                        5,
                        "Chicken Wrap",
                        "A quick wrap filled with chicken and vegetables.",
                        "1 tortilla, 100g chicken, lettuce, tomato, sauce",
                        "1. Cook the chicken in a pan.\n" +
                                "2. Cut the lettuce and tomato.\n" +
                                "3. Place everything on the tortilla.\n" +
                                "4. Add sauce and roll the tortilla.",
                        20
                )
        );
    }
    @PostMapping("/recipe")
    public String addRecipe(@RequestBody Recipe recipe){
        return "Created";
    }

    @PutMapping("/recipe")
    public String updatedRecipe(@RequestBody Recipe recipe){
        return "Updated";
    }
}
