package com.seblong.okr.entities;

import lombok.Data;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@Data
@Document(collection = "c_follow")
public class Follow {

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
     * 目标id
     */
    @Indexed
    private String target;

    @Transient
    private String name;

    @Transient
    private String avatar;

    @Transient
    private String thumb_avatar;

    @JsonIgnore
    private Long created;

    @PersistenceConstructor
    public Follow(String employee, String target, Long created) {
        this.employee = employee;
        this.target = target;
        this.created = created;
    }
}
