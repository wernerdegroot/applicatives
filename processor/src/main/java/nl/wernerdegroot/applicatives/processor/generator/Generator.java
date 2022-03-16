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
    private List<TypeParameter> inputTypeConstructorArguments;
    private TypeParameter resultTypeConstructorArgument;
    private String methodName;
    private List<String> inputParameterNames;
    private String selfParameterName;
    private String combinatorParameterName;
    private String maxTupleSizeParameterName;
    private TypeConstructor accumulationTypeConstructor;
    private TypeConstructor permissiveAccumulationTypeConstructor;
    private TypeConstructor inputTypeConstructor;
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

    public Generator withInputTypeConstructorArguments(List<TypeParameter> inputTypeConstructorArguments) {
        this.inputTypeConstructorArguments = inputTypeConstructorArguments;
        return this;
    }

    public List<TypeParameter> takeInputTypeConstructorArguments(int toTake) {
        return inputTypeConstructorArguments.subList(0, toTake);
    }

    public List<Type> getInputTypeConstructorArgumentsAsTypes() {
        return inputTypeConstructorArguments
                .stream()
                .map(TypeParameter::asType)
                .collect(toList());
    }

    public List<TypeArgument> getInputTypeConstructorArgumentsAsTypeArguments() {
        return getInputTypeConstructorArgumentsAsTypes()
                .stream()
                .map(Type::invariant)
                .collect(toList());
    }

    public List<Type> takeInputTypeConstructorArgumentsAsTypes(int toTake) {
        return getInputTypeConstructorArgumentsAsTypes().subList(0, toTake);
    }

    public List<TypeArgument> takeInputTypeConstructorArgumentsAsTypeArguments(int toTake) {
        return takeInputTypeConstructorArgumentsAsTypes(toTake)
                .stream()
                .map(Type::invariant)
                .collect(toList());
    }

    public List<Type> takeInputParameterTypes(int toTake) {
        return takeInputParameterTypes(toTake, permissiveAccumulationTypeConstructor, inputTypeConstructor);
    }

    public List<Type> takeInputParameterTypes(int toTake, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor) {
        List<Type> result = new ArrayList<>();
        getInputTypeConstructorArgumentsAsTypes()
                .stream()
                .limit(1)
                .map(permissiveAccumulationTypeConstructor::apply)
                .forEachOrdered(result::add);
        getInputTypeConstructorArgumentsAsTypes()
                .stream()
                .limit(toTake)
                .skip(1)
                .map(inputTypeConstructor::apply)
                .forEachOrdered(result::add);
        return result;
    }

    public Generator withResultTypeConstructorArgument(TypeParameter resultTypeConstructorArgument) {
        this.resultTypeConstructorArgument = resultTypeConstructorArgument;
        return this;
    }

    public Type getResultType() {
        return resultTypeConstructorArgument.asType().using(accumulationTypeConstructor);
    }

    public Generator withMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Generator withInputParameterNames(List<String> inputParameterNames) {
        this.inputParameterNames = inputParameterNames;
        return this;
    }

    public List<String> takeInputParameterNames(int toTake) {
        return inputParameterNames.subList(0, toTake);
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

    public Generator withAccumulationTypeConstructor(TypeConstructor accumulationTypeConstructor) {
        this.accumulationTypeConstructor = accumulationTypeConstructor;
        return this;
    }

    public Generator withPermissiveAccumulationTypeConstructor(TypeConstructor permissiveAccumulationTypeConstructor) {
        this.permissiveAccumulationTypeConstructor = permissiveAccumulationTypeConstructor;
        return this;
    }

    public Generator withInputTypeConstructor(TypeConstructor inputTypeConstructor) {
        this.inputTypeConstructor = inputTypeConstructor;
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
                .withBody(combineMethods())
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

    private List<String> combineMethods() {
        List<String> lines = new ArrayList<>();
        lines.addAll(combineMethodWithArityTwo());
        IntStream.rangeClosed(3, maxArity).forEach(arity -> {
            lines.add(EMPTY_LINE);
            lines.addAll(combineMethodWithArity(arity));
        });
        return lines;
    }

    private List<String> combineMethodWithArityTwo() {
        int arity = 2;

        return method()
                .withTypeParameters(takeInputTypeConstructorArguments(arity))
                .withTypeParameters(resultTypeConstructorArgument.getName())
                .withReturnType(getResultType())
                .withName(methodName)
                .withParameterTypes(takeInputParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(
                        BI_FUNCTION.with(
                                inputTypeConstructorArguments.get(0).contravariant(),
                                inputTypeConstructorArguments.get(1).contravariant(),
                                resultTypeConstructorArgument.covariant()
                        ),
                        combinatorParameterName
                )
                .lines();
    }

    private List<String> combineMethodWithArity(int arity) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeInputTypeConstructorArguments(arity))
                .withTypeParameters(resultTypeConstructorArgument.getName())
                .withReturnType(getResultType())
                .withName(methodName)
                .withParameterTypes(takeInputParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(lambdaType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withReturnStatement(
                        methodCall()
                                .withObjectPath(THIS)
                                .withMethodName(methodName)
                                .withArguments(
                                        methodCall()
                                                .withType(getFullyQualifiedTupleClass())
                                                .withTypeArguments(takeInputTypeConstructorArgumentsAsTypeArguments(arity))
                                                .withTypeArguments(nCopies(NUMBER_OF_TUPLE_TYPE_PARAMETERS - arity, OBJECT.invariant()))
                                                .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                                .withMethodName(TUPLE_METHOD_NAME)
                                                .withArguments(THIS)
                                                .withArguments(takeInputParameterNames(arity - 1))
                                                .withArguments(Integer.toString(arity))
                                                .generate(),
                                        inputParameterNames.get(arity - 1),
                                        methodReference().withObjectPath(combinatorParameterName).withMethodName(COMPOSITION_FUNCTION_APPLY_METHOD).generate()
                                )
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
                        .withArguments(takeInputParameterNames(arity))
                        .withArguments(combinatorParameterName)
                        .generate()
        );
    }

    private List<String> liftMethodWithArity(int arity, String lambdaBody) {
        return method()
                .withModifiers(DEFAULT)
                .withTypeParameters(takeInputTypeConstructorArguments(arity))
                .withTypeParameters(resultTypeConstructorArgument.getName())
                .withReturnType(lambdaType(accumulationTypeConstructor, permissiveAccumulationTypeConstructor, inputTypeConstructor, arity))
                .withName(liftMethodName)
                .withParameter(lambdaType(TypeConstructor.placeholder(), TypeConstructor.placeholder(), TypeConstructor.placeholder(), arity), combinatorParameterName)
                .withReturnStatement(
                        lambda()
                                .withParameterNames(takeInputParameterNames(arity))
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
                        .withMethodName(methodName)
                        .withArguments(
                                methodCall()
                                        .withType(getFullyQualifiedTupleClass())
                                        .withTypeArguments(getInputTypeConstructorArgumentsAsTypeArguments())
                                        .withTypeArguments(getClassTypeParametersAsTypeArguments())
                                        .withMethodName(TUPLE_METHOD_NAME)
                                        .withArguments(selfParameterName)
                                        .withArguments(takeInputParameterNames(arity - 1))
                                        .withArguments(maxTupleSizeParameterName)
                                        .generate(),
                                inputParameterNames.get(arity - 1),
                                methodReference()
                                        .withType(FAST_TUPLE)
                                        .withMethodName(witherForIndex(arity - 1))
                                        .generate()
                        )
                        .generate()
        );
    }

    private List<String> tupleMethodWithArity(int arity, String methodBody) {
        return method()
                .withModifiers(PUBLIC, STATIC)
                .withTypeParameters(inputTypeConstructorArguments)
                // Since these are all static methods, that don't have access to any class type parameters
                // we need to make sure that the class type parameters are available as additional method
                // type parameters:
                .withTypeParameters(classTypeParameters)
                .withReturnType(FAST_TUPLE.with(getInputTypeConstructorArgumentsAsTypeArguments()).using(accumulationTypeConstructor))
                .withName(TUPLE_METHOD_NAME)
                .withParameter(getFullyQualifiedClassNameToGenerate().with(getClassTypeParametersAsTypeArguments()), selfParameterName)
                .withParameterTypes(takeInputParameterTypes(arity))
                .andParameterNames(takeInputParameterNames(arity))
                .withParameter(INT, maxTupleSizeParameterName)
                .withReturnStatement(methodBody)
                .lines();
    }

    private Type lambdaType(TypeConstructor accumulationTypeConstructor, TypeConstructor permissiveAccumulationTypeConstructor, TypeConstructor inputTypeConstructor, int arity) {
        List<TypeArgument> typeArguments = new ArrayList<>();
        takeInputParameterTypes(arity, permissiveAccumulationTypeConstructor, inputTypeConstructor)
                .stream()
                .map(Type::contravariant)
                .forEachOrdered(typeArguments::add);
        typeArguments.add(resultTypeConstructorArgument.asType().using(accumulationTypeConstructor).covariant());

        return Type.concrete(fullyQualifiedNameOfFunction(arity), typeArguments);
    }

    private static FullyQualifiedName fullyQualifiedNameOfFunction(int arity) {
        return arity == 2
                ? BI_FUNCTION.getFullyQualifiedName()
                : FullyQualifiedName.of("nl.wernerdegroot.applicatives.runtime.Function" + arity);
    }
}
