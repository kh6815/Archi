package com.architecture.archi.db.repository.content;

import com.architecture.archi.db.entity.content.ContentFileEntity;
import com.architecture.archi.db.entity.file.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContentFileRepository extends JpaRepository<ContentFileEntity, Long> {
}
