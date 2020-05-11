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

import java.util.List;

@Data
@ApiModel("Company-公司属性")
@Document(collection = "c_company")
public class Company {

    @ApiModelProperty(value = "unique", name = "unique", dataType = "String", example = "公司unique")
    @JsonProperty(value = "unique")
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private ObjectId id;

    /**
     * 授权方企业微信名称
     */
    @ApiModelProperty(value = "name", name = "name", dataType = "String", example = "公司名称")
    private String name;

    /**
     * 所绑定的企业微信主体名称(仅认证过的企业有)
     */
    private String fullName;

    /**
     * 授权方cropId
     */
    @ApiModelProperty(value = "corpId", name = "corpId", dataType = "String", example = "公司corpId")
    @Indexed
    private String corpId;

    /**
     * 授权方应用id
     */
    private String agentId;

    /**
     * 授权方应用名字
     */
    private String agentName;

    /**
     * 权限等级
     * 1:通讯录基本信息只读
     * 2:通讯录全部信息只读
     * 3:通讯录全部信息读写
     * 4:单个基本信息只读
     * 5:通讯录全部信息只写
     */
    private int level;

    /**
     * 授权方应用方形头像
     */
    private String agentSquareLogoUrl;

    /**
     * 授权方应用圆形头像
     */
    private String agentRoundLogoUrl;

    /**
     * 应用可见范围（部门）
     */
    private List<Integer> allowParty;

    /**
     * 应用可见范围（成员）
     */
    private List<String> allowUser;

    /**
     * 应用可见范围（标签）
     */
    private List<Integer> allowTag;

    /**
     * 额外通讯录（部门）
     */
    private List<Integer> extraParty;

    /**
     * 额外通讯录（成员）
     */
    private List<String> extraUser;

    /**
     * 额外通讯录（标签）
     */
    private List<Integer> extraTag;

    /**
     * 授权操作人的userid
     */
    private String authUserId;

    /**
     * 授权操作人的名字
     */
    private String authUserName;

    /**
     * 授权操作人的头像
     */
    private String authUserAvatar;

    /**
     * 授权方企业微信方形头像
     */
    private String corpSquareLogoUrl;

    @ApiModelProperty(value = "end", name = "end", dataType = "Long", example = "授权截止时间")
    private long end;

    @ApiModelProperty(value = "employees", name = "employees", dataType = "Integer", example = "公司员工数")
    private int employees;

    /**
     * 企业永久授权码
     */
    private String permanentCode;

    @ApiModelProperty(value = "created", name = "created", dataType = "Long", example = "创建时间")
    private long created;

    @ApiModelProperty(value = "updated", name = "updated", dataType = "Long", example = "更新时间")
    private long updated;

    /**
     * 激活状态: 1=已付费已激活，2=已禁用，3=已付费未激活, 0=未付费可试用
     */
    @ApiModelProperty(value = "status", name = "status", dataType = "Integer", example = "状态")
    private int status;

    public Company() {
    }

    @PersistenceConstructor
    public Company(String name, String fullName, String corpId, String agentId, String agentName, int level, String agentSquareLogoUrl, String agentRoundLogoUrl, List<Integer> allowParty, List<String> allowUser, List<Integer> allowTag, List<Integer> extraParty, List<String> extraUser, List<Integer> extraTag, String authUserId, String authUserName, String authUserAvatar, String corpSquareLogoUrl, long end, int employees, String permanentCode, long created, long updated, int status) {
        this.name = name;
        this.fullName = fullName;
        this.corpId = corpId;
        this.agentId = agentId;
        this.agentName = agentName;
        this.level = level;
        this.agentSquareLogoUrl = agentSquareLogoUrl;
        this.agentRoundLogoUrl = agentRoundLogoUrl;
        this.allowParty = allowParty;
        this.allowUser = allowUser;
        this.allowTag = allowTag;
        this.extraParty = extraParty;
        this.extraUser = extraUser;
        this.extraTag = extraTag;
        this.authUserId = authUserId;
        this.authUserName = authUserName;
        this.authUserAvatar = authUserAvatar;
        this.corpSquareLogoUrl = corpSquareLogoUrl;
        this.end = end;
        this.employees = employees;
        this.permanentCode = permanentCode;
        this.created = created;
        this.updated = updated;
        this.status = status;
    }
}
