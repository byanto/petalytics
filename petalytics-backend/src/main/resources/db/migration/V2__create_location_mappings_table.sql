CREATE TABLE location_mappings (
    id UUID PRIMARY KEY,
    location_type VARCHAR(50) NOT NULL,
    raw_name VARCHAR(255) NOT NULL,
    standardized_name VARCHAR(255) NOT NULL,
    CONSTRAINT uk_type_variant UNIQUE (location_type, raw_name)
);