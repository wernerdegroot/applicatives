package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.Finalizer;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Initializer;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.getterForIndex;
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

public class CovariantGenerator extends Generator<CovariantGenerator> {

    private static final FullyQualifiedName FAST_TUPLE = FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.FastTuple");
    private static final String FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME = "withMaxSize";
    private static final String APPLY_METHOD = "apply";

    private String combinatorParameterName;
    private String maxTupleSizeParameterName;
    private String tupleParameterName;
    private String elementParameterName;

    public static CovariantGenerator generator() {
        return new CovariantGenerator();
    }

    @Override
    protected CovariantGenerator getThis() {
        return this;
    }

    public CovariantGenerator withCombinatorParameterName(String combinatorParameterName) {
        this.combinatorParameterName = combinatorParameterName;
        return this;
    }

    public CovariantGenerator withMaxTupleSizeParameterName(String maxTupleSizeParameterName) {
        this.maxTupleSizeParameterName = maxTupleSizeParameterName;
        return this;
    }

    public CovariantGenerator withTupleParameterName(String tupleParameterName) {
        this.tupleParameterName = tupleParameterName;
        return this;
    }

    public CovariantGenerator withElementParameterName(String elementParameterName) {
        this.elementParameterName = elementParameterName;
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
        List<MethodGenerator> combineMethods = new ArrayList<>();

        // If we have an initializer method, we need to generate an addition `combine`-method with
        // arity two. Both `combine`-method have different parameters. The abstract method
        // may use a different type constructor for its first parameter and its second parameter
        // (`partiallyAccumulatedTypeConstructor` and `inputTypeConstructor` respectively). The
        // concrete method's parameters all use the same type constructor (`inputTypeConstructor`).
        if (hasInitializer()) {
            combineMethods.add(combineMethodWithArityTwo());
        }

        IntStream.rangeClosed(3, maxArity).forEach(arity -> {
            combineMethods.add(combineMethodWithArity(arity));
        });

        return combineMethods;
    }

    private MethodGenerator abstractAccumulatorMethod() {
        int arity = 2;

        return method()
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(returnTypeConstructorArgument.asType().using(accumulator.getAccumulatedTypeConstructor()))
                .withName(accumulator.getName())
                .withParameterTypes(takeParameterTypes(arity, accumulator.getPartiallyAccumulatedTypeConstructor(), accumulator.getInputTypeConstructor()))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(
                        BI_FUNCTION.with(
                                parameterTypeConstructorArguments.get(0).contravariant(),
                                parameterTypeConstructorArguments.get(1).contravariant(),
                                returnTypeConstructorArgument.covariant()
                        ),
                        combinatorParameterName
                );
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
                        combinatorParameterName
                )
                .generate();

        return combineMethodWithArity(
                arity,
                optionalFinalizer
                        .map(Finalizer::getName)
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
                                .withArguments(Integer.toString(arity))
                                .generate(),
                        inputParameterNames.get(arity - 1),
                        lambda()
                                .withParameterNames(tupleParameterName, elementParameterName)
                                .withExpression(
                                        methodCall()
                                                .withObjectPath(combinatorParameterName)
                                                .withMethodName(APPLY_METHOD)
                                                .withArguments(
                                                        IntStream.range(0, arity - 1)
                                                                .boxed()
                                                                .map(elementIndex -> methodCall().withObjectPath(tupleParameterName).withMethodName(getterForIndex(elementIndex)).generate())
                                                                .collect(toList())
                                                )
                                                .withArguments(elementParameterName)
                                                .generate()

                                )
                                .generate()
                )
                .generate();

        return combineMethodWithArity(
                arity,
                optionalFinalizer
                        .map(Finalizer::getName)
                        .map(finalizerMethodName -> methodCall().withObjectPath(THIS).withMethodName(finalizerMethodName).withArguments(methodBody).generate())
                        .orElse(methodBody)
        );
    }

    private MethodGenerator combineMethodWithArity(int arity, String returnStatement) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(getReturnType())
                .withName(accumulator.getName())
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withReturnStatement(returnStatement);
    }

    private List<MethodGenerator> liftMethods() {
        List<MethodGenerator> liftMethods = new ArrayList<>();
        IntStream.rangeClosed(2, maxArity).forEachOrdered(arity -> {
            liftMethods.add(liftMethodWithArity(arity));
        });
        return liftMethods;
    }

    private MethodGenerator liftMethodWithArity(int arity) {
        return liftMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(THIS)
                        .withMethodName(accumulator.getName())
                        .withArguments(takeInputParameterNames(arity))
                        .withArguments(combinatorParameterName)
                        .generate()
        );
    }

    private MethodGenerator liftMethodWithArity(int arity, String lambdaBody) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeParameterTypeConstructorArguments(arity))
                .withTypeParameters(returnTypeConstructorArgument.getName())
                .withReturnType(lambdaReturnType(getReturnTypeConstructor(), getOtherParametersTypeConstructor(), getFirstParameterTypeConstructor(), arity))
                .withName(liftMethodName)
                .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withReturnStatement(
                        lambda()
                                .withParameterNames(takeInputParameterNames(arity))
                                .withExpression(lambdaBody)
                                .multiline()
                );
    }

    private List<MethodGenerator> tupleMethods() {
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

    private MethodGenerator tupleMethodWithArityTwo() {
        String firstInputParameterName = inputParameterNames.get(0);
        String secondInputParameterName = inputParameterNames.get(1);
        return tupleMethodWithArity(
                2,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(accumulator.getName())
                        .withArguments(
                                optionalInitializer.map(initializer -> methodCall().withObjectPath(selfParameterName).withMethodName(initializer.getName()).withArguments(firstInputParameterName).generate()).orElse(firstInputParameterName),
                                secondInputParameterName,
                                methodCall()
                                        .withType(FAST_TUPLE)
                                        .withMethodName(FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME)
                                        .withArguments(maxTupleSizeParameterName)
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

    private MethodGenerator tupleMethodWithArity(int arity, String methodBody) {
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
                .withReturnStatement(methodBody);
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
}
