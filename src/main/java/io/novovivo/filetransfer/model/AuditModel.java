package io.novovivo.filetransfer.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
    value = {"created", "updated"},
    allowGetters = true)
public abstract class AuditModel implements Serializable {
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", updatable = false)
  @CreatedDate
  @Getter
  @Setter
  public Date createdAt;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated")
  @LastModifiedDate
  @Getter
  @Setter
  public Date updatedAt;
}
