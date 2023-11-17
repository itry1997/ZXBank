package com.it.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.sys.entity.RoleMenu;
import com.it.sys.entity.Task;
import io.lettuce.core.dynamic.annotation.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TaskMapper extends BaseMapper<Task> {
    List<Task> findAllTask(Integer userId);
    Task findTaskById(Integer id);
    void updateTask(@Param("id") Integer id, @Param("title") String title, @Param("description") String description, @Param("endtime") LocalDateTime endtime, @Param("state") Integer state);
}
