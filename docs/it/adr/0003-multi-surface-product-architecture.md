# ADR-0003 — Un ecosistema, più esperienze specializzate

- **Stato:** Proposta
- **Data:** 2026-07-19
- **Decision owner:** DNA Platform

## Contesto

DNA comprende domini diversi e deve supportare smartphone, Android Auto e Android Automotive OS. Una UI universale produrrebbe esperienze non adatte ai diversi dispositivi; applicazioni completamente separate renderebbero invece più difficile condividere identità, mappa, GeoRoom, consenso e casi d'uso cross-domain.

Android for Cars consente soltanto categorie e template specifici, progettati per limitare la distrazione. Android Automotive OS richiede inoltre una build dedicata, pur permettendo di condividere il `CarAppService` e gran parte della logica con Android Auto.

## Decisione

DNA adotterà:

1. una sola app mobile iniziale, modulare e multi-dominio;
2. un design system comune con accenti di dominio;
3. domini rappresentati come lenti e feature module, non come app incollate;
4. una superficie cartografica comune;
5. Android Auto organizzato per compiti di guida, non per domini;
6. un modulo `car-experience` condiviso;
7. una build Android Automotive OS separata;
8. contratti applicativi condivisi e presentazioni specifiche per superficie;
9. categorie Car dichiarate progressivamente e solo per funzionalità reali;
10. possibilità futura di generare app verticali separate dalla stessa codebase.

Lo swipe tra domini può essere una scorciatoia, ma non il solo meccanismo di navigazione.

## Struttura di riferimento

```text
:app-mobile
:app-automotive
:car-experience
:design-system
:core:*
:feature:travel
:feature:shopping
:feature:social
:feature:economy
```

## Conseguenze positive

- esperienza coerente e riconoscibile;
- onboarding e identità unici;
- riuso di mappa, consenso, Commons e stato;
- possibilità di correlazioni cross-domain;
- UI Car conforme e più semplice;
- separazione delle dipendenze di piattaforma;
- possibilità di più APK in futuro senza riscrivere i domini.

## Conseguenze negative

- app mobile più ampia e con navigazione più complessa;
- necessità di governance rigorosa del design system;
- rischio di accoppiamento tra feature module;
- necessità di filtrare con attenzione ciò che viene esposto in auto;
- più build e manifest da mantenere;
- revisione Google Play più delicata quando si aggiungono categorie Car.

## Alternative considerate

### Un APK distinto per ogni dominio fin dall'inizio

Rimandato: aumenta installazioni, autenticazioni, deep link e duplicazione prima che esista evidenza di un vantaggio.

### Una UI identica su telefono e auto

Scartata: non rispetta i vincoli di input, sicurezza e distrazione del contesto automobilistico.

### Un unico menu di domini anche in Android Auto

Scartato: l'utente in auto deve trovare compiti immediati e conformi alle categorie, non esplorare l'organizzazione interna della piattaforma.

### Solo swipe per cambiare dominio

Scartato come navigazione primaria per scarsa visibilità, accessibilità e conflitti gestuali.

## Vincoli

- i moduli di dominio non dipendono da UI specifiche;
- `car-experience` non importa schermate mobile;
- le categorie Android for Cars non vengono usate come scorciatoia per accedere a template;
- funzioni non consentite durante la guida restano sul telefono o in esperienze parked ammesse;
- Economy non viene presentato come app finanziaria Android Auto;
- Shopping in auto è limitato a luoghi, tappe, ritiri e azioni brevi compatibili;
- Social in auto è limitato alle esperienze di comunicazione effettivamente ammesse;
- ogni dominio può essere escluso da una build.

## Piano di validazione

1. costruire il design system e il `DomainSwitcher` mobile;
2. definire la mappa comune con layer Travel e Shopping;
3. implementare `car-experience` inizialmente come NAVIGATION;
4. testare stato condiviso telefono → Android Auto;
5. aggiungere un caso cross-domain ridotto in auto;
6. valutare POI solo con una UX completa;
7. creare il modulo Automotive e riusare il `CarAppService`;
8. verificare i criteri Car App Quality per ogni categoria dichiarata.

## Criteri di riesame

Riesaminare la decisione quando:

- l'app mobile supera limiti misurati di dimensione o complessità;
- un dominio richiede release o requisiti legali indipendenti;
- Google Play impone vincoli incompatibili tra categorie;
- emergono pubblici realmente separati;
- l'esperienza Car richiede un listing o package distinto.
