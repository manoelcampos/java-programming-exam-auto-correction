package io.github.manoelcampos.examcorrection;

import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * @author Manoel Campos
 */
public abstract class ClassTest extends AbstractTest {
    private static final Logger log = java.util.logging.Logger.getLogger(ClassTest.class.getName());

    public ClassTest(final Map<String, String> expectedFieldsMap) {
        super(expectedFieldsMap);
    }

    @Override
    @Test
    protected void classType() {
        final var msg = "%s deve ser uma class".formatted(testedClassName);
        assertThat(msg, !testedClass.isLocalClass());
    }

    @Test
    void fieldsVisibility(){
        expectedFieldsMap.forEach((fieldName, fieldType) -> {
            final var field = testedClass.getField(fieldName);
            final var notFound = "%s não encontrado".formatted(fieldName);
            assertThat(notFound, field, is(notNullValue()));
            final var msg = "Atributo %s deve ser privado".formatted(fieldName);
            assertThat(msg, field.getModifiers().contains(JavaModifier.PRIVATE));
        });
    }

    @Test
    void hasGetters(){
        expectedFieldsMap.forEach((fieldName, fieldType) -> {
            final var method = "get%s".formatted(camelCaseFieldName(fieldName));
            final var getter = findGetter(fieldName);
            final var msg = "Getter para o atributo %s não encontrado".formatted(fieldName);
            assertThat(msg.formatted(method), getter, is(notNullValue()));
        });
    }

    @Test
    void hasSetters(){
        expectedFieldsMap.forEach((fieldName, fieldType) -> {
            final var getter = findSetter(fieldName);
            final var msg = "Setter para o atributo %s não encontrado".formatted(fieldName);
            assertThat(msg, getter, is(notNullValue()));
        });
    }

    private JavaMethod findGetter(final String fieldName) {
        final var method = "get" + camelCaseFieldName(fieldName);
        return findMethod(method);
    }

    private Object findSetter(final String fieldName) {
        final var method = "set" + camelCaseFieldName(fieldName);
        return findMethod(method, expectedFieldsMap.get(fieldName));
    }

    private JavaMethod findMethod(final String methodName, final String ...parameterTypes) {
        try {
            return testedClass.getMethod(methodName, loadMethodParamClasses(parameterTypes));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Erro ao localizar método %s".formatted(methodName), e);
            return null;
        }
    }

    /**
     * Loads the classes that represent the types of parameters from a given method.
     * @param parameterTypes the name of the type for each parameter for the method
     * @return
     */
    private Class<?>[] loadMethodParamClasses(final String[] parameterTypes) {
        final var list = Arrays.stream(parameterTypes).map(ClassTest::findClass).toList();
        return list.toArray(Class<?>[]::new);
    }

    private static Class<?> findClass(final String className)  {
        final var javaTypes = List.of("String", "int", "double", "float", "boolean", "char",
                                      "byte", "short", "long", "Integer", "Double", "Float", "Boolean", "Character",
                                      "Byte", "Short", "Long", "Short", "Byte");
        final var fullClassName = javaTypes.contains(className) ? "java.lang." + className : className;
        try {
            return Class.forName(fullClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static String camelCaseFieldName(String fieldName) {
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
