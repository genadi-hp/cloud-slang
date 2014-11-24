package com.hp.score.lang.tests.operation;/*
 * Licensed to Hewlett-Packard Development Company, L.P. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
*/

import com.hp.score.api.Score;
import com.hp.score.api.TriggeringProperties;
import com.hp.score.events.EventBus;
import com.hp.score.events.EventConstants;
import com.hp.score.events.ScoreEvent;
import com.hp.score.events.ScoreEventListener;
import com.hp.score.lang.compiler.SlangCompiler;
import com.hp.score.lang.entities.CompilationArtifact;
import com.hp.score.lang.entities.ScoreLangConstants;
import com.hp.score.lang.runtime.env.RunEnvironment;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import static com.hp.score.lang.entities.ScoreLangConstants.*;

/*
 * Created by orius123 on 12/11/14.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/META-INF/spring/systemTestContext.xml")
public class SystemsTestsParent {

    @Autowired
    protected SlangCompiler compiler;

    @Autowired
    protected EventBus eventBus;

    @Autowired
    protected Score score;

    private LinkedBlockingQueue<ScoreEvent> queue = new LinkedBlockingQueue<>();

    protected ScoreEvent trigger(CompilationArtifact compilationArtifact, Map<String, Serializable> userInputs) throws InterruptedException {
        Map<String, Serializable> executionContext = createExecutionContext(userInputs);

        TriggeringProperties triggeringProperties = TriggeringProperties
                .create(compilationArtifact.getExecutionPlan())
                .setDependencies(compilationArtifact.getDependencies())
                .setContext(executionContext);

        registerHandlers();
        score.trigger(triggeringProperties);
        ScoreEvent event;
        do {
            event = queue.take();
            Assert.assertNotSame("Error event has been thrown during execution", EVENT_STEP_ERROR, event.getEventType());
            System.out.println("Event received: " + event.getEventType() + " Data is: " + event.getData());
        } while(!EventConstants.SCORE_FINISHED_EVENT.equals(event.getEventType()));
        return event;
    }


    private static Map<String, Serializable> createExecutionContext(Map<String, Serializable> userInputs) {
        Map<String, Serializable> executionContext = new HashMap<>();
        executionContext.put(ScoreLangConstants.RUN_ENV, new RunEnvironment());
        executionContext.put(ScoreLangConstants.USER_INPUTS_KEY, (Serializable) userInputs);
        return executionContext;
    }

    private void registerHandlers() {
        Set<String> handlerTypes = new HashSet<>();
        handlerTypes.add(EventConstants.SCORE_FINISHED_EVENT);
        handlerTypes.add(EventConstants.SCORE_ERROR_EVENT);
        handlerTypes.add(EventConstants.SCORE_FAILURE_EVENT);
        handlerTypes.add(EVENT_ACTION_START);
        handlerTypes.add(EVENT_ACTION_END);
        handlerTypes.add(EVENT_ACTION_ERROR);
        handlerTypes.add(EVENT_INPUT_START);
        handlerTypes.add(EVENT_INPUT_END);
        handlerTypes.add(EVENT_OUTPUT_START);
        handlerTypes.add(EVENT_OUTPUT_END);
        handlerTypes.add(EVENT_STEP_ERROR);
        handlerTypes.add(EVENT_EXECUTION_FINISHED);
        eventBus.subscribe(new ScoreEventListener() {

            @Override
            public void onEvent(ScoreEvent event) {
                try {
                    queue.put(event);
                } catch(InterruptedException ignore) {}
            }
        }, handlerTypes);
    }
}
