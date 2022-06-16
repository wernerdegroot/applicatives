* Docs for contravariant: https://www.youtube.com/watch?v=IJ_bVVsQhvc (counter-intuitive = contra-intuitive)
* Contravariant (selective) functors 
* Test deconstructor (and other things too) when using a constructor or deconstructor of a generic type like Tuple3.
* Implement a `@Covariant.Instance` for instance-methods (maybe only useful for monads?)
* Remove Google testing library because it's all pretty easy to do manually (check TypeConstructor tests)
* Static -> class, abstract instance method -> abstract class
* Because builder-methods are not for exposing, perhaps pass reference to builder object instead (make this configurable `style = "inheritance"` or `style = "delegation"`).
* combineAndThen
* Implement free applicative functors
* Traversal (perhaps requires `Hkt<List<?>, T>`)
* Document process to publish
* Github Actions
* Code coverage
* Remove Google auto-registration. Why again?
* element.getKind == record?
* Imports
* Introduce `ParameterName` and `ClassName`?
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
* Benji Webers decomposition function `<T> T (C source, BiFunction<A, B, T> extractor)` instead of the current Decomposition
* Alternative, Decidable. Also see https://www.youtube.com/watch?v=IJ_bVVsQhvc and https://en.wikibooks.org/wiki/Haskell/Alternative_and_MonadPlus
* Explicit type arguments to method call are not `TypeArgument` (for a data type). They can't specify variance. Used in generators.
* Fix
        ```
        default This withType(GenericType type) {
            return withType(FullyQualifiedName.of(type.getName().raw()));
        }
        ```
