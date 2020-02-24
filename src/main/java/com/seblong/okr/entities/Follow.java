package com.seblong.okr.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "c_follow")
public class Follow {

    @JsonProperty(value = "unique")
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

    private String name;

    private String avatar;

    private String thumb_avatar;

    private Long created;

    @PersistenceConstructor
    public Follow(String employee, String target, String name, String avatar, String thumb_avatar, Long created) {
        this.employee = employee;
        this.target = target;
        this.name = name;
        this.avatar = avatar;
        this.thumb_avatar = thumb_avatar;
        this.created = created;
    }
}
