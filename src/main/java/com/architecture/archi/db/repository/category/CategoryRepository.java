package com.architecture.archi.db.repository.category;

import com.architecture.archi.db.entity.category.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Integer> {
}
