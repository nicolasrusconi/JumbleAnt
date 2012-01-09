/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * @author nicolas.rusconi
 **/

package com.jumble.report;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Package {

    private final String name;
    private String score = "0%"; 
    private List<MutatedClassReport> classes = new ArrayList<MutatedClassReport>();

    public Package(String name) {
        this.name = name;
    }

    public void add(MutatedClassReport report) {
        classes.add(report);
    }

    public Integer processScores() {
        Integer totalScores = Integer.valueOf(0);
        for (MutatedClassReport clazz : classes) {
            totalScores = totalScores + clazz.getScore();
        }
        int packageScore = totalScores / classes.size();
        score = packageScore + "%";
        return packageScore; 
    }

    public String getName() {
        return name;
    }

    public String getScore() {
        return score;
    }

    public List<MutatedClassReport> getReports() {
        return Collections.unmodifiableList(classes);
    }

}
