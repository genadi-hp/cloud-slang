package io.cloudslang.lang.systemtests;

/**
 * Created by Genadi Rabinovich, genadi@hpe.com on 16/05/2016.
 */

import com.google.common.collect.Sets;
import io.cloudslang.lang.compiler.SlangSource;
import io.cloudslang.lang.entities.CompilationArtifact;
import io.cloudslang.lang.entities.ScoreLangConstants;
import io.cloudslang.lang.entities.SystemProperty;
import io.cloudslang.lang.entities.bindings.values.Value;
import io.cloudslang.lang.runtime.events.LanguageEventData;
import io.cloudslang.score.events.ScoreEvent;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FlowWithPyPiPythonVersioningTest extends SystemsTestsParent {
    @Test
    public void testFlowWithOperationWithDifferentVersions() throws Exception {
        URI flow = getClass().getResource("/yaml/versioning/py_test_requests_flow.yaml").toURI();
        URI operation = getClass().getResource("/yaml/versioning/py_test_requests_2_7_0_op.sl").toURI();

        Set<SlangSource> dependencies = Sets.newHashSet(SlangSource.fromFile(operation));
        CompilationArtifact compilationArtifact = slang.compile(SlangSource.fromFile(flow), dependencies);

        ScoreEvent event = trigger(compilationArtifact, new HashMap<String, Value>(), new HashSet<SystemProperty>());
        assertEquals(ScoreLangConstants.EVENT_EXECUTION_FINISHED, event.getEventType());
        LanguageEventData languageEventData = (LanguageEventData) event.getData();

        Integer code = (Integer) languageEventData.getOutputs().get("code");
        assertNotNull("'code' output was not returned", code);
        assertEquals(200, code.intValue());

        String headers = (String) languageEventData.getOutputs().get("headers");
        assertNotNull("'headers' output was not returned", headers);
        assertEquals("text/html", headers);

        String text = (String) languageEventData.getOutputs().get("text");
        assertNotNull("'text' output was not returned", text);
        assertTrue(text.startsWith("<!DOCTYPE html PUBLIC "));
    }

}

