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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProcessorTemplateTest implements VarianceProcessorTemplateTest {

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
        public void noteAnnotationFound(MockElement mockElement, String classNameToGenerate, String combineMethodNameToGenerate, String liftMethodNameToGenerate, int maxArity) {
            Log.of("Found annotation of type '%s' on  mock element '%s'", getAnnotationType().getCanonicalName(), mockElement.description)
                    .withDetail("Class name", classNameToGenerate)
                    .withDetail("Method name for `combine`", combineMethodNameToGenerate)
                    .withDetail("Method name for `lift`", liftMethodNameToGenerate)
                    .withDetail("Maximum arity", maxArity, i -> Integer.toString(i))
                    .append(asNote());
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
        public void errorConversionToDomainFailed(MockElement mockElement, Throwable throwable) {
            Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for mock element '%s': %s", mockElement.description, throwable.getMessage()).append(asError());
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
                    .orElse("");
        }

        public String getLogged(Diagnostic.Kind diagnosticKind) {
            return Optional.ofNullable(loggingBackends.get(diagnosticKind))
                    .map(StringBuilderLoggingBackend::build)
                    .orElse("");
        }
    }

    private final TypeParameterName T = TypeParameterName.of("T");
    private final TypeParameterName U = TypeParameterName.of("U");
    private final TypeParameterName V = TypeParameterName.of("V");

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenValidMethod() throws NoSuchMethodException, IOException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenValidMethod");

        Map<String, String> configuration = new HashMap<>();
        TestProcessor processor = new TestProcessor(configuration);
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
        processor.process(mockElement);

        String expectedContents = getResourceClassFileAsString("OptionalsOverloads");
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertEquals(expectedInfo.trim(), toVerifyInfo.trim());
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenValidMethodWithVerboseLogging() throws NoSuchMethodException, IOException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenValidMethodWithVerboseLogging");

        Map<String, String> configuration = new HashMap<>();
        configuration.put("applicatives.verbose", "true");
        TestProcessor processor = new TestProcessor(configuration);
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
        processor.process(mockElement);

        String expectedContents = getResourceClassFileAsString("OptionalsOverloads");
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = getResourceFileAsString("/" + self.getName() + ".log");
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertEquals(expectedInfo.trim(), toVerifyInfo.trim());
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileTransformingToDomain() throws NoSuchMethodException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenErrorWhileTransformingToDomain");

        Map<String, String> configuration = new HashMap<>();
        TestProcessor processor = new TestProcessor(configuration) {
            @Override
            public Method toMethodOrMethods(MockElement mockElement) {
                throw new IllegalArgumentException("There was a problem!");
            }
        };
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
        processor.process(mockElement);

        String expectedContents = "";
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for mock element 'givenErrorWhileTransformingToDomain': There was a problem!\nEnable verbose logging to see a stack trace.\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertTrue(toVerifyInfo.startsWith(expectedInfo));
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileTransformingToDomainWithVerboseLogging() throws NoSuchMethodException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenErrorWhileTransformingToDomainWithVerboseLogging");

        Map<String, String> configuration = new HashMap<>();
        configuration.put("applicatives.verbose", "true");
        TestProcessor processor = new TestProcessor(configuration) {
            @Override
            public Method toMethodOrMethods(MockElement mockElement) {
                throw new IllegalArgumentException("There was a problem!");
            }
        };
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
        processor.process(mockElement);

        String expectedContents = "";
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for mock element 'givenErrorWhileTransformingToDomainWithVerboseLogging': There was a problem!\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "Found annotation of type 'nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest.MockAnnotation' on  mock element 'givenErrorWhileTransformingToDomainWithVerboseLogging'\n" +
                " - Class name: *Overloads\n" +
                " - Method name for `combine`: *\n" +
                " - Method name for `lift`: lift\n" +
                " - Maximum arity: 2\n" +
                "java.lang.IllegalArgumentException: There was a problem!\n" +
                "\tat nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertTrue(toVerifyInfo.startsWith(expectedInfo));
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenValidationError() throws NoSuchMethodException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenValidationError");

        Map<String, String> configuration = new HashMap<>();
        TestProcessor processor = new TestProcessor(configuration) {
            @Override
            public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
                return Validated.invalid(
                        Log.of("Pretty bad"),
                        Log.of("Not good at all"),
                        Log.of("Problematic")
                );
            }
        };
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
        processor.process(mockElement);

        String expectedContents = "";
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "Method 'combine' in class 'nl.wernerdegroot.applicatives.Optionals' does not meet all criteria for code generation\n - Pretty bad\n - Not good at all\n - Problematic\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertTrue(toVerifyInfo.startsWith(expectedInfo));
    }

    @Test
    @MockAnnotation(maxArity = -1)
    public void givenConfigValidationError() throws NoSuchMethodException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenConfigValidationError");

        Map<String, String> configuration = new HashMap<>();
        TestProcessor processor = new TestProcessor(configuration);
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
        processor.process(mockElement);

        String expectedContents = "";
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "Configuration of 'nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest.MockAnnotation' not valid\n - Maximum arity should be between 2 and 26 (but was -1)\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertTrue(toVerifyInfo.startsWith(expectedInfo));
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileWritingToFile() throws NoSuchMethodException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenErrorWhileTransformingToDomainWithVerboseLogging");

        Map<String, String> configuration = new HashMap<>();
        TestProcessor processor = new TestProcessor(configuration) {
            @Override
            public PrintWriter getPrintWriterForFile(FullyQualifiedName fullyQualifiedName) throws IOException {
                throw new IOException("There was a problem!");
            }
        };
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
        processor.process(mockElement);

        String expectedContents = "";
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "Error saving generated code to .java-file on disk (nl.wernerdegroot.applicatives.OptionalsOverloads): There was a problem!\nEnable verbose logging to see a stack trace.\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertTrue(toVerifyInfo.startsWith(expectedInfo));
    }

    @Test
    @MockAnnotation(maxArity = 2)
    public void givenErrorWhileValidating() throws NoSuchMethodException {
        java.lang.reflect.Method self = ProcessorTemplateTest.class.getMethod("givenErrorWhileTransformingToDomainWithVerboseLogging");

        Map<String, String> configuration = new HashMap<>();
        TestProcessor processor = new TestProcessor(configuration) {
            @Override
            public Validated<Log, Validator.Result> validate(ContainingClass containingClass, Method method) {
                throw new IllegalArgumentException("There was a problem!");
            }
        };
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
        processor.process(mockElement);

        String expectedContents = "";
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "Error occurred while processing annotation of type 'interface nl.wernerdegroot.applicatives.processor.ProcessorTemplateTest$MockAnnotation': There was a problem!\nEnable verbose logging to see a stack trace.\n";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertTrue(toVerifyInfo.startsWith(expectedInfo));
    }
}
