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

package com.hazelcast.sql.impl.calcite.physical.rule;

import com.hazelcast.sql.impl.calcite.HazelcastConventions;
import com.hazelcast.sql.impl.calcite.RuleUtils;
import com.hazelcast.sql.impl.calcite.logical.rel.RootLogicalRel;
import com.hazelcast.sql.impl.calcite.physical.distribution.PhysicalDistributionTrait;
import com.hazelcast.sql.impl.calcite.physical.rel.RootPhysicalRel;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.RelNode;

public class RootPhysicalRule extends RelOptRule {
    public static final RelOptRule INSTANCE = new RootPhysicalRule();

    private RootPhysicalRule() {
        super(
            RuleUtils.parentChild(RootLogicalRel.class, RelNode.class, HazelcastConventions.LOGICAL),
            RootPhysicalRule.class.getSimpleName()
        );
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        RootLogicalRel root = call.rel(0);
        RelNode input = call.rel(1);

        RootPhysicalRel newRoot = new RootPhysicalRel(
            root.getCluster(),
            RuleUtils.toPhysicalConvention(root.getTraitSet(), PhysicalDistributionTrait.SINGLETON),
            RuleUtils.toPhysicalInput(input, PhysicalDistributionTrait.SINGLETON)
        );

        call.transformTo(newRoot);
    }
}
