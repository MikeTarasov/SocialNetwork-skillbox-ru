package ru.skillbox.socialnetwork.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.List;
public class FriendController {

    @GetMapping("/friends/request")
    public List request(){
        return null;
    }

    @GetMapping("/friends/recommendations")
    public List recommendations(){
        return null;
    }
    @GetMapping("/is/friends")
    public boolean isExist(){
        return false;
    }

    @GetMapping("/friends")
    public List list(){
        return null;
    }

    @PostMapping("/friends")
    public long add(){
        return 0;
    }

    @DeleteMapping("/friends")
    public long delete(){
        return 0;
    }

    @GetMapping("/friends/{id}")
    public ResponseEntity get(@PathVariable int id){
        return null;
    }
}
