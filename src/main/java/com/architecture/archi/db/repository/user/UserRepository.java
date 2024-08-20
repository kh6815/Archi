package com.architecture.archi.db.repository.user;


import com.architecture.archi.db.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

//    boolean existsById(String userId);
}
