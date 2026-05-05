SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS azioni;
DROP TABLE IF EXISTS oggetti;
DROP TABLE IF EXISTS autori;
DROP TABLE IF EXISTS utenti_verificati;
DROP TABLE IF EXISTS utenti;

SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE IF NOT EXISTS utenti (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS utenti_verificati (
    utente_id INT PRIMARY KEY,
    titolo VARCHAR(150),
    CONSTRAINT fk_verificato_utente FOREIGN KEY (utente_id) REFERENCES utenti(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS autori (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    utente_verificato_id INT UNIQUE,
    CONSTRAINT fk_autore_verificato FOREIGN KEY (utente_verificato_id) REFERENCES utenti_verificati(utente_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS oggetti (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(255) NOT NULL,
    descrizione TEXT,
    anno INT,
    costo DECIMAL(10, 2) NOT NULL,
    grandezza VARCHAR(100),
    link_immagine VARCHAR(500),
    tipo_opera ENUM('DIPINTO', 'SCULTURA') NOT NULL,
    peso DECIMAL(10, 2),
    stato ENUM('DISPONIBILE', 'IN_VALUTAZIONE', 'VENDUTO') NOT NULL DEFAULT 'DISPONIBILE',
    autore_id INT,
    CONSTRAINT fk_oggetto_autore FOREIGN KEY (autore_id) REFERENCES autori(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS azioni (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_azione ENUM('COMPRA', 'VENDE') NOT NULL,
    prezzo_al_momento DECIMAL(10, 2) NOT NULL,
    metodo_pagamento ENUM('PAYPAL', 'BONIFICO', 'P2P', 'REVOLUT') NOT NULL,
    annullata BOOLEAN NOT NULL DEFAULT FALSE,
    utente_id INT NOT NULL,
    oggetto_id INT NOT NULL,
    CONSTRAINT fk_azione_utente FOREIGN KEY (utente_id) REFERENCES utenti(id) ON DELETE CASCADE,
    CONSTRAINT fk_azione_oggetto FOREIGN KEY (oggetto_id) REFERENCES oggetti(id) ON DELETE CASCADE
);

DROP TRIGGER IF EXISTS check_vende_solo_verificato;

DELIMITER $$
CREATE TRIGGER check_vende_solo_verificato
    BEFORE INSERT ON azioni
    FOR EACH ROW
BEGIN
    IF NEW.tipo_azione = 'VENDE' THEN
        IF NOT EXISTS (
            SELECT 1 FROM utenti_verificati
            WHERE utente_id = NEW.utente_id
        ) THEN
            SIGNAL SQLSTATE '45000'
                SET MESSAGE_TEXT = 'Solo un utente verificato può vendere un oggetto';
        END IF;
    END IF;
END$$
DELIMITER ;

CREATE INDEX idx_azioni_utente  ON azioni(utente_id);
CREATE INDEX idx_azioni_oggetto ON azioni(oggetto_id);
CREATE INDEX idx_oggetti_autore ON oggetti(autore_id);