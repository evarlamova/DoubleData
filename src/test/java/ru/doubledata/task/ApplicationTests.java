package ru.doubledata.task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.matchers.Contains;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.doubledata.task.controllers.TasksController;
import ru.doubledata.task.models.Task;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTests {

    private String userUuid = "d5f9e27e-bddb-4629-a1b4-884ac493ce0b";
    private MockMvc mockMvc;

    @Mock
    private TasksController tasksController;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Autowired
    private TestRestTemplate template;

    @Test
    public void initialTest() {
        ResponseEntity<String> response = this.template.getForEntity("/", String.class, 8080);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void userIndexTest() throws Exception {
        mockMvc.perform(get("/user/"))
                .andExpect(status().isOk())
                .andExpect(content().string(new Contains("uuid")));
    }

    @Test
    public void taskListTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/taskTypes/"))
                .andExpect(status().isOk()).andReturn();
        assert(result.getResponse().getContentAsString().equals("[\"MD5\",\"SHA-1\",\"SHA-256\"]"));
    }

    @Test
    public void  hashSumByFileTest() throws Exception {
        MockMultipartFile firstFile = new MockMultipartFile("file", "testFile", null, "some xml".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.fileUpload("/api/fileupload")
                .file(firstFile)
                .param("linkInput", "defaultValue")
                .param("algoType", "MD5")
                .param("isLocal", "true")
                .param("userUuid", userUuid)).andExpect(status().isOk());
    }

    @Test
    public void hashSumByUriTest() throws Exception {
        mockMvc.perform(post("/api/fileupload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .param("linkInput", "https://sb.scorecardresearch.com/beacon.js")
                .param("algoType", "MD5")
                .param("isLocal", "false")
                .param("userUuid", userUuid)).andExpect(status().isOk());
    }

    @Test
    public void hashSumForLocalFileTest() throws Exception {
        assertTrue(new File("testFileMD5.txt").createNewFile());
        Task task = new Task("testFileMD5.txt", "testObjectName", Task.AlgoType.MD5.getAlgoString(), true, userUuid);
        task.run();
        assertEquals("D41D8CD98F00B204E9800998ECF8427E", task.getResult());

        assertTrue(new File("testFileSH_1.txt").createNewFile());
        task = new Task("testFileSH_1.txt", "testObjectName", Task.AlgoType.SHA_1.getAlgoString(), true, userUuid);
        task.run();
        assertEquals("DA39A3EE5E6B4B0D3255BFEF95601890AFD80709", task.getResult());

        assertTrue(new File("testFileSH-256.txt").createNewFile());
        task = new Task("testFileSH-256.txt", "testObjectName", Task.AlgoType.SHA_256.getAlgoString(), true, userUuid);
        task.run();
        assertEquals("E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855", task.getResult());
    }

}
