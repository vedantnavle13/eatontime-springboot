-- ─────────────────────────────────────────────────────────────────────────────
-- V1__init_schema.sql
-- Location: src/main/resources/db/migration/V1__init_schema.sql
-- ─────────────────────────────────────────────────────────────────────────────

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE users (
                       id               VARCHAR(36)  PRIMARY KEY,
                       email            VARCHAR(255) NOT NULL UNIQUE,
                       password_hash    VARCHAR(255) NOT NULL,
                       name             VARCHAR(255),
                       phone            VARCHAR(20),
                       avatar_url       VARCHAR(500),
                       role             VARCHAR(30)  NOT NULL DEFAULT 'CUSTOMER',
                       email_verified   BOOLEAN      NOT NULL DEFAULT FALSE,
                       active           BOOLEAN      NOT NULL DEFAULT TRUE,
                       created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Restaurants ───────────────────────────────────────────────────────────────
CREATE TABLE restaurants (
                             id                    VARCHAR(36)  PRIMARY KEY,
                             name                  VARCHAR(255) NOT NULL,
                             description           TEXT,
                             address               VARCHAR(500),
                             city                  VARCHAR(100),
                             locality              VARCHAR(100),
                             phone                 VARCHAR(20),
                             email                 VARCHAR(255),
                             cover_image_url       VARCHAR(500),
                             latitude              DOUBLE PRECISION,
                             longitude             DOUBLE PRECISION,
                             average_cost_for_two  INTEGER,
                             average_rating        DOUBLE PRECISION DEFAULT 0.0,
                             total_reviews         INTEGER          DEFAULT 0,
                             active                BOOLEAN          NOT NULL DEFAULT TRUE,
                             owner_id              VARCHAR(36)  REFERENCES users(id),
                             created_at            TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Restaurant cuisines (element collection) ──────────────────────────────────
CREATE TABLE restaurant_cuisines (
                                     restaurant_id  VARCHAR(36) NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
                                     cuisine        VARCHAR(50) NOT NULL
);

-- ── Dining tables ─────────────────────────────────────────────────────────────
CREATE TABLE dining_tables (
                               id             VARCHAR(36)  PRIMARY KEY,
                               restaurant_id  VARCHAR(36)  NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
                               table_number   VARCHAR(20)  NOT NULL,
                               capacity       INTEGER      NOT NULL,
                               status         VARCHAR(20)  NOT NULL DEFAULT 'AVAILABLE',
                               is_outdoor     BOOLEAN      NOT NULL DEFAULT FALSE,
                               is_accessible  BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ── Operating hours ───────────────────────────────────────────────────────────
CREATE TABLE operating_hours (
                                 id             VARCHAR(36)  PRIMARY KEY,
                                 restaurant_id  VARCHAR(36)  NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
                                 day            VARCHAR(10)  NOT NULL,
                                 open_time      TIME,
                                 close_time     TIME,
                                 closed         BOOLEAN      NOT NULL DEFAULT FALSE
);

-- ── Reservations ──────────────────────────────────────────────────────────────
CREATE TABLE reservations (
                              id                 VARCHAR(36)  PRIMARY KEY,
                              user_id            VARCHAR(36)  NOT NULL REFERENCES users(id),
                              restaurant_id      VARCHAR(36)  NOT NULL REFERENCES restaurants(id),
                              table_id           VARCHAR(36)  REFERENCES dining_tables(id),
                              reservation_date   DATE         NOT NULL,
                              slot_time          TIME         NOT NULL,
                              party_size         INTEGER      NOT NULL,
                              status             VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
                              special_requests   TEXT,
                              qr_code_token      VARCHAR(36)  UNIQUE,
                              qr_scanned_at      TIMESTAMP,
                              created_at         TIMESTAMP    NOT NULL DEFAULT NOW(),
                              updated_at         TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Reviews ───────────────────────────────────────────────────────────────────
CREATE TABLE reviews (
                         id              VARCHAR(36)  PRIMARY KEY,
                         reservation_id  VARCHAR(36)  NOT NULL UNIQUE REFERENCES reservations(id),
                         restaurant_id   VARCHAR(36)  NOT NULL REFERENCES restaurants(id),
                         user_id         VARCHAR(36)  NOT NULL REFERENCES users(id),
                         rating          INTEGER      NOT NULL CHECK (rating BETWEEN 1 AND 5),
                         comment         VARCHAR(1000),
                         created_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Saved restaurants (wishlist) ──────────────────────────────────────────────
CREATE TABLE saved_restaurants (
                                   user_id        VARCHAR(36) NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                   restaurant_id  VARCHAR(36) NOT NULL REFERENCES restaurants(id) ON DELETE CASCADE,
                                   PRIMARY KEY (user_id, restaurant_id)
);

-- ── Indexes for common queries ────────────────────────────────────────────────
CREATE INDEX idx_restaurants_city      ON restaurants(city);
CREATE INDEX idx_restaurants_locality  ON restaurants(locality);
CREATE INDEX idx_restaurants_active    ON restaurants(active);
CREATE INDEX idx_reservations_user     ON reservations(user_id);
CREATE INDEX idx_reservations_restaurant_date ON reservations(restaurant_id, reservation_date);
CREATE INDEX idx_reservations_qr_token ON reservations(qr_code_token);
CREATE INDEX idx_reviews_restaurant    ON reviews(restaurant_id);