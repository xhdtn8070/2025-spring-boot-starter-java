// PostApplicationService.java
package org.tikim.sample.domain.board.post.service.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.tikim.sample.domain.board.post.exception.PostException;
import org.tikim.sample.domain.board.post.service.application.dto.*;
import org.tikim.sample.domain.board.post.service.domain.PostDomainService;
import org.tikim.sample.domain.board.post.service.domain.dto.*;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;

@Service
@RequiredArgsConstructor
public class PostApplicationService {

    private final PostDomainService postDomainService;

    @Transactional
    public void create(Long userId, PostCreateServiceRequest req) {
        postDomainService.create(new PostCreateDomainRequest(userId, req.title(), req.content()));
    }

    @Transactional
    public void update(Long userId, PostUpdateServiceRequest req) {
        PostDetailDomainResponse d = postDomainService.get(req.postId());
        if (!d.isWriter(userId)) {
            throw new PostException(ErrorMessage.WRITER_NOT_MATCH, CriticalLevel.NON_CRITICAL);
        }
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
