-- Artists
INSERT INTO artists (name, type) VALUES 
('Serj Tankian', 'SOLO'),
('Mike Shinoda', 'SOLO'),
('Michel Teló', 'SOLO'),
('Guns N'' Roses', 'BAND');

-- Albums

-- Serj Tankian
INSERT INTO albums (title, release_year) VALUES 
('Elect the Dead', 2007),
('Harakiri', 2012),
('Black Blooms', 2019),
('The Rough Dog', 2018);

-- Mike Shinoda
INSERT INTO albums (title, release_year) VALUES 
('The Rising Tied', 2005),
('Post Traumatic', 2018),
('Post Traumatic EP', 2018),
('Where''d You Go', 2006);

-- Michel Teló
INSERT INTO albums (title, release_year) VALUES 
('Bem Sertanejo', 2014),
('Bem Sertanejo - O Show (Ao Vivo)', 2017),
('Bem Sertanejo (1ª Temporada) - EP', 2014);

-- Guns N' Roses
INSERT INTO albums (title, release_year) VALUES 
('Appetite for Destruction', 1987),
('Use Your Illusion I', 1991),
('Use Your Illusion II', 1991),
('Greatest Hits', 2004);

-- Album-Artist Associations

-- Serj Tankian
INSERT INTO album_artist (album_id, artist_id) VALUES 
((SELECT id FROM albums WHERE title = 'Elect the Dead'), (SELECT id FROM artists WHERE name = 'Serj Tankian')),
((SELECT id FROM albums WHERE title = 'Harakiri'), (SELECT id FROM artists WHERE name = 'Serj Tankian')),
((SELECT id FROM albums WHERE title = 'Black Blooms'), (SELECT id FROM artists WHERE name = 'Serj Tankian')),
((SELECT id FROM albums WHERE title = 'The Rough Dog'), (SELECT id FROM artists WHERE name = 'Serj Tankian'));

-- Mike Shinoda
INSERT INTO album_artist (album_id, artist_id) VALUES 
((SELECT id FROM albums WHERE title = 'The Rising Tied'), (SELECT id FROM artists WHERE name = 'Mike Shinoda')),
((SELECT id FROM albums WHERE title = 'Post Traumatic'), (SELECT id FROM artists WHERE name = 'Mike Shinoda')),
((SELECT id FROM albums WHERE title = 'Post Traumatic EP'), (SELECT id FROM artists WHERE name = 'Mike Shinoda')),
((SELECT id FROM albums WHERE title = 'Where''d You Go'), (SELECT id FROM artists WHERE name = 'Mike Shinoda'));

-- Michel Teló
INSERT INTO album_artist (album_id, artist_id) VALUES 
((SELECT id FROM albums WHERE title = 'Bem Sertanejo'), (SELECT id FROM artists WHERE name = 'Michel Teló')),
((SELECT id FROM albums WHERE title = 'Bem Sertanejo - O Show (Ao Vivo)'), (SELECT id FROM artists WHERE name = 'Michel Teló')),
((SELECT id FROM albums WHERE title = 'Bem Sertanejo (1ª Temporada) - EP'), (SELECT id FROM artists WHERE name = 'Michel Teló'));

-- Guns N' Roses
INSERT INTO album_artist (album_id, artist_id) VALUES 
((SELECT id FROM albums WHERE title = 'Appetite for Destruction'), (SELECT id FROM artists WHERE name = 'Guns N'' Roses')),
((SELECT id FROM albums WHERE title = 'Use Your Illusion I'), (SELECT id FROM artists WHERE name = 'Guns N'' Roses')),
((SELECT id FROM albums WHERE title = 'Use Your Illusion II'), (SELECT id FROM artists WHERE name = 'Guns N'' Roses')),
((SELECT id FROM albums WHERE title = 'Greatest Hits'), (SELECT id FROM artists WHERE name = 'Guns N'' Roses'));
