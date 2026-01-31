CREATE TABLE artists (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL
);

CREATE TABLE albums (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    release_year INTEGER NOT NULL,
    cover_url VARCHAR(255)
);

CREATE TABLE regionals (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    external_id BIGINT,
    CONSTRAINT uk_regional_external_id UNIQUE (external_id)
);

CREATE TABLE album_artist (
    album_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    CONSTRAINT pk_album_artist PRIMARY KEY (album_id, artist_id),
    CONSTRAINT fk_album_artist_album FOREIGN KEY (album_id) REFERENCES albums (id),
    CONSTRAINT fk_album_artist_artist FOREIGN KEY (artist_id) REFERENCES artists (id)
);

-- Index
CREATE INDEX idx_artist_name ON artists(name);
CREATE INDEX idx_album_title ON albums(title);
CREATE INDEX idx_regional_external_id ON regionals(external_id);
CREATE INDEX idx_regional_ativo ON regionals(ativo);
