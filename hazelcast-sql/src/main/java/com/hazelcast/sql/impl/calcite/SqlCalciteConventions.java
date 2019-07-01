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

package com.hazelcast.sql.impl.calcite;

import com.hazelcast.sql.impl.calcite.logical.rel.LogicalRel;
import com.hazelcast.sql.impl.calcite.physical.rel.PhysicalRel;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelTraitSet;

/**
 * Hazelcast conventions.
 */
public class SqlCalciteConventions {
    /** Logical convention. */
    public static final Convention LOGICAL = new Convention.Impl("LOGICAL", LogicalRel.class);

    /** Physical convention. */
    public static final Convention HAZELCAST_PHYSICAL = new Convention.Impl("PHYSICAL", PhysicalRel.class) {
        @Override
        public boolean canConvertConvention(Convention toConvention) {
            return true;
        }

        @Override
        public boolean useAbstractConvertersForConversion(RelTraitSet fromTraits, RelTraitSet toTraits) {
            return true;
        }
    };

    private SqlCalciteConventions() {
        // No-op.
    }
}
