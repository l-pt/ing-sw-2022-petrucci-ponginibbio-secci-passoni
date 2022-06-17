# Prova Finale di Ingegneria del Software - AA 2021-2022
![alt text](src/main/resources/Eriantys.jpg)

Il progetto consiste nell'implementazione del gioco da tavolo [ERIANTYS](http://www.craniocreations.it/prodotto/eriantys/) in linguaggio Java. In particore di un sistema distribuito composto da un singolo server in grado di gestire più partite contemporaneamente e multipli client (uno per giocatore) che possono partecipare ad una sola partita alla volta. Sono stati utilizzati il pattern MVC (Model-View-Controller) per progettare l'intero sistema e i socket TCP-IP per la gestione della rete.

#### Regole del gioco:
- [Italiano](src/main/resources/documents/eriantys_regole.pdf)
- [English](src/main/resources/documents/eriantys_rules.pdf)

#### Requisiti del progetto:
- [Requisiti](src/main/resources/documents/requirements.pdf)

## Documentazione

### UML
I seguenti diagrammi delle classi rappresentano rispettivamente il diagramma UML iniziale dell'applicazione e i diagrammi UML finali che mostrano come è stato progettato il software:
- [UML alto livello]()
- [UML basso livello]()

### Peer review
TO DO

### JavaDoc
La seguente documentazione include una descrizione delle classi e dei metodi utilizzati, segue le tecniche di documentazione di Java e può essere consultata al seguente indirizzo: [Javadoc]()

### Test di unità
Al seguente link è possibile consultare il report della coverage dei test effettuati con Junit: [Report]()

### Librerie e Plugins
|Libreria/Plugin|Descrizione|
|---------------|-----------|
|__Maven__|Strumento di automazione della compilazione utilizzato principalmente per progetti Java.|
|__JUnit__|Framework di unit testing.|
|__Swing__|TO DO.|
|__Gson__|Libreria per il supporto al parsing di file in formato json.|
|__TO DO__|TO DO.|

## Funzionalità

### Funzionalità Sviluppate
- __Regole Complete:__ si considerino le regole per lo svolgimento di partite da 2 o da 3 giocatori,
includendo la variante per esperti (si richiede l'implementazione di almeno 8 diverse carte personaggio,
a scelta del gruppo).
- __CLI:__ TO DO
- __GUI:__ TO DO
- __Socket:__ TO DO
- __3 Funzionalità Avanzate:__
    - __Carte personaggio:__ Implementazione di tutte e 12 le carte personaggio.
    - __Partita a 4 giocatori:__ Implementazione delle partite a 2, 3 o 4 giocatori.
    - __Partite multiple:__ Realizzazione del server in modo che possa gestire più partite contemporaneamente.

## Compilazione e packaging
TO DO

### Jars
TO DO

## Esecuzione
TO DO

### Client
Il client viene eseguito scegliendo l'interfaccia con cui giocare, le possibili scelte sono da linea di comando (CLI) o interfaccia grafica (GUI).

- #### CLI
Per una migliore esperienza di gioco da linea di comando è necessario lanciare il client con un terminale che supporti la codifica UTF-8 e gli ANSI escape codes. Per lanciare il client in modalità CLI digitare il seguente comando:
```
java -jar clientcli.jar
```

- #### GUI
TO DO
```
TO DO
```

### Server
TO DO
```
TO DO
```

## Componenti del gruppo
- [__Luca Petrucci__](https://github.com/l-pt)
- [__Simone Ponginibbio__](https://github.com/simonepongi)
- [__Paolo Secci__](https://github.com/paolosecci)
- [__Matteo Passoni__](https://github.com/matteopassoni)
