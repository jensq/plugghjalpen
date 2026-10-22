# Plugghjälpen

Övningsapp för Android (mobil och surfplatta) för att lära sig klockan och engelska glosor.
Appen läser upp frågor och ger återkoppling med Androids inbyggda talsyntes.

## Övningar
* **Klockan** – tryck på den blå knappen för att höra en tid, t.ex. "fem över sju". Välj den av
  tre klockor som visar rätt tid. Fel svar ger "Försök igen!", rätt svar ger "Rätt, bra jobbat!".
  Felalternativen är typiska förväxlingar: en timme fel (halv åtta / halv sju), "över" mot "i"
  (fem över / fem i) och visarna förväxlade.
* **Engelska** – ett engelskt ord visas (tryck för att höra uttalet). Välj rätt svenskt ord av fem.
  Orden finns i `app/src/main/java/se/plugghjalpen/Words.kt` och är lätta att fylla på.

* **Matte** – plus och minus med tal upp till 20 (hälften av talen håller sig inom 10). Tre svar att
  välja mellan; felalternativen är typiska misstag: ett eller två fel, tio fel eller fel räknesätt.
* **Pengar** – svenska mynt (1, 2, 5 och 10 kr) och 20-kronorssedeln, högst 50 kr. Två sorters frågor:
  "Hur mycket är det?" (räkna ihop pengarna) och "Välj 15 kr" (välj rätt hög med pengar).
  Mynten är förenklade men har rätt färg och rätt storlek i förhållande till varandra.

## Bygga och uppdatera
Varje push till `main` bygger APK:n med GitHub Actions och publicerar den som en release
(`build-<nummer>`). Appen kollar vid start om det finns en nyare release och erbjuder sig att
ladda ner och installera den (samma system som R56 video). Det går också att trycka på
**Sök efter uppdateringar** längst ner på startsidan.

Första gången: ladda ner `Plugghjalpen.apk` från senaste releasen under **Releases** på GitHub
och öppna filen på telefonen/plattan (tillåt installation från okända källor när Android frågar).

## Rösten
Appen använder telefonens text-till-tal. Saknas svensk eller engelsk röst visar appen en ruta
som leder till installationen (Inställningar → Text till tal, t.ex. Google Talsyntes).
