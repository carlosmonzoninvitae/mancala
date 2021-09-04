package com.bol.mancala.controller;

import com.bol.mancala.dto.MatchDTO;
import com.bol.mancala.dto.StartMatchDTO;
import com.bol.mancala.model.Match;
import com.bol.mancala.service.IMatchService;
import com.bol.mancala.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class MatchController {

    @Autowired
    private MatchService matchService;


//
//    public MatchController(MatchService matchService) {
//        this.matchService = matchService;
//    }

//    @PostMapping("/matches")//The function receives a POST request, processes it, creates a new Todo and saves it to the database, and returns a resource link to the created todo.    @PostMapping
//    public ResponseEntity<Match> saveTodo(@RequestBody StartMatchDTO startMatchDTO, HttpServletRequest request) {
//        Match newMatch = this.matchService.startMatch(request.getSession().hashCode());
//        return new ResponseEntity<>(newMatch, HttpStatus.CREATED);
//    }

    // TODO: Look here example of converter
//    @RequestMapping(value = “/{id}”, method = RequestMethod.GET)
//    @ResponseBody
//    public PostDto getPost(@PathVariable(“id”) Long id) {
//        return convertToDto(postService.getPostById(id));
//    }
//    @RequestMapping(value = “/{id}”, method = RequestMethod.PUT)
//    @ResponseStatus(HttpStatus.OK)
//    public void updatePost(@RequestBody PostDto postDto) {
//        Post post = convertToEntity(postDto);
//        postService.updatePost(post);
//    }
//
//    private PostDto convertToDto(Post post) {
//        PostDto postDto = modelMapper.map(post, PostDto.class);
//        postDto.setSubmissionDate(post.getSubmissionDate(),
//                userService.getCurrentUser().getPreference().getTimezone());
//        return postDto;
//    }
//
//    private Post convertToEntity(PostDto postDto) throws ParseException {
//        Post post = modelMapper.map(postDto, Post.class);
//        post.setSubmissionDate(postDto.getSubmissionDateConverted(
//                userService.getCurrentUser().getPreference().getTimezone()));
//
//        if (postDto.getId() != null) {
//            Post oldPost = postService.getPostById(postDto.getId());
//            post.setRedditID(oldPost.getRedditID());
//            post.setSent(oldPost.isSent());
//        }
//        return post;
//    }
//}

}
