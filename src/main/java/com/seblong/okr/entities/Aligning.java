package com.seblong.okr.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * 用户对齐
 */
@Data
@Document(collection = "c_aligning")
public class Aligning {

    @JsonProperty(value = "unique")
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private ObjectId id;

    /**
     * 员工id
     */
    @Indexed
    private String employee;

    /**
     * 当前objective
     */
    @Indexed
    private String objective;

    /**
     * 对齐objective
     */
    @Indexed
    private String targetO;

    /**
     * 对齐员工id
     */
    private String targetE;

    /**
     * OKR周期id
     */
    private String period;

    @JsonIgnore
    private Long created;

    public Aligning() {
    }

    @PersistenceConstructor
    public Aligning(String employee, String objective, String targetO, String targetE, String period, Long created) {
        this.employee = employee;
        this.objective = objective;
        this.targetO = targetO;
        this.targetE = targetE;
        this.period = period;
        this.created = created;
    }
}
