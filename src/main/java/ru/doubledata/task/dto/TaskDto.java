package ru.doubledata.task.dto;

import ru.doubledata.task.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskDto {
    private String taskUuid;
    private String message;
    private String objectName;
    private String result;
    private String algoType;

    public void setMessage(Task.Status status, LocalDateTime begin, String excepionMessage){
        switch (status.getStatusMessage()) {
            case ("NEW"):
                message = "task is waiting";
                break;
            case ("RUNNING") :
                long duration = Duration.between(begin, LocalDateTime.now()).toMillis()/1000 ;
                message = "task is running " + duration;
                break;
            case ("COMPLETED"):
                message = "completed";
                break;
            case ("EXCEPTIONALLY"):
                message = status.getStatusMessage() + ":  " + excepionMessage;
                break;
        }
    }

    public void setResult(Task.Status status, String hex) {
        if (Task.Status.COMPLETED.equals(status))
            this.result = hex;
    }

    public String getTaskUuid() {
        return taskUuid;
    }

    public void setTaskUuid(String taskUuid) {
        this.taskUuid = taskUuid;
    }

    public String getAlgoType() {
        return algoType;
    }

    public void setAlgoType(String algoType) {
        this.algoType = algoType;
    }

    public String getMessage() {
        return message;
    }

    public String getResult() {
        return result;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }

}
