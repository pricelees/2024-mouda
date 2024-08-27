package mouda.backend.config;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HeaderCheckController {

	@GetMapping("/v1/headers")
	public String headers(HttpServletRequest request) {
		return Collections.list(request.getHeaderNames()).stream()
			.map(name -> name + ": " + Collections.list(request.getHeaders(name)))
			.collect(Collectors.joining("<br>"));
	}

	@GetMapping("/v1/headers/froxy")
	public String froxyHeaders(HttpServletRequest request) {
		String scheme = request.getScheme();
		String host = request.getServerName();
		int port = request.getServerPort();

		return String.format("scheme: %s<br>", scheme)
			+ String.format("host: %s<br>", host)
			+ String.format("port: %d<br>", port);
	}
}
