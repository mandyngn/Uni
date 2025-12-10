{
  ("use strict");

  //returns live HTLM collection with all the matching tagnames
  const headline = document.getElementsByTagName("h2");
  console.log(headline);

  //returns an HTML element object
  const intro = document.getElementById("intro");
  console.log(intro, typeof intro);
  console.log(intro.parentElement);
  console.log(intro.innerHTML);

  //returns a live HTML collection with all the elements with the matching classname
  const sections = document.getElementsByClassName("section");
  console.log(sections);

  //returns a live HTML collection
  const checkboxes = document.getElementsByName("bike");
  console.log(checkboxes, typeof checkboxes);

  //returns the first matching element as an element object
  const nav = document.querySelector("nav");
  console.log(nav.previousSibling);
  console.log(nav.nextElementSibling);
  const body = document.querySelector("body");
  console.log(body.childNodes);

  //returns static NodeList
  const allRadio = document.querySelectorAll("[type=radio]");
  console.log(allRadio);

  let name = window.prompt("Wie ist dein Name?");

  alert(`Hallo ${name}!`);
  alert(`Und Tschüss ${name}!`);

  // function greetUser(name) {
  //   alert("Hi " + name + " und Tschüss!");
  // }
  // let username = prompt("Wie ist dein Name?");
  // greetUser(username);
}
