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
 * @Author：Zuochao（Edward）Dou
 */

@Entity
@Table(name = "local")
public class LocalSocketServerPorts extends AuditModel implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Getter
    @Setter
    private Long id;

    @Column(name = "ip")
    @Getter
    @Setter
    private String ip;

    @Column(name = "port", nullable = false)
    @Getter
    @Setter
    private Integer port;

    @Column(name = "session_id")
    @Getter
    @Setter
    private String sessionId = null;

    @Column(name = "chunk_id")
    @Getter
    @Setter
    private Integer chunkId = null;

    @Column(name = "in_use", nullable = false)
    @Getter
    @Setter
    private Boolean inUse = false;
}
