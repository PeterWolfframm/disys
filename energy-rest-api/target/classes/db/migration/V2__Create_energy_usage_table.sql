CREATE TABLE energy_usage (
    id BIGSERIAL PRIMARY KEY,
    hour TIMESTAMP NOT NULL UNIQUE,
    community_produced DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
    community_used DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
    grid_used DECIMAL(10, 3) NOT NULL DEFAULT 0.000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_energy_usage_hour ON energy_usage(hour); 