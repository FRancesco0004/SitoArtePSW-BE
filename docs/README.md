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

**DTO (Data Transfer Object)** — Per la comunicazione tra Frontend e Backend

**Facade** — La usiamo così da poter astrarre tutto il processo di controllo del prezzo corretto ed acquisto-

**Proxy** — Usato in due modi distinti:
- *Protection Proxy* (tramite Spring Security): controlla che solo gli utenti verificati possano mettere in vendita un'opera.

- *Virtual Proxy*: le immagini delle opere vengono caricate solo quando la vista le richiede davvero.

### Come gestiamo i comportamenti

**Template Method** — Al checkout, l'utente può pagare con PayPal,
Revolut, P2P o Bonifico. I metodi nelle classi sono gli stessi,
cambia solo l'API del servizio di pagamento.

**State** — Le opere hanno una vita: nascono `DISPONIBILE`, passano per `IN_VALUTAZIONE` quando qualcuno sta tentando l'acquisto, e arrivano a `VENDUTO`.

**Command** — Ogni acquisto e ogni vendita viene incapsulata in un oggetto Command (`CompraCMD`, `VendeCMD`). Questo ci dà due cose gratis: uno storico completo delle transazioni e la possibilità di annullare un'operazione recente.

**Observer** — Quando un acquisto va a buon fine, il servizio di pagamento lancia una notifica asincrona a tutti gli "osservatori" registrati: l'acquirente riceve la ricevuta via email, il venditore viene avvisato.