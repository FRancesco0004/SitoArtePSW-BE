# MAPPER CHE CI SERVONO

Ci serviranno solamente 3 mapper dal momento che i DTO che dovranno comunicare con le Entity saranno solo:
* (SI) Registrazione Request -> Utente
* (NO) Acquisto Request -> deve comunicare solo con i service
* (NO) Prezzo Ipotizzato Request -> anche lui solo con i service
* (SI) Utente Response -> Utente
* (SI) Oggetto Anteprima e Dettaglio -> Oggetto e Autore
* (SI) Azione Response -> Azione

Inoltre nei mapper ho commentato i mapper espliciti che non utilizzano il plugin MapStruct