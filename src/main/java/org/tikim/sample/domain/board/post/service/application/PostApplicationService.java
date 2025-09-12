// PostApplicationService.java
package org.tikim.sample.domain.board.post.service.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.tikim.sample.domain.board.post.service.application.dto.*;
import org.tikim.sample.domain.board.post.service.domain.PostDomainService;
import org.tikim.sample.domain.board.post.service.domain.dto.*;

@Service
@RequiredArgsConstructor
public class PostApplicationService {

    private final PostDomainService postDomainService;

    @Transactional
    public void create(PostCreateServiceRequest req) {
        postDomainService.create(new PostCreateDomainRequest(req.title(), req.content()));
    }

    @Transactional
    public void update(PostUpdateServiceRequest req) {
        postDomainService.update(
            req.postId(),
            new PostUpdateDomainRequest(req.title(), req.content())
        );
    }

    @Transactional
    public void delete(Long postId) {
        postDomainService.delete(postId);
    }

    @Transactional(readOnly = true)
    public PostDetailServiceResponse get(Long postId) {
        PostDetailDomainResponse d = postDomainService.get(postId);
        return PostDetailServiceResponse.of(d);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryServiceResponse> search(String keyword, Pageable pageable) {
        Page<PostSearchDomainResponse> page = postDomainService.search(
            new PostSearchDomainRequest(keyword, pageable)
        );
        return page.map(PostSummaryServiceResponse::of);

    }
}
