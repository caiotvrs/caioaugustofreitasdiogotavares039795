-- Artists
INSERT INTO artists (name, type) VALUES 
('Serj Tankian', 'SOLO'),
('Mike Shinoda', 'SOLO'),
('Guns N'' Roses', 'BAND');

-- Albums
-- Serj Tankian - Elect the Dead (2007)
INSERT INTO albums (title, release_year, cover_url) VALUES 
('Elect the Dead', 2007, NULL);

-- Mike Shinoda - Post Traumatic (2018)
INSERT INTO albums (title, release_year, cover_url) VALUES 
('Post Traumatic', 2018, NULL);

-- Guns N' Roses - Appetite for Destruction (1987)
INSERT INTO albums (title, release_year, cover_url) VALUES 
('Appetite for Destruction', 1987, NULL);

-- Associations
-- Serj -> Elect the Dead
INSERT INTO album_artist (album_id, artist_id) VALUES 
((SELECT id FROM albums WHERE title = 'Elect the Dead'), (SELECT id FROM artists WHERE name = 'Serj Tankian'));

-- Mike -> Post Traumatic
INSERT INTO album_artist (album_id, artist_id) VALUES 
((SELECT id FROM albums WHERE title = 'Post Traumatic'), (SELECT id FROM artists WHERE name = 'Mike Shinoda'));

-- GnR -> Appetite
INSERT INTO album_artist (album_id, artist_id) VALUES 
((SELECT id FROM albums WHERE title = 'Appetite for Destruction'), (SELECT id FROM artists WHERE name = 'Guns N'' Roses'));
