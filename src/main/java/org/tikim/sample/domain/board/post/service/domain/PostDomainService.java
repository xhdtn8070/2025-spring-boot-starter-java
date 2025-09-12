// PostDomainService.java
package org.tikim.sample.domain.board.post.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tikim.sample.domain.board.post.entity.Post;
import org.tikim.sample.domain.board.post.exception.PostException;
import org.tikim.sample.domain.board.post.repository.PostDslRepository;
import org.tikim.sample.domain.board.post.repository.PostRepository;
import org.tikim.sample.domain.board.post.service.domain.dto.*;
import org.tikim.sample.domain.board.post.service.domain.dto.PostDetailDomainResponse;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;
import org.tikim.sample.global.vaildation.util.ValidationUtil;

@Service
@RequiredArgsConstructor
public class PostDomainService {

    private final PostRepository postRepository;
    private final PostDslRepository postDslRepository;

    @Transactional
    public void create(PostCreateDomainRequest req) {
        ValidationUtil.validate(req);
        Post p = Post.of(req);
        postRepository.save(p);
    }

    @Transactional
    public void update(Long postId, PostUpdateDomainRequest req) {
        ValidationUtil.validate(req);
        Post p = postRepository.findById(postId)
            .orElseThrow(() -> new PostException(ErrorMessage.POST_NOT_EXIST, CriticalLevel.NON_CRITICAL));
        p.update(req);
        postRepository.save(p);
    }

    @Transactional
    public void delete(Long postId) {
        Post p = postRepository.findById(postId)
            .orElseThrow(() -> new PostException(ErrorMessage.POST_NOT_EXIST, CriticalLevel.NON_CRITICAL));
        postRepository.delete(p);
    }

    // PostDomainService.java
    @Transactional(readOnly = true)
    public PostDetailDomainResponse get(Long postId) {
        if (postId == null) {
            throw new PostException(ErrorMessage.PARAMETER_IS_NOT_CORRECT, CriticalLevel.NON_CRITICAL);
        }
        Post p = postRepository.findById(postId)
            .orElseThrow(() -> new PostException(ErrorMessage.POST_NOT_EXIST, CriticalLevel.NON_CRITICAL));
        return PostDetailDomainResponse.of(p);
    }

    @Transactional(readOnly = true)
    public Page<PostSearchDomainResponse> search(PostSearchDomainRequest req) {
        ValidationUtil.validate(req);
        return postDslRepository.search(req.keyword(), req.pageable())
            .map(PostSearchDomainResponse::of);
    }

}
