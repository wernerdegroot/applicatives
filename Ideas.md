* Validate values of annotation parameters (no -1 overloads)
* Remove duplication of processors
* Return type of `lift` does not need to be co- or contravariant in its arguments
* Solve warnings about missing encoding
* Provide more context for errors:
    > [ERROR] Method 'combine' in package 'nl.wernerdegroot.applicatives.prelude' does not meet all criteria for code generation
    > [ERROR]  - Method requires exactly 3 type parameters, but found 4
    > [ERROR] -> [Help 1]
* Make sure that none of the input parameters (Optional<A>, Optional<B>) contain A, B or C
* Collector applicative
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
