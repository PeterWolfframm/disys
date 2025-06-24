CREATE TABLE energy_percentage (
    id BIGSERIAL PRIMARY KEY,
    hour TIMESTAMP NOT NULL UNIQUE,
    community_depleted DECIMAL(5, 2) NOT NULL,
    grid_portion DECIMAL(5, 2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_energy_percentage_hour ON energy_percentage(hour); 