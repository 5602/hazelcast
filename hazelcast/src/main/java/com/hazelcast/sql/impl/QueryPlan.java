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

import com.hazelcast.nio.Address;
import com.hazelcast.util.collection.PartitionIdSet;

import java.util.List;
import java.util.Map;

/**
 * Prepared query plan.
 */
public class QueryPlan {
    /** Fragments. */
    private final List<QueryFragment> fragments;

    /** Partition mapping. */
    private final Map<String, PartitionIdSet> partMap;

    /** Remote addresses. */
    private final List<Address> remoteAddresses;

    public QueryPlan(
        List<QueryFragment> fragments,
        Map<String, PartitionIdSet> partMap,
        List<Address> remoteAddresses
    ) {
        this.fragments = fragments;
        this.partMap = partMap;
        this.remoteAddresses = remoteAddresses;
    }

    public List<QueryFragment> getFragments() {
        return fragments;
    }

    public Map<String, PartitionIdSet> getPartitionMap() {
        return partMap;
    }

    public List<Address> getRemoteAddresses() {
        return remoteAddresses;
    }
}
