(function init() {
  "use strict";

  const img = document.querySelector("img");

  function changeSky() {
    let sky = prompt("Wie ist der Himmel bei Dir?");
    console.log(sky, typeof sky);
    if (sky === null) {
      alert("Abbrechen ist nicht erlaubt!");
      changeSky();
    } else if (sky.toLowerCase() === "hell") {
      img.parentElement.className = "happy";
    } else if (sky.toLowerCase() === "dunkel") {
      img.parentElement.className = "sad";
    } else {
      alert("Ungültige Eingabe!");
      changeSky();
    }
  }
  changeSky();
  /* **************************************** */
  /* default behavior unterdrücken */

  const link = document.querySelector("a");
  // console.log(link);

  link.addEventListener(
    "click",
    (e) => {
      let answer = prompt("Link freischalten?");
      if (
        (answer.toLocaleLowerCase() === "nein") |
        (answer.toLocaleLowerCase() === "nö")
      )
        console.log(e.preventDefault());
      else alert("Viel Spaß!");
    },
    false
  );

  /* Formularprüfung: preventDefault() bis alle Eingaben richtig sind. */
  const firstName = document.getElementById("user");
  const submit = document.querySelector("[type=submit]");

  const readName = () => console.log(firstName.value);

  submit.addEventListener(
    "click",
    (e) => {
      e.preventDefault();
      readName();
      checkInterests();
      checkMood();
    },
    false
  );

  /* Checkbox auslesen */
  const allChecks = document.querySelectorAll("[type=checkbox]");
  // console.log(allChecks);

  function checkInterests() {
    let interests = [];
    // console.log(interests);
    allChecks.forEach((check) => {
      if (check.checked) interests.push(check.value);
    });
    console.log(interests);
  }

  /* Radiobuttons auslesen */
  const allRadios = document.getElementsByName("mood");
  function checkMood() {
    let mood = "g";
    for (let i = 0; i < allRadios.length; i++) {
      if (allRadios[i].checked) mood = allRadios[i].value;
      break;
    }
    console.log(mood);
    // folgende Anweisungen sollten EINE eigene Funktion sein
    if (mood === "g") {
      img.parentElement.className = "happy";
      img.src = "pics/sonne.png";
      img.alt = "Wunderbare Sonne!";
      // alert("Super!");
      showMessage("Super, " + firstName.value);
    } else {
      img.parentElement.className = "sad";
      img.src = "pics/sonneSad.jpg";
      img.alt = "oder Ärgernis";
      // alert("Schade.");
      showMessage("Schade, " + firstName.value);
    }
  }

  /* Den DOM Baum ändern, hier durch Hinzufügen von Elementen. */
  function showMessage(text) {
    // Elemente löschen, falls schon welche vorhanden sind.
    if (document.forms[0].lastElementChild.nodeName == "P")
      document.forms[0].lastElementChild.remove();
    let p = document.createElement("p");
    p.appendChild(document.createTextNode(text));
    document.forms[0].appendChild(p);
  }
  // showMessage();

  function insertElement() {
    // 1. Element erzeugen
    let p = document.createElement("p");
    // 2. Content und/oder Attribute hinzufügen
    let text = document.createTextNode("Test");
    // 3. Content und/oder Attribute in das Element einfügen
    p.appendChild(text);
    // 4. Fertiges Element in das Dokument einfügen
    document.body.appendChild(p);
  }
  // insertElement();
})();
