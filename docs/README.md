# Arte per chi la sa valutare

Questa piattaforma unisce il mercato dell'arte a una meccanica di gioco semplice ma coinvolgente: **puoi acquistare un'opera solo se riesci ad indovinarne (o avvicinarti abbastanza a) il suo valore reale**. Chi ha l'occhio fino compra. Gli altri si allenano.

Gli utenti verificati possono sia mettere in vendita che acquistare. Gli utenti standard possono tentare la fortuna — e affinare il proprio gusto.

---

## Come è costruita l'applicazione

Il progetto è sviluppato in **Spring Boot** e segue un'architettura a livelli classica: ogni parte ha il suo ruolo preciso, e nessuna si intromette nel lavoro delle altre.

Client ↔ Controller ↔ Service ↔ Repository ↔ Database

- **Entity** — gli oggetti che vivono nel database
- **Repository** — parla con il DB
- **Service** — qui risiede tutta la logica di business
- **Controller** — espone le API REST

---

## Le scelte di design (e perché le abbiamo fatte)

Per tenere il codice pulito, scalabile ed evolvibile abbiamo adottato alcuni Design Pattern classici della *Gang of Four*.

### Come creiamo gli oggetti

**Factory Method** — Quando si aggiunge un'opera la Factory decide quale oggetto istanziare in base al tipo ricevuto, rappresentato da un'enum (`TipoOpera.DIPINTO` / `TipoOpera.SCULTURA`).

**Builder** — Invece di un costruttore con dieci parametri che nessuno ricorda in quale ordine vadano, usiamo il Builder attraverso la notazione @Builder di Lombok.

### Come colleghiamo i pezzi

**DTO (Data Transfer Object)** — Per la comunicazione tra Frontend e Backend.

**Facade** — La usiamo così da poter astrarre tutto il processo di controllo del prezzo corretto ed acquisto.

**Proxy** — Usato in due modi distinti:
- *Protection Proxy* (tramite Spring Security): controlla che solo gli utenti verificati possano mettere in vendita un'opera.
- *Virtual Proxy*: le immagini delle opere vengono caricate solo quando la vista le richiede davvero.

### Come gestiamo i comportamenti

**Template Method** — Al checkout, l'utente può pagare con PayPal, Revolut, P2P o Bonifico. I metodi nelle classi sono gli stessi, cambia solo l'API del servizio di pagamento.

**State** — Le opere hanno una vita: nascono `DISPONIBILE`, passano per `IN_VALUTAZIONE` quando qualcuno sta tentando l'acquisto, e arrivano a `VENDUTO`.

**Command** — Ogni acquisto e ogni vendita viene incapsulata in un oggetto Command (`CompraCMD`, `VendeCMD`). Questo ci dà due cose gratis: uno storico completo delle transazioni e la possibilità di annullare un'operazione recente.

**Observer** — Quando un acquisto va a buon fine, il servizio di pagamento lancia una notifica asincrona a tutti gli "osservatori" registrati: l'acquirente riceve la ricevuta via email, il venditore viene avvisato.

---


## Setup Keycloak (Linux)

Keycloak gestisce tutta l'autenticazione — login, token JWT e gestione utenti. Non è una libreria ma un server separato che va avviato prima del backend.

### 1. Scarica Keycloak

```bash
curl -L https://github.com/keycloak/keycloak/releases/download/24.0.4/keycloak-24.0.4.tar.gz -o keycloak.tar.gz
tar -xzf keycloak.tar.gz
cd keycloak-24.0.4/bin
```

### 2. Avvia Keycloak per la prima volta

```bash
./kc.sh start-dev --http-port=8180
```

Aspetta fino a quando vedi nel terminale:

```
Keycloak 24.0.4 on JVM started. Listening on: http://0.0.0.0:8180
```

### 3. Crea l'utente admin

Apri il browser su `http://localhost:8180` e crea l'utente admin con username `admin` e password `admin`.

### 4. Configura Keycloak (da un secondo terminale)

Esegui i seguenti comandi in ordine dalla cartella `bin` di Keycloak:

```bash
# Autenticati come admin
./kcadm.sh config credentials \
  --server http://localhost:8180 \
  --realm master \
  --user admin \
  --password admin

# Crea il realm
./kcadm.sh create realms \
  -s realm=art-platform \
  -s enabled=true

# Crea il client
./kcadm.sh create clients \
  -r art-platform \
  -s clientId=art-platform-client \
  -s enabled=true \
  -s publicClient=true \
  -s directAccessGrantsEnabled=true \
  -s 'redirectUris=["http://localhost:8080/*"]' \
  -s 'webOrigins=["http://localhost:8080"]'
```

> ⚠️ Questi comandi vanno eseguiti **una sola volta** durante il primo setup.

---

## Avvio del progetto

Ogni volta che si vuole lavorare al progetto, avviare sempre Keycloak **prima** del backend:

```bash
# Terminale 1 — avvia Keycloak
cd keycloak-24.0.4/bin
./kc.sh start-dev --http-port=8180

# Terminale 2 — avvia Spring Boot
./mvnw spring-boot:run
```

> ⚠️ Senza Keycloak attivo la registrazione e il login non funzionano.

---

## Variabili d'ambiente

Prima di avviare il backend configura le seguenti variabili d'ambiente in IntelliJ (Run → Edit Configurations → Environment Variables):

```
DB_USERNAME             = il tuo username MySQL
DB_PASSWORD             = la tua password MySQL
KEYCLOAK_ADMIN_USERNAME = admin
KEYCLOAK_ADMIN_PASSWORD = admin
```

---

## API principali

### Registrazione

```
POST http://localhost:8080/api/utenti/registra
Content-Type: application/json

{
    "nome": "Mario",
    "cognome": "Rossi",
    "email": "mario.rossi@test.it",
    "password": "Password123!"
}
```

### Login

```
POST http://localhost:8180/realms/art-platform/protocol/openid-connect/token
Content-Type: application/x-www-form-urlencoded

client_id=art-platform-client
username=mario.rossi@test.it
password=Password123!
grant_type=password
```

Restituisce un `access_token` JWT da usare nelle richieste successive.

### Richieste autenticate

Aggiungi l'header ad ogni richiesta protetta:

```
Authorization: Bearer <access_token>
```