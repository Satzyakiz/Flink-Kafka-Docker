/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.myorg.quickstart;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicEvent {
    private String timestamp;
    private String nodeId;
    private String subsystem;
    private String parameter;
    private Integer valueRaw;
    private Double valueHrf;
    private String data;

    public static BasicEvent fromString(String data) {
        return new BasicEvent("timestamp", "node_id", "subsystem", "parameter", 425, 12.33, data);
    }

    @Override
    public String toString() {
        return String.format("timestamp: %s, node_id: %s", timestamp, nodeId);
    }
}
