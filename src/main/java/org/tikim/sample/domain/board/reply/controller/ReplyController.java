// ReplyController.java
package org.tikim.sample.domain.board.reply.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.tikim.sample.domain.board.reply.controller.dto.request.ReplyCreateControllerRequest;
import org.tikim.sample.domain.board.reply.controller.dto.request.ReplyUpdateControllerRequest;
import org.tikim.sample.domain.board.reply.controller.dto.response.ReplyControllerResponse;
import org.tikim.sample.domain.board.reply.service.application.ReplyApplicationService;
import org.tikim.sample.domain.board.reply.service.application.dto.*;
import org.tikim.sample.global.auth.dto.LoggedInUser;
import org.tikim.sample.global.response.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class ReplyController {

    private final ReplyApplicationService replyAppService;

    // Create
    @PostMapping("/posts/{postId}/replies")
    public ResponseEntity<ApiResponse<Void>> create(
        @LoggedInUser Long userId,
        @PathVariable Long postId,
        @RequestBody @Valid ReplyCreateControllerRequest req
    ) {
        replyAppService.create(userId, new ReplyCreateServiceRequest(postId, req.content()));
        return ApiResponse.toResponseEntity(ApiResponse.success(HttpStatus.CREATED));
    }

    // Update
    @PutMapping("/replies/{replyId}")
    public ResponseEntity<ApiResponse<Void>> update(
        @LoggedInUser Long userId,
        @PathVariable Long replyId,
        @RequestBody @Valid ReplyUpdateControllerRequest req
    ) {
        replyAppService.update(userId, new ReplyUpdateServiceRequest(replyId, req.content()));
        return ApiResponse.toResponseEntity(ApiResponse.success(HttpStatus.OK));
    }

    // Delete
    @DeleteMapping("/replies/{replyId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long replyId) {
        replyAppService.delete(replyId);
        return ApiResponse.toResponseEntity(ApiResponse.success(HttpStatus.NO_CONTENT));
    }

    // List(Search) by Post
    @GetMapping("/posts/{postId}/replies")
    public ResponseEntity<ApiResponse<Page<ReplyControllerResponse>>> list(
        @PathVariable Long postId,
        Pageable pageable
    ) {
        Page<ReplyServiceResponse> page = replyAppService.search(postId, pageable);
        Page<ReplyControllerResponse> mapped = page.map(r ->
            new ReplyControllerResponse(r.id(), r.postId(), r.content(), r.createdAt())
        );
        return ApiResponse.toResponseEntity(ApiResponse.success(mapped, HttpStatus.OK));
    }

    // Detail (single)
    @GetMapping("/replies/{replyId}")
    public ResponseEntity<ApiResponse<ReplyControllerResponse>> detail(@PathVariable Long replyId) {
        ReplyDetailServiceResponse s = replyAppService.get(replyId);
        ReplyControllerResponse body =
            new ReplyControllerResponse(s.id(), s.postId(), s.content(), s.createdAt());
        return ApiResponse.toResponseEntity(ApiResponse.success(body, HttpStatus.OK));
    }
}
