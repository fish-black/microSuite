package com.fishblack.micro.suite.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fishblack.micro.suite.model.validation.ValidEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import static com.fishblack.micro.suite.rabbitmq.utils.EnumUtils.getEnumFromString;

/**
 * Related Entity object to represent relation to the notification entity
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RelatedEntity {

    @NotNull
    @ValidEnum(enumClass = Relation.class)
    private String relation;

    @NotNull
    @Valid
    private Entity entity;

    public Relation getRelation() {
        return getEnumFromString(Relation.class, relation);
    }

    public void setRelation(Relation relation) {
        this.relation = relation.toJsonValue();
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelatedEntity that = (RelatedEntity) o;

        if (relation != that.relation) return false;
        return !(entity != null ? !entity.equals(that.entity) : that.entity != null);

    }
}
