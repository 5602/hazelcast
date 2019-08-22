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
import com.hazelcast.sql.impl.calcite.logical.rel.SortLogicalRel;
import com.hazelcast.sql.impl.calcite.physical.distribution.PhysicalDistributionTrait;
import com.hazelcast.sql.impl.calcite.physical.rel.SortMergeExchangePhysicalRel;
import com.hazelcast.sql.impl.calcite.physical.rel.SortPhysicalRel;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.rel.RelNode;

public class SortPhysicalRule extends RelOptRule {
    public static final RelOptRule INSTANCE = new SortPhysicalRule();

    private SortPhysicalRule() {
        super(
            RuleUtils.parentChild(SortLogicalRel.class, RelNode.class, HazelcastConventions.LOGICAL),
            SortPhysicalRule.class.getSimpleName()
        );
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        SortLogicalRel sort = call.rel(0);
        RelNode input = sort.getInput();

        SortPhysicalRel localSort = new SortPhysicalRel(
            sort.getCluster(),
            RuleUtils.toPhysicalConvention(sort.getTraitSet(), PhysicalDistributionTrait.ANY),
            RuleUtils.toPhysicalInput(input, PhysicalDistributionTrait.ANY),
            sort.getCollation(),
            sort.offset,
            sort.fetch
        );

        SortMergeExchangePhysicalRel exchange = new SortMergeExchangePhysicalRel(
            sort.getCluster(),
            RuleUtils.toPhysicalConvention(sort.getTraitSet(), PhysicalDistributionTrait.SINGLETON),
            localSort,
            sort.getCollation()
        );

        call.transformTo(exchange);
    }
}
