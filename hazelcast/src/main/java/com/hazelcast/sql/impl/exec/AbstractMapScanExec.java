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

package com.hazelcast.sql.impl.exec;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.sql.impl.QueryContext;
import com.hazelcast.sql.impl.SqlUtils;
import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.row.HeapRow;
import com.hazelcast.sql.impl.row.KeyValueRow;
import com.hazelcast.sql.impl.row.KeyValueRowExtractor;
import com.hazelcast.sql.impl.worker.data.DataWorker;

import java.util.List;

import static com.hazelcast.query.QueryConstants.KEY_ATTRIBUTE_NAME;
import static com.hazelcast.query.QueryConstants.THIS_ATTRIBUTE_NAME;

/**
 * Common operator for map scans.
 */
public abstract class AbstractMapScanExec extends AbstractExec implements KeyValueRowExtractor {
    /** Map name. */
    protected final String mapName;

    /** Projection expressions. */
    protected final List<Expression> projections;

    /** Filter. */
    protected final Expression<Boolean> filter;

    /** Serialization service. */
    protected InternalSerializationService serializationService;

    /** Extractors. */
    private Extractors extractors;

    /** Row to get data with extractors. */
    private KeyValueRow keyValueRow;

    protected AbstractMapScanExec(String mapName, List<Expression> projections, Expression<Boolean> filter) {
        this.mapName = mapName;
        this.projections = projections;
        this.filter = filter;
    }

    @Override
    protected final void setup0(QueryContext ctx, DataWorker worker) {
        serializationService = (InternalSerializationService)ctx.getNodeEngine().getSerializationService();
        extractors = createExtractors();

        keyValueRow = new KeyValueRow(this);
    }

    @Override
    public boolean canReset() {
        return true;
    }

    @Override
    public Object extract(Object key, Object val, String path) {
        path = normalizePath(path);

        Object res;

        if (KEY_ATTRIBUTE_NAME.value().equals(path))
            res = key;
        else if (THIS_ATTRIBUTE_NAME.value().equals(path))
            res = val;
        else {
            String keyPath = SqlUtils.extractKeyPath(path);

            Object target;

            if (keyPath != null) {
                target = key;

                path = keyPath;
            }
            else
                target = val;

            res = extractors.extract(target, path, null);
        }

        if (res instanceof HazelcastJsonValue)
            res = Json.parse(res.toString());

        return res;
    }

    protected HeapRow prepareRow(Object key, Object val) {
        keyValueRow.setKeyValue(key, val);

        // Filter.
        if (filter != null && !filter.eval(ctx, keyValueRow))
            return null;

        // Project.
        HeapRow row = new HeapRow(projections.size());

        for (int j = 0; j < projections.size(); j++) {
            Object projectionRes = projections.get(j).eval(ctx, keyValueRow);

            row.set(j, projectionRes);
        }

        return row;
    }

    /**
     * Create extractors for the given operator.
     *
     * @return Extractors for map.
     */
    protected abstract Extractors createExtractors();

    protected abstract String normalizePath(String path);
}
