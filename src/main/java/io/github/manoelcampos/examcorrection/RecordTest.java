package io.github.manoelcampos.examcorrection;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Manoel Campos
 */
public abstract class RecordTest extends AbstractTest{
    public RecordTest(final Map<String, String> fields) {
        super(fields);
    }

    @Override
    @Test
    protected void classType() {
        final var msg = "%s deve ser um Record".formatted(testedClassName);
        assertThat(msg, testedClass.isRecord());
    }
}
