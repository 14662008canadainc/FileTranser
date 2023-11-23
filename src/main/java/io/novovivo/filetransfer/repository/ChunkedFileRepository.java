package io.novovivo.filetransfer.repository;

import io.novovivo.filetransfer.model.ChunkedFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author：Zuochao（Edward）Dou
 */
@Repository
public interface ChunkedFileRepository extends JpaRepository<ChunkedFile, Long>, CrudRepository<ChunkedFile, Long> {
 List<ChunkedFile> findByType(String type);
 List<ChunkedFile> findByFilename(String filename);
}
