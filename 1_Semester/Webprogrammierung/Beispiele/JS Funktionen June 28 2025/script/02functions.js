/* IIFE um das gesamte Skript in einer Funktion zu kapseln (namespace). */
(function init() {
  ("use strict");

  // Funktionsdeklaration
  /* Die Funktion kann auch vor der Deklaration aufgerufen werden
    (hoisting): */
  //   squareTwo();
  /*   function squareTwo() {
    let square = 2 * 2;
    console.log(square);
  } */
  // ohne vereinbarten return, gibt die Funktion undefined zurück
  //   console.log(squareTwo());

  /*  function squareAnything(x) {
    return x * x;
  } */
  //   console.log(squareAnything(3));
  //   console.log(squareAnything());
  //   console.log(squareAnything("a"));
  //   console.log(squareAnything(10, true, "test", null, undefined));

  /*   function funcArgs(x, y) {
    console.log(`${x} und ${y}`);
    console.log(arguments);
    for (let i = 0; i < arguments.length; i++) {
      console.log(arguments[i]);
    }
  } */
  //   funcArgs(10, 100, "irgendwas");
  //   funcArgs();
  //   funcArgs(1, 2);

  // Parameter können Default Werte bekommen, falls sie nicht mit Argumenten bestückt werden.
  /*   function funcArgsDefault(x, y = 2) {
    console.log(`${x} und ${y}`);
  } */
  //   funcArgsDefault(1);
  //   funcArgsDefault();
  /* **************************************************** */

  // squareExpression(); kein hoisting
  // Funktionsausdruck
  // kein array-like für arguments (s. Funktionsdeklaration)
  /*   const squareExpression = function (x) {
    let square = x * x;
    console.log(`Funktionsausdruck: ${square}`);
    console.log(arguments);
  }; */

  // Aufruf
  //   squareExpression(3);
  // Referenz
  //   console.log(squareExpression);

  /* **************************************************** */
  // Arrow Functions
  /*   const multiply = (x, y = 2) => {
    console.log(`Arrow Function: ${x} * ${y} = ${x * y}`);
  }; */
  //   multiply(4, 10);
  //   multiply(2);

  /*   const noArgs = () => "no arguments";
  console.log(noArgs); */

  /*   const showArg = (x) => {
    console.log(x);
  }; */
  //   showArg(5);

  //   const showReturn = (x) => console.log(x);
  //   console.log(showReturn(100));
})();
