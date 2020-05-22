package com.seblong.okr.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.seblong.okr.enums.EntityStatus;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "c_comment")
public class Comment {

    @JsonProperty(value = "unique")
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private ObjectId id;

    @Indexed
    private String period;

    @Indexed
    private String owner;

    private String employee;

    private String content;

    @Indexed
    private String companyId;

    @Indexed
    private Long created;

    private String status;

    @JsonIgnore
    private Long updated;

    @Transient
    private String name;

    @Transient
    private String avatar;

    @Transient
    private String thumb_avatar;

    public Comment() {
        this.created = System.currentTimeMillis();
        this.updated = System.currentTimeMillis();
        this.status = EntityStatus.ACTIVED.toString();
    }

    @PersistenceConstructor
    public Comment(String period, String owner, String employee, String content, String companyId, Long created, String status, Long updated) {
        this.period = period;
        this.owner = owner;
        this.employee = employee;
        this.content = content;
        this.companyId = companyId;
        this.created = created;
        this.status = status;
        this.updated = updated;
    }
}
