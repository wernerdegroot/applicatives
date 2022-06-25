package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.Parameter;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameter;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static nl.wernerdegroot.applicatives.processor.generator.MethodReferenceGenerator.methodReference;
import static nl.wernerdegroot.applicatives.processor.generator.ParametersGenerator.parameters;

public class InvariantGenerator extends Generator<InvariantGenerator> {

    private static final FullyQualifiedName FAST_TUPLE = FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.FastTuple");
    private static final String FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME = "withMaxSize";
    private static final String COMBINATOR_APPLY_METHOD_NAME = "apply";
    private static final String DECOMPOSITION_DECOMPOSE_METHOD_NAME = "decompose";
    private static final String FUNCTION_IDENTITY_METHOD_NAME = "identity";

    private String combinatorParameterName;
    private String maxTupleSizeParameterName;
    private String tupleParameterName;
    private String elementParameterName;
    private TypeParameter intermediateTypeConstructorArgument;
    private String decompositionParameterName;
    private String toIntermediateParameterName;
    private String extractLeftParameterName;
    private String extractRightParameterName;

    public static InvariantGenerator generator() {
        return new InvariantGenerator();
    }

    @Override
    protected InvariantGenerator getThis() {
        return this;
    }

    public InvariantGenerator withCombinatorParameterName(String combinatorParameterName) {
        this.combinatorParameterName = combinatorParameterName;
        return this;
    }

    public InvariantGenerator withMaxTupleSizeParameterName(String maxTupleSizeParameterName) {
        this.maxTupleSizeParameterName = maxTupleSizeParameterName;
        return this;
    }

    public InvariantGenerator withTupleParameterName(String tupleParameterName) {
        this.tupleParameterName = tupleParameterName;
        return this;
    }

    public InvariantGenerator withElementParameterName(String elementParameterName) {
        this.elementParameterName = elementParameterName;
        return this;
    }

    public InvariantGenerator withIntermediateTypeConstructorArgument(TypeParameter intermediateTypeConstructorArgument) {
        this.intermediateTypeConstructorArgument = intermediateTypeConstructorArgument;
        return this;
    }

    public InvariantGenerator withDecompositionParameterName(String decompositionParameterName) {
        this.decompositionParameterName = decompositionParameterName;
        return this;
    }

    public InvariantGenerator withToIntermediateParameterName(String toIntermediateParameterName) {
        this.toIntermediateParameterName = toIntermediateParameterName;
        return this;
    }

    public InvariantGenerator withExtractLeftParameterName(String extractLeftParameterName) {
        this.extractLeftParameterName = extractLeftParameterName;
        return this;
    }

    public InvariantGenerator withExtractRightParameterName(String extractRightParameterName) {
        this.extractRightParameterName = extractRightParameterName;
        return this;
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
                        BI_FUNCTION.with(
                                parameterTypeConstructorArguments.get(0).contravariant(),
                                parameterTypeConstructorArguments.get(1).contravariant(),
                                returnTypeConstructorArgument.covariant()
                        ),
                        combinatorParameterName
                )
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

    @Override
    protected CombineMethod getCombineMethod() {
        return new CombineMethod() {

            @Override
            public boolean shouldGenerateArityTwo() {
                return true;
            }

            @Override
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethod(int arity) {
                return asList(
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
                                .generate(),
                        methodReference().withObjectPath(decompositionParameterName).withMethodName(DECOMPOSITION_DECOMPOSE_METHOD_NAME).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(withouterForIndex(arity - 1)).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(arity)).withMethodName(getterForIndex(arity - 1)).generate()
                );
            }

            @Override
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForArityTwo() {
                return asList(
                        combinatorParameterName,
                        methodReference().withObjectPath(decompositionParameterName).withMethodName(DECOMPOSITION_DECOMPOSE_METHOD_NAME).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(2)).withMethodName(getterForIndex(0)).generate(),
                        methodReference().withObjectPath(fullyQualifiedNameOfTupleWithArity(2)).withMethodName(getterForIndex(1)).generate()
                );
            }

            @Override
            public List<String> getAdditionalArgumentsToPassToTupleMethod(int arity) {
                return singletonList(Integer.toString(arity));
            }

            @Override
            public List<Parameter> getAdditionalParameters(int arity) {
                List<TypeArgument> decompositionTypeArguments = new ArrayList<>();
                decompositionTypeArguments.add(returnTypeConstructorArgument.asType().contravariant());
                decompositionTypeArguments.addAll(takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));

                return parameters()
                        .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                        .withParameter(
                                Type.concrete(fullyQualifiedNameOfDecomposition(arity), decompositionTypeArguments),
                                decompositionParameterName
                        )
                        .unwrap();
            }
        };
    }

    @Override
    protected Optional<SimplifiedCombineMethod> getSimplifiedCombineMethod() {
        return Optional.of(
                new SimplifiedCombineMethod() {
                    @Override
                    public List<TypeParameter> getTypeParameters(int arity) {
                        List<TypeParameter> typeParameters = new ArrayList<>();
                        typeParameters.addAll(takeParameterTypeConstructorArguments(arity));
                        typeParameters.add(returnTypeConstructorArgument.getName().extending(Type.concrete(fullyQualifiedNameOfDecomposable(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity))));
                        return typeParameters;
                    }

                    @Override
                    public List<Parameter> getAdditionalParameters(int arity) {
                        return parameters()
                                .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                                .unwrap();
                    }

                    @Override
                    public List<String> getAdditionalArgumentsToPassToCombineMethod() {
                        return asList(
                                combinatorParameterName,
                                methodReference().withType(returnTypeConstructorArgument.asType()).withMethodName(DECOMPOSITION_DECOMPOSE_METHOD_NAME).generate()
                        );
                    }
                }
        );
    }

    @Override
    protected LiftMethod getLiftMethod() {
        return new LiftMethod() {
            @Override
            public List<Parameter> getAdditionalLiftMethodParametersToPassOnToCombineMethod(int arity) {
                List<TypeArgument> decompositionTypeArguments = new ArrayList<>();
                decompositionTypeArguments.add(returnTypeConstructorArgument.asType().contravariant());
                decompositionTypeArguments.addAll(takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));

                return parameters()
                        .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                        .withParameter(Type.concrete(fullyQualifiedNameOfDecomposition(arity), decompositionTypeArguments), decompositionParameterName)
                        .unwrap();
            }
        };
    }

    @Override
    protected Optional<SimplifiedLiftMethod> getSimplifiedLiftMethod() {
        return getSimplifiedCombineMethod().map(SimplifiedCombineMethod::asSimplifiedLiftMethod);
    }

    @Override
    protected TupleMethod getTupleMethod() {
        return new TupleMethod() {

            @Override
            public List<Parameter> getAdditionalTupleMethodParametersToPassOnToTupleMethod(int arity) {
                return parameters()
                        .withParameter(INT, maxTupleSizeParameterName)
                        .unwrap();
            }

            @Override
            public List<TypeArgument> getTypeArgumentsToPassOnToAccumulatorMethodForTupleMethod(int arity) {
                return asList(
                        getCovariantTupleTypeOfArity(arity - 1).invariant(),
                        parameterTypeConstructorArguments.get(arity - 1).asType().invariant(),
                        getCovariantTupleTypeOfArity(arity).invariant(),
                        getCovariantTupleTypeOfArity(arity).invariant()
                );
            }

            @Override
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethod(int arity) {
                return asList(
                        methodReference()
                                .withType(fullyQualifiedNameOfTupleWithArity(arity - 1))
                                .withMethodName(witherForIndex(arity - 1))
                                .generate(),
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
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethodWithArityTwo() {
                return asList(
                        methodCall()
                                .withType(FAST_TUPLE)
                                .withMethodName(FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME)
                                .withArguments(maxTupleSizeParameterName)
                                .generate(),
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

            private Type getCovariantTupleTypeOfArity(int arity) {
                return Type.concrete(fullyQualifiedNameOfTupleWithArity(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));
            }
        };
    }

    private FullyQualifiedName fullyQualifiedNameOfDecomposable(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.decompositions.Decomposable" + arity);
    }

    private FullyQualifiedName fullyQualifiedNameOfDecomposition(int arity) {
        return FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.decompositions.Decomposition" + arity);
    }
}
