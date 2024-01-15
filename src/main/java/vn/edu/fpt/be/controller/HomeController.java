package vn.edu.fpt.be.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class HomeController {
    @GetMapping
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("duoc vai lon");
    }
}
