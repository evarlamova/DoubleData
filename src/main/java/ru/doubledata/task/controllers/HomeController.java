package ru.doubledata.task.controllers;

import org.springframework.boot.Banner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.doubledata.task.models.Task;
import ru.doubledata.task.models.User;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class HomeController {

    @RequestMapping("/user")
    public User createUser() {
        User user = new User();
        user.setUuid(UUID.randomUUID().toString());
        return user;
    }

    @RequestMapping("/taskTypes")
    public List<String> enumType(){
        return Arrays.asList(Task.AlgoType.values()).stream()
                .map(Task.AlgoType::getAlgoString).collect(Collectors.toList());
    }

}
