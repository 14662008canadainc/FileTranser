package io.novovivo.filetransfer.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Zuochao(Edward) Dou
 */
@Entity
@Table(name = "chunkedFile")
public class ChunkedFile extends AuditModel implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(name = "filename", nullable = false)
    @Getter
    @Setter
    private String filename;

    @Column(name = "type", nullable = false)
    @Getter
    @Setter
    private String type;
}
