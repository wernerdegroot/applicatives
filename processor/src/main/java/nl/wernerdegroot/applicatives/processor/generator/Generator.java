package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.ClassName;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.PackageName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.witherForIndex;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.INT;
import static nl.wernerdegroot.applicatives.processor.generator.ClassOrInterfaceGenerator.classOrInterface;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
import static nl.wernerdegroot.applicatives.processor.generator.Lines.lines;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static nl.wernerdegroot.applicatives.processor.generator.MethodGenerator.method;
import static nl.wernerdegroot.applicatives.processor.generator.MethodReferenceGenerator.methodReference;

public class Generator {

    private static final int NUMBER_OF_TUPLE_TYPE_PARAMETERS = 26;
    private static final ClassName TUPLE_CLASS_NAME = ClassName.of("Tuples");
    private static final String TUPLE_METHOD_NAME = "tuple";
    private static final FullyQualifiedName FAST_TUPLE = FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.FastTuple");
    private static final String FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME = "withMaxSize";
    private static final String FAST_TUPLE_EMPTY_WITH_MAX_SIZE_METHOD_NAME = "emptyWithMaxSize";
    private static final String FUNCTION_2_FROM_BI_FUNCTION_METHOD_NAME = "fromBiFunction";
    private static final String FUNCTION_N_APPLY_METHOD = "apply";

    private PackageName packageName;
    private String classNameToGenerate;
    private List<TypeParameter> classTypeParameters;
    private List<TypeParameter> parameterTypeConstructorArguments;
    private TypeParameter returnTypeConstructorArgument;
    private Optional<String> optionalInitializerMethodName;
    private String accumulatorMethodName;
    private Optional<String> optionalFinalizerMethodName;
    private List<String> inputParameterNames;
    private String valueParameterName;
    private String selfParameterName;
    private String combinatorParameterName;
    private String maxTupleSizeParameterName;
    private Optional<TypeConstructor> optionalInitializedTypeConstructor;
    private TypeConstructor inputTypeConstructor;
    private TypeConstructor partiallyAccumulatedTypeConstructor;
    private TypeConstructor accumulatedTypeConstructor;
    private Optional<TypeConstructor> optionalToFinalizeTypeConstructor;
    private Optional<TypeConstructor> optionalFinalizedTypeConstructor;
    private String liftMethodName;
    private int maxArity;

    public static Generator generator() {
        return new Generator();
    }

    public Generator withPackageName(PackageName packageName) {
        this.packageName = packageName;
        return this;
    }

    public Generator withClassNameToGenerate(String classNameToGenerate) {
        this.classNameToGenerate = classNameToGenerate;
        return this;
    }

    public FullyQualifiedName getFullyQualifiedClassNameToGenerate() {
        return packageName.withClassName(ClassName.of(classNameToGenerate));
    }

    public FullyQualifiedName getFullyQualifiedTupleClass() {
        return getFullyQualifiedClassNameToGenerate().withClassName(TUPLE_CLASS_NAME);
    }

    public Generator withClassTypeParameters(List<TypeParameter> classTypeParameters) {
        this.classTypeParameters = classTypeParameters;
        return this;
    }

    public List<TypeArgument> getClassTypeParametersAsTypeArguments() {
        return classTypeParameters
                .stream()
                .map(TypeParameter::invariant)
                .collect(toList());
    }

    public Generator withParameterTypeConstructorArguments(List<TypeParameter> inputTypeConstructorArguments) {
        this.parameterTypeConstructorArguments = inputTypeConstructorArguments;
        return this;
    }

    public List<TypeParameter> takeParameterTypeConstructorArguments(int toTake) {
        return parameterTypeConstructorArguments.subList(0, toTake);
    }

    public List<Type> getParameterTypeConstructorArgumentsAsTypes() {
        return parameterTypeConstructorArguments
                .stream()
                .map(TypeParameter::asType)
                .collect(toList());
    }

    public List<Type> takeParameterTypeConstructorArgumentsAsTypes(int toTake) {
        return getParameterTypeConstructorArgumentsAsTypes().subList(0, toTake);
    }

    public List<TypeArgument> takeParameterTypeConstructorArgumentsAsTypeArguments(int toTake) {
        return takeParameterTypeConstructorArgumentsAsTypes(toTake)
                .stream()
                .map(Type::invariant)
                .collect(toList());
    }

    public List<Type> takeParameterTypes(int toTake) {
        return takeParameterTypes(toTake, getFirstParameterTypeConstructor(), getOtherParametersTypeConstructor());
    }

    public TypeConstructor getFirstParameterTypeConstructor() {
        // If the user provided an initializer method, all parameters
        // use the same type constructor (`inputTypeConstructor`).
        // If no initializer method is available, the first parameter
        // uses a different type constructor than the other parameters
        // (`partiallyAccumulatedTypeConstructor`).
        return hasInitializer()
                ? getOtherParametersTypeConstructor()
                : partiallyAccumulatedTypeConstructor;
    }

    public TypeConstructor getOtherParametersTypeConstructor() {
        return inputTypeConstructor;
    }

    public List<Type> takeParameterTypes(int toTake, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor) {
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

    public Generator withReturnTypeConstructorArgument(TypeParameter resultTypeConstructorArgument) {
        this.returnTypeConstructorArgument = resultTypeConstructorArgument;
        return this;
    }

    public TypeConstructor getReturnTypeConstructor() {
        return optionalFinalizedTypeConstructor.orElse(accumulatedTypeConstructor);
    }

    private TypeConstructor getTupleMethodReturnTypeConstructor(int arity) {
        // If the arity is equal to zero, we are dealing with a special case. We use the initializer method
        // to wrap an empty tuple using the `initializedTypeConstructor`. We can pass that as the
        // input to the accumulator method that the user defined.
        if (arity == 0) {

            if (!optionalInitializedTypeConstructor.isPresent()) {
                throw new IllegalStateException("An initializer method is required for a tuple method of arity zero");
            }

            return optionalInitializedTypeConstructor.get();
        } else {
            return accumulatedTypeConstructor;
        }
    }

    public Type getReturnType() {
        return returnTypeConstructorArgument.asType().using(getReturnTypeConstructor());
    }

    public Generator withAccumulatorMethodName(String accumulatorMethodName) {
        this.accumulatorMethodName = accumulatorMethodName;
        return this;
    }

    public Generator withOptionalInitializerMethodName(Optional<String> optionalInitializerMethodName) {
        this.optionalInitializerMethodName = optionalInitializerMethodName;
        return this;
    }

    public boolean hasInitializer() {
        return optionalInitializerMethodName.isPresent();
    }

    public Generator withOptionalFinalizerMethodName(Optional<String> optionalFinalizerMethodName) {
        this.optionalFinalizerMethodName = optionalFinalizerMethodName;
        return this;
    }

    public Generator withInputParameterNames(List<String> inputParameterNames) {
        this.inputParameterNames = inputParameterNames;
        return this;
    }

    public List<String> takeInputParameterNames(int toTake) {
        return inputParameterNames.subList(0, toTake);
    }

    public Generator withValueParameterName(String valueParameterName) {
        this.valueParameterName = valueParameterName;
        return this;
    }

    public Generator withSelfParameterName(String selfParameterName) {
        this.selfParameterName = selfParameterName;
        return this;
    }

    public Generator withCombinatorParameterName(String combinatorParameterName) {
        this.combinatorParameterName = combinatorParameterName;
        return this;
    }

    public Generator withMaxTupleSizeParameterName(String maxTupleSizeParameterName) {
        this.maxTupleSizeParameterName = maxTupleSizeParameterName;
        return this;
    }

    public Generator withOptionalInitializedTypeConstructor(Optional<TypeConstructor> optionalInitializedTypeConstructor) {
        this.optionalInitializedTypeConstructor = optionalInitializedTypeConstructor;
        return this;
    }

    public Generator withInputTypeConstructor(TypeConstructor inputTypeConstructor) {
        this.inputTypeConstructor = inputTypeConstructor;
        return this;
    }

    public Generator withPartiallyAccumulatedTypeConstructor(TypeConstructor partiallyAccumulatedTypeConstructor) {
        this.partiallyAccumulatedTypeConstructor = partiallyAccumulatedTypeConstructor;
        return this;
    }

    public Generator withAccumulatedTypeConstructor(TypeConstructor accumulatedTypeConstructor) {
        this.accumulatedTypeConstructor = accumulatedTypeConstructor;
        return this;
    }

    public Generator withOptionalToFinalizeTypeConstructor(Optional<TypeConstructor> optionalToFinalizeTypeConstructor) {
        this.optionalToFinalizeTypeConstructor = optionalToFinalizeTypeConstructor;
        return this;
    }

    public Generator withOptionalFinalizedTypeConstructor(Optional<TypeConstructor> optionalFinalizedTypeConstructor) {
        this.optionalFinalizedTypeConstructor = optionalFinalizedTypeConstructor;
        return this;
    }

    public Generator withLiftMethodName(String liftMethodName) {
        this.liftMethodName = liftMethodName;
        return this;
    }

    public Generator withMaxArity(int maxArity) {
        this.maxArity = maxArity;
        return this;
    }

    public String generate() {
        Lines lines = lines();
        lines.append(PACKAGE + SPACE + packageName.raw() + SEMICOLON);
        lines.append(EMPTY_LINE);

        // Place to gather methods:
        Lines methods = lines();

        // If the client provided an Initializer, generate an abstract
        // method for it and append it to the methods.
        optionalAbstractInitializerMethod().ifPresent(abstractInitializerMethod -> {
            methods.append(abstractInitializerMethod).append(EMPTY_LINE);
        });

        // If the client provided a Finalizer, generate an abstract
        // method for it and append it to the methods.
        optionalAbstractFinalizerMethod().ifPresent(abstractFinalizerMehtod -> {
            methods.append(abstractFinalizerMehtod).append(EMPTY_LINE);
        });

        // Continue adding the combine- and lift-methods.
        methods
                .append(combineMethods())
                .append(EMPTY_LINE)
                .append(liftMethods());

        lines.append(
                classOrInterface()
                        .asInterface()
                        .withModifiers(PUBLIC)
                        .withName(classNameToGenerate)
                        .withTypeParameters(classTypeParameters)
                        .withBody(methods)
                        .withBody(EMPTY_LINE)
                        .withBody(
                                classOrInterface()
                                        .asClass()
                                        .withName(TUPLE_CLASS_NAME.raw())
                                        .withBody(tupleMethods())
                                        .lines()
                        )
                        .lines()
        );

        return String.join(LINE_FEED, lines);
    }

    private Optional<List<String>> optionalAbstractInitializerMethod() {
        if (optionalInitializedTypeConstructor.isPresent() && optionalInitializerMethodName.isPresent()) {
            TypeConstructor initializedTypeConstructor = optionalInitializedTypeConstructor.get();
            String initializerMethodName = optionalInitializerMethodName.get();
            List<String> lines = method()
                    .withTypeParameters(returnTypeConstructorArgument.getName())
                    .withReturnType(returnTypeConstructorArgument.asType().using(initializedTypeConstructor))
                    .withName(initializerMethodName)
                    .withParameter(returnTypeConstructorArgument.asType(), valueParameterName)
                    .lines();
            return Optional.of(lines);
        } else {
            return Optional.empty();
        }
    }

    private Optional<List<String>> optionalAbstractFinalizerMethod() {
        if (optionalFinalizedTypeConstructor.isPresent() && optionalFinalizerMethodName.isPresent() && optionalToFinalizeTypeConstructor.isPresent()) {
            TypeConstructor finalizedTypeConstructor = optionalFinalizedTypeConstructor.get();
            String finalizerMethodName = optionalFinalizerMethodName.get();
            TypeConstructor toFinalizeTypeConstructor = optionalToFinalizeTypeConstructor.get();

            List<String> lines = method()
                    .withTypeParameters(returnTypeConstructorArgument.getName())
                    .withReturnType(returnTypeConstructorArgument.asType().using(finalizedTypeConstructor))
                    .withName(finalizerMethodName)
                    .withParameter(returnTypeConstructorArgument.asType().using(toFinalizeTypeConstructor), valueParameterName)
                    .lines();

            return Optional.of(lines);
        } else {
            return Optional.empty();
        }
    }

    private List<String> combineMethods() {
        List<String> lines = new ArrayList<>();
        lines.addAll(abstractCombineMethodWithArityTwo());

        // If we have an Initializer, we need to generate an addition `combine`-method with
        // arity two. Both `combine`-method have different parameters. The abstract method
        // may use a different type constructor for its first parameter and its second parameter
        // (`permissiveAccumulationTypeConstructor` and `inputTypeConstructor` respectively). The
        // concrete method's parameters all use the same type constructor (`inputTypeConstructor`).
        if (hasInitializer()) {
            lines.add(EMPTY_LINE);
            lines.addAll(combineMethodWithArity(2));
        }

        IntStream.rangeClosed(3, maxArity).forEach(arity -> {
            lines.add(EMPTY_LINE);
            lines.addAll(combineMethodWithArity(arity));
        });
        return lines;
    }

    private List<String> abstractCombineMethodWithArityTwo() {
        int arity = 2;

        return method()
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(returnTypeConstructorArgument.asType().using(accumulatedTypeConstructor))
                .withName(accumulatorMethodName)
                .withParameterTypes(takeParameterTypes(arity, partiallyAccumulatedTypeConstructor, inputTypeConstructor))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(
                        BI_FUNCTION.with(
                                parameterTypeConstructorArguments.get(0).contravariant(),
                                parameterTypeConstructorArguments.get(1).contravariant(),
                                returnTypeConstructorArgument.covariant()
                        ),
                        combinatorParameterName
                )
                .lines();
    }

    private List<String> combineMethodWithArity(int arity) {

        // If the arity is equal to two, we are not dealing with a `Function2` like we want,
        // but a `BiFunction` (to stay as close as possible to the Java standard library).
        // To call the proper `apply`-method, we need to convert it to a `Function2` first.
        String function = combinatorParameterName;
        if (arity == 2) {
            function = methodCall()
                    .withObjectPath(fullyQualifiedNameOfArbitraryArityFunction(2))
                    .withMethodName(FUNCTION_2_FROM_BI_FUNCTION_METHOD_NAME)
                    .withArguments(combinatorParameterName)
                    .generate();
        }

        UnaryOperator<String> finalization = optionalFinalizerMethodName
                .<UnaryOperator<String>>map(finalizerMethodName -> argument -> methodCall().withObjectPath(THIS).withMethodName(finalizerMethodName).withArguments(argument).generate())
                .orElse(UnaryOperator.identity());

        return combineMethodWithArity(
                arity,
                finalization.apply(
                        methodCall()
                                .withObjectPath(THIS)
                                .withMethodName(accumulatorMethodName)
                                .withArguments(
                                        methodCall()
                                                .withType(getFullyQualifiedTupleClass())
                                                .withTypeArguments(takeParameterTypeConstructorArgumentsAsTypeArguments(arity - 1))
                                                .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                                .withMethodName(TUPLE_METHOD_NAME)
                                                .withArguments(THIS)
                                                .withArguments(takeInputParameterNames(arity - 1))
                                                .withArguments(Integer.toString(arity))
                                                .generate(),
                                        inputParameterNames.get(arity - 1),
                                        methodReference().withObjectPath(function).withMethodName(FUNCTION_N_APPLY_METHOD).generate()
                                )
                                .generate()
                )
        );
    }

    private List<String> combineMethodWithArity(int arity, String returnStatement) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(getReturnType())
                .withName(accumulatorMethodName)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(lambdaType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withReturnStatement(returnStatement)
                .lines();
    }

    private List<String> liftMethods() {
        return IntStream.rangeClosed(3, maxArity)
                .boxed()
                .collect(
                        () -> this.liftMethodWithArity(2),
                        (acc, i) -> {
                            acc.add(EMPTY_LINE);
                            acc.addAll(liftMethodWithArity(i));
                        },
                        List::addAll
                );
    }

    private List<String> liftMethodWithArity(int arity) {
        return liftMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(THIS)
                        .withMethodName(accumulatorMethodName)
                        .withArguments(takeInputParameterNames(arity))
                        .withArguments(combinatorParameterName)
                        .generate()
        );
    }

    private List<String> liftMethodWithArity(int arity, String lambdaBody) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(lambdaType(getReturnTypeConstructor(), getOtherParametersTypeConstructor(), getFirstParameterTypeConstructor(), arity))
                .withName(liftMethodName)
                .withParameter(lambdaType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withReturnStatement(
                        lambda()
                                .withParameterNames(takeInputParameterNames(arity))
                                .withExpression(lambdaBody)
                                .multiline()
                )
                .lines();
    }

    private List<String> tupleMethods() {

        Lines lines = lines();
        if (hasInitializer()) {
            String initializerMethodName = optionalInitializerMethodName.get();

            lines.append(tupleMethodWithArityZero(initializerMethodName)).append(EMPTY_LINE);
            lines.append(tupleMethodWithArity(1)).append(EMPTY_LINE);
            lines.append(tupleMethodWithArity(2));
        } else {
            lines.append(tupleMethodWithArityTwo());
        }

        for (int arity = 3; arity < maxArity; arity++) {
            lines.append(EMPTY_LINE);
            lines.append(tupleMethodWithArity(arity));
        }

        return lines;
    }

    private List<String> tupleMethodWithArityZero(String initializerMethodName) {
        return tupleMethodWithArity(
                0,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(initializerMethodName)
                        .withArguments(
                                methodCall()
                                        .withType(FAST_TUPLE)
                                        .withMethodName(FAST_TUPLE_EMPTY_WITH_MAX_SIZE_METHOD_NAME)
                                        .withArguments(maxTupleSizeParameterName)
                                        .generate()
                        )
                        .generate()

        );
    }

    private List<String> tupleMethodWithArityTwo() {
        return tupleMethodWithArity(
                2,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(accumulatorMethodName)
                        .withArguments(
                                inputParameterNames.get(0),
                                inputParameterNames.get(1),
                                methodCall()
                                        .withType(FAST_TUPLE)
                                        .withMethodName(FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME)
                                        .withArguments(maxTupleSizeParameterName)
                                        .generate()
                        )
                        .generate()
        );
    }

    private List<String> tupleMethodWithArity(int arity) {
        return tupleMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(accumulatorMethodName)
                        .withArguments(
                                methodCall()
                                        .withType(getFullyQualifiedTupleClass())
                                        .withTypeArguments(takeParameterTypeConstructorArgumentsAsTypeArguments(arity - 1))
                                        .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                        .withMethodName(TUPLE_METHOD_NAME)
                                        .withArguments(selfParameterName)
                                        .withArguments(takeInputParameterNames(arity - 1))
                                        .withArguments(maxTupleSizeParameterName)
                                        .generate(),
                                inputParameterNames.get(arity - 1),
                                methodReference()
                                        .withType(fullyQualifiedNameOfTupleWithArity(arity - 1))
                                        .withMethodName(witherForIndex(arity - 1))
                                        .generate()
                        )
                        .generate()
        );
    }

    private List<String> tupleMethodWithArity(int arity, String methodBody) {
        return method()
                .withModifiers(PUBLIC, STATIC)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                // Since these are all static methods, that don't have access to any class type parameters
                // we need to make sure that the class type parameters are available as additional method
                // type parameters:
                .withTypeParameters(classTypeParameters)
                .withReturnType(Type.concrete(fullyQualifiedNameOfTupleWithArity(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity)).using(getTupleMethodReturnTypeConstructor(arity)))
                .withName(TUPLE_METHOD_NAME)
                .withParameter(getFullyQualifiedClassNameToGenerate().with(getClassTypeParametersAsTypeArguments()), selfParameterName)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(INT, maxTupleSizeParameterName)
                .withReturnStatement(methodBody)
                .lines();
    }

    private Type lambdaType(TypeConstructor returnTypeConstructor, TypeConstructor firstParameterTypeConstructor, TypeConstructor otherParametersTypeConstructor, int arity) {
        List<TypeArgument> typeArguments = new ArrayList<>();
        takeParameterTypes(arity, firstParameterTypeConstructor, otherParametersTypeConstructor)
                .stream()
                .map(Type::contravariant)
                .forEachOrdered(typeArguments::add);
        typeArguments.add(returnTypeConstructorArgument.asType().using(returnTypeConstructor).covariant());

        return Type.concrete(fullyQualifiedNameOfFunction(arity), typeArguments);
    }

    private static FullyQualifiedName fullyQualifiedNameOfFunction(int arity) {
        return arity == 2 ? BI_FUNCTION.getFullyQualifiedName() : fullyQualifiedNameOfArbitraryArityFunction(arity);
    }

    private static FullyQualifiedName fullyQualifiedNameOfArbitraryArityFunction(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Function" + arity);
    }

    private static FullyQualifiedName fullyQualifiedNameOfTupleWithArity(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Tuple" + arity);
    }
}
