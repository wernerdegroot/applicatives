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
import static nl.wernerdegroot.applicatives.processor.Ordinals.getterForIndex;
import static nl.wernerdegroot.applicatives.processor.Ordinals.witherForIndex;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.INT;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
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
    protected CombineMethod getCombineMethod() {
        return new CombineMethod() {

            @Override
            public boolean shouldGenerateArityTwo() {
                return hasInitializer();
            }

            @Override
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethod(int arity) {
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
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForArityTwo() {
                return singletonList(combinatorParameterName);
            }

            @Override
            public List<String> getAdditionalArgumentsToPassToTupleMethod(int arity) {
                return singletonList(Integer.toString(arity));
            }

            @Override
            public List<Parameter> getAdditionalParameters(int arity) {
                return parameters()
                        .withParameter(lambdaParameterType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                        .unwrap();
            }
        };
    }

    @Override
    protected Optional<Generator<CovariantGenerator>.SimplifiedCombineMethod> getSimplifiedCombineMethod() {
        return Optional.empty();
    }

    @Override
    protected LiftMethod getLiftMethod() {
        return getCombineMethod().asLiftMethod();
    }

    @Override
    protected Optional<SimplifiedLiftMethod> getSimplifiedLiftMethod() {
        return Optional.empty();
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
                        getCovariantTupleTypeOfArity(arity).invariant()
                );
            }

            @Override
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethod(int arity) {
                return singletonList(
                        methodReference()
                                .withType(fullyQualifiedNameOfTupleWithArity(arity - 1))
                                .withMethodName(witherForIndex(arity - 1))
                                .generate()
                );
            }

            @Override
            public List<String> getAdditionalArgumentsToPassOnToAccumulatorMethodForTupleMethodWithArityTwo() {
                return singletonList(
                        methodCall()
                                .withType(FAST_TUPLE)
                                .withMethodName(FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME)
                                .withArguments(maxTupleSizeParameterName)
                                .generate()
                );
            }

            // TODO: move to base class
            private Type getCovariantTupleTypeOfArity(int arity) {
                return Type.concrete(fullyQualifiedNameOfTupleWithArity(arity), takeParameterTypeConstructorArgumentsAsTypeArguments(arity, Type::covariant));
            }
        };
    }
}
