package com.it.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.it.common.utils.JwtUtil;
import com.it.sys.dto.TaskDto;
import com.it.sys.entity.Task;
import com.it.sys.entity.User;
import com.it.sys.mapper.TaskMapper;
import com.it.sys.service.ITaskService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;


@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements ITaskService {
    @Resource
    private TaskMapper taskMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Override
    public Boolean  addTask(Task task,String token) {
        User user = jwtUtil.parseToken(token,User.class);
        task.setUserid(user.getId());
        task.setUsername(user.getUsername());
        task.setStatus(0);
        task.setNextstatus(1);
        taskMapper.insert(task);
        return true;
    }

    @Override
    public Boolean deleteUserById(Integer id, String token) {
        User user = null;
        Task task = taskMapper.selectById(id);
        try {
            user = jwtUtil.parseToken(token, User.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(user != null&&user.getId().equals(task.getUserid())){
            taskMapper.deleteById(user.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<Task> getTaskById(Integer userId) {
        return taskMapper.findAllTask(userId);
    }

    @Override
    public boolean updateTask(Integer id, Task updateTask, String token){
        User user = null;
        try{
            user = jwtUtil.parseToken(token, User.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        Task beforetask = findTaskById(id);
        Integer state_before = beforetask.getStatus();
        String title = updateTask.getTitle();
        String description = updateTask.getDescription();
        LocalDateTime endtime = updateTask.getEndtime();
        Integer state_after = updateTask.getStatus();
        if(user!=null&&user.getId().equals(updateTask.getUserid())&& state_before!=2){
            if(state_after >= state_before){
                taskMapper.updateTask(id, title, description, endtime, state_after);
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    @Override
    public Task findTaskById(Integer id){
        Task task = taskMapper.findTaskById(id);
        return task;
    }

    //已创建  进行中  已完成
    @Override
    public TaskDto getById(Integer id) {
        Task task = taskMapper.selectById(id);
        TaskDto taskDto = new TaskDto();
        BeanUtils.copyProperties(task,taskDto);
        if(task.getStatus().equals(0)){
            taskDto.setNextStatusString("进行中");
            taskDto.setNextstatus(1);
        }else if (task.getStatus().equals(1)){
            taskDto.setNextStatusString("已完成");
            taskDto.setNextstatus(2);
        }
        return taskDto;
    }
}
