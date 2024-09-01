package com.architecture.archi.db.repository.user;

import com.architecture.archi.db.entity.user.UserFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserFileRepository extends JpaRepository<UserFileEntity, Long> {
}
