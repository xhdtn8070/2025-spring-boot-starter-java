// PostUpdateServiceRequest.java
package org.tikim.sample.domain.board.post.service.application.dto;

public record PostUpdateServiceRequest(Long postId, String title, String content) {}
