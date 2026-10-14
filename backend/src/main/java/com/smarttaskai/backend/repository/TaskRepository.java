
package com.smarttaskai.backend.repository;

import com.smarttaskai.backend.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
