# README - Services

Questo documento descrive i Service da implementare nel progetto.

---

# Service previsti


- `UtenteService`
- `UtenteVerificatoService`
- `AutoreService`
- `OggettoService`
- `ValutazioneService`
- `PagamentoService`
- `AzioneService`
- `AcquistoService`
- `NotificaService`
- `AcquistoFacade`

---
# UtenteService

`UtenteService` gestisce le operazioni relative agli utenti standard.

Entity collegata:

`Utente`

Repository collegato:

`UtenteRepository`


## Metodi da implementare

### `registraUtente(RegistrazioneRequest request`

Registra un nuovo utente.

### `loginUtente(LoginRequest request`

Restituisce il token generato con JWT


### `getUtenteById(Integer id)`

Recupera un utente tramite il suo id.


### `getProfiloUtente(String email)`

Recupera un utente tramite la sua email.


### `existsByEmail(String email)`

Controlla se esiste già un utente con una determinata email.


---

# UtenteVerificatoService

`UtenteVerificatoService` gestisce gli utenti verificati.

Un utente verificato è un utente che può mettere opere in vendita.

Entity collegata:

`UtenteVerificato`

Repository collegato:

`UtenteVerificatoRepository`


## Metodi da implementare

### `isVerificato(Integer utenteId)`

Controlla se un utente è verificato.



### `getUtenteVerificatoById(Integer utenteId)`

Recupera un utente verificato tramite id.



### `promuoviUtenteAVerificato(Integer utenteId, String titolo)`

Trasforma un utente standard in utente verificato.

---

# AutoreService

`AutoreService` gestisce gli autori delle opere.

Entity collegata:

`Autore`

Repository collegato:

`AutoreRepository`


## Metodi da implementare

### `creaAutore(AutoreRequestDTO request)`

Crea un nuovo autore.



### `getAutoreById(Integer id)`

Recupera un autore tramite id.


### `getTuttiGliAutori()`

Restituisce tutti gli autori presenti nel sistema.


### `getAutoriByNome(String nome)`

Recupera gli autori filtrando per nome.

### `getAutoriByCognome(String cognome)`

Recupera gli autori filtrando per cognome.


### `collegaAutoreAUtenteVerificato(Integer autoreId, Integer utenteId)`

Collega un autore a un utente verificato.

---
# OggettoService

`OggettoService` gestisce le opere d'arte presenti nella piattaforma.

Entity collegata:

`Oggetto`

Repository collegato:

`OggettoRepository`



## Metodi da implementare

### `creaOggetto(OggettoRequestDTO request, Integer utenteId)`

Crea una nuova opera d'arte.



### `getOggettoById(Integer id)`

Recupera il dettaglio di una singola opera.


### `getTuttiGliOggetti()`

Restituisce tutte le opere presenti nel database.



### `getOggettiDisponibili()`

Restituisce solo le opere disponibili.



### `getOggettiByTipo(TipoOpera tipoOpera)`

Restituisce le opere filtrate per tipo.


### `getOggettiDisponibiliByTipo(TipoOpera tipoOpera)`

Restituisce le opere disponibili filtrate anche per tipo.

### `getOggettiByAutore(Integer autoreId)`

Restituisce tutte le opere di un determinato autore.

### `getOggettoRandomDisponibile()`

Restituisce un'opera disponibile casuale.

### `cambiaStato(Integer oggettoId, StatoOggetto nuovoStato)`

Modifica lo stato di un'opera.

Stati previsti (Enum):

- `DISPONIBILE`
- `IN_VALUTAZIONE`
- `VENDUTO`

### `eliminaOggetto(Integer oggettoId, Integer utenteId)`

Elimina o rimuove un'opera.

---

# ValutazioneService

`ValutazioneService` gestisce la valutazione del prezzo stimato dall'utente.

## Metodi da implementare

### `verificaValutazione(BigDecimal costoReale, BigDecimal prezzoStimato)`

Controlla se il prezzo stimato dall'utente è abbastanza vicino al valore reale.

### `calcolaRangeValido(BigDecimal costoReale)`

Calcola il range di prezzo valido per acquistare un'opera.

---

# PagamentoService

`PagamentoService` gestisce il pagamento.

## Metodi da implementare

### `paga(BigDecimal importo, MetodoPagamento metodoPagamento)`

Esegue o simula il pagamento.


### `selezionaStrategia(MetodoPagamento metodoPagamento)`

Seleziona la strategia di pagamento corretta.

Strategie previste (Enum):

- PayPal
- bonifico
- P2P
- Revolut

---

# AzioneService

`AzioneService` gestisce lo storico delle azioni.

Entity collegata:

`Azione`


## Metodi da implementare

### `creaAzioneAcquisto(Utente utente, Oggetto oggetto, MetodoPagamento metodoPagamento)`

Crea una nuova azione di acquisto.


### `creaAzioneVendita(Utente utente, Oggetto oggetto, MetodoPagamento metodoPagamento)`

Crea una nuova azione di vendita.


### `getAzioniByUtente(Integer utenteId)`

Restituisce tutte le azioni effettuate da un utente.


### `getAzioniByOggetto(Integer oggettoId)`

Restituisce tutte le azioni collegate a una determinata opera.


### `getAzioniByTipo(TipoAzione tipoAzione)`

Restituisce tutte le azioni di un certo tipo.


### `getAzioniAnnullate()`

Restituisce tutte le azioni annullate.


### `annullaAzione(Integer azioneId, Integer utenteId)`

Annulla un'azione, se possibile.

---

# AcquistoService

`AcquistoService` gestisce il tentativo di acquisto di un'opera.


## Metodi da implementare

### `acquistaOggetto(AcquistoRequestDTO request, Integer utenteId)`

Gestisce il processo di acquisto di un'opera.


### `prezzoAccettabile(BigDecimal costoReale, BigDecimal prezzoStimato)`

Controlla se il prezzo stimato rientra nel range accettabile.

---

# NotificaService

`NotificaService` gestisce le notifiche agli utenti.


## Metodi da implementare

### `notificaAcquistoRiuscito(Utente acquirente, Oggetto oggetto)`

Invia una notifica all'acquirente quando l'acquisto va a buon fine.


### `notificaVenditore(Utente venditore, Oggetto oggetto)`

Invia una notifica al venditore quando una sua opera viene venduta.

---

# AcquistoFacade

`AcquistoFacade` coordina il processo completo di acquisto.


## Metodi da implementare

### `tentaAcquisto(AcquistoRequestDTO request, Integer utenteId)`

Coordina l'intero processo di acquisto.
