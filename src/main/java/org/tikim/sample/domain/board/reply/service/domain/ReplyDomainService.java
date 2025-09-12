// ReplyDomainService.java
package org.tikim.sample.domain.board.reply.service.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tikim.sample.domain.board.post.entity.Post;
import org.tikim.sample.domain.board.post.repository.PostRepository;
import org.tikim.sample.domain.board.reply.entity.Reply;
import org.tikim.sample.domain.board.reply.exception.ReplyException;
import org.tikim.sample.domain.board.reply.repository.ReplyDslRepository;
import org.tikim.sample.domain.board.reply.repository.ReplyRepository;
import org.tikim.sample.domain.board.reply.service.domain.dto.*;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;
import org.tikim.sample.global.vaildation.util.ValidationUtil;

@Service
@RequiredArgsConstructor
public class ReplyDomainService {

    private final ReplyRepository replyRepository;
    private final PostRepository postRepository;
    private final ReplyDslRepository replyDslRepository;

    @Transactional
    public void create(ReplyCreateDomainRequest req) {
        ValidationUtil.validate(req);
        Post post = postRepository.findById(req.postId())
            .orElseThrow(() -> new ReplyException(ErrorMessage.REPLY_NOT_EXIST, CriticalLevel.NON_CRITICAL));
        Reply reply = Reply.of(post, req);
        replyRepository.save(reply);
    }

    @Transactional
    public void update(Long replyId, ReplyUpdateDomainRequest req) {
        ValidationUtil.validate(req);
        Reply r = replyRepository.findById(replyId)
            .orElseThrow(() -> new ReplyException(ErrorMessage.REPLY_NOT_EXIST, CriticalLevel.NON_CRITICAL));
        r.update(req); // 필요 시 Reply 엔티티에 해당 시그니처 추가
        replyRepository.save(r);
    }

    @Transactional
    public void delete(Long replyId) {
        Reply r = replyRepository.findById(replyId)
            .orElseThrow(() -> new ReplyException(ErrorMessage.REPLY_NOT_EXIST, CriticalLevel.NON_CRITICAL));
        replyRepository.delete(r);
    }

    @Transactional(readOnly = true)
    public ReplyDetailDomainResponse get(Long replyId) {
        Reply r = replyRepository.findById(replyId)
            .orElseThrow(() -> new ReplyException(ErrorMessage.REPLY_NOT_EXIST, CriticalLevel.NON_CRITICAL));
        return ReplyDetailDomainResponse.of(r);
    }

    @Transactional(readOnly = true)
    public Page<ReplyDomainResponse> search(ReplySearchDomainRequest req) {
        ValidationUtil.validate(req);
        return replyDslRepository.findPageByPostId(req.postId(), req.pageable())
            .map(s -> new ReplyDomainResponse(s.id(), s.postId(), s.content(), s.createdAt()));
    }
}
