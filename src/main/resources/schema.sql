CREATE TABLE IF NOT EXISTS utenti (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    attivo BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS utenti_verificati (
    utente_id INT PRIMARY KEY,
    titolo VARCHAR(150),
    CONSTRAINT fk_verificato_utente FOREIGN KEY (utente_id)
        REFERENCES utenti(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS autori (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cognome VARCHAR(100) NOT NULL,
    utente_verificato_id INT UNIQUE,
    CONSTRAINT fk_autore_verificato FOREIGN KEY (utente_verificato_id)
        REFERENCES utenti_verificati(utente_id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS oggetti (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titolo VARCHAR(255) NOT NULL,
    descrizione TEXT,
    anno INT,
    costo DECIMAL(10, 2) NOT NULL,
    grandezza VARCHAR(100),
    version INT NOT NULL DEFAULT 0,
    link_immagine VARCHAR(500),
    tipo_opera ENUM('DIPINTO', 'SCULTURA') NOT NULL,
    peso DECIMAL(10, 2),
    stato ENUM('DISPONIBILE', 'IN_VALUTAZIONE', 'VENDUTO') NOT NULL DEFAULT 'DISPONIBILE',
    autore_id INT,
    CONSTRAINT fk_oggetto_autore FOREIGN KEY (autore_id)
        REFERENCES autori(id) ON DELETE SET NULL,
    INDEX idx_oggetti_autore (autore_id)
);

CREATE TABLE IF NOT EXISTS azioni (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_azione ENUM('COMPRA', 'VENDE') NOT NULL,
    prezzo_al_momento DECIMAL(10, 2) NOT NULL,
    sconto_applicato BOOLEAN NOT NULL DEFAULT FALSE,
    metodo_pagamento ENUM('PAYPAL', 'BONIFICO', 'P2P', 'REVOLUT') NULL,
    annullata BOOLEAN NOT NULL DEFAULT FALSE,
    utente_id INT NOT NULL,
    oggetto_id INT NOT NULL,
    CONSTRAINT fk_azione_utente FOREIGN KEY (utente_id)
        REFERENCES utenti(id) ON DELETE CASCADE,
    CONSTRAINT fk_azione_oggetto FOREIGN KEY (oggetto_id)
        REFERENCES oggetti(id) ON DELETE CASCADE,
    INDEX idx_azioni_utente (utente_id),
    INDEX idx_azioni_oggetto (oggetto_id)
);