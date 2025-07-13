(function init() {
  "use strict";

  //Primitive Datentypen und Ausdrücke
  const person = {
    firstName: "Susi",
    lastName: "Schmidt",
    happy: true,
    start: 5 + 1,
  };

  //   console.log(person);

  //object in JS konvertieren zu String
  let personFlat = JSON.stringify(person);
  //   console.log(personFlat);
  //aus JSON in js konvertieren (zurück konvertieren)
  console.log(JSON.parse(personFlat));

  //Funktionen können nicht konvertiert werden
  const complexPerson = {
    firstName: "Susi",
    lastName: "Schmidt",
    hobbies: ["turnen", "tanzen", "tischlern"],
    birthYear: 2000,
    age: function () {
      let thisYear = new Date().getFullYear();
      console.log(thisYear - this.birthYear);
    },
  };

  console.log(complexPerson);

  let complexPersonFlat = JSON.stringify(complexPerson);
  console.log(complexPersonFlat);
  let complexPersonFlatBackToJS = JSON.parse(complexPersonFlat);
  console.log(complexPersonFlatBackToJS);
})();
