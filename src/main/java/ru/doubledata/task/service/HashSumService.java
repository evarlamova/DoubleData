package ru.doubledata.task.service;

import org.springframework.stereotype.Component;
import ru.doubledata.task.dto.TaskDto;
import ru.doubledata.task.models.Task;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Component
public class HashSumService {
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
    private final ExecutorService executor = new ThreadPoolExecutor(100, 100, 0L,
            TimeUnit.SECONDS, tasks);
    final Map<String, List<Task>> userTasksMap = new ConcurrentHashMap<>();

    public void countHash(Task task) throws Exception {
        addTask(task);
        executor.submit(task);
    }

    public void cancelTask(String userUuid, String taskUuid) {
        userTasksMap.get(userUuid).stream()
                .filter(t -> t.getTaskUuid().equals(taskUuid))
                .forEach(t -> t.setCancelled(true));
    }

    @SuppressWarnings("unchecked")
    public List<TaskDto> getTasksList(String userUuid) {
        List<Task> tasksList = userTasksMap.getOrDefault(userUuid,Collections.EMPTY_LIST);
        return tasksList.stream()
                .filter(t -> !t.isCancelled())
                .map(t -> {
                    TaskDto taskDto = new TaskDto();
                    taskDto.setObjectName(t.getObjectName());
                    taskDto.setMessage(t.getStatus(), t.getBegin(), t.getExceptionMessage());
                    taskDto.setTaskUuid(t.getTaskUuid());
                    taskDto.setResult(t.getStatus(), t.getResult());
                    taskDto.setAlgoType(t.getAlgoType());
                    return taskDto;
                })
                .collect(Collectors.toList());
    }

    public void addExceptionallyTask(Task task, Exception e) {
        task.setStatus(Task.Status.EXCEPTIONALLY);
        task.setExceptionMessage(e.getMessage());
        addTask(task);
    }

    void addTask(Task task) {
        List<Task> taskList = userTasksMap.get(task.getUserUuid());
        if (taskList == null) {
            synchronized (this) {
                if ((taskList = userTasksMap.get(task.getUserUuid())) == null) {
                    taskList = new CopyOnWriteArrayList<>();
                    userTasksMap.put(task.getUserUuid(), taskList);
                }
            }
        }
        taskList.add(task);
    }
}
