package com.simba.quartz.web;

import com.simba.quartz.web.model.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class DemoController {

    @PostMapping("/hello")
    public ResponseEntity<Payload> handle(@RequestBody Payload payload) {
        return ResponseEntity.ok(payload);
    }
}
