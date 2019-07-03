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

package com.hazelcast.sql.impl.calcite.logical.rule;

import com.hazelcast.sql.impl.calcite.HazelcastConventions;
import com.hazelcast.sql.impl.calcite.RuleUtils;
import com.hazelcast.sql.impl.calcite.logical.rel.MapScanLogicalRel;
import org.apache.calcite.plan.Convention;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.core.RelFactories;
import org.apache.calcite.rel.logical.LogicalTableScan;

public class MapScanLogicalRule extends RelOptRule {
    public static final RelOptRule INSTANCE = new MapScanLogicalRule();

    private MapScanLogicalRule() {
        super(
            RuleUtils.single(LogicalTableScan.class, Convention.NONE),
            RelFactories.LOGICAL_BUILDER,
            "MapScanLogicalRule"
        );
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        LogicalTableScan access = call.rel(0);

        RelTraitSet traits = access.getTraitSet().plus(HazelcastConventions.LOGICAL);

        call.transformTo(new MapScanLogicalRel(access.getCluster(), traits, access.getTable()));
    }
}
