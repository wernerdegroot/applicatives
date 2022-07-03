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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static nl.wernerdegroot.applicatives.processor.domain.Modifier.PUBLIC;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.BI_FUNCTION;
import static nl.wernerdegroot.applicatives.processor.domain.type.Type.OPTIONAL;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        public void noteConversionToDomainFailed(MockElement mockElement) {
            Log.of("Failure transforming from objects from 'javax.lang.model' to objects from 'nl.wernerdegroot.applicatives.processor.domain' for mock element '%s'", mockElement.description).append(asError());
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
        public void errorValidationFailed(ContainingClass containingClass, Method method, Set<Log> errorMessages) {
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
            return writers.get(fullyQualifiedName).toString();
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

        String expectedContents = getResourceFileAsString("OptionalsOverloads");
        String toVerifyContents = processor.getWritten(FullyQualifiedName.of("nl.wernerdegroot.applicatives.OptionalsOverloads"));
        assertEquals(expectedContents, toVerifyContents);

        String expectedErrors = "";
        String toVerifyErrors = processor.getLogged(Diagnostic.Kind.ERROR);
        assertEquals(expectedErrors, toVerifyErrors);

        String expectedInfo = "";
        String toVerifyInfo = processor.getLogged(Diagnostic.Kind.NOTE);
        assertEquals(expectedInfo, toVerifyInfo);
    }
}
