package ir.co.sadad.pushnotification.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
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
    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdOn;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY", columnDefinition = "char(15)", length = 15)
    private String modifiedBy;

    @LastModifiedDate
    @Column(name = "MODIFIED_ON")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss" )
    private Date modifiedOn;

    @JsonIgnore
    @Version
    @Column(name = "OPT_LOCK", nullable = false, columnDefinition = "integer DEFAULT 0")
    private Long version = 0L;

    @JsonIgnore
    public boolean isNew() {
        return this.id == null;
    }


}