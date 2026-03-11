CREATE TABLE IF NOT EXISTS history_owners (
   id SERIAL PRIMARY KEY,
   owner_id INT NOT NULL REFERENCES owner(id),
   car_id INT NOT NULL REFERENCES car(id)
);