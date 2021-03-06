# boolean-parser

This project is a library of functions for working with boolean expressions. It offers:
* The ability to build boolean expressions from various classes. So far the following are supported:
  * `And`, `Or`, `Xor` - support an arbitrary number of arguments,
  * `Not`,
  * `Literal`, `Variable`, 
  * `Function` - a "boolean function factory" that makes it possible to easily describe any boolean function of arbitrary arity and use them in expressions.
* Various expression representations:
  * Representation of any function in the standard basis (`&`, `|`, `~`),
  * Conjunctive, disjunctive, full conjunctive and full disjunctive normal forms,
  * Zhegalkin polynomial form.
* A parser that allows to enter a boolean expression from the console.
