package com.example.testingwithmockmvc.blog.task;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.security.RolesAllowed;
import java.net.URI;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;


    @PostMapping
    public ResponseEntity<Void> createNewTask(
            @RequestBody JsonNode payload,
            UriComponentsBuilder uriComponentsBuilder
    ) {

        Long taskId = this.taskService
                .createTask(
                        payload.get("taskTitle").asText()
                );
        URI uriRedirect = uriComponentsBuilder.path("/api/tasks/{taskId}")
                .build(taskId);

        return ResponseEntity
                .created(uriRedirect)
                .build();
    }

    @DeleteMapping
    @RolesAllowed("ADMIN")
    @RequestMapping("/{taskId}")
    public void deleteTask(@PathVariable Long taskId) {
        this.taskService.deleteTask(taskId);
    }
}
