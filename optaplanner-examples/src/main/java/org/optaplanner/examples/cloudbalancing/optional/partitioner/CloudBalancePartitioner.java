/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cloudbalancing.optional.partitioner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.optaplanner.core.impl.heuristic.common.PropertiesConfigurable;
import org.optaplanner.core.impl.partitionedsearch.partitioner.SolutionPartitioner;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;

public class CloudBalancePartitioner implements SolutionPartitioner<CloudBalance>, PropertiesConfigurable {

    private int partCount;
    private int minimumProcessListSize;

    @Override
    public void applyCustomProperties(Map<String, String> customPropertyMap) {
        String partCountString = customPropertyMap.remove("partCount");
        try {
            partCount = partCountString == null ? 4 : Integer.parseInt(partCountString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The partCount (" + partCountString + ") cannot be parsed.", e);
        }
        String minimumProcessListSizeString = customPropertyMap.remove("minimumProcessListSize");
        try {
            minimumProcessListSize = minimumProcessListSizeString == null ? 75 : Integer.parseInt(minimumProcessListSizeString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The minimumProcessListSize (" + minimumProcessListSizeString + ") cannot be parsed.", e);
        }
        if (!customPropertyMap.isEmpty()) {
            throw new IllegalStateException("The customProperties (" + customPropertyMap.keySet()
                    + ") are not supported.");
        }
    }

    @Override
    public List<CloudBalance> splitWorkingSolution(ScoreDirector<CloudBalance> scoreDirector, Integer runnablePartThreadLimit) {
        CloudBalance originalSolution = scoreDirector.getWorkingSolution();
        int partCount = this.partCount;
        if (originalSolution.getProcessList().size() / partCount < minimumProcessListSize) {
            partCount = originalSolution.getProcessList().size() / minimumProcessListSize;
        }

        List<CloudBalance> partList = new ArrayList<>(partCount);
        for (int i = 0; i < partCount; i++) {
            CloudBalance partSolution = new CloudBalance(originalSolution.getId(),
                    new ArrayList<>(originalSolution.getComputerList().size() / partCount + 1),
                    new ArrayList<>(originalSolution.getProcessList().size() / partCount + 1));
            partList.add(partSolution);
        }
        int partIndex = 0;
        for (CloudComputer originalComputer : originalSolution.getComputerList()) {
            CloudBalance part = partList.get(partIndex);
            part.getComputerList().add(new CloudComputer(
                    originalComputer.getId(),
                    originalComputer.getCpuPower(), originalComputer.getMemory(),
                    originalComputer.getNetworkBandwidth(), originalComputer.getCost()));
            partIndex = (partIndex + 1) % partList.size();
        }
        partIndex = 0;
        for (CloudProcess originalProcess : originalSolution.getProcessList()) {
            CloudBalance part = partList.get(partIndex);
            part.getProcessList().add(new CloudProcess(
                    originalProcess.getId(),
                    originalProcess.getRequiredCpuPower(), originalProcess.getRequiredMemory(),
                    originalProcess.getRequiredNetworkBandwidth()));
            if (originalProcess.getComputer() != null) {
                // TODO switch the reference to the partition cloned computer
                // TODO Then fail fast if the computer isn't in the same partition with this exception:
                throw new IllegalStateException("The initialized process (" + originalProcess
                        + ") has a computer (" + originalProcess.getComputer()
                        + ") which belongs to the another partition.");
            }
            partIndex = (partIndex + 1) % partList.size();
        }
        return partList;
    }

}
