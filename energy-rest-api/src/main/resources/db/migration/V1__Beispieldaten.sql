-- Beispieldaten 
CREATE TABLE energy_data (
    id BIGSERIAL PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    energy_consumption DECIMAL(10,2) NOT NULL
);