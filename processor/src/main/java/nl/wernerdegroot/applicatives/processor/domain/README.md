# Domain

We have to assign some meaningful names to things, to be able to manipulate them in code without having to describe them every them.

Consider

```java
public class Lists {

    @Initializer
    public <A> ArrayList<A> singleton(A value) {
        ...
    }

    @Accumulator
    public <A, B, C> ArrayList<C> combine(ArrayList<? extends A> left, List<? extends B> right, BiFunction<? super A, ? super B, ? extends C> fn) {
        ...
    }

    @Finalizer
    public <A> List<A> finalize(ArrayList<? extends A> toFinalize) {
        ...
    }
}
```

* **Initializer method** -- A (optional) method to wrap a value in an **initialized type constructor**.
* **Accumulator method** -- A (mandatory) method to combine two values wrapped in type constructors (`left` wrapped by a **partially accumulated type constructor** and `right` wrapped by a **input type constructor**) into one (wrapped by a **accumulated type constructor**).
* **Finalizer method** -- A (optional) method to do some last minute transformations from a value wrapped by a **to finalize type constructor** into a value wrapped by a **finalized type constructor**.

Overloads like the following are generated:

```java
interface Generated {
    
    default <P1, P2, R> List<R> combine(List<? extends P1> first, List<? extends P2> second, BiFunction<? super P1, ? super P2, ? extends R> fn) {
        ...
    }
}
```

* The parameters `first` and `second` are the **parameters** (a value of one of the **type parameters**, wrapped by the appropriate type constructor, which is usually a **input type constructor**).
* The `BiFunction` is called the **combinator parameter**.
* The method returns a value of the **result type parameter** wrapped by the appropriate type constructor, which is usually the **finalized type constructor**.