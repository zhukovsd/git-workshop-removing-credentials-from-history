ALTER TABLE locations
ADD CONSTRAINT uk_locations_user_coordinates
UNIQUE (user_id, latitude, longitude);