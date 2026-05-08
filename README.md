#Vet-SGBD

Aplicatia este un sistem simplu de gestiune pentru o clinica veterinara, construita pe ideea de „master-detail” (parinte-copil). Practic, imparte ecranul in doua: in stanga ai o lista cu stapanii de animale (parintii), iar cand dai click pe unul dintre ei, in dreapta iti apar automat doar animalele lui (copiii). Din interfata poti sa adaugi un animal nou pentru stapanul selectat, sa ii modifici datele (cum ar fi varsta) sau sa il stergi din sistem. Totul este facut clasic, in Java cu Swing pentru interfata si JDBC pur pentru baza de date.

Cum functioneaza baza de date?

Am ales sa folosesc SQLite pentru ca mi s-a parut mult mai practic pentru un proiect desktop. Nu ai nevoie sa instalezi sau sa rulezi un server separat gen PostgreSQL.
Baza de date este un simplu fisier numit vet.bd.
Aplicatia este "smart": daca o rulezi si nu gaseste fisierul, il creeaza ea automat, face tabelele si baga cateva date de test ca sa ai cu ce sa te joci din prima secunda. *Am lasat si un fisier script_creare.sql in proiect, in caz ca vrei sa vezi structura exacta sau sa o rulezi manual (acolo am inclus si o relatie M-N intre Medici si Programari, conform cerintelor).

Conexiunea (Fara ORM)

Totul e scris "de mana" cu JDBC.
Folosesc driver-ul de SQLite (org.xerial:sqlite-jdbc).
Conexiunea se deschide doar cand e nevoie si se inchide mereu singura, pentru ca am folosit blocuri try-with-resources in Java.

Cum pornesti aplicatia?

Proiectul e facut cu Gradle, deci e super simplu:
Deschizi folderul in IntelliJ IDEA.
Astepti sa isi descarce Gradle dependentele.
Mergi in src/main/java/ro/vet/Main.java si ii dai Run, sau rulezi task-ul run din panoul de Gradle.
