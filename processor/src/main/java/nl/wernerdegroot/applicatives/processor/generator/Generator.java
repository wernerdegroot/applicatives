package nl.wernerdegroot.applicatives.processor.generator;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.domain.type.TypeArgument;
import nl.wernerdegroot.applicatives.processor.domain.typeconstructor.TypeConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.Ordinals.witherForIndex;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.*;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.*;
import static nl.wernerdegroot.applicatives.processor.generator.ClassOrInterfaceGenerator.classOrInterface;
import static nl.wernerdegroot.applicatives.processor.generator.Constants.*;
import static nl.wernerdegroot.applicatives.processor.generator.LambdaGenerator.lambda;
import static nl.wernerdegroot.applicatives.processor.generator.MethodCallGenerator.methodCall;
import static nl.wernerdegroot.applicatives.processor.generator.MethodGenerator.method;
import static nl.wernerdegroot.applicatives.processor.generator.MethodReferenceGenerator.methodReference;

public class Generator {

    private static final int NUMBER_OF_TUPLE_TYPE_PARAMETERS = 26;
    private static final ClassName TUPLE_CLASS_NAME = ClassName.of("Tuples");
    private static final String TUPLE_METHOD_NAME = "tuple";
    private static final String COMPOSITION_FUNCTION_APPLY_METHOD = "apply";
    private static final FullyQualifiedName FAST_TUPLE = FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.FastTuple");
    private static final String FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME = "withMaxSize";

    private PackageName packageName;
    private String classNameToGenerate;
    private List<TypeParameter> classTypeParameters;
    private List<TypeParameter> primaryMethodTypeParameters;
    private TypeParameter resultTypeParameter;
    private List<TypeParameter> secondaryMethodTypeParameters;
    private String methodName;
    private List<String> primaryParameterNames;
    private List<Parameter> secondaryParameters;
    private String selfParameterName;
    private String combinatorParameterName;
    private String maxTupleSizeParameterName;
    private TypeConstructor parameterTypeConstructor;
    private TypeConstructor resultTypeConstructor;
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

    public Generator withPrimaryMethodTypeParameters(List<TypeParameter> primaryMethodTypeParameters) {
        this.primaryMethodTypeParameters = primaryMethodTypeParameters;
        return this;
    }

    public List<TypeParameter> takePrimaryMethodTypeParameters(int toTake) {
        return primaryMethodTypeParameters.subList(0, toTake);
    }

    public List<Type> getPrimaryMethodTypeParametersAsTypes() {
        return primaryMethodTypeParameters
                .stream()
                .map(TypeParameter::asType)
                .collect(toList());
    }

    public List<TypeArgument> getPrimaryMethodTypeParametersAsTypeArguments() {
        return getPrimaryMethodTypeParametersAsTypes()
                .stream()
                .map(Type::invariant)
                .collect(toList());
    }

    public List<Type> takePrimaryMethodTypeParametersAsTypes(int toTake) {
        return getPrimaryMethodTypeParametersAsTypes().subList(0, toTake);
    }

    public List<TypeArgument> takePrimaryMethodTypeParametersAsTypeArguments(int toTake) {
        return takePrimaryMethodTypeParametersAsTypes(toTake)
                .stream()
                .map(Type::invariant)
                .collect(toList());
    }

    public List<Type> getLiftedPrimaryMethodTypeParametersAsTypes() {
        return getPrimaryMethodTypeParametersAsTypes()
                .stream()
                .map(parameterTypeConstructor::apply)
                .collect(toList());
    }

    public List<Type> takeLiftedPrimaryMethodTypeParametersAsTypes(int toTake) {
        return getLiftedPrimaryMethodTypeParametersAsTypes().subList(0, toTake);
    }

    public Generator withResultTypeParameter(TypeParameter resultTypeParameter) {
        this.resultTypeParameter = resultTypeParameter;
        return this;
    }

    public Type getLiftedResultType() {
        return resultTypeParameter.asType().using(resultTypeConstructor);
    }

    public Generator withSecondaryMethodTypeParameters(List<TypeParameter> secondaryMethodTypeParameters) {
        this.secondaryMethodTypeParameters = secondaryMethodTypeParameters;
        return this;
    }

    public List<TypeArgument> getSecondaryMethodTypeParametersAsTypeArguments() {
        return secondaryMethodTypeParameters
                .stream()
                .map(TypeParameter::invariant)
                .collect(toList());
    }

    public Generator withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Generator withPrimaryParameterNames(List<String> primaryParameterNames) {
        this.primaryParameterNames = primaryParameterNames;
        return this;
    }

    public List<String> takePrimaryParameterNames(int toTake) {
        return primaryParameterNames.subList(0, toTake);
    }

    public Generator withSecondaryParameters(List<Parameter> secondaryParameters) {
        this.secondaryParameters = secondaryParameters;
        return this;
    }

    public List<String> getSecondaryParameterNames() {
        return secondaryParameters.stream().map(Parameter::getName).collect(toList());
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

    public Generator withParameterTypeConstructor(TypeConstructor parameterTypeConstructor) {
        this.parameterTypeConstructor = parameterTypeConstructor;
        return this;
    }

    public Generator withResultTypeConstructor(TypeConstructor resultTypeConstructor) {
        this.resultTypeConstructor = resultTypeConstructor;
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
        List<String> lines = new ArrayList<>();
        lines.add(PACKAGE + SPACE + packageName.raw() + SEMICOLON);
        lines.add(EMPTY_LINE);

        classOrInterface()
                .asInterface()
                .withModifiers(PUBLIC)
                .withName(classNameToGenerate)
                .withTypeParameters(classTypeParameters)
                .withBody(composeMethods())
                .withBody(EMPTY_LINE)
                .withBody(liftMethods())
                .withBody(EMPTY_LINE)
                .withBody(
                        classOrInterface()
                                .asClass()
                                .withName(TUPLE_CLASS_NAME.raw())
                                .withBody(tupleMethods())
                                .lines()
                )
                .lines()
                .forEach(lines::add);
        return String.join(LINE_FEED, lines);
    }

    private List<String> composeMethods() {
        List<String> lines = new ArrayList<>();
        lines.addAll(composeMethodWithArityTwo());
        IntStream.rangeClosed(3, maxArity).forEach(arity -> {
            lines.add(EMPTY_LINE);
            lines.addAll(composeMethodWithArity(arity));
        });
        return lines;
    }

    private List<String> composeMethodWithArityTwo() {
        int arity = 2;

        return method()
                .withTypeParameters(takePrimaryMethodTypeParameters(arity))
                .withTypeParameters(resultTypeParameter.getName())
                .withTypeParameters(secondaryMethodTypeParameters)
                .withReturnType(getLiftedResultType())
                .withName(methodName)
                .withParameterTypes(takeLiftedPrimaryMethodTypeParametersAsTypes(arity))
                .andParameterNames(takePrimaryParameterNames(arity))
                .withParameter(
                        BI_FUNCTION.with(
                                primaryMethodTypeParameters.get(0).contravariant(),
                                primaryMethodTypeParameters.get(1).contravariant(),
                                resultTypeParameter.covariant()
                        ),
                        combinatorParameterName
                )
                .withParameters(secondaryParameters)
                .lines();
    }

    private List<String> composeMethodWithArity(int arity) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takePrimaryMethodTypeParameters(arity))
                .withTypeParameters(resultTypeParameter.getName())
                .withTypeParameters(secondaryMethodTypeParameters)
                .withReturnType(getLiftedResultType())
                .withName(methodName)
                .withParameterTypes(takeLiftedPrimaryMethodTypeParametersAsTypes(arity))
                .andParameterNames(takePrimaryParameterNames(arity))
                .withParameter(lambdaType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withParameters(secondaryParameters)
                .withReturnStatement(
                        methodCall()
                                .withObjectPath(THIS)
                                .withMethodName(methodName)
                                .withArguments(
                                        methodCall()
                                                .withType(getFullyQualifiedTupleClass())
                                                .withTypeArguments(takePrimaryMethodTypeParametersAsTypeArguments(arity))
                                                .withTypeArguments(nCopies(NUMBER_OF_TUPLE_TYPE_PARAMETERS - arity, OBJECT.invariant()))
                                                .withTypeArguments(getSecondaryMethodTypeParametersAsTypeArguments())
                                                .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                                .withMethodName(TUPLE_METHOD_NAME)
                                                .withArguments(THIS)
                                                .withArguments(takePrimaryParameterNames(arity - 1))
                                                .withArguments(Integer.toString(arity))
                                                .withArguments(getSecondaryParameterNames())
                                                .generate(),
                                        primaryParameterNames.get(arity - 1),
                                        methodReference().withObjectPath(combinatorParameterName).withMethodName(COMPOSITION_FUNCTION_APPLY_METHOD).generate()
                                )
                                .withArguments(getSecondaryParameterNames())
                                .generate()
                )
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
                        .withMethodName(methodName)
                        .withArguments(takePrimaryParameterNames(arity))
                        .withArguments(combinatorParameterName)
                        .withArguments(getSecondaryParameterNames())
                        .generate()
        );
    }

    private List<String> liftMethodWithArity(int arity, String lambdaBody) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takePrimaryMethodTypeParameters(arity))
                .withTypeParameters(resultTypeParameter.getName())
                .withTypeParameters(secondaryMethodTypeParameters)
                .withReturnType(lambdaType(parameterTypeConstructor, resultTypeConstructor, arity))
                .withName(liftMethodName)
                .withParameter(lambdaType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withParameters(secondaryParameters)
                .withReturnStatement(
                        lambda()
                                .withParameterNames(takePrimaryParameterNames(arity))
                                .withExpression(lambdaBody)
                                .lines()
                )
                .lines();
    }

    private List<String> tupleMethods() {
        return IntStream.rangeClosed(3, maxArity - 1)
                .boxed()
                .collect(
                        this::tupleMethodWithArityTwo,
                        (acc, i) -> {
                            acc.add(EMPTY_LINE);
                            acc.addAll(tupleMethodWithArity(i));
                        },
                        List::addAll
                );
    }

    private List<String> tupleMethodWithArityTwo() {
        return tupleMethodWithArity(
                2,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(methodName)
                        .withArguments(
                                primaryParameterNames.get(0),
                                primaryParameterNames.get(1),
                                methodCall()
                                        .withType(FAST_TUPLE)
                                        .withMethodName(FAST_TUPLE_WITH_MAX_SIZE_METHOD_NAME)
                                        .withArguments(maxTupleSizeParameterName)
                                        .generate()
                        )
                        .withArguments(getSecondaryParameterNames())
                        .generate()
        );
    }

    private List<String> tupleMethodWithArity(int arity) {
        return tupleMethodWithArity(
                arity,
                methodCall()
                        .withObjectPath(selfParameterName)
                        .withMethodName(methodName)
                        .withArguments(
                                methodCall()
                                        .withType(getFullyQualifiedTupleClass())
                                        .withTypeArguments(getPrimaryMethodTypeParametersAsTypeArguments())
                                        .withTypeArguments(getSecondaryMethodTypeParametersAsTypeArguments())
                                        .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                        .withMethodName(TUPLE_METHOD_NAME)
                                        .withArguments(selfParameterName)
                                        .withArguments(takePrimaryParameterNames(arity - 1))
                                        .withArguments(maxTupleSizeParameterName)
                                        .withArguments(getSecondaryParameterNames())
                                        .generate(),
                                primaryParameterNames.get(arity - 1),
                                methodReference()
                                        .withType(FAST_TUPLE)
                                        .withMethodName(witherForIndex(arity - 1))
                                        .generate()
                        )
                        .withArguments(getSecondaryParameterNames())
                        .generate()
        );
    }

    private List<String> tupleMethodWithArity(int arity, String methodBody) {
        return method()
                .withModifiers(PUBLIC, STATIC)
                .withTypeParameters(primaryMethodTypeParameters)
                .withTypeParameters(secondaryMethodTypeParameters)
                // Since these are all static methods, that don't have access to any class type parameters
                // we need to make sure that the class type parameters are available as additional method
                // type parameters:
                .withTypeParameters(classTypeParameters)
                .withReturnType(FAST_TUPLE.with(getPrimaryMethodTypeParametersAsTypeArguments()).using(parameterTypeConstructor))
                .withName(TUPLE_METHOD_NAME)
                .withParameter(getFullyQualifiedClassNameToGenerate().with(getClassTypeParametersAsTypeArguments()), selfParameterName)
                .withParameterTypes(takeLiftedPrimaryMethodTypeParametersAsTypes(arity))
                .andParameterNames(takePrimaryParameterNames(arity))
                .withParameter(INT, maxTupleSizeParameterName)
                .withParameters(secondaryParameters)
                .withReturnStatement(methodBody)
                .lines();
    }

    private Type lambdaType(TypeConstructor parameterTypeConstructor, TypeConstructor resultTypeConstructor, int arity) {
        List<TypeArgument> typeArguments = new ArrayList<>();
        takePrimaryMethodTypeParametersAsTypes(arity)
                .stream()
                .map(parameterTypeConstructor::apply)
                .map(Type::contravariant)
                .forEachOrdered(typeArguments::add);
        typeArguments.add(resultTypeParameter.asType().using(resultTypeConstructor).covariant());

        return Type.concrete(fullyQualifiedNameOfFunction(arity), typeArguments);
    }

    private static FullyQualifiedName fullyQualifiedNameOfFunction(int arity) {
        return arity == 2
                ? BI_FUNCTION.getFullyQualifiedName()
                : FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Function" + arity);
    }
}
