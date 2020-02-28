package com.seblong.okr.services;

import com.seblong.okr.entities.Aligning;

import java.util.List;

public interface AligningService {
    Aligning align(String employeeId, String objectiveId, String periodId, String targetEId, String targetOId);

    void remove(String unique);

    Aligning getTop(String objectiveId);

    List<Aligning> getChildren(String objectiveId);

    Aligning getByObjective(String objectiveId);
}
