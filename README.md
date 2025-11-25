# Assignment 2: Task Manager CLI

## Membri del Gruppo

- Ethan Gabriel Leskovec - 886040 - e.leskovec@campus.unimib.it
- Riccardo Pretali - 870452 -  r.pretali@campus.unimib.it

## Link

- **Repository**: https://github.com/rPretali/task-manager-cli

---

## Report del Progresso

### Descrizione del Progetto

Task Manager CLI è un'applicazione Java a riga di comando che consente agli utenti di gestire attività in modo efficiente. L'applicazione implementa operazioni CRUD (Create, Read, Update, Delete) per la gestione delle attività, permettendo agli utenti di creare nuove attività, visualizzarle, modificarle ed eliminarle tramite interfaccia da riga di comando.

### Tecnologie Utilizzate

- **Linguaggio**: Java 17
- **Build Tool**: Maven 3.9.6
- **Testing Framework**: JUnit 5 (Jupiter)
- **CI/CD**: GitLab CI/CD
- **Analisi Statica**: Checkstyle, SpotBugs
- **Documentazione**: Javadoc

---

## Pipeline CI/CD - Descrizione degli Stage

La pipeline è strutturata in 6 stage principali, come richiesto dall'assignment. Ogni stage è descritto di seguito:

### **Stage Build**

**Comando**: `mvn clean compile`

Lo stage di build è responsabile della compilazione del codice sorgente e della risoluzione di tutte le dipendenze Maven specificate nel `pom.xml`. 

**Dettagli implementativi**:
- Utilizza Maven 3.9.6 con Eclipse Temurin JDK 17
- Effettua la pulizia della directory `target/` prima della compilazione
- Crea una cache locale del repository Maven (`.m2/repository/`) per velocizzare i build successivi
- Genera gli artefatti compilati che verranno utilizzati dai stage successivi
- Expire time: 1 giorno

### **Stage Verify**

Lo stage di verifica è diviso in fase di verifica statica e dinamica, eseguite in parallelo per ottimizzare i tempi di esecuzione.

#### **Verifica Statica - Checkstyle**

**Comando**: `mvn checkstyle:check`

Checkstyle esegue un'analisi statica dello stile del codice sorgente secondo standard di codifica predefiniti. Questo job verifica la conformità del codice a convenzioni di stile (indentazione, naming, lunghezza delle righe, etc.).

**Configurazione**:
- `allow_failure: true` - Le violazioni di stile non bloccano la pipeline ma vengono riportate per revisione
- Artefatti: `target/checkstyle-result.xml` (70 giorni)

#### **Verifica Statica - SpotBugs**

**Comando**: `mvn spotbugs:check`

SpotBugs esegue un'analisi statica del bytecode compilato alla ricerca di potenziali bug e pattern di codice pericolosi. Questo strumento identifica anomalie nel codice che potrebbero causare errori runtime.

**Configurazione**:
- `allow_failure: true` - I risultati vengono utilizzati come feedback di qualità senza bloccare la pipeline
- Artefatti: `target/spotbugsXml.xml` (7 giorni)

#### **Verifica Dinamica - Unit Testing (verify-tests)**

**Comando**: `mvn test`

Durante lo stage di verifica viene eseguita anche la suite completa di unit test utilizzando JUnit 5 (Jupiter). Questo fornisce una prima esecuzione dinamica del codice in parallelo con le analisi statiche.

**Configurazione**:
- Esecuzione dei test nella cartella `src/test/java/`
- Generazione di report JUnit in formato XML
- Artefatti: Report JUnit e cartella `target/surefire-reports/` (30 giorni)

### **Stage Test**

**Comando**: `mvn test`

Lo stage di test dedicato esegue la suite completa di unit test come richiesto dall'assignment. Questo stage fornisce una seconda esecuzione dei test per garantire la stabilità e la replicabilità dei risultati.

**Configurazione**:
- Esposizione automatica dei report JUnit nel formato XML (`.xml`)
- Artefatti: Report JUnit completi (30 giorni)
- I report sono visualizzabili direttamente nell'interfaccia GitLab

**Dettagli sui test**:
Il progetto include test unitari che verificano:
- Operazioni di creazione delle attività
- Operazioni di lettura e recupero delle attività
- Operazioni di aggiornamento delle attività
- Operazioni di eliminazione delle attività

### **Stage Package**

**Comando**: `mvn package -DskipTests`

Lo stage di package genera un file JAR eseguibile pronto per la distribuzione. Poiché i test sono già stati eseguiti negli stage precedenti (verify e test), in questo stage vengono saltati per ottimizzare i tempi.

**Configurazione**:
- Skippa l'esecuzione dei test (`-DskipTests`)
- Genera il JAR finale nella cartella `target/`
- Nome artefatto: `task-manager-cli-${version}.jar`
- Expire time: 90 giorni
- Nome con commit SHA: `task-manager-cli-${CI_COMMIT_SHORT_SHA}`
- Esecuzione limitata al branch `main`

**Output**:
Il JAR generato è un artefatto distributibile che può essere:
- Scaricato da GitLab per l'esecuzione locale
- Utilizzato per il deployment in ambienti production
- Utilizzato come base per la creazione di immagini Docker (implementazione futura)

### **Stage Release**

**Comando**: Placeholder per build Docker e push su GitLab Container Registry

Lo stage di release è attualmente configurato come placeholder per future implementazioni. Rappresenta il punto in cui il pacchetto JAR verrebbe containerizzato in un'immagine Docker e pubblicato su GitLab Container Registry.

**Configurazione attuale**:
- `when: manual` - L'esecuzione è manuale per evitare release accidentali
- `allow_failure: true` - Le eventuali fallimenti non bloccano la pipeline
- Esecuzione limitata al branch `main`

**Implementazione futura**:
- Build di un'immagine Docker basata su JDK 17
- Tag dell'immagine con il numero di versione
- Push dell'immagine su GitLab Container Registry per deployment successivo

### **Stage Docs**

**Comando**: `mvn javadoc:javadoc`

Lo stage di documentazione genera automaticamente la documentazione API in formato HTML a partire dalle javadoc inserite nel codice sorgente. La documentazione viene pubblicata automaticamente su GitLab Pages.

**Configurazione**:
- Genera Javadoc nella cartella `target/site/apidocs/`
- Sposta la cartella `apidocs/` in `public/` (come richiesto da GitLab Pages)
- Artefatti: Cartella `public/` completa di HTML (30 giorni)
- Esecuzione limitata al branch `main`

**Output**:
La documentazione generata include:
- Descrizione delle classi principali
- Metodi pubblici con relative javadoc
- Parametri e tipi di ritorno
- Struttura del package e delle dipendenze

---

## Struttura della Pipeline

La pipeline è strutturata come segue:

```
COMMIT → BUILD → VERIFY (parallel)      → TEST    → PACKAGE → RELEASE (manual) → DOCS
              ├─ Checkstyle
              ├─ SpotBugs
              └─ verify-tests
```

---

## Configurazione del Progetto (pom.xml)

Il file `pom.xml` specifica:

```xml
<groupId>it.unimib</groupId>
<artifactId>task-manager-cli</artifactId>
<version>1.0.0</version>
```

**Proprietà**:
- Java source/target: 17
- Encoding: UTF-8
- JUnit version: 5.10.0

**Plugin configurati**:
- **maven-surefire-plugin** (v3.5.4): Esecuzione dei test JUnit
- **maven-checkstyle-plugin** (v3.6.0): Analisi dello stile del codice
- **spotbugs-maven-plugin** (v4.9.8.0): Rilevamento di bug nel bytecode
- **maven-javadoc-plugin** (v3.6.3): Generazione della documentazione

---

## Accesso ai Risultati della Pipeline

### Report JUnit
I report dei test sono disponibili in:
- **Pipeline Details** → **Test Reports** (visualizzazione integrata in GitLab)
- **Artifacts** → `target/surefire-reports/TEST-*.xml`

### Artefatti di Build
Tutti gli artefatti generati (JAR, report XML, documentazione) sono scaricabili da:
- **Pipeline** → **Artifacts** browser

### Documentazione Javadoc
La documentazione è disponibile tramite:
- **GitLab Pages** (URL fornito nel link sopra)
- O scaricando l'artefatto `public/` dalla pipeline

### Report di Analisi
- **Checkstyle**: `target/checkstyle-result.xml`
- **SpotBugs**: `target/spotbugsXml.xml`

---

## Esecuzione Locale

### Build locale
```bash
mvn clean compile
```

### Test locale
```bash
mvn test
```

### Verifiche di qualità locali
```bash
mvn checkstyle:check
mvn spotbugs:check
```

### Package locale
```bash
mvn package
```

### Esecuzione dell'applicazione
```bash
java -jar target/task-manager-cli-1.0.0.jar
```

### Generazione documentazione locale
```bash
mvn javadoc:javadoc
open target/site/apidocs/index.html
```

---

## Note Implementative

1. **Caching Maven**: Il caching locale accelera significativamente i build successivi riducendo i tempi di download delle dipendenze
2. **Parallelizzazione**: Lo stage Verify esegue checkstyle, spotbugs e test in parallelo per ottimizzare i tempi totali
3. **Quality Gates non bloccanti**: Checkstyle e SpotBugs sono configurati con `allow_failure: true` per fornire feedback senza impedire il progresso della pipeline
4. **Reproducibilità**: Tutti i plugin hanno versioni specificate per garantire risultati reproducibili
5. **Documentazione automatica**: Javadoc è generato e pubblicato automaticamente ad ogni commit su main

---

## Risorse Esterne

- [Maven Documentation](https://maven.apache.org/guides/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/)
- [SpotBugs Maven Plugin](https://spotbugs.readthedocs.io/en/latest/maven.html)
- [Javadoc Plugin](https://maven.apache.org/plugins/maven-javadoc-plugin/)