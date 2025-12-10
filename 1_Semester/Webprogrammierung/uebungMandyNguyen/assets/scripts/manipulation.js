(function init() {
  "use strict";

  const picSources = {
    first: {
      source: "assets/pics/aprilia_rs_660.webp",
      alt: "Aprilla RS 660",
      caption: "Aprilla RS 660",
      paragraph: "Das ist ein Motorrad und die Aprilla RS 660.",
    },
    second: {
      source: "assets/pics/honda_cb750_hornet.webp",
      alt: "Honda CB750 Hornet",
      caption: "Honda CB750 Hornet",
      paragraph: "Das ist ein Motorrad und die Honda CB750 Hornet.",
    },
    third: {
      source: "assets/pics/Honda_CBR_650_R_2023.webp",
      alt: "Honda CBR650 R 2023",
      caption: "Honda CBR650 R 2023",
      paragraph: "Das ist ein Motorrad und die Honda CBR650 R 2023.",
    },
    fourth: {
      source: "assets/pics/Motorcycles.webp",
      alt: "verschiedene Motorradarten",
      caption: "verschiedene Motorradarten",
      paragraph: "Das sind viele verschiedene Motorraeder.",
    },
    fifth: {
      source: "assets/pics/Kawasaki_H2R.webp",
      alt: "Kawasaki H2R",
      caption: "Kawasaki H2R",
      paragraph: "Das ist ein Motorrad und die Kawasaki H2R.",
    },
    sixth: {
      source: "assets/pics/kawasaki_ninja_650_2023.webp",
      alt: "Kawasaki Ninja 650 2023",
      caption: "Kawasaki Ninja 650 2023",
      paragraph: "Das ist ein Motorrad und die Kawasaki Ninja 650 2023.",
    },
    seventh: {
      source: "assets/pics/Kawasaki_Z900.webp",
      alt: "Kawasaki Z900",
      caption: "Kawasaki Z900",
      paragraph: "Das ist ein Motorrad und die Kawasaki Z900.",
    },
    eigth: {
      source: "assets/pics/suzuki_gsx_8s.webp",
      alt: "Suzuki GSX 8S",
      caption: "Suzuki GSX 8S",
      paragraph: "Das ist ein Motorrad und die Suzuki GSX 8S.",
    },
    ninth: {
      source: "assets/pics/triumph_street_triple_765_rs.webp",
      alt: "Triumph Street Triple 765 RS",
      caption: "Triumph Street Triple 765 RS",
      paragraph: "Das ist ein Motorrad und die Triumph Street Triple 765 RS.",
    },
  };

  const pictures = document.querySelectorAll("img");

  function changeImage(img) {
    //returns an array of all the keys
    const keys = Object.keys(picSources);

    //with math random you get a random number between 0 and 1 but you cant get 1 only 0.99
    //multiply with array length gets you to numbers almost with the length
    //with math floor it is rounded down so in this case you get a number between 0 and 3 including both of these numbers
    const randomKey = keys[Math.floor(Math.random() * keys.length)];
    const selectedKey = picSources[randomKey];

    img.src = selectedKey.source;
    img.alt = selectedKey.alt;

    // figcaption of the corresponding figure of the img
    const caption = img.parentElement.querySelector("figcaption");
    if (caption) {
      caption.textContent = selectedKey.caption;
    }

    // checks if there is an existing text and removes it
    const existingText = img.parentElement.querySelector("p");
    if (existingText) {
      existingText.remove();
    }

    // adds new text
    const currentText = document.createElement("p");
    currentText.innerHTML = selectedKey.paragraph;
    //appendChild adds another child to the figure parentElement
    img.parentElement.appendChild(currentText);
  }

  pictures.forEach((img) => {
    img.addEventListener("click", () => {
      changeImage(img);
    });
  });
})();
