package nl.wernerdegroot.applicatives.processor;

import nl.wernerdegroot.applicatives.processor.domain.*;
import nl.wernerdegroot.applicatives.processor.domain.containing.ContainingClass;
import nl.wernerdegroot.applicatives.processor.logging.Log;
import nl.wernerdegroot.applicatives.processor.logging.LoggingBackend;
import nl.wernerdegroot.applicatives.processor.logging.StringBuilderLoggingBackend;
import nl.wernerdegroot.applicatives.processor.validation.CovariantParametersAndTypeParametersValidator;
import nl.wernerdegroot.applicatives.processor.validation.Validated;
import nl.wernerdegroot.applicatives.processor.validation.Validator;
import org.junit.jupiter.api.Test;

import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OPTIONAL;
import static org.junit.jupiter.api.Assertions.*;

public class ProcessorTemplateTest implements VarianceProcessorTemplateTest {

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenValidMethod() throws NoSuchMethodException, IOException {

        TestProcessor processor = new TestProcessor(false);
        MockElement mockElement = getMockElement("givenValidMethod");
        processor.process(mockElement);

        String expectedContents = getResourceClassFileAsString("OptionalsOverloads");
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertNull(toVerifyErrors);

        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertNull(toVerifyInfo);
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenValidMethodWithVerboseLogging() throws NoSuchMethodException, IOException {
        TestProcessor processor = new TestProcessor(true);
        MockElement mockElement = getMockElement("givenValidMethodWithVerboseLogging");
        processor.process(mockElement);

        String expectedContents = getResourceClassFileAsString("OptionalsOverloads");
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertNull(toVerifyErrors);

        String expectedInfo = "Found annotation of type 'nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest.MockAnnotation' on mock element 'givenValidMethodWithVerboseLogging'\n" +
                " - Class name: *Overloads\n" +
                " - Method name for 'combine': *\n" +
                " - Method name for 'lift': lift\n" +
                " - Maximum arity: 2\n" +
                "Successfully transformed objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain'\n" +
                "Found method 'combine' in class 'nl.wernerdegroot.applicatives.Optionals'\n" +
                " - Annotations: nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest.MockAnnotation\n" +
                " - Modifiers: public\n" +
                " - Type parameters: T, U, V\n" +
                " - Return type: java.util.Optional<V>\n" +
                " - Parameters: java.util.Optional<? extends T> left, java.util.Optional<? extends U> right, java.util.function.BiFunction<? super T, ? super U, ? extends V> compose\n" +
                "All criteria for code generation satisfied\n" +
                " - Class type parameters: none\n" +
                " - Name of initializer method: none\n" +
                " - Initialized type constructor: none\n" +
                " - Name of accumulator method: combine\n" +
                " - Input type constructor: java.util.Optional<? extends *>\n" +
                " - Partially accumulated type constructor: java.util.Optional<? extends *>\n" +
                " - Accumulated type constructor: java.util.Optional<*>\n" +
                " - Name of finalizer method: none\n" +
                " - To finalize type constructor: none\n" +
                " - Finalized type constructor: none\n" +
                "Resolved (potential) conflicts between existing type parameters and new, generated type parameters\n" +
                " - Class type parameters: none\n" +
                " - Name of initializer method: none\n" +
                " - Initialized type constructor: none\n" +
                " - Name of accumulator method: combine\n" +
                " - Input type constructor: java.util.Optional<? extends *>\n" +
                " - Partially accumulated type constructor: java.util.Optional<? extends *>\n" +
                " - Accumulated type constructor: java.util.Optional<*>\n" +
                " - Name of finalizer method: none\n" +
                " - To finalize type constructor: none\n" +
                " - Finalized type constructor: none\n" +
                "Saved generated code to .java-file on disk (nl.wernerdegroot.applicatives.OptionalsOverloads)\n" +
                "Done generating code\n";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertEquals(expectedInfo.trim(), toVerifyInfo.trim());
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileTransformingToDomain() throws NoSuchMethodException {
        TestProcessor processor = new TestProcessor(false) {
            @Override
            public Method toMethodOrMethods(MockElement mockElement) {
                throw new IllegalArgumentException("There was a problem!");
            }
        };
        MockElement mockElement = getMockElement("givenErrorWhileTransformingToDomain");
        processor.process(mockElement);

        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertNull(toVerifyContents);

        String expectedErrors = "Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for mock element 'givenErrorWhileTransformingToDomain': There was a problem!\nEnable verbose logging to see a stack trace.\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertNull(toVerifyInfo);
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileTransformingToDomainWithVerboseLogging() throws NoSuchMethodException {
        TestProcessor processor = new TestProcessor(true) {
            @Override
            public Method toMethodOrMethods(MockElement mockElement) {
                throw new IllegalArgumentException("There was a problem!");
            }
        };
        MockElement mockElement = getMockElement("givenErrorWhileTransformingToDomainWithVerboseLogging");
        processor.process(mockElement);

        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertNull(toVerifyContents);

        String expectedErrors = "Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for mock element 'givenErrorWhileTransformingToDomainWithVerboseLogging': There was a problem!\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        // Prefix of error message and stack trace. Because the stack trace is likely different
        // on different platforms (it is certainly different when running from Maven or from
        // IntelliJ) and line numbers are subject to change, we only compare prefixes.
        String expectedInfo = "Found annotation of type 'nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest.MockAnnotation' on mock element 'givenErrorWhileTransformingToDomainWithVerboseLogging'\n" +
                " - Class name: *Overloads\n" +
                " - Method name for 'combine': *\n" +
                " - Method name for 'lift': lift\n" +
                " - Maximum arity: 2\n" +
                "java.lang.IllegalArgumentException: There was a problem!\n" +
                "\tat nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertTrue(toVerifyInfo.startsWith(expectedInfo));
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenValidationError() throws NoSuchMethodException {
        TestProcessor processor = new TestProcessor(false) {
            @Override
            public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
                return Validated.invalid(
                        Log.of("Pretty bad"),
                        Log.of("Not good at all"),
                        Log.of("Problematic")
                );
            }
        };
        MockElement mockElement = getMockElement("givenValidationError");
        processor.process(mockElement);

        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertNull(toVerifyContents);

        String expectedErrors = "Method 'combine' in class 'nl.wernerdegroot.applicatives.Optionals' does not meet all criteria for code generation\n - Pretty bad\n - Not good at all\n - Problematic\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertNull(toVerifyInfo);
    }

    @Test
    @MockAnnotation(maxArity = -1)
    public void givenConfigValidationError() throws NoSuchMethodException {
        TestProcessor processor = new TestProcessor(false);
        MockElement mockElement = getMockElement("givenConfigValidationError");
        processor.process(mockElement);

        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertNull(toVerifyContents);

        String expectedErrors = "Configuration of 'nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest.MockAnnotation' not valid\n - Maximum arity should be between 2 and 26 (but was -1)\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertNull(toVerifyInfo);
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileWritingToFile() throws NoSuchMethodException {
        TestProcessor processor = new TestProcessor(false) {
            @Override
            public PrintWriter getPrintWriterForFile(FullyQualifiedName fullyQualifiedName) throws IOException {
                throw new IOException("There was a problem!");
            }
        };
        MockElement mockElement = getMockElement("givenErrorWhileWritingToFile");
        processor.process(mockElement);

        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertNull(toVerifyContents);

        String expectedErrors = "Error saving generated code to .java-file on disk (nl.wernerdegroot.applicatives.OptionalsOverloads): There was a problem!\nEnable verbose logging to see a stack trace.\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertNull(toVerifyInfo);
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileValidating() throws NoSuchMethodException {
        TestProcessor processor = new TestProcessor(false) {
            @Override
            public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
                throw new IllegalArgumentException("There was a problem!");
            }
        };
        MockElement mockElement = getMockElement("givenErrorWhileValidating");
        processor.process(mockElement);

        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertNull(toVerifyContents);

        String expectedErrors = "Error occurred while processing annotation of type 'interface nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest$MockAnnotation': There was a problem!\nEnable verbose logging to see a stack trace.\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertNull(toVerifyInfo);
    }

    private MockElement getMockElement(String methodName) throws NoSuchMethodException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod(methodName);
        MockElement mockElement = new MockElement(
                self.getName(),
                self.getAnnotation(MockAnnotation.class),
                PackageName.of("nl.wernerdegroot.applicatives")
                        .asPackage()
                        .containingClass(emptySet(), ClassName.of("Optionals")),
                Method.of(
                        singleton(MOCK_ANNOTATION_NAME),
                        singleton(PUBLIC),
                        asList(T.asTypeParameter(), U.asTypeParameter(), V.asTypeParameter()),
                        Optional.of(OPTIONAL.with(V)),
                        "combine",
                        asList(
                                Parameter.of(OPTIONAL.with(T.covariant()), "left"),
                                Parameter.of(OPTIONAL.with(U.covariant()), "right"),
                                Parameter.of(BI_FUNCTION.with(T.contravariant(), U.contravariant(), V.covariant()), "compose")
                        )
                )
        );
        return mockElement;
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MockAnnotation {
        String className() default "*Overloads";

        String combineMethodName() default "*";

        String liftMethodName() default "lift";

        int maxArity() default 26;
    }

    private static final FullyQualifiedName MOCK_ANNOTATION_NAME = FullyQualifiedName.of(MockAnnotation.class.getCanonicalName());

    class MockElement {
        private final String description;
        private final MockAnnotation annotation;
        private final ContainingClass containingClass;
        private final Method method;

        public MockElement(String description, MockAnnotation annotation, ContainingClass containingClass, Method method) {
            this.description = description;
            this.annotation = annotation;
            this.containingClass = containingClass;
            this.method = method;
        }
    }

    // As realistic as I can make it:
    class TestProcessor implements ProcessorTemplate<MockAnnotation, MockElement, MockElement, Method>, CovariantProcessorTemplate {

        private final Map<FullyQualifiedName, StringWriter> writers = new HashMap<>();
        private final Map<String, String> configuration;
        private final Map<Diagnostic.Kind, StringBuilderLoggingBackend> loggingBackends = new HashMap<>();

        public TestProcessor(Map<String, String> configuration) {
            this.configuration = configuration;
        }

        public TestProcessor(boolean verbose) {
            this.configuration = new HashMap<>();
            if (verbose) {
                this.configuration.put("applicatives.verbose", "true");
            }
        }

        @Override
        public Class<MockAnnotation> getAnnotationType() {
            return MockAnnotation.class;
        }

        @Override
        public MockAnnotation getAnnotation(MockElement mockElement) {
            return mockElement.annotation;
        }

        @Override
        public MockElement getElementToProcess(MockElement mockElement) {
            return mockElement;
        }

        @Override
        public String getClassNameToGenerate(MockAnnotation covariant) {
            return covariant.className();
        }

        @Override
        public String getCombineMethodNameToGenerate(MockAnnotation covariant) {
            return covariant.combineMethodName();
        }

        @Override
        public String getLiftMethodNameToGenerate(MockAnnotation covariant) {
            return covariant.liftMethodName();
        }

        @Override
        public int getMaxArity(MockAnnotation covariant) {
            return covariant.maxArity();
        }

        @Override
        public String describeElementToProcess(MockElement mockElement) {
            return String.format("mock element '%s'", mockElement.description);
        }

        @Override
        public ContainingClass toContainingClass(MockElement mockElement) {
            return mockElement.containingClass;
        }

        @Override
        public Method toMethodOrMethods(MockElement mockElement) {
            return mockElement.method;
        }

        @Override
        public void noteContainingClassAndMethodOrMethods(ContainingClass containingClass, Method method) {
            noteMethodFound(containingClass, method);
        }

        @Override
        public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
            return Validator.validate(containingClass, method, new CovariantParametersAndTypeParametersValidator());
        }

        @Override
        public void errorValidationFailed(ContainingClass containingClass, Method method, List<Log> errorMessages) {
            Log.of("Method '%s' in class '%s' does not meet all criteria for code generation", method.getName(), containingClass.getFullyQualifiedName().raw())
                    .withLogs(errorMessages)
                    .append(asError());
        }

        @Override
        public PrintWriter getPrintWriterForFile(FullyQualifiedName fullyQualifiedName) throws IOException {
            StringWriter writer = writers.computeIfAbsent(fullyQualifiedName, ignored -> new StringWriter());
            return new PrintWriter(writer);
        }

        @Override
        public Map<String, String> getConfiguration() {
            return configuration;
        }

        @Override
        public LoggingBackend getMessengerLoggingBackend(Diagnostic.Kind diagnosticKind) {
            return loggingBackends.computeIfAbsent(diagnosticKind, ignored -> new StringBuilderLoggingBackend());
        }

        public String getWritten(FullyQualifiedName fullyQualifiedName) {
            return Optional.ofNullable(writers.get(fullyQualifiedName))
                    .map(StringWriter::toString)
                    .orElse(null);
        }

        public String getLogged(Diagnostic.Kind diagnosticKind) {
            return Optional.ofNullable(loggingBackends.get(diagnosticKind))
                    .map(StringBuilderLoggingBackend::build)
                    .orElse(null);
        }
    }
}
