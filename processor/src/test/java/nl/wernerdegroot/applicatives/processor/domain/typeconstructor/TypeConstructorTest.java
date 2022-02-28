package nl.wernerdegroot.applicatives.processor.domain.typeconstructor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import nl.wernerdegroot.applicatives.processor.domain.FullyQualifiedName;
import nl.wernerdegroot.applicatives.processor.domain.TypeParameterName;
import nl.wernerdegroot.applicatives.processor.domain.type.ArrayType;
import nl.wernerdegroot.applicatives.processor.domain.type.GenericType;
import nl.wernerdegroot.applicatives.processor.domain.type.Type;
import nl.wernerdegroot.applicatives.processor.generator.TypeGenerator;
import org.junit.jupiter.api.Test;

import javax.tools.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.EXTENDS;
import static nl.wernerdegroot.applicatives.processor.domain.BoundType.SUPER;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.LIST;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.STRING;
import static org.junit.jupiter.api.Assertions.*;

public class TypeConstructorTest {

    public static TypeParameterName T = TypeParameterName.of("T");

    private final FullyQualifiedName ERUDITE = new FullyQualifiedName("nl.wernerdegroot.Erudite");
    private final ConcreteTypeConstructor STRING_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.String"), emptyList());
    private final ConcreteTypeConstructor INTEGER_TYPE_CONSTRUCTOR = new ConcreteTypeConstructor(FullyQualifiedName.of("java.lang.Integer"), emptyList());

    private final PlaceholderTypeConstructor placeholder = TypeConstructor.placeholder();

    @Test
    public void apply() {
        Type expected = new ArrayType(new GenericType(T));
        Type toVerify = new ArrayTypeConstructor(new PlaceholderTypeConstructor()).apply(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void array() {
        ArrayTypeConstructor expected = new ArrayTypeConstructor(new PlaceholderTypeConstructor());
        ArrayTypeConstructor toVerify = new PlaceholderTypeConstructor().array();

        assertEquals(expected, toVerify);
    }

    @Test
    public void generic() {
        GenericTypeConstructor expected = new GenericTypeConstructor(T);
        GenericTypeConstructor toVerify = TypeConstructor.generic(T);

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedNameAndListOfTypeArguments() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = TypeConstructor.concrete(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedNameAndTypeArguments() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, asList(STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR));
        ConcreteTypeConstructor toVerify = TypeConstructor.concrete(ERUDITE, STRING_TYPE_CONSTRUCTOR, new PlaceholderTypeConstructor(), INTEGER_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void concreteGivenFullyQualifiedName() {
        ConcreteTypeConstructor expected = new ConcreteTypeConstructor(ERUDITE, emptyList());
        ConcreteTypeConstructor toVerify = TypeConstructor.concrete(ERUDITE);

        assertEquals(expected, toVerify);
    }

    @Test
    public void wildcard() {
        WildcardTypeConstructor expected = new WildcardTypeConstructor(SUPER, STRING_TYPE_CONSTRUCTOR);
        WildcardTypeConstructor toVerify = TypeConstructor.wildcard(SUPER, STRING_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void arrayGivenElementTypeConstructor() {
        ArrayTypeConstructor expected = new ArrayTypeConstructor(STRING_TYPE_CONSTRUCTOR);
        ArrayTypeConstructor toVerify = TypeConstructor.array(STRING_TYPE_CONSTRUCTOR);

        assertEquals(expected, toVerify);
    }

    @Test
    public void placeholder() {
        PlaceholderTypeConstructor expected = new PlaceholderTypeConstructor();
        PlaceholderTypeConstructor toVerify = TypeConstructor.placeholder();

        assertEquals(expected, toVerify);
    }

    @Test
    public void canAccept() {

        // This test covers some interesting test cases that are not easily covered by
        // a test in one of the subclasses of `TypeConstructor`. These test cases check
        // if the subclasses work well together, and perform their function as expected.

        List<TypeConstructor> sources = withList(withWildcards(withList(withWildcards(placeholders())))).collect(toList());
        List<TypeConstructor> targets = sources;

        for (TypeConstructor source : sources) {
            for (TypeConstructor target : targets) {
                verify(target, source);
            }
        }
    }

    private Stream<TypeConstructor> placeholders() {
        return Stream.of(TypeConstructor.placeholder());
    }

    private Stream<TypeConstructor> withWildcards(Stream<TypeConstructor> s) {
        return s.flatMap(typeConstructor -> Stream.of(typeConstructor, EXTENDS.type(typeConstructor), SUPER.type(typeConstructor)));
    }

    private Stream<TypeConstructor> withList(Stream<TypeConstructor> s) {
        return s.map(typeConstructor -> LIST.of(typeConstructor));
    }

    private void verify(TypeConstructor target, TypeConstructor source) {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        out.println("class Testing {");
        out.println("  void testing() {");
        out.println("    " + TypeGenerator.generateFrom(source.apply(STRING)) + " source = null;");
        out.println("    " + TypeGenerator.generateFrom(target.apply(STRING)) + " target = source;");
        out.println("  }");
        out.println("}");
        String classBody = writer.toString();

        JavaFileObject javaFileObject = JavaFileObjects.forSourceString("Testing", classBody);
        Compilation result = Compiler.javac().compile(javaFileObject);
        boolean compiles = result.status() == Compilation.Status.SUCCESS;
        boolean canAccept = target.canAccept(source);
        assertEquals(compiles, canAccept, String.format("Assign %s to %s", typeConstructorToString(source), typeConstructorToString(target)));
    }

    private String typeConstructorToString(TypeConstructor typeConstructor) {
        Type substituteForPlaceholder = FullyQualifiedName.of("*").asType();
        Type typeConstructorAsType = typeConstructor.apply(substituteForPlaceholder);
        return TypeGenerator.generateFrom(typeConstructorAsType);
    }
}
