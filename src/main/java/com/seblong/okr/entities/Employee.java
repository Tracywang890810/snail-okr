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
     * 主部门
     */
    private int mainDepartment;

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

    /**
     * 用户企业id
     */
    private String corpId;

    /**
     * 全局唯一。对于同一个服务商，不同应用获取到企业内同一个成员的open_userid是相同的，最多64个字节。仅第三方应用可获取
     */
    private String openId;

    public Employee() {
    }

    @PersistenceConstructor
    public Employee(String userId, String name, String position, String gender, String avatar, String thumb_avatar, int mainDepartment, int status, String address, String corpId, String openId) {
        this.userId = userId;
        this.name = name;
        this.position = position;
        this.gender = gender;
        this.avatar = avatar;
        this.thumb_avatar = thumb_avatar;
        this.mainDepartment = mainDepartment;
        this.status = status;
        this.address = address;
        this.corpId = corpId;
        this.openId = openId;
    }
}
