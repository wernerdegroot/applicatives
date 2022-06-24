package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.ClassName;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.getterForIndex;
import static nl.wernerdegroot.applicatives.processor.Ordinals.withouterForIndex;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.DEFAULT;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.FUNCTION;
import static nl.wernerdegroot.applicatives.processor.generator.ClassOrInterfaceGenerator.classOrInterface;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;
import static nl.wernerdegroot.applicatives.processor.generator.Lines.lines;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static nl.wernerdegroot.applicatives.processor.generator.MethodGenerator.method;
import static nl.wernerdegroot.applicatives.processor.generator.MethodReferenceGenerator.methodReference;
import static nl.wernerdegroot.applicatives.processor.generator.ParametersGenerator.parameters;

public class ContravariantGenerator extends Generator<ContravariantGenerator> {

    private static final ClassName TUPLE_CLASS_NAME = ClassName.of("Tuples");
    private static final String TUPLE_METHOD_NAME = "tuple";
    private static final String DECOMPOSITION_DECOMPOSE_METHOD_NAME = "decompose";
    private static final String FUNCTION_IDENTITY_METHOD_NAME = "identity";

    private TypeParameter intermediateTypeConstructorArgument;
    private String decompositionParameterName;
    private String toIntermediateParameterName;
    private String extractLeftParameterName;
    private String extractRightParameterName;

    public static ContravariantGenerator generator() {
        return new ContravariantGenerator();
    }

    @Override
    protected ContravariantGenerator getThis() {
        return this;
    }

    public ContravariantGenerator withIntermediateTypeConstructorArgument(TypeParameter intermediateTypeConstructorArgument) {
        this.intermediateTypeConstructorArgument = intermediateTypeConstructorArgument;
        return this;
    }

    public ContravariantGenerator withDecompositionParameterName(String decompositionParameterName) {
        this.decompositionParameterName = decompositionParameterName;
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

    @Override
    protected List<TypeParameter> getAccumulatorTypeParameters() {
        List<TypeParameter> typeParameters = new ArrayList<>();
        typeParameters.addAll(takeParameterTypeConstructorArguments(2));
        typeParameters.add(intermediateTypeConstructorArgument);
        typeParameters.add(returnTypeConstructorArgument);
        return typeParameters;
    }

    @Override
    protected List<Parameter> getAdditionalAccumulatorParameters() {
        return parameters()
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
                )
                .unwrap();
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
                        initializeIfHasInitializer(THIS, firstInputParameterName),
                        secondInputParameterName,
                        methodReference().withObjectPath(decompositionParameterName).withMethodName(DECOMPOSITION_DECOMPOSE_METHOD_NAME).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(getterForIndex(0)).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(getterForIndex(1)).generate()
                )
                .generate();

        return combineMethodWithArity(arity, finalizeIfHasFinalizer(THIS, methodBody));
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
                        methodReference().withObjectPath(decompositionParameterName).withMethodName(DECOMPOSITION_DECOMPOSE_METHOD_NAME).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(withouterForIndex(arity - 1)).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(getterForIndex(arity - 1)).generate()
                )
                .generate();

        return combineMethodWithArity(arity, finalizeIfHasFinalizer(THIS, methodBody));
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
                .withName(combineMethodToGenerate)
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
                        .withMethodName(combineMethodToGenerate)
                        .withArguments(takeInputParameterNames(arity))
                        .withArguments(methodReference().withType(returnTypeConstructorArgument.asType()).withMethodName(DECOMPOSITION_DECOMPOSE_METHOD_NAME).generate())
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
                .withName(combineMethodToGenerate)
                .withParameterTypes(takeParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withReturnStatement(methodBody);
    }

    @Override
    protected List<Parameter> getAdditionalLiftMethodParametersToPassOnToCombineMethod(int arity) {
        List<TypeArgument> decompositionTypeArguments = new ArrayList<>();
        decompositionTypeArguments.add(returnTypeConstructorArgument.asType().contravariant());
        decompositionTypeArguments.addAll(takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));

        return parameters()
                .withParameter(Type.concrete(fullyQualifiedNameOfDecomposition(arity), decompositionTypeArguments), decompositionParameterName)
                .unwrap();
    }

    @Override
    protected List<Parameter> getAdditionalTupleMethodParametersToPassOnToTupleMethod(int arity) {
        return emptyList();
    }

    @Override
    protected List<TypeArgument> getTypeArgumentsToPassOnToAccumulatorMethod(int arity) {
        return emptyList();
    }

    @Override
    protected List<String> getAdditionalArgumentsToPassOnToAccumulatorMethod(int arity) {
        return asList(
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
        );
    }

    @Override
    protected List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethodWithArityTwo() {
        return asList(
                methodCall()
                        .withType(FUNCTION.getFullyQualifiedName())
                        .withMethodName(FUNCTION_IDENTITY_METHOD_NAME)
                        .generate(),
                methodReference()
                        .withType(fullyQualifiedNameOfTupleWithArity(2))
                        .withMethodName(getterForIndex(0))
                        .generate(),
                methodReference()
                        .withType(fullyQualifiedNameOfTupleWithArity(2))
                        .withMethodName(getterForIndex(1))
                        .generate()
        );
    }

    private FullyQualifiedName fullyQualifiedNameOfDecomposable(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable" + arity);
    }

    private FullyQualifiedName fullyQualifiedNameOfDecomposition(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition" + arity);
    }
}
