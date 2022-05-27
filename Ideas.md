* Because builder-methods are not for exposing, perhaps pass reference to builder object instead (make this configurable `style = "inheritance"` or `style = "delegation"`).
* Return type of `lift` does not need to be co- or contravariant in its arguments
* Solve warnings about missing encoding
* Provide more context for errors:
    > [ERROR] Method 'combine' in package 'nl.wernerdegroot.applicatives.prelude' does not meet all criteria for code generation
    > [ERROR]  - Method requires exactly 3 type parameters, but found 4
    > [ERROR] -> [Help 1]
* Collector applicative
* combineAndThen
* Validation should be able to accept context object to use in message templates
* Document process to publish
* Github Actions
* Code coverage
* Remove Google auto-registration
* element.getKind == record?
* Imports
* Introduce `ParameterName` and `ClassName`?
* Implement free applicative functors
* Allow subtypes in return type (return `CartesianProductList` in applicative for `List`)
* Increase coverage of processor-classes (now almost 0%)
