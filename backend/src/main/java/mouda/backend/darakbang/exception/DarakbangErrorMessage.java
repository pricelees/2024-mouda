package mouda.backend.darakbang.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DarakbangErrorMessage {

	NAME_ALREADY_EXIST("이미 존재하는 다락방 이름입니다."),
	NAME_NOT_EXIST("다락방 이름이 존재하지 않습니다."),
	CODE_NOT_EXIST("다락방 초대 코드가 존재하지 않습니다."),
	CODE_ALREADY_EXIST("이미 존재하는 초대코드입니다");

	private final String message;
}
