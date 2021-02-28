<h3>DCL</h3>

<p>Napisać w asemblerze program symulujący działanie maszyny szyfrującej DCL.
Maszyna DCL działa na zbiorze <em>dopuszczalnych znaków</em> zawierającym: duże
litery alfabetu angielskiego, cyfry 1 do 9, dwukropek, średnik, pytajnik, znak
równości, znak mniejszości, znak większości, małpę.
Jedynie znaki z tego zbioru mogą się pojawić w poprawnych parametrach programu
oraz w poprawnym wejściu i wyjściu programu.</p>

<p>Maszyna składa się z trzech bębenków szyfrujących: lewego L, prawego R
i odwracającego T.
Bębenki L i R mogą się obracać i każdy z nich może znajdować się w jednej
z 42 pozycji oznaczanych znakami z dopuszczalnego zbioru.
Maszyna zamienia tekst wejściowy na wyjściowy, wykonując dla każdego znaku ciąg
permutacji.
Jeśli bębenek L jest w pozycji <em>l</em>, a bębenek R w pozycji <em>r</em>, to
maszyna wykonuje permutację<br>
<br>
Q<sub><em>r</em></sub><sup>-1</sup>R<sup>-1</sup>Q<sub><em>r</em></sub>
Q<sub><em>l</em></sub><sup>-1</sup>L<sup>-1</sup>Q<sub><em>l</em></sub>
T
Q<sub><em>l</em></sub><sup>-1</sup>LQ<sub><em>l</em></sub>
Q<sub><em>r</em></sub><sup>-1</sup>RQ<sub><em>r</em></sub><br>
<br>
gdzie L, R i T są permutacjami bębenków zadanymi przez parametry programu.
Procesy szyfrowania i deszyfrowania są ze sobą zamienne.</p>

<p>Permutacje Q dokonują cyklicznego przesunięcia znaków zgodnie z ich kodami
ASCII.
Przykładowo Q<sub>5</sub> zamienia <code>1</code> na <code>5</code>,
<code>2</code> na <code>6</code>, <code>9</code> na <code>=</code>,
<code>=</code> na <code>A</code>, <code>A</code> na <code>E</code>,
<code>B</code> na <code>F</code>, <code>Z</code> na <code>4</code>,
a Q<sub>=</sub> zamienia <code>1</code> na <code>=</code>,
<code>2</code> na <code>></code>, <code>?</code> na <code>K</code>.
Permutacja Q<sub>1</sub> jest identycznością.
Permutacja T jest złożeniem 21 rozłącznych cykli dwuelementowych (złożenie TT
jest identycznością).
X<sup>-1</sup> oznacza permutację odwrotną do permutacji X.
Złożenie permutacji wykonuje się od prawej do lewej.</p>

<p>Przed zaszyfrowaniem każdego znaku bębenek R obraca się o jedną pozycję
(cyklicznie zgodnie z kodami ASCII pozycji), czyli jego pozycja zmienia się na
przykład z <code>1</code> na <code>2</code>, z <code>?</code> na <code>@</code>,
z <code>A</code> na <code>B</code>, z <code>B</code> na <code>C</code>,
z <code>Z</code> na <code>1</code>.
Jeśli bębenek R osiągnie tzw. pozycję obrotową, to również bębenek L obraca się
o jedną pozycję.
Pozycje obrotowe to <code>L</code>, <code>R</code>, <code>T</code>.</p>

<p>Kluczem szyfrowania jest para znaków oznaczająca początkowe pozycje bębenków
L i R.</p>

<p>Program przyjmuje cztery parametry: permutację L, permutację R, permutację T,
klucz szyfrowania.
Program czyta szyfrowany lub deszyfrowany tekst ze standardowego wejścia,
a wynik zapisuje na standardowe wyjście.
Po przetworzeniu całego wejścia program kończy się kodem 0.
Program powinien sprawdzać poprawność parametrów i danych wejściowych,
a po wykryciu błędu powinien natychmiast zakończyć się kodem 1.
Czytanie i zapisywanie powinno odbywać się w blokach, a nie znak po znaku.</p>

<p>Oceniane będą poprawność i szybkość działania programu, zajętość pamięci
(rozmiary poszczególnych sekcji), styl kodowania.
Tradycyjny styl programowania w asemblerze polega na rozpoczynaniu etykiet od
pierwszej kolumny, mnemoników od dziewiątej kolumny, a listy argumentów od
siedemnastej kolumny.
Inny akceptowalny styl prezentowany jest w przykładach pokazywanych na
zajęciach.
Kod powinien być dobrze skomentowany, co oznacza między innymi, że każda
procedura powinna być opatrzona informacją, co robi, jak przekazywane są do niej
parametry, jak przekazywany jest jej wynik, jakie rejestry modyfikuje.
To samo dotyczy makr.
Komentarza wymagają także wszystkie kluczowe lub nietrywialne linie wewnątrz
procedur lub makr.
W przypadku asemblera nie jest przesadą komentowania prawie każdej linii kodu,
ale należy jak ognia unikać komentarzy typu „zwiększenie wartości rejestru rax
o 1”. </p>

<p>Dołączone do zadania przykłady składają się z trójek plików.
Plik <code>*.key</code> zawiera parametry wywołania programu, a pliki
<code>*.a</code> i <code>*.b</code> zawierają parę tekstów odpowiadających sobie
przy szyfrowaniu i deszyfrowaniu.</p>

<p>Jako rozwiązanie należy oddać plik <code>dcl.asm</code>.
Program będzie kompilowany poleceniami:</p>
<pre>
nasm -f elf64 -w+all -w+error -o dcl.o dcl.asm
ld --fatal-warnings -o dcl dcl.o
</pre>
