package mouda.backend.config;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class HeaderCheckController {

	@GetMapping("/headers")
	public String headers(HttpServletRequest request) {
		return Collections.list(request.getHeaderNames()).stream()
			.map(name -> name + ": " + Collections.list(request.getHeaders(name)))
			.collect(Collectors.joining("<br>"));
	}
}
