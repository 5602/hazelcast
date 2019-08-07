package com.hazelcast.sql.impl.expression.call.func;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.sql.HazelcastSqlException;
import com.hazelcast.sql.SqlErrorCode;
import com.hazelcast.sql.impl.QueryContext;
import com.hazelcast.sql.impl.expression.Expression;
import com.hazelcast.sql.impl.expression.call.BiCallExpression;
import com.hazelcast.sql.impl.expression.call.CallOperator;
import com.hazelcast.sql.impl.row.Row;
import com.hazelcast.sql.impl.type.DataType;

import java.io.IOException;

/**
 * POWER function.
 */
public class DoubleDoubleRetDoubleFunction extends BiCallExpression<Double> {
    /** Operator. */
    private int operator;

    /** Type of the first argument. */
    private transient DataType operandType1;

    /** Type of the second argument. */
    private transient DataType operandType2;

    public DoubleDoubleRetDoubleFunction() {
        // No-op.
    }

    public DoubleDoubleRetDoubleFunction(Expression operand1, Expression operand2, int operator) {
        super(operand1, operand2);

        this.operator = operator;
    }

    @Override
    public Double eval(QueryContext ctx, Row row) {
        Object operand1Value = operand1.eval(ctx, row);

        if (operand1Value == null)
            return null;
        else if (operandType1 == null) {
            DataType type = operand1.getType();

            if (!type.isCanConvertToNumeric())
                throw new HazelcastSqlException(SqlErrorCode.GENERIC, "Operand 1 is not numeric: " + type);

            operandType1 = type;
        }

        Object operand2Value = operand2.eval(ctx, row);

        if (operand2Value == null)
            return null;
        else if (operandType2 == null) {
            DataType type = operand2.getType();

            if (!type.isCanConvertToNumeric())
                throw new HazelcastSqlException(SqlErrorCode.GENERIC, "Operand 2 is not numeric: " + type);

            operandType2 = type;
        }

        double operand1ValueDouble = operandType1.getConverter().asDouble(operand1Value);
        double operand2ValueDouble = operandType2.getConverter().asDouble(operand1Value);

        switch (operator) {
            case CallOperator.ATAN2:
                return Math.atan2(operand1ValueDouble, operand2ValueDouble);

            case CallOperator.POWER:
                return Math.pow(operand1ValueDouble, operand2ValueDouble);
        }

        throw new HazelcastSqlException(-1, "Unsupported operator: " + operator);
    }

    @Override
    public DataType getType() {
        return DataType.DOUBLE;
    }

    @Override
    public int operator() {
        return operator;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        super.writeData(out);

        out.writeInt(operator);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        super.readData(in);

        operator = in.readInt();
    }
}
