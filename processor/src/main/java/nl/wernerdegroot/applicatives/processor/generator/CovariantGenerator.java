package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.getterForIndex;
import static nl.wernerdegroot.applicatives.processor.Ordinals.witherForIndex;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.INT;
import static nl.wernerdegroot.applicatives.processor.generator.ClassOrInterfaceGenerator.classOrInterface;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
import static nl.wernerdegroot.applicatives.processor.generator.Lines.lines;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static nl.wernerdegroot.applicatives.processor.generator.MethodReferenceGenerator.methodReference;
import static nl.wernerdegroot.applicatives.processor.generator.ParametersGenerator.parameters;

public class CovariantGenerator extends Generator<CovariantGenerator> {

    private static final FullyQualifiedName FAST_TUPLE = FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.FastTuple");
    private static final String FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME = "withMaxSize";
    private static final String COMBINATOR_APPLY_METHOD_NAME = "apply";

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

    @Override
    protected List<TypeParameter> getAccumulatorTypeParameters() {
        List<TypeParameter> typeParameters = new ArrayList<>();
        typeParameters.addAll(takeParameterTypeConstructorArguments(2));
        typeParameters.add(returnTypeConstructorArgument);
        return typeParameters;
    }

    @Override
    protected List<Parameter> getAdditionalAccumulatorParameters() {
        return parameters()
                .withParameter(
                        BI_FUNCTION.with(
                                parameterTypeConstructorArguments.get(0).contravariant(),
                                parameterTypeConstructorArguments.get(1).contravariant(),
                                returnTypeConstructorArgument.covariant()
                        ),
                        combinatorParameterName
                )
                .unwrap();
    }

    @Override
    protected List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForCombineMethod(int arity) {
        return singletonList(
                lambda()
                        .withParameterNames(tupleParameterName, elementParameterName)
                        .withExpression(
                                methodCall()
                                        .withObjectPath(combinatorParameterName)
                                        .withMethodName(COMBINATOR_APPLY_METHOD_NAME)
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
        );
    }

    @Override
    protected List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForCombineMethodWithArityTwo() {
        return singletonList(combinatorParameterName);
    }

    @Override
    protected List<String> getAdditionalArgumentsToPassToTupleMethodForCombineMethod(int arity) {
        return singletonList(Integer.toString(arity));
    }

    @Override
    protected List<Parameter> getAdditionalParametersForCombineMethod(int arity) {
        return parameters()
                .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .unwrap();
    }

    @Override
    protected List<Parameter> getAdditionalLiftMethodParametersToPassOnToCombineMethod(int arity) {
        return parameters()
                .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .unwrap();
    }

    @Override
    protected List<Parameter> getAdditionalTupleMethodParametersToPassOnToTupleMethod(int arity) {
        return parameters()
                .withParameter(INT, maxTupleSizeParameterName)
                .unwrap();
    }

    @Override
    protected List<TypeArgument> getTypeArgumentsToPassOnToAccumulatorMethodForTupleMethod(int arity) {
        return asList(
                getCovariantTupleTypeOfArity(arity - 1).invariant(),
                parameterTypeConstructorArguments.get(arity - 1).asType().invariant(),
                getCovariantTupleTypeOfArity(arity).invariant()
        );
    }

    @Override
    protected List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethod(int arity) {
        return singletonList(
                methodReference()
                        .withType(fullyQualifiedNameOfTupleWithArity(arity - 1))
                        .withMethodName(witherForIndex(arity - 1))
                        .generate()
        );
    }

    @Override
    protected List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethodWithArityTwo() {
        return singletonList(
                methodCall()
                        .withType(FAST_TUPLE)
                        .withMethodName(FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME)
                        .withArguments(maxTupleSizeParameterName)
                        .generate()
        );
    }

    private Type getCovariantTupleTypeOfArity(int arity) {
        return Type.concrete(fullyQualifiedNameOfTupleWithArity(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));
    }
}
