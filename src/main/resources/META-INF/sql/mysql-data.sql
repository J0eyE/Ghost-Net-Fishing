-- Personen
INSERT INTO Person (name, phone) VALUES
  ('Baltic Divers',       '0451-1111'),
  ('Aqua Salvage GmbH',   '01234-555');
  
-- Beispiel-Account "Muster"
INSERT INTO account(first_name,last_name,company,phone,salt,pwd_hash)
VALUES
('Max','Muster','Beispiel GmbH','01234-555','x','y'); 

-- Missing (kein salvor)
INSERT INTO GhostNet
  (createdAt, lat,   lon,    size,   reporterName, reporterPhone, status,    salvor_id)
VALUES
  (CURRENT_TIMESTAMP(6), 54.400, 10.450, 'klein',  'Anonym',      NULL,        'MISSING',  NULL);

-- Claimed
INSERT INTO GhostNet
  (createdAt, lat,   lon,    size,   reporterName, reporterPhone, status,   salvor_id)
VALUES
  (CURRENT_TIMESTAMP(6), 54.250, 10.300, 'groß',   'Küstenschutz','01234-555','CLAIMED',
   (SELECT id FROM Person WHERE name='Aqua Salvage GmbH' LIMIT 1));

-- Recovered
INSERT INTO GhostNet
  (createdAt, lat,   lon,    size,   reporterName, reporterPhone, status,     salvor_id)
VALUES
  (CURRENT_TIMESTAMP(6), 54.050, 10.050, 'mittel', 'Segelclub',   '0171-2222','RECOVERED',
   (SELECT id FROM Person WHERE name='Baltic Divers' LIMIT 1));
