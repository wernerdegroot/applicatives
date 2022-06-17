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
import static nl.wernerdegroot.applicatives.processor.Ordinals.getterForIndex;
import static nl.wernerdegroot.applicatives.processor.Ordinals.withouterForIndex;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.FUNCTION;
import static nl.wernerdegroot.applicatives.processor.generator.ClassOrInterfaceGenerator.classOrInterface;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
import static nl.wernerdegroot.applicatives.processor.generator.Lines.lines;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static nl.wernerdegroot.applicatives.processor.generator.MethodGenerator.method;
import static nl.wernerdegroot.applicatives.processor.generator.MethodReferenceGenerator.methodReference;

public class ContravariantGenerator {

    private static final ClassName TUPLE_CLASS_NAME = ClassName.of("Tuples");
    private static final String TUPLE_METHOD_NAME = "tuple";
    private static final String DECOMPOSE_METHOD_NAME = "decompose";

    private PackageName packageName;
    private String classNameToGenerate;
    private List<TypeParameter> classTypeParameters;
    private Optional<CovariantInitializer> optionalInitializer;
    private ContravariantAccumulator accumulator;
    private Optional<CovariantFinalizer> optionalFinalizer;
    private List<TypeParameter> parameterTypeConstructorArguments;
    private TypeParameter intermediateTypeConstructorArgument;
    private TypeParameter returnTypeConstructorArgument;
    private List<String> inputParameterNames;
    private String decompositionParameterName;
    private String valueParameterName;
    private String selfParameterName;
    private String toIntermediateParameterName;
    private String extractLeftParameterName;
    private String extractRightParameterName;
    private String combineMethodName;
    private String liftMethodName;
    private int maxArity;

    public static ContravariantGenerator generator() {
        return new ContravariantGenerator();
    }

    public ContravariantGenerator withPackageName(PackageName packageName) {
        this.packageName = packageName;
        return this;
    }

    public ContravariantGenerator withClassNameToGenerate(String classNameToGenerate) {
        this.classNameToGenerate = classNameToGenerate;
        return this;
    }

    public ContravariantGenerator withClassTypeParameters(List<TypeParameter> classTypeParameters) {
        this.classTypeParameters = classTypeParameters;
        return this;
    }

    public ContravariantGenerator withParameterTypeConstructorArguments(List<TypeParameter> inputTypeConstructorArguments) {
        this.parameterTypeConstructorArguments = inputTypeConstructorArguments;
        return this;
    }

    public ContravariantGenerator withIntermediateTypeConstructorArgument(TypeParameter intermediateTypeConstructorArgument) {
        this.intermediateTypeConstructorArgument = intermediateTypeConstructorArgument;
        return this;
    }

    public ContravariantGenerator withReturnTypeConstructorArgument(TypeParameter returnTypeConstructorArgument) {
        this.returnTypeConstructorArgument = returnTypeConstructorArgument;
        return this;
    }

    public ContravariantGenerator withOptionalInitializer(Optional<CovariantInitializer> optionalInitializer) {
        this.optionalInitializer = optionalInitializer;
        return this;
    }

    public ContravariantGenerator withAccumulator(ContravariantAccumulator accumulator) {
        this.accumulator = accumulator;
        return this;
    }

    public ContravariantGenerator withOptionalFinalizer(Optional<CovariantFinalizer> optionalFinalizer) {
        this.optionalFinalizer = optionalFinalizer;
        return this;
    }

    public ContravariantGenerator withInputParameterNames(List<String> inputParameterNames) {
        this.inputParameterNames = inputParameterNames;
        return this;
    }

    public ContravariantGenerator withDecompositionParameterName(String decompositionParameterName) {
        this.decompositionParameterName = decompositionParameterName;
        return this;
    }

    public ContravariantGenerator withValueParameterName(String valueParameterName) {
        this.valueParameterName = valueParameterName;
        return this;
    }

    public ContravariantGenerator withSelfParameterName(String selfParameterName) {
        this.selfParameterName = selfParameterName;
        return this;
    }

    public ContravariantGenerator withToIntermediateParameterName(String toIntermediateParameterName) {
        this.toIntermediateParameterName = toIntermediateParameterName;
        return this;
    }

    public ContravariantGenerator withExtractLeftParameterName(String extractLeftParameterName) {
        this.extractLeftParameterName = extractLeftParameterName;
        return this;
    }

    public ContravariantGenerator withExtractRightParameterName(String extractRightParameterName) {
        this.extractRightParameterName = extractRightParameterName;
        return this;
    }

    public ContravariantGenerator withCombineMethodName(String combineMethodName) {
        this.combineMethodName = combineMethodName;
        return this;
    }

    public ContravariantGenerator withLiftMethodName(String liftMethodName) {
        this.liftMethodName = liftMethodName;
        return this;
    }

    public ContravariantGenerator withMaxArity(int maxArity) {
        this.maxArity = maxArity;
        return this;
    }

    public String generate() {
        Lines lines = lines();
        lines.append(PACKAGE + SPACE + packageName.raw() + SEMICOLON);
        lines.append(EMPTY_LINE);

        // Place to gather methods:
        List<MethodGenerator> methods = new ArrayList<>();

        // If the client provided an initializer method, generate an abstract
        // method for it and append it to the methods.
        optionalAbstractInitializerMethod().ifPresent(methods::add);

        methods.add(abstractAccumulatorMethod());

        // If the client provided a finalizer method, generate an abstract
        // method for it and append it to the methods.
        optionalAbstractFinalizerMethod().ifPresent(methods::add);

        // Continue adding the combine- and lift-methods.
        methods.addAll(combineMethods());
        methods.addAll(liftMethods());

        lines.append(
                classOrInterface()
                        .asInterface()
                        .withModifiers(PUBLIC)
                        .withName(classNameToGenerate)
                        .withTypeParameters(classTypeParameters)
                        .withBody(methods.stream().collect(Lines.collecting(MethodGenerator::lines)))
                        .withBody(EMPTY_LINE)
                        .withBody(
                                classOrInterface()
                                        .asClass()
                                        .withName(TUPLE_CLASS_NAME.raw())
                                        .withBody(tupleMethods().stream().collect(Lines.collecting(MethodGenerator::lines)))
                                        .lines()
                        )
                        .lines()
        );

        return String.join(LINE_FEED, lines);
    }

    private Optional<MethodGenerator> optionalAbstractInitializerMethod() {
        return optionalInitializer.map(initializer ->
                method()
                        .withTypeParameters(returnTypeConstructorArgument.getName())
                        .withReturnType(returnTypeConstructorArgument.asType().using(initializer.getInitializedTypeConstructor()))
                        .withName(initializer.getName())
                        .withParameter(returnTypeConstructorArgument.asType().using(initializer.getToInitializeTypeConstructor()), valueParameterName)
        );
    }

    private MethodGenerator abstractAccumulatorMethod() {
        int arity = 2;

        return method()
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(intermediateTypeConstructorArgument.getName())
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(returnTypeConstructorArgument.asType().using(accumulator.getAccumulatedTypeConstructor()))
                .withName(accumulator.getName())
                .withParameterTypes(takeParameterTypes(arity, accumulator.getPartiallyAccumulatedTypeConstructor(), accumulator.getInputTypeConstructor()))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(
                        FUNCTION.with(returnTypeConstructorArgument.contravariant(), intermediateTypeConstructorArgument.covariant()),
                        toIntermediateParameterName
                )
                .withParameter(
                        FUNCTION.with(intermediateTypeConstructorArgument.contravariant(), parameterTypeConstructorArguments.get(0).covariant()),
                        extractLeftParameterName
                )
                .withParameter(
                        FUNCTION.with(intermediateTypeConstructorArgument.contravariant(), parameterTypeConstructorArguments.get(1).covariant()),
                        extractRightParameterName
                );
    }

    private Optional<MethodGenerator> optionalAbstractFinalizerMethod() {
        return optionalFinalizer.map(finalizer ->
                method()
                        .withTypeParameters(returnTypeConstructorArgument.getName())
                        .withReturnType(returnTypeConstructorArgument.asType().using(finalizer.getFinalizedTypeConstructor()))
                        .withName(finalizer.getName())
                        .withParameter(returnTypeConstructorArgument.asType().using(finalizer.getToFinalizeTypeConstructor()), valueParameterName)
        );
    }

    private List<MethodGenerator> combineMethods() {
        List<MethodGenerator> methods = new ArrayList<>();
        methods.add(this.combineMethodWithArityTwo());
        methods.add(this.combineMethodForDecomposableWithArity(2));
        IntStream.rangeClosed(3, maxArity).forEachOrdered(arity -> {
            methods.add(combineMethodWithArity(arity));
            methods.add(combineMethodForDecomposableWithArity(arity));
        });
        return methods;
    }

    private MethodGenerator combineMethodWithArityTwo() {
        int arity = 2;
        List<String> inputParameterNames = takeInputParameterNames(arity);
        String firstInputParameterName = inputParameterNames.get(0);
        String secondInputParameterName = inputParameterNames.get(1);
        String methodBody = methodCall()
                .withObjectPath(THIS)
                .withMethodName(accumulator.getName())
                .withArguments(
                        optionalInitializer
                                .map(initializer -> methodCall().withObjectPath(THIS).withMethodName(initializer.getName()).withArguments(firstInputParameterName).generate())
                                .orElse(firstInputParameterName),
                        secondInputParameterName,
                        methodReference().withObjectPath(decompositionParameterName).withMethodName(DECOMPOSE_METHOD_NAME).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(getterForIndex(0)).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(getterForIndex(1)).generate()
                )
                .generate();

        return combineMethodWithArity(
                arity,
                optionalFinalizer
                        .map(CovariantFinalizer::getName)
                        .map(finalizerMethodName -> methodCall().withObjectPath(THIS).withMethodName(finalizerMethodName).withArguments(methodBody).generate())
                        .orElse(methodBody)
        );
    }

    private MethodGenerator combineMethodWithArity(int arity) {
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
                                .generate(),
                        inputParameterNames.get(arity - 1),
                        methodReference().withObjectPath(decompositionParameterName).withMethodName(DECOMPOSE_METHOD_NAME).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(withouterForIndex(arity - 1)).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(getterForIndex(arity - 1)).generate()
                )
                .generate();

        return combineMethodWithArity(
                arity,
                optionalFinalizer
                        .map(CovariantFinalizer::getName)
                        .map(finalizerMethodName -> methodCall().withObjectPath(THIS).withMethodName(finalizerMethodName).withArguments(methodBody).generate())
                        .orElse(methodBody)
        );
    }

    private MethodGenerator combineMethodWithArity(int arity, String methodBody) {
        List<TypeArgument> decompositionTypeArguments = new ArrayList<>();
        decompositionTypeArguments.add(returnTypeConstructorArgument.asType().contravariant());
        decompositionTypeArguments.addAll(takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(getReturnType())
                .withName(combineMethodName)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(
                        Type.concrete(fullyQualifiedNameOfDecomposition(arity), decompositionTypeArguments),
                        decompositionParameterName
                )
                .withReturnStatement(methodBody);
    }

    private MethodGenerator combineMethodForDecomposableWithArity(int arity) {
        return combineMethodForDecomposableWithArity(
                arity,
                methodCall()
                        .withObjectPath(THIS)
                        .withTypeArguments(takeParameterTypeConstructorArgumentsAsTypeArguments(arity))
                        .withTypeArguments(returnTypeConstructorArgument.asType().invariant())
                        .withMethodName(combineMethodName)
                        .withArguments(takeInputParameterNames(arity))
                        .withArguments(methodReference().withType(returnTypeConstructorArgument.asType()).withMethodName(DECOMPOSE_METHOD_NAME).generate())
                        .generate()
        );
    }

    private MethodGenerator combineMethodForDecomposableWithArity(int arity, String methodBody) {
        List<TypeArgument> decompositionTypeArguments = new ArrayList<>();
        decompositionTypeArguments.add(returnTypeConstructorArgument.asType().contravariant());
        decompositionTypeArguments.addAll(takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName().extending(Type.concrete(fullyQualifiedNameOfDecomposable(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity))))
                .withReturnType(getReturnType())
                .withName(combineMethodName)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withReturnStatement(methodBody);
    }

    private List<MethodGenerator> liftMethods() {
        return IntStream.range(2, maxArity)
                .mapToObj(this::liftMethodWithArity)
                .collect(toList());
    }

    private MethodGenerator liftMethodWithArity(int arity) {
        return liftMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(THIS)
                        .withMethodName(combineMethodName)
                        .withArguments(takeInputParameterNames(arity))
                        .withArguments(decompositionParameterName)
                        .generate()
        );
    }

    private MethodGenerator liftMethodWithArity(int arity, String lambdaBody) {
        List<TypeArgument> decompositionTypeArguments = new ArrayList<>();
        decompositionTypeArguments.add(returnTypeConstructorArgument.asType().contravariant());
        decompositionTypeArguments.addAll(takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(lambdaReturnType(getReturnTypeConstructor(), getOtherParametersTypeConstructor(), getFirstParameterTypeConstructor(), arity))
                .withName(liftMethodName)
                .withParameter(
                        Type.concrete(fullyQualifiedNameOfDecomposition(arity), decompositionTypeArguments),
                        decompositionParameterName
                )
                .withReturnStatement(
                        lambda()
                                .withParameterNames(takeInputParameterNames(arity))
                                .withExpression(lambdaBody)
                                .multiline()
                );
    }

    private List<MethodGenerator> tupleMethods() {
        List<MethodGenerator> tupleMethods = new ArrayList<>();

        tupleMethods.add(tupleMethodWithArityTwo());

        IntStream.range(3, maxArity)
                .forEachOrdered(arity -> {
                    tupleMethods.add(tupleMethodWithArity(arity));
                });

        return tupleMethods;
    }

    private MethodGenerator tupleMethodWithArityTwo() {
        String firstInputParameterName = inputParameterNames.get(0);
        String secondInputParameterName = inputParameterNames.get(1);
        int arity = 2;
        return tupleMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(accumulator.getName())
                        .withArguments(
                                optionalInitializer.map(initializer -> methodCall().withObjectPath(selfParameterName).withMethodName(initializer.getName()).withArguments(firstInputParameterName).generate()).orElse(firstInputParameterName),
                                secondInputParameterName,
                                methodCall()
                                        .withType(FUNCTION.getFullyQualifiedName())
                                        .withMethodName("identity")
                                        .generate(),
                                methodReference()
                                        .withType(fullyQualifiedNameOfTupleWithArity(arity))
                                        .withMethodName(getterForIndex(0))
                                        .generate(),
                                methodReference()
                                        .withType(fullyQualifiedNameOfTupleWithArity(arity))
                                        .withMethodName(getterForIndex(1))
                                        .generate()
                        )
                        .generate()
        );
    }

    private MethodGenerator tupleMethodWithArity(int arity) {
        return tupleMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(accumulator.getName())
                        .withArguments(
                                methodCall()
                                        .withType(getFullyQualifiedClassNameOfTupleClass())
                                        .withMethodName(TUPLE_METHOD_NAME)
                                        .withArguments(selfParameterName)
                                        .withArguments(takeInputParameterNames(arity - 1))
                                        .generate(),
                                inputParameterNames.get(arity - 1),
                                methodCall()
                                        .withType(FUNCTION.getFullyQualifiedName())
                                        .withMethodName("identity")
                                        .generate(),
                                methodReference()
                                        .withType(fullyQualifiedNameOfTupleWithArity(arity))
                                        .withMethodName(withouterForIndex(arity - 1))
                                        .generate(),
                                methodReference()
                                        .withType(fullyQualifiedNameOfTupleWithArity(arity))
                                        .withMethodName(getterForIndex(arity - 1))
                                        .generate()
                        )
                        .generate()
        );
    }

    private MethodGenerator tupleMethodWithArity(int arity, String methodBody) {
        return method()
                .withModifiers(PUBLIC, STATIC)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                // Since these are all static methods, that don't have access to any class type parameters
                // we need to make sure that the class type parameters are available as additional method
                // type parameters:
                .withTypeParameters(classTypeParameters)
                .withReturnType(Type.concrete(fullyQualifiedNameOfTupleWithArity(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant)).using(getTupleMethodReturnTypeConstructor(arity)))
                .withName(TUPLE_METHOD_NAME)
                .withParameter(getFullyQualifiedClassNameToGenerate().with(getClassTypeParametersAsTypeArguments()), selfParameterName)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withReturnStatement(methodBody);
    }

    private Type lambdaReturnType(TypeConstructor returnTypeConstructor, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor, int arity) {
        return lambdaType(returnTypeConstructor, firstParameterTypeConstructor, otherParametersTypeConstructor, arity, Type::invariant, Type::invariant);
    }

    private Type lambdaType(TypeConstructor returnTypeConstructor, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor, int arity, Function<Type, TypeArgument> parameterVariance, Function<Type, TypeArgument> returnTypeVariance) {
        List<TypeArgument> typeArguments = new ArrayList<>();
        takeParameterTypes(arity, firstParameterTypeConstructor, otherParametersTypeConstructor)
                .stream()
                .map(parameterVariance)
                .forEachOrdered(typeArguments::add);
        typeArguments.add(returnTypeVariance.apply(returnTypeConstructorArgument.asType().using(returnTypeConstructor)));

        return Type.concrete(fullyQualifiedNameOfFunction(arity), typeArguments);
    }

    private FullyQualifiedName getFullyQualifiedClassNameToGenerate() {
        return packageName.withClassName(ClassName.of(classNameToGenerate));
    }

    private FullyQualifiedName getFullyQualifiedClassNameOfTupleClass() {
        return getFullyQualifiedClassNameToGenerate().withClassName(TUPLE_CLASS_NAME);
    }

    private TypeConstructor getTupleMethodReturnTypeConstructor(int arity) {
        return accumulator.getAccumulatedTypeConstructor();
    }

    private List<String> takeInputParameterNames(int toTake) {
        return inputParameterNames.subList(0, toTake);
    }

    private List<TypeArgument> getClassTypeParametersAsTypeArguments() {
        return classTypeParameters
                .stream()
                .map(TypeParameter::invariant)
                .collect(toList());
    }

    private List<TypeParameter> takeParameterTypeConstructorArguments(int toTake) {
        return parameterTypeConstructorArguments.subList(0, toTake);
    }

    private List<Type> takeParameterTypes(int toTake) {
        return takeParameterTypes(toTake, getFirstParameterTypeConstructor(), getOtherParametersTypeConstructor());
    }

    private TypeConstructor getReturnTypeConstructor() {
        return optionalFinalizer.map(CovariantFinalizer::getFinalizedTypeConstructor).orElse(accumulator.getAccumulatedTypeConstructor());
    }

    private TypeConstructor getFirstParameterTypeConstructor() {
        // If the user provided an initializer method, all parameters
        // use the same type constructor (`inputTypeConstructor`).
        // If no initializer method is available, the first parameter
        // uses a different type constructor than the other parameters
        // (`partiallyAccumulatedTypeConstructor`).
        return hasInitializer()
                ? getOtherParametersTypeConstructor()
                : accumulator.getPartiallyAccumulatedTypeConstructor();
    }

    private Type getReturnType() {
        return returnTypeConstructorArgument.asType().using(getReturnTypeConstructor());
    }

    private boolean hasInitializer() {
        return optionalInitializer.isPresent();
    }

    private TypeConstructor getOtherParametersTypeConstructor() {
        return accumulator.getInputTypeConstructor();
    }

    private List<Type> takeParameterTypeConstructorArgumentsAsTypes(int toTake) {
        return getParameterTypeConstructorArgumentsAsTypes().subList(0, toTake);
    }

    private List<TypeArgument> takeParameterTypeConstructorArgumentsAsTypeArguments(int toTake, Function<Type, TypeArgument> variance) {
        return takeParameterTypeConstructorArgumentsAsTypes(toTake)
                .stream()
                .map(variance)
                .collect(toList());
    }

    private List<TypeArgument> takeParameterTypeConstructorArgumentsAsTypeArguments(int toTake) {
        return takeParameterTypeConstructorArgumentsAsTypeArguments(toTake, Type::invariant);
    }

    private List<Type> takeParameterTypes(int toTake, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor) {
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

    private List<Type> getParameterTypeConstructorArgumentsAsTypes() {
        return parameterTypeConstructorArguments
                .stream()
                .map(TypeParameter::asType)
                .collect(toList());
    }

    private FullyQualifiedName fullyQualifiedNameOfFunction(int arity) {
        return arity == 2 ? BI_FUNCTION.getFullyQualifiedName() : fullyQualifiedNameOfArbitraryArityFunction(arity);
    }

    private FullyQualifiedName fullyQualifiedNameOfArbitraryArityFunction(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Function" + arity);
    }

    private FullyQualifiedName fullyQualifiedNameOfTuple() {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Tuple");
    }

    private FullyQualifiedName fullyQualifiedNameOfTupleWithArity(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Tuple" + arity);
    }

    private FullyQualifiedName fullyQualifiedNameOfDecomposable(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable" + arity);
    }

    private FullyQualifiedName fullyQualifiedNameOfDecomposition(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition" + arity);
    }
}
