package mouda.backend.chat.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mouda.backend.chat.dto.request.ChatCreateRequest;
import mouda.backend.chat.service.ChatService;
import mouda.backend.member.domain.Member;

@RestController
@RequestMapping("/v1/chat")
@RequiredArgsConstructor
public class ChatController {

	private final ChatService chatService;

	@PostMapping
	public ResponseEntity<Void> createChat(
		@RequestBody ChatCreateRequest chatCreateRequest,
		Member member
	) {
		chatService.createChat(chatCreateRequest, member);
		return ResponseEntity.ok().build();
	}
}
