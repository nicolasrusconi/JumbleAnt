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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JumbleRunReport {

    private String score = "0%";
    private Integer totalScore = 0;
    private List<Package> packages = new ArrayList<Package>();
    private Map<String, Package> packagesMap = new HashMap<String, Package>(); 

    public void add(MutatedClassReport report) {
        String packageName = parsePackagName(report.getClassName());
        Package thePackage = packagesMap.get(packageName);

        if (thePackage == null) {
            thePackage = new Package(packageName);
            packagesMap.put(packageName, thePackage);
            packages.add(thePackage);
        }
        thePackage.add(report);
    }

    private String parsePackagName(String className) {
        int lastDotIndex = className.lastIndexOf(".");
        return className.substring(0, lastDotIndex);
    }

    public String processScores() {
        totalScore = 0;
        for (Package aPackage : packages) {
            totalScore = totalScore + aPackage.processScores();
        }
        score = totalScore / packages.size() + "%";
        return score;
    }

    public List<Package> getPackages() {
        return Collections.unmodifiableList(packages);
    }

    public String getScore() {
        return score;
    }

    public Integer getScoreAsInt() {
        return Integer.valueOf(score.substring(0, score.length() - 1));
    }
}
