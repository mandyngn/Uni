/* Der Anweisungsblock begründet einen namespace. */
{
  ("use strict");
  //   https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Strict_mode

  // Methoden, die der Browser zur Verfügung stellt.
  //   Informationen an den Anwender
  //   alert("Hallo Welt!");
  //   Informationen vom Anwender entgegennehme.
  //   prompt("Wer bist Du?");
  //   Methode für Entwickler
  console.log("test");

  //   Variablen: Datentypen und Scope
  //   Deklaration mit var: Funktionscope
  var message = "Hallo Welt!";
  //   Aufruf
  console.log(message, typeof message);
  message = 42;
  console.log(message, typeof message);
  message = true;
  console.log(message, typeof message);

  //   Deklaration mit let oder const: Blockscope
  let text = "Ich habe einen Blockscope!";
  const NUM = 2;
  //   NUM = 4;
  //   console.log("Nach dem Error ...");

  //   Leert die Konsolenausgaben
  console.clear();
  let n = 0,
    m = 0;
  let test = true;
  let sign = "1";
  let nothing = null;
  let alsoNothing = undefined;
  console.log(test, typeof test);
  console.log(sign, typeof sign);
  console.log(nothing, typeof nothing);
  console.log(alsoNothing, typeof alsoNothing);

  n = 1;
  //   Vergleich in JavaScript (ES ECMA Script)
  if (n == m) console.log(n + " ist gleich " + m);
  //   else console.log(n + " ist ungleich " + m);
  else console.log(`${n} ist ungleich ${m}`);

  //   Strings: "", '', ```

  //   Implizite Konvertierung
  // == gleicher Wert
  // === gleicher Wert und gleicher Typ
  if (test == n) console.log(`${test} ist gleich ${n}`);
  if (test === n) console.log(`${test} ist gleich ${n}`);
  else
    console.log(
      `${test} und ${n} haben den gleichen Wert, aber unterschiedliche Typen.`
    );

  // Testen Sie auch null, undefined, true, false, 0,"0",1,"1","", ...

  //   null: kein Wert
  //  undefined: kein Wert und kein Typ
  let notInitialized;
  console.log(notInitialized, typeof notInitialized);

  //   Zusammengesetzte Typen:
  //   const types = new Array();
  const types = ["number", "string", "boolean", "undefined", "null"];
  types[0] = 42;
  console.log(types);
  //   Die Elemente innerhalb eines Arrays müssen nicht vom selben Typ sein.

  // Iteration über Array typischer Weise: for oder forEach()
  for (let i = 0; i < types.length; i++) {
    console.log(types[i]);
  }

  //   Besprechung im Rahmen mit Funktionen
  types.forEach((item) => {
    console.log(item);
  });

  //   Array in JS ist von variabler Länge
  console.log(types[10]);
  types[10] = "unverhofft";
  console.log(types);
  console.log(types.length);

  console.log("Der Typ von Array ist: ", typeof types);

  //   Objekt Deklaration
  const person = {
    first: "John",
    last: "Doe",
    age: 42,
    wanted: true,
  };

  console.log("Der Typ von Object ist ", typeof person);
  console.log(person);
  for (key in person) {
    console.log(person[key]);
  }
}
// Das Skript wird abgebrochen, weil text hier nicht existiert.
// console.log(text);
console.log("Außerhalb vom Anweisungsblock" + message);
