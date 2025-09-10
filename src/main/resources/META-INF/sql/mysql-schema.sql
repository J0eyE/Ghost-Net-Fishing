-- Personen für Bergung etc.
CREATE TABLE IF NOT EXISTS Person (
  id     BIGINT NOT NULL AUTO_INCREMENT,
  name   VARCHAR(100),
  phone  VARCHAR(30),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Geisternetze
CREATE TABLE IF NOT EXISTS GhostNet (
  id   BIGINT NOT NULL AUTO_INCREMENT,
  createdAt TIMESTAMP(6) NULL DEFAULT CURRENT_TIMESTAMP(6),
  lat  DOUBLE,
  lon  DOUBLE,
  size VARCHAR(255),

  reporterName  VARCHAR(100),
  reporterPhone VARCHAR(30),

  missingReporterName  VARCHAR(100),
  missingReporterPhone VARCHAR(30),

  status VARCHAR(16) NOT NULL,           -- Enum als String: REPORTED | MISSING | CLAIMED | RECOVERED

  salvor_id BIGINT,
  PRIMARY KEY (id),
  KEY idx_salvor (salvor_id),
  CONSTRAINT fk_ghostnet_salvor FOREIGN KEY (salvor_id) REFERENCES Person(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Accounts für Registrierung/Login
CREATE TABLE IF NOT EXISTS account (
  id          BIGINT NOT NULL AUTO_INCREMENT,
  first_name  VARCHAR(100) NOT NULL,
  last_name   VARCHAR(100) NOT NULL,
  company     VARCHAR(120),
  phone       VARCHAR(30)  NOT NULL,
  salt        VARCHAR(255) NOT NULL,
  pwd_hash    VARCHAR(255) NOT NULL,
  CONSTRAINT uq_account_last_name UNIQUE (last_name),
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

