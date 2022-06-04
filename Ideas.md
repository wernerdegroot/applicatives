* No access modifiers should be a validation error too
* Static -> class, abstract instance method -> abstract class
* Because builder-methods are not for exposing, perhaps pass reference to builder object instead (make this configurable `style = "inheritance"` or `style = "delegation"`).
* Return type of `lift` does not need to be co- or contravariant in its arguments
* Provide more context for errors:
    > [ERROR] Method 'combine' in package 'nl.wernerdegroot.applicatives.prelude' does not meet all criteria for code generation
    > [ERROR]  - Method requires exactly 3 type parameters, but found 4
    > [ERROR] -> [Help 1]
* Traversal (perhaps requires `Hkt<List<?>, T>`)
* combineAndThen
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
* Introduce monad, with a builder like `FastTuple` that allows:
    
    ```
    var personFuture = PersonFuture
      .bindFirstName(ctx -> getFutureFirstName())
      .letLastName(ctx -> getLastName())
      .bind(ctx -> somethingElse)
      .returning(ctx -> new Person(ctx.getFirstName(), ctx.getLastName());
    ```