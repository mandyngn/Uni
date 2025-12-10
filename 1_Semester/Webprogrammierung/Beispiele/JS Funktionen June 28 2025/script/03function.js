(function init(){
    "use strict";

    // Function Declaration:
    // hoisting!
    function showMessage(){
        console.log("Hello World!");
    }
    // call
    showMessage();
    
    // Function Expression, using anonymous functions
    const welcome = function(){
        console.log("Welcome!");
    }
    welcome();

    // Parameter: passing information into the function
    function greetUser(firstName){
        alert(`Hello ${firstName}!`);
    }
    let firstName = prompt("Who are you?");
    greetUser(firstName);
    // fewer arguments are possible --> undefined
    greetUser();
    // more arguments are also possible
    greetUser(firstName, "John");

    // showing arguments: not possible für Function Expressions!
    function howManyArgs(){
        console.log(arguments);
        console.log(arguments.length);
        for(let i=0; i<arguments.length; i++){
            console.log(arguments[i]);
        }
        console.log(typeof arguments);
    }
    // arguments is an array-like object, you cannot use array methods

    howManyArgs();
    howManyArgs("Hello",true,42);

    function person(firstName, lastName, age){
        console.log(`first name: ${firstName}`);
        console.log(`last name: ${lastName}`);
        console.log(`age: ${age}`);
    }

    person("John", "Doe", 23);
    person("Jane", "Doe");

    // Rest Parameters
    function user(firstName, lastName, age, ...restArgs){
        console.log(`restArgs: ${restArgs.length}`);
/*         for(let i=0; i<restArgs.length; i++){
            console.log(restArgs[i]);
        } */
        restArgs.forEach(function(item){
            console.log(item);
        });
        // try forEach with arguments!
        console.log(`arguments: ${arguments.length}`);
        console.log(`restArgs: ${restArgs.length}`);
    }
    user("Jane", "Doe", 27, true, null);

    // variadic functions
    function count(...numbers){
        console.log("All arguments are in an array!");
        for(let i=0; i<numbers.length; i++){
            console.log(`Round: ${i+1} ${numbers[i]}`);
        }
    }

    count(1,2,3,4,56,100);

    // default parameters
    function greeting(firstName="John Doe"){
        console.log(`Greetings ${firstName}!`);
    }
    greeting("Jane");
    greeting();

    // return values
    function square(x){
        const result = x * x;
        return result;
    }
    square(2);
    console.log(square(3));
    // reference
    console.log(square);
    // functions can return any datatype

    // spread operator
    const user1 = ["John", "Doe", 1234];
    const user2 = ["Jane", "Doe", 5678];
    function createUser(firstName, lastName, password){
       const user = {
        firstName : firstName,
        lastName : lastName,
        password :password
       };
       return user;
    }

    createUser(...user1);
    console.log(createUser(...user1));
    console.log(createUser(...user2));

    // functions within functions
    // later

    // short notation: arrow functions
    // arrow functions are function expressions

    const sum = (x,y) => {let s = x + y;
                            return s;}

    console.log(sum(1,2));

    // only one return statement
    const sumShort = (x,y) => x + y;
    console.log(`sum = ${sumShort(2,2)}`);

    // only one parameter
    const message = yourName => console.log(`Hello ${yourName}`);
    message("John Doe");

    // no parameter
    const hello = () => console.log("I have no arguments.");

    // returning objects
    const showUser = (firstName, lastName) => (
        {
            firstName: firstName,
            lastName:lastName
        }
    );
    console.log(showUser("Jane", "Doe"));

    // more on arrow functions later:
    // 'this' is defined by the context of the declaration, not the execution
    // you cannot use 'new', so no Constructor functions

    // execution context
    
    // global execution context:
    // code outside of all functions
    // there is only one global context

    // function context
    // all code within a functions
    // each function call has its own functions context
    


})();