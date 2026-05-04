# Piattaforma Web: Arte solo per chi la sa valutare

Questa piattaforma permette la compravendita di opere d'arte con una meccanica di gamification unica: puoi acquistare un'opera **solo se riesci a indovinarne o avvicinarti al suo reale valore**.  
Gli utenti verificati possono sia vendere che comprare, mentre gli utenti standard possono unicamente tentare l'acquisto.

---

## Architettura di Spring Boot: Controller, Service, Repository ed Entity

In Spring Boot, l'applicazione è strutturata secondo un'architettura a livelli (Layered Architecture). Questo pattern serve a separare le responsabilità, rendendo il codice più pulito, modulare e facile da testare.

### 1. Entity (Entità)
* Contiene gli oggetti per la rappresentazione e lo scambio dei dati, direttamente mappati sulle tabelle del Database (JPA/Hibernate).

### 2. Repository
* Interfaccia con il DB. Si occupa di tutte le operazioni CRUD (Create, Read, Update, Delete).

### 3. Service
* Implementa la logica di business. Qui risiede il cuore dell'applicazione (es. calcolare se il prezzo indovinato è sufficientemente vicino a quello reale).

### 4. Controller
* Interfaccia con la UI. Espone le API (REST), riceve le richieste HTTP, delega le operazioni ai Service e restituisce le risposte al client.

**Schema per capirci:**  
`Client <---> Controller <---> Service <---> Repository <---> Database (Entity)`

---

## Design Pattern Utilizzati

Per garantire un codice scalabile, manutenibile e pulito, il progetto implementa i seguenti Design Pattern della "Gang of Four" (GoF) e pattern architetturali standard.

### Pattern Creazionali

* **Factory Method:** Utilizzato per la creazione dinamica delle opere d'arte. A seconda della richiesta, la Factory istanzia l'oggetto corretto scegliendo tra le generalizzazioni `DIPINTO` o `SCULTURA`. Il tipo è rappresentato da un'**enum** (`TipoOpera.DIPINTO`, `TipoOpera.SCULTURA`), che in Java è già un singleton per natura.

* **Builder:** Applicato per la costruzione degli oggetti complessi come `UTENTE` e `OGGETTO`, che presentano molti attributi (titolo, descrizione, anno, costo, grandezza, ecc.). Evita costruttori monolitici e rende il codice più leggibile e manutenibile.

### Pattern Strutturali

* **DTO (Data Transfer Object):** Utilizzato per il trasferimento dei dati tra il livello Controller e il Client. Evita di esporre le Entity JPA del database (che contengono dati sensibili come la password) e previene vulnerabilità di sicurezza e cicli infiniti nella serializzazione JSON.

* **Facade:** Utilizzato per creare un'interfaccia semplificata per il processo di acquisto. Unisce sotto un unico metodo l'estrazione randomica dell'opera, il controllo dell'ipotesi di prezzo dell'utente, la gestione del pagamento e l'aggiornamento del database.

* **Proxy (Protection & Virtual Proxy):**
    * *Protection Proxy:* Fa da intermediario verificando se l'utente che tenta di chiamare l'azione `VENDE` è effettivamente un utente `VERIFICATO`, bloccando l'operazione altrimenti. Implementato tramite Spring Security.
    * *Virtual Proxy:* Sfruttato per il lazy loading (caricamento ritardato) dell'immagine dell'opera, caricandola effettivamente in memoria solo quando la vista la richiede, riducendo il carico iniziale della pagina.

### Pattern Comportamentali

* **Strategy:** Utilizzato per il sistema di pagamento al momento del checkout. Permette di incapsulare i diversi metodi di pagamento (Carta di Credito, PayPal, Bonifico) in classi separate e intercambiabili a runtime, senza modificare il codice del Service.

* **State:** Gestisce il ciclo di vita dell'`OGGETTO`. Un'opera transita attraverso gli stati `DISPONIBILE` → `IN_VALUTAZIONE` (quando un utente sta cercando di indovinarne il prezzo) → `VENDUTO`.

* **Command:** Ogni operazione di compra o vendita viene incapsulata come oggetto Command (`CompraCMD`, `VendeCMD`) e salvata in uno storico. Questo permette sia di consultare tutte le transazioni passate dell'utente, sia di annullare un'operazione recente se necessario.

* **Observer:** Utilizzato al termine di una transazione di `COMPRA` andata a buon fine. Il servizio di pagamento agisce da **Subject** e notifica in modo asincrono tutti gli "osservatori" registrati (es. invio email con ricevuta all'acquirente, notifica al venditore).

---