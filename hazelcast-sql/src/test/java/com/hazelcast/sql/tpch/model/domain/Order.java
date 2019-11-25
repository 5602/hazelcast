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

package com.hazelcast.sql.tpch.model.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * TPC-H model: order.
 */
public class Order implements Serializable {
    public String o_orderstatus;
    public BigDecimal o_totalprice;
    public LocalDate o_orderdate;
    public String o_orderpriority;
    public String o_clerk;
    public int o_shippriority;
    public String o_comment;

    public Order() {
        // No-op.
    }

    public Order(
        String o_orderstatus,
        BigDecimal o_totalprice,
        LocalDate o_orderdate,
        String o_orderpriority,
        String o_clerk,
        int o_shippriority,
        String o_comment
    ) {
        this.o_orderstatus = o_orderstatus;
        this.o_totalprice = o_totalprice;
        this.o_orderdate = o_orderdate;
        this.o_orderpriority = o_orderpriority;
        this.o_clerk = o_clerk;
        this.o_shippriority = o_shippriority;
        this.o_comment = o_comment;
    }

    public static class Key implements Serializable {
        public long o_orderkey;
        public long o_custkey;

        public Key() {
            // No-op.
        }

        public Key(long o_orderkey, long o_custkey) {
            this.o_orderkey = o_orderkey;
            this.o_custkey = o_custkey;
        }
    }
}
