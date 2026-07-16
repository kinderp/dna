# Scenario: chat while an external navigator is foreground

id: `lab.conversation.external-navigation.v0`
status: `stable-doc`
scenario kind: `conversation-automotive`

## Learning goal

Capire perché chat e navigazione sono subsystem separati e come un messaggio
rimane attivo con Waze o Google Maps in primo piano.

## User story

Waze è aperto. Arriva un messaggio da un compagno di strada. Il conducente lo
ascolta e detta una risposta senza Travel DNA che rubi il focus.

## Fixture

- active trip;
- external navigation mode;
- driver role;
- incoming message at virtual t=30s;
- network loss after reply;
- network restore at t=60s.

## Trigger

Push/live signal per conversation ID.

## Expected evidence

1. messaggio recuperato e salvato localmente;
2. driving policy valutata;
3. notification/voice surface pubblicata;
4. reply dettata;
5. operation salvata in outbox;
6. stato queued durante offline;
7. invio dopo reconnect;
8. external navigator remains foreground.

## Tracepoints

```text
MESSAGE_DURABLY_RECEIVED
DRIVE_POLICY_EVALUATED
DRIVER_SAFE_MESSAGE_PUBLISHED
VOICE_REPLY_QUEUED
```

## Module path target

```text
PushSignalAdapter
-> ConversationSyncUseCase
-> LocalConversationStore
-> DriveInteractionPolicy
-> CarConversationSurfacePort
-> VoiceReplyHandler
-> OutboxStore
-> ConversationTransportPort
```

## State changes

```text
incoming: SIGNAL -> STORED -> PRESENTED
outgoing: DRAFT -> QUEUED -> FAILED_RETRYABLE -> SENDING -> ACCEPTED
```

## Performance properties

- no navigation runtime lock;
- local store update before presentation;
- no message body in verbose logs;
- UI response within chat delivery budget.

## Safety properties

- no manual text typing in driver mode;
- critical maneuver can defer read-aloud;
- user can mute/stop;
- passenger mode is separate.

## Missing tests

- Android Auto MessagingStyle integration;
- CarPlay/SiriKit integration;
- process death after queue;
- duplicate push;
- blocked sender;
- message priority vs maneuver.

## Non-goals

- full Waze API integration;
- arbitrary split-screen layout;
- end-to-end encryption v0;
- attachment sharing while driving.
