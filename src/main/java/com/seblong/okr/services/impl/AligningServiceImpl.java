package com.seblong.okr.services.impl;

import com.seblong.okr.entities.Aligning;
import com.seblong.okr.repositories.AligningRepository;
import com.seblong.okr.services.AligningService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AligningServiceImpl implements AligningService {

    @Autowired
    private AligningRepository aligningRepo;

    @Override
    public Aligning align(String employeeId, String objectiveId, String periodId, String targetEId, String targetOId) {
        Aligning aligning = new Aligning();
        aligning.setCreated(System.currentTimeMillis());
        aligning.setEmployee(employeeId);
        aligning.setObjective(objectiveId);
        aligning.setPeriod(periodId);
        aligning.setTargetE(targetEId);
        aligning.setTargetO(targetOId);
        aligning = aligningRepo.save(aligning);
        return aligning;
    }

    @Override
    public void remove(String unique) {
        aligningRepo.deleteById(new ObjectId(unique));
    }

    @Override
    public Aligning getTop(String objectiveId) {
        return aligningRepo.findByObjective(objectiveId);
    }

    @Override
    public List<Aligning> getChildren(String objectiveId) {
        return aligningRepo.findByTargetO(objectiveId);
    }

    @Override
    public Aligning getByObjective(String objectiveId) {
        return aligningRepo.findByObjective(objectiveId);
    }


}
