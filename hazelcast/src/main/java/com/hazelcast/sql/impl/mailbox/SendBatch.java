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

package com.hazelcast.sql.impl.mailbox;

import com.hazelcast.sql.impl.row.Row;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO TODO: Implement metadata!.
public class SendBatch implements DataSerializable {
    /** Rows being transferred. */
    private List<Row> rows;

    /** Laft batch marker. */
    private boolean last;

    public SendBatch() {
        // No-op.
    }

    public SendBatch(List<Row> rows, boolean last) {
        this.rows = rows;
        this.last = last;
    }

    public List<Row> getRows() {
        return rows;
    }

    public boolean isLast() {
        return last;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(rows.size());

        for (Row row : rows)
            out.writeObject(row);

        out.writeBoolean(last);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int rowCnt = in.readInt();

        if (rowCnt == 0)
            rows = Collections.emptyList();
        else {
            rows = new ArrayList<>(rowCnt);

            for (int i = 0; i < rowCnt; i++)
                rows.add(in.readObject());
        }

        last = in.readBoolean();
    }
}
