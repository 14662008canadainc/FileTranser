package io.novovivo.filetransfer.repository;

import io.novovivo.filetransfer.model.RemoteSocketServerPorts;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author：Zuochao（Edward）Dou
 */

@Repository
public interface RemoteSocketServerPortsRepository extends JpaRepository<RemoteSocketServerPorts, Long>, CrudRepository<RemoteSocketServerPorts, Long> {

    List<RemoteSocketServerPorts> findByInUse(Boolean inUse);

    RemoteSocketServerPorts findByPort(Integer port);
}
