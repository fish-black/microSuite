package com.fishblack.micro.suite.rabbitmq.utils;

import com.fishblack.micro.suite.model.Entity;
import com.fishblack.micro.suite.model.EntityType;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder to build entity
 */
public class EntityBuilder {

    private String guid = null;
    private Map<String, String> fields = null;

    public EntityBuilder setGuid(String guid) {
        this.guid = guid;
        return this;
    }

    public EntityBuilder setField(String key, String value){
        if(fields == null){
            fields = new LinkedHashMap<>();
        }
        fields.put(key, value);
        return this;
    }

    private Entity createEntity(EntityType entityType) {
        Entity entity = new Entity();
        entity.setType(entityType);
        final String requestType = fields != null ? fields.get(Entity.REQUEST_TYPE) : null;
        switch (entityType){
            case ANNOUNCEMENT:
                entity.setUri(null);
                break;
            case ORDER:
                entity.setUri(NotificationUtils.createOrderUri(guid));
                break;
            case REQUEST:
                if (EntityType.EXTERNAL.name().equals(requestType)) {
                    entity.setUri(NotificationUtils.createExternalRequestUri(guid));
                } else {
                    entity.setUri(NotificationUtils.createRequestUri(guid));
                }
                break;
            case APPROVAL:
                entity.setUri(NotificationUtils.createApprovalUri(guid));
                break;
            case COMMENT:
                entity.setUri(NotificationUtils.createCommentUri(guid));
                break;
            case SUPPORT:
                entity.setUri(NotificationUtils.createRequestUri(guid));
                break;
            case EXTERNAL:
                entity.setUri(NotificationUtils.createExternalRequestUri(guid));
                break;
            default:
                entity.setUri("unknown");
        }
        entity.setFields(fields);
        return entity;
    }

    /**
     * Creates announcement entity
     * @return Entity
     */
    public Entity createAnnouncementEntity(){
        return createEntity(EntityType.ANNOUNCEMENT);
    }

    /**
     * Creates order entity
     * @return Entity
     */
    public Entity createOrderEntity(){
        return createEntity(EntityType.ORDER);
    }

    /**
     * Creates request entity
     * @return Entity
     */
    public Entity createRequestEntity(){
        return createEntity(EntityType.REQUEST);
    }

    /**
     * Creates request entity with a specified requestType
     * @return Entity
     */
    public Entity createRequestEntity(String requestType){
        return createEntity(EntityType.valueOf(requestType));
    }

    /**
     * Creates approval entity
     * @return Entity
     */
    public Entity createApprovalEntity(){
        return createEntity(EntityType.APPROVAL);
    }

    /**
     * Creates comment entity
     * @return Entity
     */
    public Entity createCommentEntity(){
        return createEntity(EntityType.COMMENT);
    }
}