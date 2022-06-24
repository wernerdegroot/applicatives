package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.THIS;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static nl.wernerdegroot.applicatives.processor.generator.MethodGenerator.method;

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
    protected String combineMethodToGenerate;
    protected String liftMethodToGenerate;
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

    public This withCombineMethodToGenerate(String combineMethodName) {
        this.combineMethodToGenerate = combineMethodName;
        return getThis();
    }

    public This withLiftMethodToGenerate(String liftMethodName) {
        this.liftMethodToGenerate = liftMethodName;
        return getThis();
    }

    public This withMaxArity(int maxArity) {
        this.maxArity = maxArity;
        return getThis();
    }

    protected Optional<MethodGenerator> optionalAbstractInitializerMethod() {
        return optionalInitializer.map(initializer ->
                method()
                        .withTypeParameters(returnTypeConstructorArgument.getName())
                        .withReturnType(returnTypeConstructorArgument.asType().using(initializer.getInitializedTypeConstructor()))
                        .withName(initializer.getName())
                        .withParameter(returnTypeConstructorArgument.asType().using(initializer.getToInitializeTypeConstructor()), valueParameterName)
        );
    }

    protected abstract List<TypeParameter> getAccumulatorTypeParameters();

    protected abstract List<Parameter> getAdditionalAccumulatorParameters();

    protected MethodGenerator abstractAccumulatorMethod() {
        int arity = 2;

        return method()
                .withTypeParameters(getAccumulatorTypeParameters())
                .withReturnType(getAccumulatorReturnType())
                .withName(accumulator.getName())
                .withParameterTypes(takeParameterTypes(arity, accumulator.getPartiallyAccumulatedTypeConstructor(), accumulator.getInputTypeConstructor()))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameters(getAdditionalAccumulatorParameters());
    }

    protected Optional<MethodGenerator> optionalAbstractFinalizerMethod() {
        return optionalFinalizer.map(finalizer ->
                method()
                        .withTypeParameters(returnTypeConstructorArgument.getName())
                        .withReturnType(returnTypeConstructorArgument.asType().using(finalizer.getFinalizedTypeConstructor()))
                        .withName(finalizer.getName())
                        .withParameter(returnTypeConstructorArgument.asType().using(finalizer.getToFinalizeTypeConstructor()), valueParameterName)
        );
    }

    protected abstract List<Parameter> getAdditionalParametersForCombineMethod(int arity);

    protected abstract List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForCombineMethod(int arity);

    protected abstract List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForCombineMethodWithArityTwo();

    protected abstract List<String> getAdditionalArgumentsToPassToTupleMethodForCombineMethod(int arity);

    protected MethodGenerator combineMethodWithArityTwo() {
        int arity = 2;
        List<String> inputParameterNames = takeInputParameterNames(arity);
        String firstInputParameterName = inputParameterNames.get(0);
        String secondInputParameterName = inputParameterNames.get(1);
        String methodBody = methodCall()
                .withObjectPath(THIS)
                .withMethodName(accumulator.getName())
                .withArguments(
                        initializeIfHasInitializer(THIS, firstInputParameterName),
                        secondInputParameterName
                )
                .withArguments(getAdditionalArgumentsToPassOnToAccumulatorMethodForCombineMethodWithArityTwo())
                .generate();

        return combineMethodWithArity(arity, finalizeIfHasFinalizer(THIS, methodBody));
    }

    protected MethodGenerator combineMethodWithArity(int arity) {
        String methodBody = methodCall()
                .withObjectPath(THIS)
                .withMethodName(accumulator.getName())
                .withArguments(
                        methodCall()
                                .withType(getFullyQualifiedClassNameOfTupleClass())
                                .withTypeArguments(takeParameterTypeConstructorArgumentsAsTypeArguments(arity - 1))
                                .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                .withMethodName(TUPLE_METHOD_NAME)
                                .withArguments(THIS)
                                .withArguments(takeInputParameterNames(arity - 1))
                                .withArguments(getAdditionalArgumentsToPassToTupleMethodForCombineMethod(arity))
                                .generate(),
                        inputParameterNames.get(arity - 1)
                )
                .withArguments(getAdditionalArgumentsToPassOnToAccumulatorMethodForCombineMethod(arity))
                .generate();

        return combineMethodWithArity(arity, finalizeIfHasFinalizer(THIS, methodBody));
    }

    protected MethodGenerator combineMethodWithArity(int arity, String methodBody) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(getReturnType())
                .withName(combineMethodToGenerate)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameters(getAdditionalParametersForCombineMethod(arity))
                .withReturnStatement(methodBody);
    }

    protected abstract List<Parameter> getAdditionalLiftMethodParametersToPassOnToCombineMethod(int arity);

    protected List<MethodGenerator> liftMethods() {
        List<MethodGenerator> liftMethods = new ArrayList<>();
        IntStream.rangeClosed(2, maxArity).forEachOrdered(arity -> {
            liftMethods.add(liftMethodWithArity(arity));
        });
        return liftMethods;
    }

    private MethodGenerator liftMethodWithArity(int arity) {

        List<String> additionalLiftMethodParameterNamesToPassOnToCombineMethod = getAdditionalLiftMethodParametersToPassOnToCombineMethod(arity)
                .stream()
                .map(Parameter::getName)
                .collect(toList());

        return liftMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(THIS)
                        .withMethodName(combineMethodToGenerate)
                        .withArguments(takeInputParameterNames(arity))
                        .withArguments(additionalLiftMethodParameterNamesToPassOnToCombineMethod)
                        .generate()
        );
    }

    private MethodGenerator liftMethodWithArity(int arity, String lambdaBody) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(lambdaReturnType(getReturnTypeConstructor(), getOtherParametersTypeConstructor(), getFirstParameterTypeConstructor(), arity))
                .withName(liftMethodToGenerate)
                .withParameters(getAdditionalLiftMethodParametersToPassOnToCombineMethod(arity))
                .withReturnStatement(
                        lambda()
                                .withParameterNames(takeInputParameterNames(arity))
                                .withExpression(lambdaBody)
                                .multiline()
                );
    }

    protected abstract List<Parameter> getAdditionalTupleMethodParametersToPassOnToTupleMethod(int arity);

    protected abstract List<TypeArgument> getTypeArgumentsToPassOnToAccumulatorMethodForTupleMethod(int arity);

    protected abstract List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethod(int arity);

    protected abstract List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethodWithArityTwo();

    protected List<MethodGenerator> tupleMethods() {
        List<MethodGenerator> tupleMethods = new ArrayList<>();

        if (maxArity >= 3) {
            tupleMethods.add(tupleMethodWithArityTwo());
        }

        IntStream.range(3, maxArity)
                .forEachOrdered(arity -> {
                    tupleMethods.add(tupleMethodWithArity(arity));
                });

        return tupleMethods;
    }

    protected MethodGenerator tupleMethodWithArityTwo() {
        String firstInputParameterName = inputParameterNames.get(0);
        String secondInputParameterName = inputParameterNames.get(1);
        int arity = 2;
        return tupleMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(accumulator.getName())
                        .withArguments(
                                initializeIfHasInitializer(selfParameterName, firstInputParameterName),
                                secondInputParameterName
                        )
                        .withArguments(getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethodWithArityTwo())
                        .generate()
        );
    }

    protected MethodGenerator tupleMethodWithArity(int arity) {

        List<String> additionalTupleMethodParameterNamesToPassOnToTupleMethod = getAdditionalTupleMethodParametersToPassOnToTupleMethod(arity)
                .stream()
                .map(Parameter::getName)
                .collect(toList());

        return tupleMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withTypeArguments(getTypeArgumentsToPassOnToAccumulatorMethodForTupleMethod(arity))
                        .withMethodName(accumulator.getName())
                        .withArguments(
                                methodCall()
                                        .withType(getFullyQualifiedClassNameOfTupleClass())
                                        .withTypeArguments(takeParameterTypeConstructorArgumentsAsTypeArguments(arity - 1))
                                        .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                        .withMethodName(TUPLE_METHOD_NAME)
                                        .withArguments(selfParameterName)
                                        .withArguments(takeInputParameterNames(arity - 1))
                                        .withArguments(additionalTupleMethodParameterNamesToPassOnToTupleMethod)
                                        .generate(),
                                inputParameterNames.get(arity - 1)
                        )
                        .withArguments(getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethod(arity))
                        .generate()
        );
    }

    protected MethodGenerator tupleMethodWithArity(int arity, String methodBody) {
        return method()
                .withModifiers(PUBLIC, STATIC)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                // Since these are all static methods, that don't have access to any class type parameters
                // we need to make sure that the class type parameters are available as additional method
                // type parameters:
                .withTypeParameters(classTypeParameters)
                .withReturnType(getTupleMethodReturnType(arity))
                .withName(TUPLE_METHOD_NAME)
                .withParameter(getFullyQualifiedClassNameToGenerate().with(getClassTypeParametersAsTypeArguments()), selfParameterName)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameters(getAdditionalTupleMethodParametersToPassOnToTupleMethod(arity))
                .withReturnStatement(methodBody);
    }

    private Type getTupleMethodReturnType(int arity) {
        return Type.concrete(fullyQualifiedNameOfTupleWithArity(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant)).using(getTupleMethodReturnTypeConstructor(arity));
    }

    private TypeConstructor getTupleMethodReturnTypeConstructor(int arity) {
        // If the arity is equal to zero, we are dealing with a special case. We use the initializer method
        // to wrap an empty tuple using the `initializedTypeConstructor`. We can pass that as the
        // input to the accumulator method that the user defined.
        if (arity == 0) {
            return optionalInitializer
                    .map(Initializer::getInitializedTypeConstructor)
                    .orElseThrow(() -> new IllegalStateException("An initializer method is required for a tuple method of arity zero"));
        } else {
            return accumulator.getAccumulatedTypeConstructor();
        }
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

    protected Type getAccumulatorReturnType() {
        return returnTypeConstructorArgument.asType().using(accumulator.getAccumulatedTypeConstructor());
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

    protected String initializeIfHasInitializer(String objectName, String toInitialize) {
        return optionalInitializer.map(initializer -> methodCall().withObjectPath(objectName).withMethodName(initializer.getName()).withArguments(toInitialize).generate()).orElse(toInitialize);
    }

    protected String finalizeIfHasFinalizer(String objectName, String methodBody) {
        return optionalFinalizer
                .map(Finalizer::getName)
                .map(finalizerMethodName -> methodCall().withObjectPath(objectName).withMethodName(finalizerMethodName).withArguments(methodBody).generate())
                .orElse(methodBody);
    }

    protected FullyQualifiedName fullyQualifiedNameOfArbitraryArityFunction(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Function" + arity);
    }

    protected FullyQualifiedName fullyQualifiedNameOfTupleWithArity(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Tuple" + arity);
    }

}
