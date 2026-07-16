# Roadmap dello spike LoRa e comunicazioni alternative

## Stato

`open — nessuna decisione architetturale`

Questo documento prepara la prossima discussione. Non conclude che LoRa o
LoRaWAN siano utili a Travel DNA.

## Prima distinzione da studiare

La discussione dovrà separare:

- LoRa: modulazione radio;
- LoRaWAN: protocollo di rete con gateway e network server;
- peer-to-peer LoRa proprietario;
- mesh radio;
- Bluetooth LE;
- Wi-Fi Direct/Nearby;
- satellite/emergency messaging;
- cellular data.

Usare “LoRa” come sinonimo di messaggistica tra telefoni è un'ipotesi da non accettare senza verifica. Lo spike deve stabilire se i dispositivi
target espongono hardware e API utilizzabili oppure se serve un accessorio o
un'infrastruttura esterna.

## Domanda di prodotto

Quale problema concreto risolve?

Candidati:

- piccoli messaggi tra veicoli in aree senza copertura;
- beacon in campeggi o sentieri;
- dispositivi esterni dedicati;
- infrastruttura partner nelle aree di sosta;
- safety check-in;
- scambio Store-and-Forward.

## Domande tecniche

- hardware necessario;
- smartphone diretto o accessorio;
- raggio reale in auto/città/montagna;
- velocità e payload;
- duty cycle e normative europee;
- latenza;
- collisioni;
- identità e pairing;
- cifratura;
- aggiornamenti firmware;
- consumo;
- costo;
- gateway;
- interoperabilità;
- affidabilità in movimento;
- antenna e installazione.

## Domande di sicurezza e privacy

- tracking radio;
- replay;
- spoofing;
- jamming;
- metadata;
- chiavi;
- revoca;
- broadcast non desiderato;
- abuso/spam;
- responsabilità safety.

## Architettura possibile da valutare

Solo come ipotesi:

```text
Travel DNA mobile
-> Bluetooth to external LoRa accessory
-> LoRa small encrypted envelope
-> receiving accessory
-> Bluetooth to peer mobile
-> Travel DNA sync when possible
```

Oppure:

```text
mobile/backend
-> LoRaWAN gateway infrastructure
-> network server
-> application server
```

Sono architetture molto diverse.

## Payload candidato

Mai chat completa o fotografie. Possibili piccoli envelope:

```text
ephemeral sender id
message type
short ciphertext
sequence/replay protection
expiry
coarse context
```

## Criteri di successo dello spike

- caso d'uso irraggiungibile bene con tecnologie più semplici;
- prototipo hardware reale;
- test range/mobility;
- legal/regulatory review;
- threat model;
- payload and latency measurements;
- cost per user/infrastructure;
- adapter contract;
- degraded behavior.

## Alternative da confrontare

- delayed cellular sync;
- Bluetooth at stops;
- QR/NFC;
- Nearby Connections;
- Wi-Fi Aware/Direct;
- accessory satellite;
- no real-time: store for next connectivity.

## Output della futura discussione

1. problem statement;
2. technology primer;
3. experiment plan;
4. measured report;
5. ADR accept/reject/defer;
6. update technology matrix.

## Non-obiettivi ora

- acquistare hardware;
- scegliere frequenze;
- progettare protocollo crittografico;
- promettere messaggi a lungo raggio;
- inserire LoRa nell'MVP.
