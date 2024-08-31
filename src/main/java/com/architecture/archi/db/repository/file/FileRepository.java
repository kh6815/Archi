package com.architecture.archi.db.repository.file;

import com.architecture.archi.db.entity.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByIdIn(List<Long> id);
}
