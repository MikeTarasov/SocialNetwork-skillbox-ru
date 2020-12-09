package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


import java.util.List;
public class FriendController {

    @GetMapping("/friends/")
    public List list(){

    }

    @PostMapping("/friends/")
    public long add(){

    }

    @DeleteMapping("/friends/")
    public long delete(){

    }

    @GetMapping("/friends/{id}")
    public ResponseEntity get(@PathVariable int id){

    }
}
