## Come è costruita l'applicazione

Il progetto è sviluppato in Spring Boot e segue un'architettura a livelli classica: ogni parte ha il suo ruolo preciso e non si intromette nel lavoro delle altre.

Client ↔ Controller ↔ Service ↔ Repository ↔ Database

Scendendo nel dettaglio: le Entity sono gli oggetti che vivono nel database; il Repository ci parla direttamente; nel Service risiede tutta la logica di business; il Controller espone le API REST verso l'esterno.

---

## Le scelte di design (e perché le abbiamo fatte)

Per tenere il codice pulito, scalabile ed evolvibile abbiamo adottato alcuni Design Pattern classici della *Gang of Four*.

### Come creiamo gli oggetti

Per la creazione degli oggetti usiamo il Factory Method: quando si aggiunge un'opera, la Factory decide quale istanziare in base al tipo ricevuto, rappresentato da un'enum (`TipoOpera.DIPINTO` / `TipoOpera.SCULTURA`). Per i costruttori invece usiamo il Builder tramite la notazione `@Builder` di Lombok, perché nessuno ricorda mai in quale ordine vadano dieci parametri.

### Come colleghiamo i pezzi

La comunicazione tra Frontend e Backend passa per i DTO (Data Transfer Object). Abbiamo usato anche la Facade per astrarre tutto il processo di controllo del prezzo e acquisto; e il Proxy in due modi: il "Protection Proxy" (tramite Spring Security) controlla che solo gli utenti verificati possano mettere in vendita un'opera, mentre il "Virtual Proxy" fa sì che le immagini vengano caricate solo quando la vista le richiede davvero.

### Come gestiamo i comportamenti

Al checkout l'utente può pagare con PayPal, Revolut, P2P o Bonifico: i metodi nelle classi sono gli stessi, cambia solo l'API del servizio di pagamento, e qui entra in gioco il Template Method. Le opere hanno poi una loro "vita": nascono `DISPONIBILE`,e arrivano a `VENDUTO`, passando per `RITIRATO` se viene annullata l'operazione di vendita; il pattern State gestisce questa progressione.

Ogni acquisto e ogni vendita viene incapsulato in un oggetto Command (`CompraCMD`, `VendeCMD`), il che ci dà due cose: uno storico completo delle transazioni e la possibilità di annullare un'operazione recente. Infine, quando un acquisto va a buon fine, il servizio di pagamento lancia una notifica asincrona a tutti gli "osservatori" registrati tramite il pattern Observer: l'acquirente riceve la ricevuta via email, il venditore viene avvisato.

---

## Requisiti

- Java 17 o superiore
- Maven 3.8 o superiore
- Docker e Docker Compose

---

## Setup e avvio con Docker

L'intera infrastruttura (Database MySQL, Server Keycloak e Backend Spring Boot) è containerizzata e gestita tramite Docker Compose, quindi non serve installare o avviare nulla a mano.

### 1. Variabili d'ambiente (file `.env`)

Prima di avviare il progetto, assicurati di avere un file chiamato esattamente `.env` nella directory principale del progetto, allo stesso livello del `docker-compose.yml`. Deve contenere queste variabili:

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

### 2. Attivazione del demone Docker (solo Linux)

Se stai usando Linux, assicurati che il motore di Docker sia in esecuzione prima di procedere:

```bash
sudo systemctl start docker
```

Se vuoi che Docker parta automaticamente all'accensione del PC, puoi eseguire anche `sudo systemctl enable docker`.

### 3. Compilazione del backend

Docker non compila il codice Java, ma impacchetta l'eseguibile; quindi ogni volta che modifichi il sorgente devi generare un nuovo `.jar` prima di avviare i container:

```bash
./mvnw clean package -DskipTests
```

Se devi solo accendere l'infrastruttura per testare le API e non hai toccato il codice, puoi saltare questo passaggio.

### 4. Avvio dell'ambiente

Per far partire Database, Keycloak e Backend tutti insieme in background:

```bash
sudo docker compose up -d --build
```

Una volta avviato, i servizi saranno raggiungibili a questi indirizzi:
- Backend (API): `http://localhost:8080`
- Keycloak (Console Admin): `http://localhost:8180`
- MySQL: `localhost:3307`

---

## Configurazione di Keycloak (solo al primo avvio)

Il container di Keycloak parte da zero, quindi bisogna configurare il Realm, il Client e i Ruoli. Grazie ai volumi Docker l'operazione va fatta solo la prima volta; eseguiremo lo script direttamente dentro il container in esecuzione, senza installare nulla sul PC.

Entra nel terminale del container:

```bash
sudo docker exec -it app_keycloak bash
cd /opt/keycloak/bin
```

Autenticati internamente (nota: dentro il container Keycloak risponde sulla porta 8080, non 8180):

```bash
./kcadm.sh config credentials \
  --server http://localhost:8080 \
  --realm master \
  --user admin \
  --password admin
```

Crea il realm e il client:

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

Crea i ruoli della piattaforma:

```bash
./kcadm.sh create roles -r art-platform -s name=USER
./kcadm.sh create roles -r art-platform -s name=USER_VERIFICATO
```

Imposta il Frontend URL del realm: nelle impostazioni generali del realm `art-platform`, metti nel campo "Frontend URL" il valore `http://keycloak:8080`. Questo serve al backend, che gira dentro la rete Docker, per raggiungere Keycloak tramite il nome del container invece di `localhost`.

Digita `exit` per uscire dal terminale del container.

### Web origins per il frontend Angular

Per far comunicare il frontend Angular (che gira su `http://localhost:4200`) con Keycloak, devi aggiungere la sua origine alla lista delle Web Origins del client dall'interfaccia grafica. Apri `http://localhost:8180/admin`, accedi con le credenziali admin, seleziona il realm `art-platform`, vai su Clients e clicca su `art-platform-client`; nella scheda Settings trova il campo "Web origins" e aggiungi `http://localhost:4200` (oltre a quella già presente `http://keycloak:8080`). Salva. Senza questa configurazione le richieste dal frontend verranno bloccate dal browser con un errore CORS.

### Assegnare un ruolo a un utente

Dopo che un utente si è registrato, puoi assegnargli un ruolo rientrando nel container (`sudo docker exec -it app_keycloak bash`) ed eseguendo:

```bash
# Per un utente standard:
./opt/keycloak/bin/kcadm.sh add-roles -r art-platform --uusername email@example.com --rolename USER

# Per un utente verificato:
./opt/keycloak/bin/kcadm.sh add-roles -r art-platform --uusername email@example.com --rolename USER_VERIFICATO
```

---

## Spegnimento dell'ambiente

Per fermare l'applicazione e liberare la memoria, posizionati nella cartella del progetto ed esegui:

```bash
sudo docker compose down
```

I dati del database e le configurazioni di Keycloak sono salvati in volumi permanenti (`db_data` e `keycloak_data`) e non vanno persi allo spegnimento; al prossimo `docker compose up -d` la piattaforma sarà già pronta.

---

## Risoluzione dei problemi

Se chiamando l'endpoint `/api/utenti/registra` ricevi un errore 500, quasi certamente il backend non riesce a comunicare con Keycloak. Le cause più comuni sono due: hai dimenticato lo script di configurazione iniziale (il realm `art-platform` non esiste ancora), oppure le credenziali nel file `.env` non corrispondono a quelle nel `docker-compose.yml`. Per capire cosa sta bloccando esattamente il backend, leggi gli ultimi 100 log con la relativa stack trace:

```bash
sudo docker compose logs --tail=100 backend
```
