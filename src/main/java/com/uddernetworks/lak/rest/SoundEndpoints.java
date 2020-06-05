package com.uddernetworks.lak.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller // This means that this class is a Controller
@RequestMapping(path = "/sounds") // This means URL's start with /demo (after Application path)
public class SoundEndpoints {
//    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
//    private UserRepository userRepository;

    @PostMapping(path = "/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewUser(@RequestParam String name, @RequestParam String email) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        return "Saved";
    }

    @GetMapping(path = "/list")
    public @ResponseBody
    Iterable<Void> getAllSounds() {
        return null;
    }
}