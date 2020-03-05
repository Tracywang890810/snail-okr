package com.seblong.okr.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ApiModel("Employee-用户属性")
@Document(collection = "c_employee")
public class Employee {

    @ApiModelProperty(value = "unique", name = "unique", dataType = "String", example = "用户unique")
    @JsonProperty(value = "unique")
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private ObjectId id;

    /**
     * 企业微信的用户id
     */
    @ApiModelProperty(value = "userId", name = "userId", dataType = "String", example = "企业微信用户id")
    @Indexed
    private String userId;

    /**
     * 名称
     */
    @ApiModelProperty(value = "name", name = "name", dataType = "String", example = "用户名称")
    @Indexed
    private String name;

    /**
     * 手机号码，第三方仅通讯录应用可获取
     */
    @ApiModelProperty(value = "mobile", name = "mobile", dataType = "String", example = "用户手机号")
    @Indexed
    private String mobile;

    /**
     * 职务信息
     */
    @ApiModelProperty(value = "position", name = "position", dataType = "String", example = "用户职务信息")
    private String position;

    /**
     * 性别。0表示未定义，1表示男性，2表示女性
     */
    @ApiModelProperty(value = "gender", name = "gender", dataType = "String", example = "性别。0表示未定义，1表示男性，2表示女性")
    private String gender;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "email", name = "email", dataType = "String", example = "邮箱")
    private String email;

    /**
     * 头像url
     */
    @ApiModelProperty(value = "avatar", name = "avatar", dataType = "String", example = "用户头像")
    private String avatar;

    /**
     * 头像缩略图url
     */
    @ApiModelProperty(value = "thumb_avatar", name = "thumb_avatar", dataType = "String", example = "用户头像缩略图")
    private String thumb_avatar;

    /**
     * 座机
     */
    @ApiModelProperty(value = "telephone", name = "telephone", dataType = "String", example = "座机号码")
    private String telephone;

    /**
     * 成员启用状态。1表示启用的成员，0表示被禁用。
     */
    @ApiModelProperty(value = "enable", name = "enable", dataType = "Integer", example = "成员启用状态。1表示启用的成员，0表示被禁用。")
    private int enable;

    /**
     * 激活状态: 1=已激活，2=已禁用，4=未激活
     * 已激活代表已激活企业微信或已关注微工作台（原企业号）。未激活代表既未激活企业微信又未关注微工作台（原企业号）。
     */
    @ApiModelProperty(value = "status", name = "status", dataType = "Integer", example = "激活状态: 1=已激活，2=已禁用，4=未激活")
    private int status;

    /**
     * 地址。
     */
    @ApiModelProperty(value = "address", name = "address", dataType = "String", example = "地址")
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
