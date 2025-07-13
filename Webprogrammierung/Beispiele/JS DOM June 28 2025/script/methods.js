{
  ("use strict");

  // Methoden, um auf Elemente im HTML Dokument zuzugreifen.
  // Einzelne Elemente
  const h1 = document.getElementById("head-line");
  console.log(h1);
  console.log(h1.id);
  console.log(h1.parentElement);
  console.log(h1.childNodes);
  console.log(h1.innerHTML);

  const submit = document.querySelector("[type=submit]");
  console.log(submit);

  // Mehrere Elemente
  const allP = document.getElementsByTagName("p");
  console.log(allP);
  console.log(allP[0].innerHTML);
  console.log(allP[0].childNodes);

  const allEm = document.getElementsByClassName("emphasize");
  console.log(allEm);
  const allEmAsArray = Array.from(allEm);
  console.log(allEmAsArray);

  const allRadio = document.getElementsByName("mood");
  console.log(allRadio);

  const allCheck = document.querySelectorAll("[type=checkbox]");
  console.log(allCheck);

  //   Das forms object ist vordefiniert,
  // finden Sie andere solche Objekte.
  console.log(document.forms[0], typeof document.forms[0]);
  console.log(document.forms[0].parentElement.childNodes);
}
