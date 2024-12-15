package haui.android.taskmanager.models;

import java.io.Serializable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Task implements Serializable {
    int taskID;
    String taskName;
    String description;
    String startDate;
    String startTime;
    String endDate;
    String endTime;
    int priority;
    int statusID;
    int tagID;

    public String getStringWorking(){
        return "Working | Deadline: " + endTime + " at " + endDate;
    }

    public String getStringNewWork(){
        return "Start at: " + startDate + "\nEnd at: " + endDate;
    }

    public String getStringComplete(){
        return "Complete: \nStart at: " + startDate + "\nEnd at: " + endDate;
    }

    public String getStringLate(){
        return "Late: \nStart at: " + startDate + "\nEnd at: " + endDate;
    }
}
