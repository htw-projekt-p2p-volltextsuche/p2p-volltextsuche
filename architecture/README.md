# Beispiel Partition By Keyword von zentraler Volltextsuche aus

**Drei Dokumente:** d1, d2, d3
**Vier Terme:** t1, t2, t3, t4
**Drei Peers:** p1, p2, p3

Dokumente:
```
d1 = {t1, t2, t3}
d2 = {t4}
d3 = {t1, t2}
```

_Hashen der Terme auf die Peers:_
T1 -> p1
T2 -> p2
T3 -> p3
T4 -> p1

## Indexing
1. Wenn ein Dokument neu indexiert werden soll, kann z.B. durch Hashing des Dokuments der verantwortliche Peer bestimmt werden, der das Dokument vorverarbeitet (tokenizing, stemming, indexing, …)
2. Alle gefundenen Terme werden gehashed und nach zuständigen Peers zusammengefasst
3. Die Terme werden inklusive der Postinglisten als Batch (über die P2P Logik) an den jeweils zuständigen Peer gesendet
4. Der jeweils zuständige Peer fügt die Terme mit Postinglisten in seinen eigenen (lokalen) Invertierten Index hinzu oder nimmt es in die bereits vorhandene Postingliste auf, falls der Term bereits bekannt ist.

### Beispiel:
Invertierter Index auf den einzelnen Peers:
```
P1 {
	t1 = [d1, d3]
	t4 = [d2]
}
P2 {
	t2 = [d1, d3]
}
P3 {
	t3 = [d1]
}
```

Der gesamte Index eines jeden Peers könnte nun noch auf den jeweiligen Vorgänger repliziert werden. Fällt ein Peer unerwartet aus, kann der vorherige Peer als Fallback dienen:
```
P1 {
	t1 = [d1, d3]
	t2 = [d1, d3]
	t4 = [d2]
}
P2 {
	t2 = [d1, d3]
	t3 = [d1]
}
P3 {
	t1 = [d1, d3]
	t3 = [d1]
	t4 = [d2]
}
```

Die Redundanz gleicht sich mit steigender Anzahl der Peers aus und könnte ggF. sogar auch auf den Nachfolger ausgeweitet werden.


## Retrieval
1. Es wird ein Request gegen die Volltextsuche auf einem beliebigem Peer gestellt
	* 	Das wird in unserem Fall immer der Peer sein, von dessen UI die Suche gestellt wurde
2. Die Volltextsuche extrahiert die einzelnen Terme der Query und ruft diese über die P2P Logik aus dem _Distributed Inverted Index_ ab
	* Für den Fall, dass einer der gesuchten Terme auf Peer liegt, von dem die Anfrage ausgeht, sollten Optimierungen vorgenommen werden, so dass der Zugriff einem einfachen lokalen Zugriff gleicht, der aber trotzdem über den _Distributed Inverted Index_ abstrahiert wird.
3. Sobald die Volltextsuche die Postinglisten erhalten hat, kann sie diese verarbeiten, das Ranking durchführen und die sortierten Ergebnisse zurück liefern.
	* 	Hier kann überlegt werden, ob die Postinglisten inkrementell zurück gegeben werden, so dass die Volltextsuche schon arbeiten kann, während die P2P-Logik die restlichen Postinglisten noch sucht.

### Beispiel:

_Anfragen:_
A1 = „t1 AND t3“
A2 =„t3 AND t4“
A3 = „t2“

**A1:** 
Anfrage landet auf beliebigem Peer, dieser greift auf den Invertierten Index zu und baut das Ergebnis zusammen. Fällt der Peer für einer der Terme aus, wird über den DHT (das P2P Framework) auf den Fallback Peer zurückgegriffen.
In beiden Fällen vollständiges und zuverlässiges Ergebnis, auch wenn einer der Peers ausfällt.
**A2:**
Gleiches Verhalten wie bei A1.
Vollständiges und zuverlässiges Ergebnis, auch wenn einer der Peers ausfällt
**A3:**
Gleiches Verhalten wie bei A1.
Vollständiges und zuverlässiges Ergebnis, auch wenn einer der Peers ausfällt


## Indexing und Retrieval nach Parteizugehörigkeit und Rednername
Damit auch performant nach allen Reden einer Partei oder eines einzelnen Redners gesucht werden kann werden beim Indexing zwei Spezial-Tokens für Partei- und Rednername angelegt.
Diese bekommen einen besonderen Prefix, damit sie von einfachen Termen unterschieden werden können.
Beim indizieren einer Rede werden dann also jeweils zwei zusätzliche Schlüssel im _Distributed Inverted Index_ abgelegt, die beide das entsprechende Dokument beinhalten.

### Beispiel
> d1 = {t1, t2, t3} von Angela Merkel (CDU)  

**Hashing:**
```
t1 -> p1
t2 -> p2
t3 -> p3
_speaker:angelamerkel -> p1
_affiliation:cdu -> p2
```
_Der Prefix ist hier nur ein Beispiel und muss im Weiteren noch festgelegt werden_

**Index:**
```
P1 {
	t1 = [d1]
}
P2 {
	t2 = [d1]
}
P3 {
	t3 = [d1]
}
```

- - - -

## Argumentation der Design-Entscheidung
Nachteil gegenüber des _Partition By Document_ Ansatzes ist, dass die Suche nur als gesamtes Netzwerk wirklich funktionsfähig ist. Bei _Partition By Document_ kann jeder Peer eine eigenständige Volltextsuche für alle ihm zugeordneten Dokumente vornehmen.

Vorteil hingegen ist, dass das Netzwerk nicht für jede einzelne Query geflutet (Broadcast) werden muss, sondern lediglich die für die entsprechenden Keywords der Query verantwortlichen Peers angefragt werden müssen.
