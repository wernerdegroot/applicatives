* Allow non-builder to have method type parameters again.
* Because builder-methods are not for exposing, perhaps pass reference to builder object instead (make this configurable `style = "inheritance"` or `style = "delegation"`).
* Validate values of annotation parameters (no -1 overloads)
* Validate that type constructors for accumulator contain only one of the type parameter (no `C` in first parameter for example)
* Remove duplication of processors
* Return type of `lift` does not need to be co- or contravariant in its arguments
* Solve warnings about missing encoding
* Provide more context for errors:
    > [ERROR] Method 'combine' in package 'nl.wernerdegroot.applicatives.prelude' does not meet all criteria for code generation
    > [ERROR]  - Method requires exactly 3 type parameters, but found 4
    > [ERROR] -> [Help 1]
* Make sure that none of the input parameters (Optional<A>, Optional<B>) contain A, B or C
* Collector applicative
* combineAndThen
* Validation should be able to accept context object to use in message templates
* Allow for statics (just a single super-class allowed though)
* Document process to publish
* Github Actions
* Code coverage
* Add `Collector` to interface
* Remove Google auto-registration
* element.getKind == record?
* Imports
* Introduce `ParameterName` and `ClassName`?
* Implement free applicative functors
* Allow subtypes in return type (return `CartesianProductList` in applicative for `List`)
* Increase coverage of processor-classes (now almost 0%)
