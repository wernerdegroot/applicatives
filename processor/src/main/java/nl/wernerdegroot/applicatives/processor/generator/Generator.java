package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;

public abstract class Generator<This> {

    protected static final ClassName TUPLE_CLASS_NAME = ClassName.of("Tuples");
    protected static final String TUPLE_METHOD_NAME = "tuple";

    protected PackageName packageName;
    protected String classNameToGenerate;
    protected List<TypeParameter> classTypeParameters;
    protected List<TypeParameter> parameterTypeConstructorArguments;
    protected TypeParameter returnTypeConstructorArgument;
    protected Optional<Initializer> optionalInitializer;
    protected Accumulator accumulator;
    protected Optional<Finalizer> optionalFinalizer;
    protected List<String> inputParameterNames;
    protected String valueParameterName;
    protected String selfParameterName;
    protected String combineMethodName;
    protected String liftMethodName;
    protected int maxArity;

    protected abstract This getThis();

    public This withPackageName(PackageName packageName) {
        this.packageName = packageName;
        return getThis();
    }

    public This withClassNameToGenerate(String classNameToGenerate) {
        this.classNameToGenerate = classNameToGenerate;
        return getThis();
    }

    public This withClassTypeParameters(List<TypeParameter> classTypeParameters) {
        this.classTypeParameters = classTypeParameters;
        return getThis();
    }

    public This withParameterTypeConstructorArguments(List<TypeParameter> inputTypeConstructorArguments) {
        this.parameterTypeConstructorArguments = inputTypeConstructorArguments;
        return getThis();
    }

    public This withReturnTypeConstructorArgument(TypeParameter returnTypeConstructorArgument) {
        this.returnTypeConstructorArgument = returnTypeConstructorArgument;
        return getThis();
    }

    public This withOptionalInitializer(Optional<Initializer> optionalInitializer) {
        this.optionalInitializer = optionalInitializer;
        return getThis();
    }

    public This withAccumulator(Accumulator accumulator) {
        this.accumulator = accumulator;
        return getThis();
    }

    public This withOptionalFinalizer(Optional<Finalizer> optionalFinalizer) {
        this.optionalFinalizer = optionalFinalizer;
        return getThis();
    }

    public This withInputParameterNames(List<String> inputParameterNames) {
        this.inputParameterNames = inputParameterNames;
        return getThis();
    }

    public This withValueParameterName(String valueParameterName) {
        this.valueParameterName = valueParameterName;
        return getThis();
    }

    public This withSelfParameterName(String selfParameterName) {
        this.selfParameterName = selfParameterName;
        return getThis();
    }

    public This withCombineMethodName(String combineMethodName) {
        this.combineMethodName = combineMethodName;
        return getThis();
    }

    public This withLiftMethodName(String liftMethodName) {
        this.liftMethodName = liftMethodName;
        return getThis();
    }

    public This withMaxArity(int maxArity) {
        this.maxArity = maxArity;
        return getThis();
    }

    protected boolean hasInitializer() {
        return optionalInitializer.isPresent();
    }

    protected FullyQualifiedName getFullyQualifiedClassNameToGenerate() {
        return packageName.withClassName(ClassName.of(classNameToGenerate));
    }

    protected FullyQualifiedName getFullyQualifiedClassNameOfTupleClass() {
        return getFullyQualifiedClassNameToGenerate().withClassName(TUPLE_CLASS_NAME);
    }

    protected List<TypeArgument> getClassTypeParametersAsTypeArguments() {
        return classTypeParameters
                .stream()
                .map(TypeParameter::invariant)
                .collect(toList());
    }

    protected List<TypeParameter> takeParameterTypeConstructorArguments(int toTake) {
        return parameterTypeConstructorArguments.subList(0, toTake);
    }

    protected List<Type> getParameterTypeConstructorArgumentsAsTypes() {
        return parameterTypeConstructorArguments
                .stream()
                .map(TypeParameter::asType)
                .collect(toList());
    }

    protected List<Type> takeParameterTypeConstructorArgumentsAsTypes(int toTake) {
        return getParameterTypeConstructorArgumentsAsTypes().subList(0, toTake);
    }

    protected List<TypeArgument> takeParameterTypeConstructorArgumentsAsTypeArguments(int toTake) {
        return takeParameterTypeConstructorArgumentsAsTypeArguments(toTake, Type::invariant);
    }

    protected List<TypeArgument> takeParameterTypeConstructorArgumentsAsTypeArguments(int toTake, Function<Type, TypeArgument> variance) {
        return takeParameterTypeConstructorArgumentsAsTypes(toTake)
                .stream()
                .map(variance)
                .collect(toList());
    }

    protected Type getReturnType() {
        return returnTypeConstructorArgument.asType().using(getReturnTypeConstructor());
    }

    protected TypeConstructor getReturnTypeConstructor() {
        return optionalFinalizer.map(Finalizer::getFinalizedTypeConstructor).orElse(accumulator.getAccumulatedTypeConstructor());
    }

    protected List<Type> takeParameterTypes(int toTake) {
        return takeParameterTypes(toTake, getFirstParameterTypeConstructor(), getOtherParametersTypeConstructor());
    }

    protected List<Type> takeParameterTypes(int toTake, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor) {
        List<Type> result = new ArrayList<>();

        getParameterTypeConstructorArgumentsAsTypes()
                .stream()
                .limit(1)
                .map(firstParameterTypeConstructor::apply)
                .forEachOrdered(result::add);
        getParameterTypeConstructorArgumentsAsTypes()
                .stream()
                .limit(toTake)
                .skip(1)
                .map(otherParametersTypeConstructor::apply)
                .forEachOrdered(result::add);
        return result;
    }

    protected TypeConstructor getFirstParameterTypeConstructor() {
        // If the user provided an initializer method, all parameters
        // use the same type constructor (`inputTypeConstructor`).
        // If no initializer method is available, the first parameter
        // uses a different type constructor than the other parameters
        // (`partiallyAccumulatedTypeConstructor`).
        return hasInitializer()
                ? getOtherParametersTypeConstructor()
                : accumulator.getPartiallyAccumulatedTypeConstructor();
    }

    protected TypeConstructor getOtherParametersTypeConstructor() {
        return accumulator.getInputTypeConstructor();
    }

    protected List<String> takeInputParameterNames(int toTake) {
        return inputParameterNames.subList(0, toTake);
    }

    protected FullyQualifiedName fullyQualifiedNameOfFunction(int arity) {
        return arity == 2 ? BI_FUNCTION.getFullyQualifiedName() : fullyQualifiedNameOfArbitraryArityFunction(arity);
    }

    protected Type lambdaParameterType(TypeConstructor returnTypeConstructor, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor, int arity) {
        return lambdaType(returnTypeConstructor, firstParameterTypeConstructor, otherParametersTypeConstructor, arity, Type::contravariant, Type::covariant);
    }

    protected Type lambdaReturnType(TypeConstructor returnTypeConstructor, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor, int arity) {
        return lambdaType(returnTypeConstructor, firstParameterTypeConstructor, otherParametersTypeConstructor, arity, Type::invariant, Type::invariant);
    }

    protected Type lambdaType(TypeConstructor returnTypeConstructor, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor, int arity, Function<Type, TypeArgument> parameterVariance, Function<Type, TypeArgument> returnTypeVariance) {
        List<TypeArgument> typeArguments = new ArrayList<>();
        takeParameterTypes(arity, firstParameterTypeConstructor, otherParametersTypeConstructor)
                .stream()
                .map(parameterVariance)
                .forEachOrdered(typeArguments::add);
        typeArguments.add(returnTypeVariance.apply(returnTypeConstructorArgument.asType().using(returnTypeConstructor)));

        return Type.concrete(fullyQualifiedNameOfFunction(arity), typeArguments);
    }

    protected FullyQualifiedName fullyQualifiedNameOfArbitraryArityFunction(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Function" + arity);
    }

    protected FullyQualifiedName fullyQualifiedNameOfTupleWithArity(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Tuple" + arity);
    }

}
