package com.seblong.okr.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "c_employee")
public class Employee {

    @JsonProperty(value = "unique")
    @Id
    private ObjectId id;

    /**
     * 企业微信的用户id
     */
    @Indexed
    private String userId;

    /**
     * 名称
     */
    @Indexed
    private String name;

    /**
     * 手机号码，第三方仅通讯录应用可获取
     */
    @Indexed
    private String mobile;

    /**
     * 职务信息
     */
    private String position;

    /**
     * 性别。0表示未定义，1表示男性，2表示女性
     */
    private String gender;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像url
     */
    private String avatar;

    /**
     * 头像缩略图url
     */
    private String thumb_avatar;

    /**
     * 座机
     */
    private String telephone;

    /**
     * 成员启用状态。1表示启用的成员，0表示被禁用。
     */
    private int enable;

    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活
     * 已激活代表已激活企业微信或已关注微工作台（原企业号）。未激活代表既未激活企业微信又未关注微工作台（原企业号）。
     */
    private int status;

    /**
     * 地址。
     */
    private String address;

    public Employee() {
    }

    @PersistenceConstructor
    public Employee(String userId, String name, String mobile, String position, String gender, String email, String avatar, String thumb_avatar, String telephone, int enable, int status, String address) {
        this.userId = userId;
        this.name = name;
        this.mobile = mobile;
        this.position = position;
        this.gender = gender;
        this.email = email;
        this.avatar = avatar;
        this.thumb_avatar = thumb_avatar;
        this.telephone = telephone;
        this.enable = enable;
        this.status = status;
        this.address = address;
    }
}
