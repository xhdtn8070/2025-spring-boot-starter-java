// ReplyApplicationService.java
package org.tikim.sample.domain.board.reply.service.application;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tikim.sample.domain.board.reply.exception.ReplyException;
import org.tikim.sample.domain.board.reply.service.application.dto.*;
import org.tikim.sample.domain.board.reply.service.domain.ReplyDomainService;
import org.tikim.sample.domain.board.reply.service.domain.dto.*;
import org.tikim.sample.global.exception.enums.CriticalLevel;
import org.tikim.sample.global.exception.enums.ErrorMessage;

@Service
@RequiredArgsConstructor
public class ReplyApplicationService {

    private final ReplyDomainService replyDomainService;

    @Transactional
    public void create(Long userId, ReplyCreateServiceRequest req) {
        replyDomainService.create(new ReplyCreateDomainRequest(userId, req.postId(), req.content()));
    }

    @Transactional
    public void update(Long userId, ReplyUpdateServiceRequest req) {
        ReplyDetailDomainResponse d = replyDomainService.get(req.replyId());
        if (!d.isWriter(userId)) {
            throw new ReplyException(ErrorMessage.WRITER_NOT_MATCH, CriticalLevel.NON_CRITICAL);
        }

        replyDomainService.update(
            req.replyId(),
            new ReplyUpdateDomainRequest(req.content())
        );
    }

    @Transactional
    public void delete(Long replyId) {
        replyDomainService.delete(replyId);
    }

    @Transactional(readOnly = true)
    public ReplyDetailServiceResponse get(Long replyId) {
        ReplyDetailDomainResponse d = replyDomainService.get(replyId);
        return ReplyDetailServiceResponse.of(d);
    }

    @Transactional(readOnly = true)
    public Page<ReplyServiceResponse> search(Long postId, Pageable pageable) {
        Page<ReplyDomainResponse> page = replyDomainService.search(
            new ReplySearchDomainRequest(postId, pageable)
        );
        return page.map(ReplyServiceResponse::of);
    }
}
