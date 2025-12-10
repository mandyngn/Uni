(function init() {
  "use strict";

  const form = document.forms[0];
  //   console.log(form);

  const submit = document.querySelector("[type=submit]");
  //Datentype speziell für Formulare, Untertyp von Object
  //   const data = new FormData();
  //   console.log(data);

  submit.addEventListener(
    "click",
    (e) => {
      e.preventDefault();
      console.log("Nicht neu laden");
      new FormData(form);
    },
    false
  );

  form.addEventListener(
    "formdata",
    (e) => {
      let data = e.formData;
      console.log(data);
      console.log(data.entries());

      //   for (let value of data.entries()) {
      //     console.log(value);
      //   }

      for (let value of data.entries()) {
        data[value[0]] = value[1];
        console.log(data[value[0]]);
      }

      let JSONformData = JSON.stringify(data);
      console.log(JSONformData);
      console.log(data);
    },
    false
  );
})();
