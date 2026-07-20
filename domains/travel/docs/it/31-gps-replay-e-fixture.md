# GPS replay e fixture

## Perché il replay viene prima della strada

Il test su strada è costoso, non deterministico e difficile da ripetere. Il replay
permette a studenti e algoritmi diversi di vedere lo stesso input.

## Formato fixture v0

Una fixture contiene metadata e campioni.

```yaml
id: highway-missed-exit-v0
kind: synthetic
coordinate_reference: WGS84
sample_rate_hz: 1
clock: virtual
privacy: public-synthetic
license: CC0-project-generated
ground_truth:
  missed_exit_at_ms: 42000
  off_route_confirmed_between_ms: [45000, 50000]
```

Campione:

```json
{
  "t_ms": 1000,
  "lat": 42.0001,
  "lon": 12.0002,
  "accuracy_m": 5.0,
  "speed_mps": 27.0,
  "bearing_deg": 182.0
}
```

## Clock virtuale

Il runner non usa `sleep` reale. Avanza il tempo e produce eventi. Vantaggi:

- test veloci;
- timing ripetibile;
- accelerazione 1x/10x/100x;
- pause;
- step;
- injection di rete e messaggi.

## Eventi laterali

Una fixture può includere:

```text
network down/up
route provider timeout
message received
photo observed
app background/foreground
GPS permission revoked
vehicle stopped
```

Questo consente scenari cross-domain.

## Ground truth

Non sempre esiste. Distinguere:

- `exact`: scenario sintetico;
- `annotated`: umano ha segnato eventi;
- `expected range`: tolleranza;
- `unknown`: fixture esplorativa.

Un test automatico forte richiede ground truth adeguata.

## Generazione sintetica

Pipeline:

1. route base;
2. campionamento lungo polyline;
3. modello velocità;
4. rumore controllato;
5. accuratezza;
6. deviazione;
7. gap/galleria;
8. expected events.

Seed casuale registrato per riproducibilità.

## Anonimizzazione di tracce reali

Preferire sintetico. Se una traccia reale è necessaria:

- consenso;
- taglio origine/destinazione;
- traslazione o sostituzione percorso;
- rimozione timestamp assoluti;
- rimozione soste sensibili;
- review manuale;
- documento provenance;
- licenza.

Non basta rimuovere il nome: una route casa-lavoro identifica.

## Runner

Comando target futuro:

```text
./tdna replay highway-missed-exit-v0 \
  --engine ferrostar \
  --shadow tdna-guidance \
  --speed 100 \
  --report build/replay/report.json
```

Output:

- trace timeline;
- snapshot;
- expected vs actual;
- provider comparison;
- performance counters;
- errors;
- optional visualization.

## Comparison

Tra engine:

- matched segment;
- progress;
- maneuver;
- off-route time;
- reroute count;
- ETA;
- latency;
- allocation/memory;
- divergence reason.

Non ridurre tutto a “uguale/non uguale”. Due route valide possono differire.

## Golden timeline

Esempio:

```text
0s   NAVIGATION_STARTED
42s  EXIT_PASSED
46s  OFF_ROUTE_SUSPECTED
49s  OFF_ROUTE_CONFIRMED
50s  REROUTE_REQUESTED
53s  ROUTE_REPLACED
```

Tolleranze temporali dichiarate.

## Fixture catalog

Ogni fixture entra in un catalogo con:

- difficoltà;
- feature;
- engine support;
- expected test;
- ultima verifica;
- known limitations.

## Visualizer

Futuro Lab:

- mappa;
- punto raw;
- punto matched;
- route;
- state;
- maneuver;
- timeline;
- grafico latenza.

Il visualizer legge output del runner; non entra nel hot path.

## Scenari v0

1. straight highway;
2. missed exit;
3. noisy parallel road;
4. tunnel gap;
5. stop and resume;
6. external navigator shadow confidence;
7. message during critical maneuver;
8. daily page stop/photo association.

## Errori comuni

- usare tempo reale nei test;
- fixture senza metadata;
- coordinate personali;
- nessuna ground truth;
- soglie hard-coded solo per una route;
- golden troppo preciso per dati rumorosi;
- misurare performance in debug e chiamarla produzione.
