package mouda.backend.notification.implement.fcm;

import static org.mockito.Mockito.*;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import com.google.firebase.IncomingHttpResponse;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;

import mouda.backend.notification.domain.CommonNotification;
import mouda.backend.notification.domain.NotificationType;
import mouda.backend.notification.infrastructure.repository.FcmTokenRepository;

@ExtendWith(MockitoExtension.class)
class FcmResponseHandlerTest {

	@Mock
	private List<FcmConfigBuilder> fcmConfigBuilders;

	@InjectMocks
	private FcmMessageFactory fcmMessageFactory;

	@Mock
	private ScheduledExecutorService scheduler;

	@InjectMocks
	private FcmResponseHandler fcmResponseHandler;

	private CommonNotification notification;
	private BatchResponse batchResponse;
	private FirebaseMessaging firebaseMessaging;
	private List<SendResponse> sendResponses;

	private SendResponse responseWith500;

	@BeforeEach
	void setUp() {
		notification = CommonNotification.builder()
			.title("Title")
			.body("body")
			.type(NotificationType.MOIM_CREATED)
			.redirectUrl("test")
			.build();
		batchResponse = mock(BatchResponse.class);

		responseWith500 = mock(SendResponse.class);

		FirebaseMessagingException exceptionWith500 = mock(FirebaseMessagingException.class);

		lenient().when(responseWith500.getException()).thenReturn(exceptionWith500);

		lenient().when(exceptionWith500.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INTERNAL);

		Iterator<FcmConfigBuilder> iterator = mock(Iterator.class);
		lenient().when(fcmConfigBuilders.iterator()).thenReturn(iterator);
		lenient().when(iterator.hasNext()).thenReturn(false);

		try (MockedStatic<FirebaseMessaging> firebaseMessagingMockedStatic = Mockito.mockStatic(
			FirebaseMessaging.class)) {
			firebaseMessaging = mock(FirebaseMessaging.class);
			firebaseMessagingMockedStatic.when(FirebaseMessaging::getInstance).thenReturn(firebaseMessaging);
		}
	}

	@DisplayName("400 에러에 대해서는 재시도하지 않는다.")
	@Test
	void handleBatchResponse_NoRetryableTokens() {
		SendResponse responseWith400 = mock(SendResponse.class);
		FirebaseMessagingException exceptionWith400 = mock(FirebaseMessagingException.class);
		when(responseWith400.isSuccessful()).thenReturn(false);
		when(responseWith400.getException()).thenReturn(exceptionWith400);
		when(exceptionWith400.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INVALID_ARGUMENT);

		sendResponses = List.of(responseWith400);
		when(batchResponse.getResponses()).thenReturn(sendResponses);

		fcmResponseHandler.handleBatchResponse(batchResponse, notification, List.of("token_400"));
		verify(scheduler, never()).schedule(any(Runnable.class), anyLong(), any(TimeUnit.class));
	}

	@DisplayName("429 에러에 대해서는 Retry-After 헤더를 확인한 뒤 재시도한다.")
	@Test
	void handleBatchResponse_RetryWith429Tokens() throws FirebaseMessagingException {
		SendResponse responseWith429 = mock(SendResponse.class);
		FirebaseMessagingException exceptionWith429 = mock(FirebaseMessagingException.class);
		when(responseWith429.isSuccessful()).thenReturn(false);
		when(responseWith429.getException()).thenReturn(exceptionWith429);
		when(exceptionWith429.getMessagingErrorCode()).thenReturn(MessagingErrorCode.QUOTA_EXCEEDED);

		IncomingHttpResponse httpResponseWith429 = mock(IncomingHttpResponse.class);
		when(exceptionWith429.getHttpResponse()).thenReturn(httpResponseWith429);
		when(httpResponseWith429.getHeaders()).thenReturn(Map.of("Retry-After", 5));

		sendResponses = List.of(responseWith429);
		when(batchResponse.getResponses()).thenReturn(sendResponses);

		Iterator<String> tokenIterator = mock(Iterator.class);
		when(tokenIterator.hasNext()).thenReturn(true, false);
		when(tokenIterator.next()).thenReturn("token_429");

		List<String> tokens = mock(List.class);
		when(tokens.size()).thenReturn(1);
		when(tokens.iterator()).thenReturn(tokenIterator);

		List<MulticastMessage> messages = List.of(mock(MulticastMessage.class));
		when(fcmMessageFactory.createMessage(notification, tokens)).thenReturn(messages);
		when(firebaseMessaging.sendEachForMulticast(any())).thenReturn(batchResponse);


		fcmResponseHandler.handleBatchResponse(batchResponse, notification, tokens);
		verify(scheduler, atLeastOnce()).schedule(any(Runnable.class), eq(5), eq(TimeUnit.SECONDS));
	}

	//
	// @Test
	// void testHandleBatchResponse_RetryWith429Tokens() {
	// 	when(FcmFailedResponse.from(batchResponse, initialTokens)).thenReturn(failedResponse);
	// 	when(failedResponse.hasNoRetryableTokens()).thenReturn(false);
	// 	when(failedResponse.hasFailedWith429Tokens()).thenReturn(true);
	// 	when(failedResponse.getRetryAfterSeconds()).thenReturn(5);
	//
	// 	// 스케줄러 호출 시 사용하는 Runnable을 캡처합니다.
	// 	ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);
	//
	// 	fcmResponseHandler.handleBatchResponse(batchResponse, notification, initialTokens);
	//
	// 	// 스케줄러가 제대로 호출되었는지 확인합니다.
	// 	verify(scheduler).schedule(runnableCaptor.capture(), eq(5L), eq(TimeUnit.SECONDS));
	//
	// 	// 캡처된 Runnable을 실행하여 내부 로직을 테스트할 수 있습니다.
	// 	Runnable scheduledTask = runnableCaptor.getValue();
	// 	scheduledTask.run();
	//
	// 	// 메시지 생성 및 재시도가 호출되었는지 확인합니다.
	// 	verify(fcmMessageFactory, times(1)).createMessage(notification, failedResponse.getNonRetryableFailedTokens());
	// }
}
