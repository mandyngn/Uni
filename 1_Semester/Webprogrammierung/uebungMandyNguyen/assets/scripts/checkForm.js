(function init() {
  "use strict";

  const form = document.forms[0];
  console.log(form, form.elements);

  const submit = document.querySelector("[type=submit]");

  submit.addEventListener(
    "click",
    (e) => {
      e.preventDefault();
      validateForm(form);
    },
    false
  );

  function validateForm(el) {
    deleteMessage();
    const fields = Array.from(form.elements);

    if (el.checkValidity()) {
      showMessage("Well done! Thank you.");
    } else {
      fields.forEach((el) => {
        if (!el.validity.valid) {
          whichError(el);
        }
      });
    }
  }

  function whichError(field) {
    let validity = field.validity;

    if (validity.valueMissing)
      showMessage(`${field.previousElementSibling.innerHTML} bitte ausfüllen!`);
    if (validity.patternMismatch)
      showMessage(
        `${field.previousElementSibling.innerHTML} zu wenig oder falsche Zeichen!`
      );
    if (validity.rangeUnderflow || validity.rangeOverflow)
      showMessage(`${field.previousElementSibling.innerHTML} ungültig!`);
  }

  function showMessage(message) {
    let p = document.createElement("p");
    p.appendChild(document.createTextNode(message));
    p.classList.add("invalid");
    form.appendChild(p);
  }

  function deleteMessage() {
    while (form.lastElementChild.nodeName === "P") {
      form.lastElementChild.remove();
    }
  }
})();
