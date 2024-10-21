# Analiză sintactică 1 - Specificații sintactice

## Introducere

- Obiectivul laboratorului este implementarea unui analizor sintactic
  pentru limbajul CPLang, introdus la laboratorul anterior.
- Descărcați arhiva ce conține scheletul laboratorului.
- Înainte de parcurgerea exercițiilor, citiți cu atenție scheletul de
  cod. Pentru fiecare exercițiu, găsiți în cod explicații și sugestii
  de implementare. Umăriți marcajele **`TODO x`** unde `x` este
  numărul exercițiului. Sursele de interes sunt
  **`parser/CPLangParser.g4`** și **`Test.java`**.

## Ex. 1. Specificația sintactică a limbajului CPLang

- Generarea analizorului sintactic presupune prezența fișierului
  **`CPLangLexer.tokens`** (generat de ANTLR pentru analiza lexicală)
  lângă **`parser/CPLangParser.g4`** .  Generați fișierele
  analizorului lexical, apoi copiați **`lexer/CPLangLexer.tokens`** în
  directorul **`parser/`**.
- De asemenea, pentru a putea folosi funcția `Test Rule`, copiați
  **`lexer/CPLangLexer.g4`** în directorul **`parser/`**.

- Completați gramatica din fișierul **`CPLangParser.g4`**, urmărind
  marcajele **`TODO 1`**, astfel încât să puteți analiza programe
  CPLang. Pentru a testa o anumită regulă sintactică, faceți click
  dreapta pe numele regulii în specificație și selectați `Test
  Rule`. În panoul din stânga puteți introduce un șir de analizat, iar
  în panoul din dreapta puteți vizualiza arborele de derivare.
- În final, urmariți marcajele **`TODO 1`** din **`Test.java`**.
- În metoda **`main`** se decomentează metoda corespunzătoare
  exercițiului.  Fișierul de intrare este **`manual.txt`**.

## Ex. 2. Evaluator de expresii aritmetice

- Implementați, folosind ***visitors***, un evaluator de expresii
  aritmetice simple. Expresiile vor fi separate de `;` și vor conține
  doar literali numerici întregi, paranteze, și operatorii
  `+-*/`. **Atenție** la ordinea obișnuită a operațiilor!
- Urmăriți marcajele **`TODO 2`** din **`Test.java`**.
- Pentru testare, decomentați linia corespunzătoare în `main`.
  Fișierul de intrare este **`input2.txt`**, iar referința este
  **`reference2.txt`**.

Exemplu de intrare:

    (3*2+6)/2;
    8+8*2;
    1+1/2;

Exemplu de ieșire:

    6
    24
    1
