package com.fmahadybd.hospital_services;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

    @GetMapping("/home")
    public String getHello(){
        return "hello==================== Fahim";
    }
    
}
