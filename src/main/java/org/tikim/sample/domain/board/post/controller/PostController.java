// PostController.java
package org.tikim.sample.domain.board.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tikim.sample.domain.board.post.controller.dto.request.PostCreateControllerRequest;
import org.tikim.sample.domain.board.post.controller.dto.request.PostUpdateControllerRequest;
import org.tikim.sample.domain.board.post.controller.dto.response.PostDetailControllerResponse;
import org.tikim.sample.domain.board.post.controller.dto.response.PostSummaryControllerResponse;
import org.tikim.sample.domain.board.post.service.application.PostApplicationService;
import org.tikim.sample.domain.board.post.service.application.dto.*;
import org.tikim.sample.global.response.dto.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {

    private final PostApplicationService postAppService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> create(@RequestBody @Valid PostCreateControllerRequest req) {
        postAppService.create(new PostCreateServiceRequest(req.title(), req.content()));
        // NOTE: ApiResponse.success(data, status)는 현재 status를 반영하지 않고 200을 고정 사용합니다.
        return ApiResponse.toResponseEntity(ApiResponse.success(HttpStatus.CREATED));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> update(
        @PathVariable Long postId,
        @RequestBody @Valid PostUpdateControllerRequest req
    ) {
        postAppService.update(new PostUpdateServiceRequest(postId, req.title(), req.content()));
        return ApiResponse.toResponseEntity(ApiResponse.success(HttpStatus.OK));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long postId) {
        postAppService.delete(postId);
        return ApiResponse.toResponseEntity(ApiResponse.success(HttpStatus.NO_CONTENT));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostSummaryControllerResponse>>> search(
        @RequestParam(required = false) String keyword,
        Pageable pageable
    ) {
        Page<PostSummaryServiceResponse> page = postAppService.search(keyword, pageable);

        // 서비스 요약 응답: (id, title, createdAt, replyCount) 기준
        Page<PostSummaryControllerResponse> mapped = page.map(s ->
            new PostSummaryControllerResponse(
                s.id(),
                s.title(),
                s.createdAt(),
                s.updatedAt(),
                s.replyCount()
            )
        );

        return ApiResponse.toResponseEntity(ApiResponse.success(mapped, HttpStatus.OK));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailControllerResponse>> detail(@PathVariable Long postId) {
        PostDetailServiceResponse s = postAppService.get(postId);
        PostDetailControllerResponse body = new PostDetailControllerResponse(
            s.id(), s.title(), s.content(), s.createdAt(), s.updatedAt()
        );
        return ApiResponse.toResponseEntity(ApiResponse.success(body, HttpStatus.OK));
    }
}
