package ru.doubledata.task.controllers;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.doubledata.task.dto.TaskDto;
import ru.doubledata.task.models.Task;
import ru.doubledata.task.service.HashSumService;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.UUID;

@RestController
public class TasksController {

    @Autowired
    HashSumService hashSumService;


    @RequestMapping(value = "/api/fileupload", method = RequestMethod.POST)
    void fileUpload(@RequestParam(value = "file", required = false) MultipartFile file,
                    @RequestParam(value = "linkInput") String linkInput,
                    @RequestParam("algoType") String algoType,
                    @RequestParam("isLocal") boolean isLocal,
                    @RequestParam("userUuid") String userUuid) throws Exception {
        try (InputStream fis = isLocal ? file.getInputStream() :
                new URL(linkInput).openStream()) {
            String sourceName;
            String objectPath;
            Task task;
            if (isLocal) {
                sourceName = UUID.randomUUID() + ".tmp";
                objectPath = file.getName();
                File f = new File(sourceName);
                if (f.createNewFile()) {
                    try (OutputStream bis = new BufferedOutputStream(new FileOutputStream(f))) {
                        IOUtils.copy(fis, bis);
                        bis.flush();
                    }
                }
            } else {
                sourceName = linkInput;
                objectPath = linkInput;
            }
            task = new Task(sourceName, objectPath, algoType, isLocal, userUuid);
            hashSumService.countHash(task);
        } catch (IOException e){
            hashSumService.addExceptionallyTask(new Task("", linkInput, algoType, isLocal, userUuid), e);
        }
    }


    @RequestMapping("{userUuid}/statusUpdate")
    List<TaskDto> updateTaskList(@PathVariable String userUuid) {
        return hashSumService.getTasksList(userUuid);
    }

    @RequestMapping("{userUuid}/{taskUuid}/deleteTask")
    List<Task> taskListAfterDelete(@PathVariable String userUuid, @PathVariable String taskUuid) {
        hashSumService.cancelTask(userUuid, taskUuid);
        return null;
    }
}
