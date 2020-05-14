package com.fishblack.micro.suite.model;

import com.fishblack.micro.suite.model.validation.ValidEnum;
import com.fishblack.micro.suite.rabbitmq.utils.NotificationUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import com.fishblack.micro.suite.rabbitmq.utils.TenantSerializer;
import com.fishblack.micro.suite.rabbitmq.utils.UserSerializer;
import io.dropwizard.jackson.Jackson;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import java.io.IOException;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.fishblack.micro.suite.rabbitmq.utils.EnumUtils.getEnumFromString;


/**
 * Notification type. todo: all Size annotation needs to be adjusted as per db schema.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Notification implements Routable {

    private UUID id;

    /**
     * Timestamp in ISO_8601 format (2016-02-11T06:15:18.546Z).
     * <p>
     * TODO: Strict date validation
     */
    @NotNull
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z",
            message = "must be in ISO_8601 format: YYYY-MM-DDThh:mm:ss.sssZ")
    private String timestamp;

    @JsonSerialize(using = UserSerializer.class)
    private String user;

    @NotNull
    @NotEmpty
    @NotBlank
    @JsonSerialize(using = TenantSerializer.class)
    private String tenant;

    @NotNull
    @ValidEnum(enumClass = State.class)
    private String state;

    @NotNull
    @ValidEnum(enumClass = Severity.class)
    private String severity;

    @NotNull
    @NotEmpty
    @NotBlank
    private String source;

    @NotNull
    @Valid
    private Entity entity;

    @Valid
    private List<RelatedEntity> relatedEntities;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = NotificationUtils.handleUserCase(user);
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = NotificationUtils.handleTenantCase(tenant);
    }

    public State getState() {
        return getEnumFromString(State.class, state);
    }

    public void setState(State state) {
        this.state = state != null ? state.toJsonValue() : null;
    }

    public Severity getSeverity() {
        return getEnumFromString(Severity.class, severity);
    }

    public void setSeverity(Severity severity) {
        this.severity = severity != null ? severity.toJsonValue() : null;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public List<RelatedEntity> getRelatedEntities() {
        return relatedEntities;
    }

    public void setRelatedEntities(List<RelatedEntity> relatedEntities) {
        this.relatedEntities = relatedEntities;
    }

    public String toJsonString() throws IOException {
        ObjectMapper mapper = Jackson.newObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper.writeValueAsString(this);
    }

    /**
     * Create a notification object from json message.
     *
     * @param msg notification json message
     * @return Notification object
     * @throws IOException
     */
    public static Notification toNotification(String msg) throws IOException {
        ObjectMapper mapper = Jackson.newObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        Notification notification = mapper.readValue(msg, Notification.class);
        validateNotification(notification);
        return notification;
    }


    public static void validateNotification(Notification notification) throws IOException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Notification>> violations = validator.validate(notification);
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Invalid notification json message: ");
        if (violations.size() != 0) {
            for (ConstraintViolation<Notification> violation : violations) {
                errorMsg.append(violation.getPropertyPath());
                errorMsg.append(" ");
                errorMsg.append(violation.getMessage());
                errorMsg.append(", ");
            }
            String message = errorMsg.toString();
            throw new IOException(message.substring(0, message.length() - 2));
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Notification that = (Notification) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (!timestamp.equals(that.timestamp)) return false;
        if (user != null ? !user.equals(that.user) : that.user != null) return false;
        if (!tenant.equals(that.tenant)) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (!severity.equals(that.severity)) return false;
        if (!source.equals(that.source)) return false;
        if (!entity.equals(that.entity)) return false;
        return !(relatedEntities != null ? !relatedEntities.equals(that.relatedEntities) : that.relatedEntities != null);

    }

    @JsonIgnore
    @Override
    public String getJsonMessage() throws IOException {
        return toJsonString();
    }

    @JsonIgnore
    @Override
    public String getRoutingKey() {
        StringBuilder sb = new StringBuilder();
        sb.append("propel.notification.message.");
        if (this.getUser() != null && !this.getUser().trim().isEmpty()) {
            sb.append("user.");
            sb.append(this.getTenant());
            sb.append(".");
            sb.append(this.getUser());
        } else {
            sb.append("tenant.");
            sb.append(this.getTenant());
        }
        return sb.toString();
    }
}

