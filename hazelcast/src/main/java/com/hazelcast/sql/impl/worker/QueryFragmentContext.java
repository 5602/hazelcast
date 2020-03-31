/*
 * Copyright (c) 2008-2020, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.sql.impl.worker;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.sql.impl.expression.ExpressionEvalContext;
import com.hazelcast.sql.impl.state.QueryStateCallback;

import java.util.List;

/**
 * Context of a running query fragment.
 */
public final class QueryFragmentContext implements ExpressionEvalContext {

    private final List<Object> arguments;
    private final QueryFragmentScheduleCallback scheduleCallback;
    private final QueryStateCallback stateCallback;

    // TODO: Pass serialization service as constructor argument instead.
    private final InternalSerializationService serializationService;

    public QueryFragmentContext(
        List<Object> arguments,
        QueryFragmentScheduleCallback scheduleCallback,
        QueryStateCallback stateCallback,
        InternalSerializationService serializationService
    ) {
        assert arguments != null;

        this.arguments = arguments;
        this.scheduleCallback = scheduleCallback;
        this.stateCallback = stateCallback;
        this.serializationService = serializationService;
    }

    @Override
    public List<Object> getArguments() {
        return arguments;
    }

    public void schedule() {
        scheduleCallback.schedule();
    }

    public void checkCancelled() {
        stateCallback.checkCancelled();
    }

    public InternalSerializationService getSerializationService() {
        return serializationService;
    }
}
