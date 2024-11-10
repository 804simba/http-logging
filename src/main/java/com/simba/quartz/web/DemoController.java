package com.simba.quartz.web;

import com.simba.quartz.web.model.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class DemoController {
//    private final PasswordEncoder passwordEncoder;

    @PostMapping("/hello")
    public ResponseEntity<Payload> handle(@RequestBody Payload payload) {
        return ResponseEntity.ok(payload);
    }

//    @GetMapping("encrypt/{payload}")
//    public ResponseEntity<String> encrypt(@PathVariable String payload) {
//        // $2a$10$txYzL.m5vHJJ1KY5FBfvV.jLz8mLHDL2mZz407YxUIcZ3vtzN.paa sax02@yopmail.com
//
//        // $2a$10$hqn.I.fhNHehIbKw/PhNgONq0lzCaJdXizXlkOgfjtu/AZLUa9tlC 1234
//        log.info("Encrypting payload: {}", passwordEncoder.encode(payload));
//        return payload != null ? ResponseEntity.ok(passwordEncoder.encode(payload)) : ResponseEntity.notFound().build();
//    }
}
