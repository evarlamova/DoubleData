package ru.doubledata.task.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.doubledata.task.dto.TaskDto;
import ru.doubledata.task.models.Task;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HashSumServiceTest {

    private static final String UUID = "uuid";
    private static final String TASK_ID = "taskUuid";
    private static final File file0 = new File("testFileMD5.txt");
    private static final File file1 = new File("testFile1MD5.txt");
    private static final File file2 = new File("testFile2MD5.txt");
    private HashSumService hashSumService = new HashSumService();

    @Before
    public void init() throws Exception {
        if (!file0.exists()) assertTrue(file0.createNewFile());
        if (!file1.exists()) assertTrue(file1.createNewFile());
        if (!file2.exists()) assertTrue(file2.createNewFile());
    }

    @After
    public void destroy() throws Exception {
        if (file0.exists()) file0.delete();
        if (file1.exists()) file1.delete();
        if (file2.exists()) file2.delete();
    }

    @Test
    public void countHash() throws Exception {
        Task task = mock(Task.class);
        when(task.getUserUuid()).thenReturn(UUID);
        hashSumService.countHash(task);
        assertThat(hashSumService.userTasksMap.size(), is(1));
    }

    @Test
    public void cancelTask_mock() throws Exception {
        Task task = mock(Task.class);
        when(task.getUserUuid()).thenReturn(UUID);
        when(task.getTaskUuid()).thenReturn(TASK_ID);
        hashSumService.countHash(task);
        hashSumService.cancelTask(UUID, TASK_ID);
        verify(task, atLeastOnce()).setCancelled(true);
    }

    @Test
    public void cancelTask_realObject() throws Exception {

        Task task = new Task("testFileMD5.txt", "testObjectName",
                Task.AlgoType.MD5.getAlgoString(), true, UUID);
        Task task1 = new Task("testFile1MD5.txt", "testObjectName",
                Task.AlgoType.MD5.getAlgoString(), true, UUID);
        Task task2 = new Task("testFile2MD5.txt", "testObjectName",
                Task.AlgoType.MD5.getAlgoString(), true, UUID);
        hashSumService.countHash(task);
        hashSumService.countHash(task1);
        hashSumService.countHash(task1);
        assertThat(hashSumService.userTasksMap.size(), is(1));
        assertThat(hashSumService.userTasksMap.get(UUID).size(), is(3));
        hashSumService.cancelTask(UUID, task.getTaskUuid());
        assertThat(task.isCancelled(), is(true));
        assertThat(task1.isCancelled(), is(false));
        assertThat(task2.isCancelled(), is(false));
    }


    @Test
    public void getTasksList_empty() throws Exception {
        List<TaskDto> result = hashSumService.getTasksList(UUID);
        assertNotNull(result);
        assertThat(result.size(), is(0));
    }

    @Test
    public void getTasksList_nonEmpty() throws Exception {
        Task task = new Task("testFileMD5.txt", "testObjectName",
                Task.AlgoType.MD5.getAlgoString(), true, UUID);
        hashSumService.countHash(task);
        List<TaskDto> result = hashSumService.getTasksList(UUID);
        assertNotNull(result);
        assertThat(result.size(), is(1));
    }

    @Test
    public void addExceptionallyTask() throws Exception {
        Task task = new Task("testFileMD5.txt", "testObjectName",
                Task.AlgoType.MD5.getAlgoString(), true, UUID);
        hashSumService.addExceptionallyTask(task, new Exception());
        assertThat(hashSumService.userTasksMap.size(), is(1));
        Task mockedTask1 = mock(Task.class);
        when(mockedTask1.getUserUuid()).thenReturn(UUID);
        when(mockedTask1.getTaskUuid()).thenReturn(TASK_ID);
        when(mockedTask1.getStatus()).thenReturn(Task.Status.COMPLETED);
        Task mockedTask2 = mock(Task.class);
        when(mockedTask2.getUserUuid()).thenReturn(UUID);
        when(mockedTask2.getStatus()).thenReturn(Task.Status.COMPLETED);
        hashSumService.addTask(mockedTask1);
        hashSumService.addTask(mockedTask2);
        Map<Task.Status, List<Task>> result =
                hashSumService.userTasksMap.entrySet().stream()
                .flatMap(entry -> entry.getValue().stream())
                        .collect(Collectors.groupingBy(Task::getStatus));
        assertThat(result.get(Task.Status.COMPLETED).size(), is(2));
        assertThat(result.get(Task.Status.EXCEPTIONALLY).size(), is(1));
    }

}