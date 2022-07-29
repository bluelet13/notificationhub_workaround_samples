package com.example.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo")
public class DemoController {

    @GetMapping()
    public ResponseEntity<?> send() {
        var hub = new CustomNotificationHub(getConnectionString(), getHubPath());
        var isSuccess = hub.send(getMessage(), CustomNotificationHub.Platform.Fcm);
        return ResponseEntity.ok(isSuccess);
    }

    private String getConnectionString() {
        return "Endpoint=sb://praisecard-dev-nhn.servicebus.windows.net/;SharedAccessKeyName=DefaultFullSharedAccessSignature;SharedAccessKey=irfWkS15NKdnKASbv46yb0OeKp9LN/XZDE4khC0RTR8=";
    }

    private String getHubPath() {
        return "praisecard-dev";
    }

    private String getMessage() {
        return "{\"data\":{\"msg\":\"Hello from Java!\"}}";
    }
}
