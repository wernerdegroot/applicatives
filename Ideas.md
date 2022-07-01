* JSON benchmarks at https://github.com/fabienrenaud/java-json-benchmark/blob/master/src/main/java/com/github/fabienrenaud/jjb/stream/UsersStreamDeserializer.java
* Add tests for builder with _just_ an intializer, or _just_ a finalizer.
* Can we generate a lift signature from a combine signature? Should be possible. Definitely for the simplified ones.
* Support for static methods
* Use processor (perhaps older version, to prevent circular dependencies) to generate `Validated` methods.
* Test deconstructor (and other things too) when using a constructor or deconstructor of a generic type like Tuple3.
* Implement a `@Covariant.Instance` for instance-methods (maybe only useful for monads?)
* Remove Google testing library because it's all pretty easy to do manually (check TypeConstructor tests)
* Static -> class, abstract instance method -> abstract class
* Because builder-methods are not for exposing, perhaps pass reference to builder object instead (make this configurable `style = "inheritance"` or `style = "delegation"`).
* combineAndThen
* Document process to publish
* Github Actions
* Code coverage
* Remove Google auto-registration. Why again?
* element.getKind == record?
* Increase coverage of processor-classes (now almost 0%)
* Imports
* Introduce `ParameterName` and `ClassName`?
* Allow subtypes in return type (return `CartesianProductList` in applicative for `List`)
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
* Implement free applicative functors
