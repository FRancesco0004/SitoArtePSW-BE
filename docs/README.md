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

**Builder** — Invece di un costruttore con dieci parametri che nessuno ricorda in quale ordine vadano, usiamo il Builder attraverso la notazione `@Builder` di Lombok.

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

## Requisiti

- Java 17 o superiore
- Maven 3.8 o superiore
- Docker e Docker Compose

---

## Setup e Avvio con Docker

L'intera infrastruttura del progetto (Database MySQL, Server Keycloak e Backend Spring Boot) è containerizzata e gestita tramite Docker Compose. Non è necessario installare o avviare alcun servizio manualmente.

### 1. Variabili d'ambiente (File `.env`)
Prima di avviare il progetto, assicurati di avere un file chiamato esattamente `.env` nella directory principale (root) del progetto, allo stesso livello del file `docker-compose.yml`.

Il file deve contenere queste variabili essenziali per il routing e la sicurezza:

```env
DB_CONTAINER_PORT=3306
DB_IMAGE=mysql:8.0
DB_LOCAL_PORT=3307
DB_PASSWORD=1234
DB_ROOT_PASSWORD=1234
DB_USERNAME=art_admin
KEYCLOAK_ADMIN_PASSWORD=admin
KEYCLOAK_ADMIN_USERNAME=admin
KEYCLOAK_IMAGE=quay.io/keycloak/keycloak:latest
KEYCLOAK_LOCAL_PORT=8180
```

### 2. Attivazione del demone Docker (Solo Linux)
Se stai utilizzando un ambiente Linux, assicurati che il motore di Docker sia in esecuzione in background prima di procedere:
```bash
sudo systemctl start docker
```
*(Opzionale)* Per far sì che Docker si avvii automaticamente all'accensione del PC, esegui anche: `sudo systemctl enable docker`.

### 3. Compilazione del Backend
Docker non compila il codice Java, ma impacchetta l'eseguibile. Pertanto, **ogni volta che modifichi il codice sorgente Java**, devi generare un nuovo file `.jar` prima di avviare i container:

```bash
./mvnw clean package -DskipTests
```
> **Quando farlo?** Solo dopo aver fatto modifiche al codice tramite il tuo IDE (es. IntelliJ). Se devi solo accendere l'infrastruttura per testare le API e non hai toccato il codice, puoi saltare questo passaggio.

### 4. Avvio dell'ambiente
Per far partire simultaneamente Database, Keycloak e Backend in background, esegui:

```bash
sudo docker compose up -d --build
```
Una volta avviato, i servizi saranno raggiungibili a questi indirizzi:
- **Backend (API):** `http://localhost:8080`
- **Keycloak (Console Admin):** `http://localhost:8180`
- **MySQL:** `localhost:3307`

---

## Configurazione di Keycloak (Solo al primo avvio)
Dato che il container di Keycloak parte da zero, è necessario configurare il Realm, il Client e i Ruoli. Grazie ai volumi Docker, **questa operazione va fatta solo la prima volta**. Invece di installare Keycloak sul PC, eseguiremo lo script direttamente all'interno del container in esecuzione.

**1. Entra nel terminale del container di Keycloak:**
```bash
sudo docker exec -it app_keycloak bash
cd /opt/keycloak/bin
```

**2. Autenticati internamente:**
*(Nota: internamente al container, Keycloak risponde sulla porta 8080)*
```bash
./kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user admin \
  --password admin
```

**3. Crea il realm e il client:**
```bash
./kcadm.sh create realms \
  -s realm=art-platform \
  -s enabled=true

./kcadm.sh create clients \
  -r art-platform \
  -s clientId=art-platform-client \
  -s enabled=true \
  -s publicClient=true \
  -s directAccessGrantsEnabled=true \
  -s 'redirectUris=["http://localhost:8080/*"]' \
  -s 'webOrigins=["http://localhost:8080"]'
```

**4. Crea i ruoli della piattaforma:**
```bash
./kcadm.sh create roles -r art-platform -s name=USER
./kcadm.sh create roles -r art-platform -s name=USER_VERIFICATO
```

**5. Imposta il Frontend URL del realm:**
Nelle impostazioni generali del realm `art-platform`, imposta il campo **Frontend URL** al seguente valore:
```
http://keycloak:8080
```
Questo consente al backend, che gira all'interno della rete Docker, di raggiungere Keycloak usando il nome del container anziché `localhost`.

*(Digita `exit` per uscire dal terminale del container e tornare al tuo PC).*

### Assegnare un ruolo a un utente
Dopo che un utente si è registrato, puoi assegnargli un ruolo ripetendo l'accesso al container (`sudo docker exec -it app_keycloak bash`) ed eseguendo:

```bash
# Per un utente standard:
./opt/keycloak/bin/kcadm.sh add-roles -r art-platform --uusername email@example.com --rolename USER

# Per un utente verificato:
./opt/keycloak/bin/kcadm.sh add-roles -r art-platform --uusername email@example.com --rolename USER_VERIFICATO
```

---

## Spegnimento dell'ambiente
Per fermare l'applicazione e liberare la memoria del sistema, posizionati nella cartella del progetto ed esegui:

```bash
sudo docker compose down
```
> I dati del database e tutte le configurazioni di Keycloak sono salvati in **volumi permanenti** (`db_data` e `keycloak_data`) e non andranno persi allo spegnimento. Al prossimo `docker compose up -d`, la piattaforma sarà già pronta all'uso!

---

## Risoluzione dei problemi (Troubleshooting)

**Errore 500 (Internal Server Error) in fase di registrazione**
Se chiamando l'endpoint `/api/utenti/registra` ricevi un errore 500, è molto probabile che il Backend non riesca a comunicare con Keycloak.
Questo accade quasi sempre se:
1. Hai dimenticato di eseguire lo script di configurazione iniziale di Keycloak descritto sopra (il realm `art-platform` non esiste).
2. Le credenziali di Keycloak nel file `.env` non corrispondono a quelle inserite nel `docker-compose.yml`.

Per capire esattamente cosa sta bloccando il backend, leggi gli ultimi 100 log con la relativa *stack trace* usando il comando:
```bash
sudo docker compose logs --tail=100 backend
```