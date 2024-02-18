package io.github.manoelcampos.examcorrection;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

/**
 * @author Manoel Campos
 */
public abstract class AbstractTest {
    protected final String testedClassName;
    protected final JavaClass testedClass;
    protected final Map<String, String> expectedFieldsMap;
    protected final Set<JavaField> fields;

    @Test
    protected abstract void classType();

    public AbstractTest(final Map<String, String> expectedFieldsMap) {
        this.testedClassName = getClass().getSimpleName().replace("Test", "");
        this.testedClass = testedClass();
        this.fields = testedClass == null ? Collections.emptySet() : testedClass.getAllFields();
        this.expectedFieldsMap = expectedFieldsMap;

        final var classType = this instanceof RecordTest ? "record" : "class";
        final var msg = "%s %s não encontrada(o) no pacote %s".formatted(classType, testedClassName, getClass().getPackageName());
        assertThat(msg, testedClass, Matchers.notNullValue());
    }

    @Test
    void hasFields() {
        final var msg = "%s deve ter pelo menos %d atributo(s)".formatted(testedClassName, expectedFieldsMap.size());
        assertThat(msg, fields, hasSize(Matchers.greaterThanOrEqualTo(expectedFieldsMap.size())));
    }

    @Test
    void checkFieldsExistence() {
        expectedFieldsMap.forEach(this::checkFieldExists);
    }

    protected final JavaClass testedClass() {
        try {
            final var classUnderTest = Class.forName("%s.%s".formatted(getClass().getPackageName(), testedClassName));
            return new ClassFileImporter().importClass(classUnderTest);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private void checkFieldExists(final String expectedFieldName, final String expectedFieldType) {
        final var msgFieldMissing = "%s deve ter um atributo chamado '%s'".formatted(testedClassName, expectedFieldName);
        final var msgFieldType = "%s.%s deve ser do tipo %s".formatted(testedClassName, expectedFieldName, expectedFieldType);

        final var nomeFieldOptional = fields.stream().filter(field -> expectedFieldName.equals(field.getName())).findFirst();
        MatcherAssert.assertThat(msgFieldMissing, nomeFieldOptional.isPresent());
        nomeFieldOptional.ifPresent(field -> assertThat(msgFieldType, getFieldType(field, expectedFieldType), equalTo(expectedFieldType)));

    }

    /**
     * Get the actual field type according if the expected field type has the package name or not.
     * @param field the field to get the type
     * @param expectedFieldType the expected type for the field
     * @return the full field name if the expected type has a package name, otherwise, the simple field name.
     */
    private static String getFieldType(final JavaField field, final String expectedFieldType) {
        final var name = field.getType().getName();
        if(expectedFieldType.contains("."))
            return name;

        if(!name.contains("."))
            return name;

        final String[] split = name.split("\\.");
        return split[split.length-1];
    }
}
