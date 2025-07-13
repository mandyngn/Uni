(function init() {
  "use strict";

  const allText = document.querySelectorAll("p");

  //Arrow-Function, create function without function call
  //go through all paragraphs with forEach
  allText.forEach((paragraph) => {
    paragraph.addEventListener("mouseenter", () => {
      paragraph.classList.add("highlighted");
    });
    paragraph.addEventListener("mouseleave", () => {
      paragraph.classList.remove("highlighted");
    });
  });

  //all img inside of figure elements
  const pictures = document.querySelectorAll("figure img");

  const picSources = [
    {
      src: "assets/pics/aprilia_rs_660.webp",
      alt: "Aprilla RS 660",
      // caption: "Aprilla RS 660",
    },
    {
      src: "assets/pics/honda_cb750_hornet.webp",
      alt: "Honda CB750 Hornet",
      // caption: "Honda CB750 Hornet",
    },
    {
      src: "assets/pics/Honda_CBR_650_R_2023.webp",
      alt: "Honda CBR650 R 2023",
      // caption: "Honda CBR650 R 2023",
    },
    {
      src: "assets/pics/Kawasaki_H2R.webp",
      alt: "Kawasaki H2R",
      // caption: "Kawasaki H2R",
    },
    {
      src: "assets/pics/kawasaki_ninja_650_2023.webp",
      alt: "Kawasaki Ninja 650 2023",
      // caption: "Kawasaki Ninja 650 2023",
    },
    {
      src: "assets/pics/Kawasaki_Z900.webp",
      alt: "Kawasaki Z900",
      // caption: "Kawasaki Z900",
    },
    {
      src: "assets/pics/Motorcycles.webp",
      alt: "verschiedene Motorradarten",
      // caption: "verschiedene Motorradarten",
    },
    {
      src: "assets/pics/suzuki_gsx_8s.webp",
      alt: "Suzuki GSX 8S",
      // caption: "Suzuki GSX 8S",
    },
    {
      src: "assets/pics/triumph_street_triple_765_rs.webp",
      alt: "Triumph Street Triple 765 RS",
      // caption: "Triumph Street Triple 765 RS",
    },
  ];

  function changeImage(img) {
    //takes the whole path of the image and splits everything at the / into an array
    //pop takes the last element of the array which is the name of the img src
    const filename = img.src.split("/").pop();

    //checks if the current image src is in the array and returns the index
    //only checks if the array contains the src and doesnt check if everything is the same
    const currentIndex = picSources.findIndex((data) =>
      data.src.includes(filename)
    );

    const nextIndex = (currentIndex + 1) % picSources.length;
    const nextImage = picSources[nextIndex];

    img.setAttribute("src", nextImage.src);
    img.setAttribute("alt", nextImage.alt);

    // //only change the caption of the chosen img
    // const caption = img.parentElement.querySelector("figcaption");
    // //checks if there is a figcaption and changes it
    // if (caption) {
    //   caption.innerHTML = nextImage.caption;
    // }

    // highlight the current figure
    pictures.forEach((pic) =>
      //remove the class from every figure
      pic.parentElement.classList.remove("active-image")
    );
    //add the class to the current figure
    img.parentElement.classList.add("active-image");
  }

  //for every img but only when clicked on
  pictures.forEach((img) => {
    img.addEventListener("click", () => {
      changeImage(img);
    });
  });
})();
