package com.it.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.it.sys.dto.TaskDto;
import com.it.sys.entity.Task;
import com.it.sys.entity.User;

import java.util.List;

public interface ITaskService  extends IService<Task> {
    Boolean addTask(Task task);
    Boolean deleteUserById(Integer id,String token);
    TaskDto getById(Integer id);

    //mkx
    boolean updateTask(Integer id, Task task, String token);

    //mkx
    List<Task> getTaskById(Integer userId);

    //mkx
    Task findTaskById(Integer id);
}
