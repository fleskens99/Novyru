CREATE DATABASE IF NOT EXISTS novyru
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE novyru;

-- 1. USERS
CREATE TABLE IF NOT EXISTS users (
                                     id INT NOT NULL AUTO_INCREMENT,
                                     email VARCHAR(254) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(80) NOT NULL,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)

    ) ENGINE=InnoDB;


-- 2. RECIPE
CREATE TABLE IF NOT EXISTS recipe (
                                      id INT NOT NULL AUTO_INCREMENT,
                                      name VARCHAR(255) NOT NULL,
    description TEXT,
    instructions JSON NOT NULL,
    difficulty VARCHAR(40),

    source ENUM('API', 'USER') NOT NULL,
    external_id VARCHAR(100) NULL,
    created_by INT NULL,

    PRIMARY KEY (id),

    UNIQUE KEY uq_api_recipe (source, external_id),

    CONSTRAINT fk_recipe_creator
    FOREIGN KEY (created_by)
    REFERENCES users(id)
    ON DELETE RESTRICT,

    CONSTRAINT chk_recipe_instructions
    CHECK (JSON_TYPE(instructions) = 'ARRAY'),

    CONSTRAINT chk_recipe_source
    CHECK (
(source = 'USER'
 AND created_by IS NOT NULL
 AND external_id IS NULL)
    OR
(source = 'API'
 AND external_id IS NOT NULL
 AND created_by IS NULL)
    )

    ) ENGINE=InnoDB;


-- 3. INGREDIENT
CREATE TABLE IF NOT EXISTS ingredient (
                                          id INT NOT NULL AUTO_INCREMENT,
                                          recipe_id INT NOT NULL,
                                          name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    unit VARCHAR(50),
    quantity DOUBLE NOT NULL,

    PRIMARY KEY (id),

    CONSTRAINT fk_ingredient_recipe
    FOREIGN KEY (recipe_id)
    REFERENCES recipe(id)
    ON DELETE CASCADE,

    CONSTRAINT chk_ingredient_quantity
    CHECK (quantity >= 0)

    ) ENGINE=InnoDB;


-- 4. FAVORITE
CREATE TABLE IF NOT EXISTS favorite (
                                        user_id INT NOT NULL,
                                        recipe_id INT NOT NULL,

                                        PRIMARY KEY (user_id, recipe_id),

    CONSTRAINT fk_favorite_user
    FOREIGN KEY (user_id)
    REFERENCES users(id)
    ON DELETE CASCADE,

    CONSTRAINT fk_favorite_recipe
    FOREIGN KEY (recipe_id)
    REFERENCES recipe(id)
    ON DELETE CASCADE

    ) ENGINE=InnoDB;