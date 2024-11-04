package ir.co.sadad.pushnotification.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * basic entity of entities
 * auditing of all entities is here .
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class AbstractEntity implements Serializable, Cloneable {

    protected static final Timestamp DELETE_AT = Timestamp.valueOf("1970-01-01 00:00:00.0");

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedBy
    @Column(name = "CREATED_BY", nullable = false, updatable = false, columnDefinition = "char(15)", length = 15)
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_ON", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdOn;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY", columnDefinition = "char(15)", length = 15)
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "MODIFIED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modifiedOn;

    @JsonIgnore
    @Version
    @Column(name = "OPT_LOCK", columnDefinition = "INTEGER DEFAULT 0")
    private Integer version = 0;

    @JsonIgnore
    public boolean isNew() {
        return this.id == null;
    }


    @Override
    public AbstractEntity clone() {
        try {
            AbstractEntity clone = (AbstractEntity) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}