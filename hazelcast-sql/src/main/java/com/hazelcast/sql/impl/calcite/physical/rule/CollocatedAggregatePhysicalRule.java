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
import com.hazelcast.sql.impl.calcite.logical.rel.AggregateLogicalRel;
import com.hazelcast.sql.impl.calcite.physical.distribution.PhysicalDistributionField;
import com.hazelcast.sql.impl.calcite.physical.distribution.PhysicalDistributionTrait;
import com.hazelcast.sql.impl.calcite.physical.rel.CollocatedAggregatePhysicalRel;
import org.apache.calcite.plan.RelOptRule;
import org.apache.calcite.plan.RelOptRuleCall;
import org.apache.calcite.plan.RelTraitSet;
import org.apache.calcite.rel.RelNode;
import org.apache.calcite.util.ImmutableBitSet;

import java.util.Collection;
import java.util.List;

/**
 * The rule that tries to create a collocated aggregate.
 */
public class CollocatedAggregatePhysicalRule extends AbstractAggregatePhysicalRule {
    public static final RelOptRule INSTANCE = new CollocatedAggregatePhysicalRule();

    private CollocatedAggregatePhysicalRule() {
        super(
            RuleUtils.parentChild(AggregateLogicalRel.class, RelNode.class, HazelcastConventions.LOGICAL),
            CollocatedAggregatePhysicalRule.class.getSimpleName()
        );
    }

    @Override
    public void onMatch(RelOptRuleCall call) {
        AggregateLogicalRel logicalAgg = call.rel(0);
        RelNode input = logicalAgg.getInput();

        RelNode convertedInput = RuleUtils.toPhysicalInput(input);

        Collection<RelNode> physicalInputs = RuleUtils.getPhysicalRelsFromSubset(convertedInput);

        for (RelNode physicalInput : physicalInputs) {
            CollocatedAggregatePhysicalRel physicalAgg = tryCreateCollocatedAggregate(call, logicalAgg, physicalInput);

            if (physicalAgg != null)
                call.transformTo(physicalAgg);
        }
    }

    /**
     * Try creating collocated aggregate from the logical aggregate.
     *
     * @param logicalAgg Logical aggregate.
     * @param input Physical input.
     */
    private CollocatedAggregatePhysicalRel tryCreateCollocatedAggregate(
        RelOptRuleCall call,
        AggregateLogicalRel logicalAgg,
        RelNode input
    ) {
        PhysicalDistributionTrait inputDist = RuleUtils.getPhysicalDistribution(input);

        switch (inputDist.getType()) {
            case DISTRIBUTED_PARTITIONED:
                boolean collocated = isCollocatedPartitioned(logicalAgg.getGroupSet(), inputDist.getFields());

                if (!collocated)
                    break;

                // TODO: Should we make agg distribution more specialized in case of PARTITIONED input? Looks like it
                // TODO: may make things worse sometimes, e.g. when subsequent group by on less number of columns is
                // TODO: applied. For example: PROJECT(a, b) -> GROUP BY (a, b, c) -> GROUP BY (a, b). May be two
                // TODO: transformations with and without distribution specialization is needed in this case?

                // Fall-through if this partitioned input could be collocated.

            case REPLICATED:
            case SINGLETON:
                AggregateCollation collation = getCollation(logicalAgg, input);

                RelTraitSet traitSet = RuleUtils.toPhysicalConvention(call.getPlanner().emptyTraitSet(), inputDist)
                    .plus(collation.getCollation());

                return new CollocatedAggregatePhysicalRel(
                    logicalAgg.getCluster(),
                    traitSet,
                    input,
                    logicalAgg.indicator,
                    logicalAgg.getGroupSet(),
                    logicalAgg.getGroupSets(),
                    logicalAgg.getAggCallList(),
                    collation.isMatchesInput()
                );
        }

        return null;
    }

    /**
     * Check whether the given group set could be executed in collocated mode for the given distribution fields of
     * partitioned input. This is the case iff input distribution fields are a prefix of the group set.
     *
     * @param groupSet Group set of original aggregate.
     * @param inputDistFields Distribution fields of an input.
     * @return {@code True} if this aggregate could be processed in collocated mode.
     */
    private static boolean isCollocatedPartitioned(
        ImmutableBitSet groupSet,
        List<PhysicalDistributionField> inputDistFields
    ) {
        // If group set size is less than the number of input distribution fields, then dist fields could not be a
        // prefix of group se tby definition.
        if (groupSet.length() < inputDistFields.size())
            return false;

        for (int i = 0; i < inputDistFields.size(); i++) {
            PhysicalDistributionField inputField = inputDistFields.get(i);

            if (inputField.getNestedField() != null)
                return false;

            if (!groupSet.get(i))
                return false;
        }

        return true;
    }
}
