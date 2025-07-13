(function () {
  "use strict";
  //alle ansprechen
  const all = document.querySelector("*");

  const showAll = (el) => console.log(el);

  for (let i = 0; i < all.clientHeight; i++) {
    all[i].addEventListener(
      "click",
      //function(e){},
      (e) => {
        showAll(all[i]);
        //um nur das target zu zeigen ohne eltern usw.
        e.stopPropagation();
      },
      //erst alle vorfahren dann das event durch true, mit false ist andersrum
      false
    );
  }
})();
