package io.cloudslang.lang.compiler.modeller.transformers;
/*******************************************************************************
* (c) Copyright 2014 Hewlett-Packard Development Company, L.P.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Apache License v2.0 which accompany this distribution.
*
* The Apache License is available at
* http://www.apache.org/licenses/LICENSE-2.0
*
*******************************************************************************/


/*
 * Created by orius123 on 05/11/14.
 */

import io.cloudslang.lang.compiler.validator.PreCompileValidator;
import io.cloudslang.lang.entities.ScoreLangConstants;
import io.cloudslang.lang.entities.bindings.Result;
import io.cloudslang.lang.entities.bindings.values.ValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class ResultsTransformer extends InOutTransformer implements Transformer<List, List<Result>> {
    
    @Autowired
    private PreCompileValidator preCompileValidator;

    @Override
    public List<Result> transform(List rawData) {
        List<Result> results = new ArrayList<>();
        // If there are no results specified, add the default SUCCESS & FAILURE results
        if(rawData == null){
            addResult(results, createNoExpressionResult(ScoreLangConstants.SUCCESS_RESULT));
            addResult(results, createNoExpressionResult(ScoreLangConstants.FAILURE_RESULT));
            return results;
        } else if (rawData.isEmpty()) {
            return results;
        }
        for (Object rawResult : rawData) {
            if (rawResult instanceof String) {
                //- some_result
                addResult(results, createNoExpressionResult((String) rawResult));
            } else if (rawResult instanceof Map) {
                // - some_result: some_expression
                // the value of the result is an expression we need to evaluate at runtime
                @SuppressWarnings("unchecked") Map.Entry<String, Serializable> entry = (Map.Entry<String, Serializable>) (((Map) rawResult).entrySet()).iterator().next();
                addResult(results, createExpressionResult(entry.getKey(), entry.getValue()));
            }
        }
        return results;
    }

    private void addResult(List<Result> results, Result element) {
        preCompileValidator.validateNoDuplicateResults(results, element);
        results.add(element);
    }

    @Override
    public List<Scope> getScopes() {
        return Collections.singletonList(Scope.AFTER_EXECUTABLE);
    }

    @Override
    public String keyToTransform() {
        return null;
    }

    private Result createNoExpressionResult(String rawResult) {
        return new Result(rawResult, null);
    }

    private Result createExpressionResult(String resultName, Serializable resultValue) {
        Accumulator accumulator = extractFunctionData(resultValue);
        return new Result(
                resultName,
                ValueFactory.create(resultValue),
                accumulator.getFunctionDependencies(),
                accumulator.getSystemPropertyDependencies()
        );
    }
}

