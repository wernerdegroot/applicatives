# Domain

## Names

In this package (and even in other packages) you will find some names that deserve further explanation. Examples include "primary method type parameters", "secondary parameter names" and more. What do they mean? Read on!

Given:

```java
class Pairs<T> implements PairsMixin<T> {

    @Override
    @Covariant(className = "PairsMixin")
    public <A, B, C> Pair<T, A> compose(Pair<T, ? extends A> left, Pair<T, ? extends B> right, Function<? super A, ? super B, ? extends C> fn, BinaryOperator<T> composeFirst) {
        T first = composeFirst.apply(left.getFirst(), right.getFirst());
        C second = fn.apply(left.getSecond(), right.getSecond());
        return Pair.of(first, second);
    }
}
```

* The *class type parameters* (if any) are the type parameters of the containing class (in this case `T`). The generated class `PairsMixin` will have the same type parameters.
* The *primary method type parameters* are the type parameters that directly participate in the composition (in this case `A`, `B` and `C`)
* The *secondary method type parameters* (if any) are the other type parameters of the composition-method (in this case there are none)
* The *primary parameter names* are the parameters that directly participate in the composition (in this case `Pair<T, ? extends A> left` and `Pair<T, ? extends B> right`)
* The *secondary parameter names* are the names of the other parameters of the composition-method (in this case `BinaryOperator<T> composeFirst`)
* The *parameter type constructor* is the type constructor (of kind `* -> *`) that both primary parameters share (in this case it's `Pair<T, ? extends *>`)
* The *result type constructor* is the type constructor (of kind `* -> *`) that the result shares with the parameters, up to covariance (in this case it's `Pair<T, *>`)
