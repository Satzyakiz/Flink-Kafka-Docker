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

import org.apache.flink.cep.pattern.spatial.GeometryEvent;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.locationtech.jts.geom.Geometry;

@Data
public class BasicEvent extends GeometryEvent {
    private String timestamp;
    private String nodeId;
    private String subsystem;
    private String parameter;
    private String temperature;
    private Integer valueRaw;
    private Double valueHrf;

    public BasicEvent() {
        super(); // Call the superclass constructor
    }

    public BasicEvent(
        String timestamp,
        String nodeId,
        String subsystem,
        String parameter,
        String temperature,
        Integer valueRaw,
        Double valueHrf,
        Geometry geometry
    ) {
        super(geometry); // Call the superclass constructor
        this.timestamp = timestamp;
        this.nodeId = nodeId;
        this.subsystem = subsystem;
        this.parameter = parameter;
        this.temperature = temperature;
        this.valueRaw = valueRaw;
        this.valueHrf = valueHrf;
    }

    public BasicEvent(
        String timestamp,
        String nodeId,
        String subsystem,
        String parameter,
        String temperature,
        Integer valueRaw,
        Double valueHrf
    ) {
        super(); // Call the superclass constructor
        this.timestamp = timestamp;
        this.nodeId = nodeId;
        this.subsystem = subsystem;
        this.parameter = parameter;
        this.temperature = temperature;
        this.valueRaw = valueRaw;
        this.valueHrf = valueHrf;
    }

    public static BasicEvent fromString(String data) {
        data = data.replaceAll("^\"|\"$", "");
        String[] parts = data.split(",");
        Integer valueRaw = Integer.parseInt(parts[5]);
        Double valueHrf = Double.parseDouble(parts[6]);
        return new BasicEvent(parts[0], parts[1], parts[2], parts[3], parts[4], valueRaw, valueHrf);
    }

    @Override
    public String toString() {
        return timestamp + "," +
               nodeId + "," +
               subsystem + "," +
               parameter + "," +
               temperature + "," +
               valueRaw + "," +
               valueHrf;
    }
}
