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

import org.apache.flink.cep.pattern.spatial.EllipseEvent;
import lombok.Data;
import java.io.Serializable;

@Data
public class BasicCircleEvent extends EllipseEvent implements Serializable {
    private String nodeId;
    private String projectId;
    private String vsn;
    private String address;
    private Double latitude;
    private Double longitude;
    private String description;
    private String startTimestamp;
    private String endTimestamp;

    public BasicCircleEvent() {
        super();
    }

    public BasicCircleEvent(
        String nodeId,
        String projectId,
        String vsn,
        String address,
        Double latitude,
        Double longitude,
        String description,
        String startTimestamp,
        String endTimestamp,
        Double centreX,
        Double centreY,
        Double height,
        Double width
    ) {
        super(centreX, centreY, height, width);
        this.nodeId = nodeId;
        this.projectId = projectId;
        this.vsn = vsn;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public BasicCircleEvent(
        String nodeId,
        String projectId,
        String vsn,
        String address,
        Double latitude,
        Double longitude,
        String description,
        String startTimestamp,
        String endTimestamp
    ) {
        super();
        this.nodeId = nodeId;
        this.projectId = projectId;
        this.vsn = vsn;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
    }

    public static BasicCircleEvent fromString(String data) {
        data = data.replaceAll("^\"|\"$", "");
        String[] parts = data.split(",");
        Double latitude = Double.parseDouble(parts[4]);
        Double longitude = Double.parseDouble(parts[5]);
        Double distanceInMeter = 1000.0 * 20;
        Double height = distanceInMeter / 111320d;
        Double width = distanceInMeter / (40075000 * Math.cos(Math.toRadians(latitude)) / 360);
        return new BasicCircleEvent(parts[0], parts[1], parts[2], parts[3], latitude, longitude, parts[6], parts[7], parts[8], latitude, longitude, height, width);
    }

    @Override
    public String toString() {
        return  nodeId + "," +
                projectId + "," +
                vsn + "," +
                address + "," +
                latitude + "," +
                longitude + "," +
                description + "," +
                startTimestamp + "," +
                endTimestamp;
    }
}
