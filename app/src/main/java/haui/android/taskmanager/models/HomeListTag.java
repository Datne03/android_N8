package haui.android.taskmanager.models;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HomeListTag implements Serializable {
    int id;
    List<TaskDetail> taskDetailList;

    public List<TaskDetail> addList(TaskDetail taskDetail){
        taskDetailList.add(taskDetail);
        return taskDetailList;
    }
}
