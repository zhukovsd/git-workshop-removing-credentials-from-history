CREATE TABLE locations (
    id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    user_id bigint NOT NULL,
    latitude DECIMAL(8, 6) NOT NULL,
    longitude DECIMAL(9, 6) NOT NULL,

    CONSTRAINT fk_locations_user_id FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);