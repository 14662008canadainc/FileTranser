package io.novovivo.filetransfer.repository;

import io.novovivo.filetransfer.model.LocalSocketServerPorts;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author：Zuochao（Edward）Dou
 */
@Repository
public interface LocalSocketServerPortsRepository extends JpaRepository<LocalSocketServerPorts, Long>, CrudRepository<LocalSocketServerPorts, Long> {

    List<LocalSocketServerPorts> findByInUse(Boolean inUse);

    LocalSocketServerPorts findByPort(Integer port);
}
