//prüft ob alles richtig ist was eingegeben wurde

(function init() {
  "use strict";
  //das ganze formular mit allem was drin ist
  const form = document.forms[0];

  //.elements zeigt alle elemente mit ID usw., kein label
  console.log(form.elements);

  //querySelector kann auf beliebigen CSS Selektor zugreifen
  const submit = document.querySelector("[type=submit");
  const reset = document.querySelector("[type=reset]");

  submit.addEventListener(
    "click",
    (e) => {
      //damit das Formular nicht immer resettet wird nachdem man auf submit clickt, die infos bleiben die eingegeben wurden
      e.preventDefault();
      validateForm(form);
    },
    false
  );

  reset.addEventListener("click", (e) => {}, false);

  // Validity anzeigen
  const allText = document.querySelectorAll("[type=text]");
  allText.forEach((text) => {
    //validity is valid wenn alles andere auf false ist
    console.log(text, text.validity);
  });
  console.clear();

  //gesamtes Formular prüfen
  function validateForm(el) {
    const formFields = Array.from(form.elements);

    //el is in dem fall das formular
    if (el.checkValidity()) {
      console.log("Alles richtig");
      showMessage("Dankeschön!");
    } else {
      console.log("falsch");

      //Einzelne Felder prüfen
      formFields.forEach((el) => {
        if (el.checkValidity) {
          alert("Richtig");
        } else {
          alert("falsch");
          whichError(el);
        }
      });
    }
  }

  function whichError(field) {
    let validity = field.validity;

    // if (validity.valueMissing) console.log("Bitte ausfüllen!");
    // if (validity.patternMismatch)
    //   console.log("Zu wenig Zeichen oder falsche Zeichen!");
    // if (validity.rangeUnderflow) console.log("Zu jung!");

    if (validity.valueMissing)
      showMessage(
        `${field.parentElement.firstElementChild.innerHTML} bitte ausfüllen!`
      );
    if (validity.patternMismatch) showMessage("zu wenig oder falsche Zeichen!");
    if (validity.rangeUnderflow) showMessage("Zu jung!");
  }

  //Meldungen an den Anwender
  function showMessage(message) {
    deleteMessage();
    let p = document.createElement("p");
    p.appendChild(document.createTextNode(message));
    p.classList.add("invalid");
    form.appendChild(p);
  }

  //Meldungen löschen
  function deleteMessage() {
    //JavaScript liest die NodeNamen in Groß
    while (form.lastElementChild.nodeName === "P") {
      form.lastElementChild.remove();
    }
  }
})();
