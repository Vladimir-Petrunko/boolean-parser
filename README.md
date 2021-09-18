# boolean-parser

This project is a library of functions for working with boolean expressions. It offers:
* The ability to build boolean expressions from various classes. So far the following are supported:
  * `And`, `Or`, `Xor` - support an arbitrary number of arguments,
  * `Not`,
  * `Literal`, `Variable`, 
  * `BinaryFunction` - a "boolean function factory" that makes it possible to easily describe any of the 16 boolean functions of two arguments and use them in expressions. Also supports an arbitrary number of arguments.
* Various expression representations:
  * Representation of any function in the standard basis (`&`, `|`, `~`),
  * Conjunctive, disjunctive, full conjunctive and full disjunctive normal forms.
* A parser that allows to enter a boolean expression from the console.

`TODO`:
* Zhegalkin polynomial form,
* Full error handling of parser (currently a subset of possible parsing errors are handled),
* Support of functions of more than two arguments.

**Currently work in progress - all ideas, suggestions and issues are greatly appreciated.**
