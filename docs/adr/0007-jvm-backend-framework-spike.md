# ADR-0007 — Spike per la scelta del framework backend JVM

- Status: Proposed
- Date: 2026-07-16

## Contesto

Il backend iniziale sarà un monolite modulare JVM. Restano due candidati
principali:

- Java 21 con Spring Boot;
- Kotlin/JVM con Ktor.

La scelta ha valore didattico e operativo e non deve essere presa solo per
preferenza personale.

## Domande dello spike

- quale struttura rende più visibili i bounded context?
- quanto è semplice spiegare HTTP, WebSocket, auth e persistence agli studenti?
- quale integrazione PostGIS e migration tooling è più chiara?
- quale costo hanno startup, memoria e build?
- quanto è naturale condividere schemi senza condividere internals?
- come si testano moduli e boundary?
- quale ecosistema offre osservabilità, sicurezza e manutenzione migliori per il
  team reale?

## Esperimento minimo

Implementare lo stesso piccolo servizio in entrambi:

```text
POST /v0/trips
GET  /v0/trips/{id}
WebSocket /v0/conversations/{id}
PostgreSQL test container
OpenTelemetry trace
```

Misurare e documentare:

- righe e concetti necessari;
- tempi build/startup;
- memoria idle;
- chiarezza dei test;
- error model;
- difficoltà percepita da un gruppo di studenti.

## Decisione

Nessuna scelta definitiva finché lo spike non è completato. Il dominio e gli
schemi non devono dipendere dal framework nel frattempo.
