package ru.doubledata.task.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class TaskTest {

    private static final String UUID = "uuid";
    private static final File file0 = new File("testFileMD5.txt");

    @Before
    public void init() throws Exception {
        if (!file0.exists()) assertTrue(file0.createNewFile());
    }

    @After
    public void destroy() throws Exception {
        if (file0.exists()) assertTrue(file0.delete());
    }

    @Test
    public void run() throws Exception {
        Task task = new Task("testFileMD5.txt", "testObjectName",
                Task.AlgoType.MD5.getAlgoString(), true, UUID);
        task.run();
        assertThat(task.getStatus(), is(Task.Status.COMPLETED));
        assertThat(task.isCancelled(), is(false));
        assertNotNull(task.getResult());
    }

    @Test
    public void run_exception() throws Exception {
        Task task = new Task("nonExist.txt", "testObjectName",
                Task.AlgoType.MD5.getAlgoString(), true, UUID);
        task.run();
        assertThat(task.getStatus(), is(Task.Status.EXCEPTIONALLY));
        assertThat(task.isCancelled(), is(false));
        assertNull(task.getResult());
    }

}