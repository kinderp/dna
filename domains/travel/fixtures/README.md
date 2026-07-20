# Deterministic fixtures

Questa cartella ospiterà input riproducibili per test, replay e scenari Lab.

## Regola sulla privacy

Non committare direttamente:

- tracce GPS personali;
- indirizzi domestici;
- conversazioni reali;
- fotografie private;
- identificatori di dispositivo o account;
- coordinate precise di alloggi o soste sensibili.

Preferire fixture sintetiche. Quando un problema nasce da un viaggio reale,
ridurre il caso a una traccia artificiale che conserva il comportamento tecnico
ma elimina il percorso personale.

## Sottocartelle

- `gps/`: campioni con timestamp, accuratezza e velocità;
- `routes/`: geometrie, manovre e metadati canonici;
- `osm/`: estratti minimi con licenza e provenance;
- `journeys/`: sequenze di eventi viaggio e diario;
- `conversations/`: messaggi fittizi e stati di consegna.
