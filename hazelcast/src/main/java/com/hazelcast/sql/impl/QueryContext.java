/*
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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

package com.hazelcast.sql.impl;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.sql.impl.worker.control.FragmentDeployment;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

/**
 * Context of the running query.
 */
public class QueryContext {
    /** Extractors updater. */
    private static final AtomicReferenceFieldUpdater<QueryContext, Extractors> EXTRACTORS_UPDATER =
        AtomicReferenceFieldUpdater.newUpdater(QueryContext.class, Extractors.class, "extractors");

    /** Node engine. */
    private final NodeEngine nodeEngine;

    /** Query ID. */
    private final QueryId queryId;

    /** Arguments. */
    private final List<Object> arguments;

    /** Root consumer. */
    private final QueryResultConsumer rootConsumer;

    /** Deployed fragments. */
    private final List<FragmentDeployment> fragmentDeployments;

    /** Maps an edge to array, whose length is stripe length, and values are data thread IDs. */
    private final Map<Integer, int[]> edgeToStripeMap;

    /** Extractors. */
    @SuppressWarnings("unused")
    private volatile Extractors extractors;

    public QueryContext(NodeEngine nodeEngine, QueryId queryId, List<Object> arguments, QueryResultConsumer rootConsumer,
        List<FragmentDeployment> fragmentDeployments, Map<Integer, int[]> edgeToStripeMap) {
        this.nodeEngine = nodeEngine;
        this.queryId = queryId;
        this.arguments = arguments;
        this.rootConsumer = rootConsumer;
        this.fragmentDeployments = fragmentDeployments;
        this.edgeToStripeMap = edgeToStripeMap;
    }

    public NodeEngine getNodeEngine() {
        return nodeEngine;
    }

    public QueryId getQueryId() {
        return queryId;
    }

    public QueryResultConsumer getRootConsumer() {
        return rootConsumer;
    }

    public List<FragmentDeployment> getFragmentDeployments() {
        return fragmentDeployments;
    }

    public Map<Integer, int[]> getEdgeToStripeMap() {
        return edgeToStripeMap;
    }

    public Object getArgument(int idx) {
        if (arguments == null || idx >= arguments.size())
            throw new IllegalArgumentException("Argument not found: " + idx);

        return arguments.get(idx);
    }

    /**
     * @return Extractors.
     */
    public Extractors getExtractors() {
        Extractors res = extractors;

        if (res != null)
            return res;

        InternalSerializationService ss = (InternalSerializationService)nodeEngine.getSerializationService();

        res = Extractors.newBuilder(ss).build();

        if (EXTRACTORS_UPDATER.compareAndSet(this, null, res))
            return res;
        else
            return extractors;
    }
}
