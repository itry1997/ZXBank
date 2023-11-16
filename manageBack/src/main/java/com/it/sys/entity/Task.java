package com.it.sys.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;

import java.io.Serializable;
import java.time.LocalDate;

@TableName("x_task")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String title;
    private String description;
    private LocalDate begintime;
    private LocalDate endtime;
    private Integer userid;
    private Integer status;
    private Integer nextstatus;

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title=" + title +
                ", describe=" + description +
                ", begin=" + begintime +
                ", end=" + endtime +
                ", userId=" + userid +
                ", state=" + status +
                ", nextState=" + nextstatus +
                "}";
    }

}
