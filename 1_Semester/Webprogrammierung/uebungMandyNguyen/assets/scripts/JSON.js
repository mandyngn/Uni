(function init() {
  "use strict";

  const form = document.forms[0];

  const submit = document.querySelector("[type=submit]");

  submit.addEventListener(
    "click",
    (e) => {
      e.preventDefault();
      new FormData(form);
    },
    false
  );

  form.addEventListener(
    "formdata",
    (e) => {
      let data = e.formData;

      for (let value of data.entries()) {
        data[value[0]] = value[1];
      }

      console.log(data);

      let Json = JSON.stringify(data);
      console.log(Json);
    },
    false
  );
})();
