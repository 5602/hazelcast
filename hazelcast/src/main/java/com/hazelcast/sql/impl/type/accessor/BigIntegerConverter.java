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

package com.hazelcast.sql.impl.type.accessor;

import com.hazelcast.sql.impl.type.GenericType;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Converter for {@link java.math.BigInteger} type.
 */
public final class BigIntegerConverter extends Converter {
    /** Singleton instance. */
    public static BigIntegerConverter INSTANCE = new BigIntegerConverter();

    @Override
    public Class getClazz() {
        return BigInteger.class;
    }

    @Override
    public GenericType getGenericType() {
        return GenericType.DECIMAL;
    }

    @Override
    public boolean asBit(Object val) {
        return cast(val).compareTo(BigInteger.ZERO) != 0;
    }

    @Override
    public final byte asTinyInt(Object val) {
        return cast(val).byteValue();
    }

    @Override
    public final short asSmallInt(Object val) {
        return cast(val).shortValue();
    }

    @Override
    public final int asInt(Object val) {
        return cast(val).intValue();
    }

    @Override
    public final long asBigInt(Object val) {
        return cast(val).longValue();
    }

    @Override
    public final BigDecimal asDecimal(Object val) {
        return new BigDecimal(cast(val));
    }

    @Override
    public final float asReal(Object val) {
        return cast(val).floatValue();
    }

    @Override
    public final double asDouble(Object val) {
        return cast(val).doubleValue();
    }

    @Override
    public final String asVarchar(Object val) {
        return cast(val).toString();
    }

    private BigInteger cast(Object val) {
        return (BigInteger)val;
    }

    private BigIntegerConverter() {
        // No-op.
    }
}
