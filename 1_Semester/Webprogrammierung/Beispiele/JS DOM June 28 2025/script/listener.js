(function init() {
  "use strict";

  const img = document.querySelector("img");
  console.log(img.src);
  const figcaption = document.querySelector("figcaption");

  function changeImage() {
    let url = img.src.match("sonne.png");
    // console.log(url);
    if (url) {
      img.src = "pics/sonneSad.jpg";
      img.alt = " ... oder Ärgernis";
      figcaption.innerHTML = "Heiße Sonne.";
    } else {
      img.src = "pics/sonne.png";
      img.alt = "Wunderbare Sonne!";
      figcaption.innerHTML = "Quelle des Wohlbefindens.";
    }
  }

  //   changeImage();

  // Der listener erwartet eine Referenz und nicht den Funktionsaufruf
  // Der Funktionsaufruf kann in eine anonyme Funktion gewrapped werden.

  img.addEventListener(
    // "event", aufzurufende Funktion, Propagation
    "click",
    (e) => {
      changeImage();
      console.log(e);
    },
    false
  );

  const domNodesTag = document.getElementsByTagName("em");
  console.log(domNodesTag);








  const markItArrow = (el) => (el.className = "mark");







  for (let i = 0; i < domNodesTag.length; i++) {
    domNodesTag[i].addEventListener(
      "click",
      (e) => {
        console.log(e.target.parentElement);
        markItArrow(e.target);
        console.log(e.target);
        console.log(e);
      },
      false
    );
  }
})();
