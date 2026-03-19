# Vet-SGBD
Aplicația este un sistem simplu de gestiune pentru o clinică veterinară, construită pe ideea de „master-detail” (părinte-copil). Practic, împarte ecranul în două: în stânga ai o listă cu stăpânii de animale (părinții), iar când dai click pe unul dintre ei, în dreapta îți apar automat doar animalele lui (copiii). Din interfață poți să adaugi un animal nou pentru stăpânul selectat, să îi modifici datele (cum ar fi vârsta) sau să îl ștergi din sistem. Totul este făcut clasic, în Java cu Swing pentru interfață și JDBC pur pentru baza de date




## Cum funcționează baza de date?
Am ales să folosesc **SQLite** pentru că mi s-a părut mult mai practic pentru un proiect desktop. Nu ai nevoie să instalezi sau să rulezi un server separat gen PostgreSQL. 
* Baza de date este un simplu fișier numit `vet.bd`.
* Aplicația este "smart": dacă o rulezi și nu găsește fișierul, îl creează ea automat, face tabelele și bagă câteva date de test ca să ai cu ce să te joci din prima secundă.
*Am lăsat și un fișier `script_creare.sql` în proiect, în caz că vrei să vezi structura exactă sau să o rulezi manual (acolo am inclus și o relație M-N între Medici și Programări, conform cerințelor)[cite: 11, 58].

## Conexiunea (Fără ORM)
Totul e scris "de mână" cu JDBC[cite: 4, 34]. 
* Folosesc driver-ul de SQLite (`org.xerial:sqlite-jdbc`).
* Conexiunea se deschide doar când e nevoie și se închide mereu singură, pentru că am folosit blocuri `try-with-resources` în Java[cite: 31, 32].

## Cum pornești aplicația?
Proiectul e făcut cu Gradle, deci e super simplu[cite: 62]:
1. Deschizi folderul în IntelliJ IDEA.
2. Aștepți să își descarce Gradle dependențele.
3. Mergi în `src/main/java/ro/vet/Main.java` și îi dai **Run**, sau rulezi task-ul `run` din panoul de Gradle.
