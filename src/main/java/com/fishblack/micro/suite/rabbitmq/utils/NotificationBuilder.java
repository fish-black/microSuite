package com.fishblack.micro.suite.rabbitmq.utils;

import com.fishblack.micro.suite.model.Entity;
import com.fishblack.micro.suite.model.EntityType;
import com.fishblack.micro.suite.model.Notification;
import com.fishblack.micro.suite.model.RelatedEntity;
import com.fishblack.micro.suite.model.Relation;
import com.fishblack.micro.suite.model.Severity;
import com.fishblack.micro.suite.model.State;
import com.fishblack.micro.suite.rabbitmq.NotificationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * To build various Notification types.
 */
public class NotificationBuilder {

    private Logger logger = LoggerFactory.getLogger(NotificationBuilder.class);

    private String tenant = null;
    private String user = null;
    private Long msgCreated = null;
    private Entity entity = null;
    private List<RelatedEntity> relatedEntities = null;
    private String source = null;

    public NotificationBuilder setTenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public NotificationBuilder setUser(String user) {
        this.user = user;
        return this;
    }

    public NotificationBuilder setMsgCreated(Long msgCreated) {
        this.msgCreated = msgCreated;
        return this;
    }

    public NotificationBuilder setMsgCreated(String msgCreatedStr) throws ParseException{
        this.msgCreated = NotificationUtils.convertStringDateToEpoch(msgCreatedStr);
        return this;
    }

    public NotificationBuilder setEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    public NotificationBuilder setRelatedEntity(Relation relation, Entity entity){
        if(relatedEntities == null){
            relatedEntities = new ArrayList<>();
        }
        RelatedEntity relatedEntity = new RelatedEntity();
        relatedEntity.setRelation(relation);
        relatedEntity.setEntity(entity);
        relatedEntities.add(relatedEntity);
        return this;
    }

    public NotificationBuilder setSource(String source){
        this.source = source;
        return this;
    }

    private Notification createNotification(){
        Notification notification = new Notification();
        if(msgCreated != null) {
            notification.setTimestamp(NotificationUtils.formatDate(msgCreated));
        } else {
            notification.setTimestamp(NotificationUtils.formatDate(System.currentTimeMillis()));
        }
        notification.setTenant(tenant);
        notification.setUser(user);
        notification.setState(State.UNREAD);
        notification.setEntity(entity);
        notification.setRelatedEntities(relatedEntities);
        return notification;
    }

    /**
     * Creates a org level notification
     * @return Notification
     */
    public Notification createAnnouncementNotification(){
        return createNotificationWithSource(source);
    }

    /**
     * Creates Order state change notification.
     * @return Notification
     */
    public Notification createOrderStateNotification(){
        return createNotificationWithSource("shopping-svc");
    }

    /**
     * Creates Request state change notification.
     * @return Notification
     */
    public Notification createRequestStateNotification(){
        return createNotificationWithSource("shopping-svc");
    }

    /**
     * Creates Request state change notification.
     * @return Notification
     */
    public Notification createRequestStateNotification(String source){
        return createNotificationWithSource(source);
    }

    /**
     * Creates Approval state change notification.
     * @return Notification
     */
    public Notification createApprovalNotification(){
        return createNotificationWithSource("policy-svc");
    }

    /**
     * Creates comment notification.
     * The only request type supported to create a comment notification are ORDER, SUPPORT, EXTERNAL, BUNDLE_ORDER, ORDER_CLUSTER_ROOT
     * The request types of ORDER, BUNDLE_ORDER, ORDER_CLUSTER_ROOT are related to ORDER, so mapping them to ORDER
     * TODO:// once refactoring to requestTypes is completed on catalog, it might require change
     * @return Notification
     */
    public Notification createCommentNotification(){

        Notification notification = createNotificationWithSource("sxadapter-svc");

        if(entity.getType().toJsonValue().equalsIgnoreCase(EntityType.COMMENT.name())){

            if(relatedEntities.size() == 0){
                logger.error("No related entities found for the Comment entity, dropping notification: ", notification);
                return null;
            }

            for(RelatedEntity relatedEntityObj : relatedEntities){

                Entity relatedEntity = relatedEntityObj.getEntity();
                if(relatedEntity.getFields().containsKey(Entity.REQUEST_TYPE)){

                    String requestType = relatedEntity.getFields().get(Entity.REQUEST_TYPE);
                    if(requestType.equalsIgnoreCase(NotificationConstants.BUNDLE_ORDER_TYPE) || requestType.equalsIgnoreCase(NotificationConstants.ORDER_CLUSTER_ROOT_TYPE)){
                        requestType = NotificationConstants.ORDER_TYPE;
                        relatedEntity.getFields().put(Entity.REQUEST_TYPE, NotificationConstants.ORDER_TYPE);
                    }

                    if(requestType.equalsIgnoreCase(NotificationConstants.ORDER_TYPE)){
                        entity.setType(EntityType.COMMENT_REQUEST);
                    } else if (requestType.equalsIgnoreCase(NotificationConstants.SUPPORT_TYPE)) {
                        entity.setType(EntityType.COMMENT_SUPPORT);
                    } else if (requestType.equalsIgnoreCase(NotificationConstants.EXTERNAL_TYPE)) {
                        entity.setType(EntityType.COMMENT_EXTERNAL);
                    } else {
                        logger.error("Unknown request type found in the related entity for the comment notification, dropping notification: ", notification);
                        return null;
                    }

                } else {
                    logger.error("No request type found in the related entity for the comment notification, dropping notification: ", notification);
                    return null;
                }
            }
            notification.setEntity(entity);
            notification.setRelatedEntities(relatedEntities);
        }
        return notification;
    }

    /**
     * Creates support request notification.
     * @return Notification
     */
    public Notification createSupportRequestNotification(){
        return createNotificationWithSource("request-svc");
    }

    /**
     * Creates external request notification.
     * @return Notification
     */
    public Notification createExternalRequestNotification(){
        return createNotificationWithSource("request-svc");
    }

    private Notification createNotificationWithSource(String source){
        Notification notification = createNotification();
        notification.setSeverity(Severity.MEDIUM);
        notification.setSource(source);
        return notification;
    }
}
