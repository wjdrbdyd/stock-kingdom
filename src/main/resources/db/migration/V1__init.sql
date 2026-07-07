-- V1: 초기 스키마 (User, Stock, UserStockHolding, Kingdom, KingdomPowerSnapshot)

CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    nickname    VARCHAR(50)  NOT NULL,
    email       VARCHAR(100) NOT NULL UNIQUE,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE stock (
    id             BIGSERIAL PRIMARY KEY,
    ticker         VARCHAR(20)  NOT NULL UNIQUE,
    company_name   VARCHAR(100) NOT NULL,
    current_price  NUMERIC(15, 2) NOT NULL,
    market_cap     NUMERIC(20, 2) NOT NULL,
    created_at     TIMESTAMP,
    updated_at     TIMESTAMP
);

CREATE TABLE user_stock_holding (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id),
    stock_id    BIGINT NOT NULL REFERENCES stock(id),
    quantity    BIGINT NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP,
    CONSTRAINT uk_user_stock UNIQUE (user_id, stock_id)
);

CREATE TABLE kingdom (
    id          BIGSERIAL PRIMARY KEY,
    stock_id    BIGINT NOT NULL UNIQUE REFERENCES stock(id),
    name        VARCHAR(50) NOT NULL,
    created_at  TIMESTAMP,
    updated_at  TIMESTAMP
);

CREATE TABLE kingdom_power_snapshot (
    id                       BIGSERIAL PRIMARY KEY,
    kingdom_id               BIGINT NOT NULL REFERENCES kingdom(id),
    snapshot_date             DATE   NOT NULL,
    total_holding_quantity    BIGINT NOT NULL,
    participant_count         INTEGER NOT NULL,
    power                     NUMERIC(20, 4) NOT NULL,
    rank                      INTEGER,
    rank_change               INTEGER,
    created_at                TIMESTAMP,
    updated_at                TIMESTAMP,
    CONSTRAINT uk_kingdom_snapshot_date UNIQUE (kingdom_id, snapshot_date)
);
