package com.example.demo;

import com.windowsazure.messaging.NotificationHub;
import org.apache.hc.core5.http.Method;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

public class CustomNotificationHub extends NotificationHub {

    public enum Platform {
        Fcm, Apns
    }

    private final String endpoint;
    private final String hubPath;

    private final static String API_VERSION = "2020-06";

    /**
     * Creates a new instance of the NotificationHub class with connection string and hub path.
     *
     * @param connectionString The connection string from the Azure Notification Hub access policies.
     * @param hubPath          The name of the Azure Notification Hub name.
     */
    public CustomNotificationHub(String connectionString, String hubPath) {
        super(connectionString, hubPath);
        this.endpoint = parseConnectionStringForEndpoint(connectionString);
        this.hubPath = hubPath;
    }

    /**
     * プッシュメッセージを送信する
     * TODO: APNS向けかFCM向けのメッセージかを振り分けできていない。あとで改善版を出す予定。
     *
     * @param body     メッセージJSON
     * @param platform プラットフォーム(利用予定)
     * @return 正常なレスポンスのステータスかどうか
     */
    public boolean send(String body, Platform platform) {
        var uri = createNotificationURI();
        var entity = createHttpEntity(uri, body);

        var template = new RestTemplate();
        var response = template.exchange(uri, HttpMethod.POST, entity, String.class);
        return HttpStatus.CREATED == response.getStatusCode();
    }

    /**
     * HTTPエンティティの作成を行う
     *
     * @param uri  URI
     * @param body 送信JSON文字列
     * @return HTTPエンティティ
     */
    protected HttpEntity<String> createHttpEntity(URI uri, String body) {
        var post = createRequest(uri, Method.POST).build();

        var headers = new HttpHeaders();
        for (var header : post.getHeaders()) {
            headers.add(header.getName(), header.getValue());
        }

        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    /**
     * プッシュ送信先のエンドポイントを作成する
     *
     * @return エンドポんとのURL
     */
    protected URI createNotificationURI() {
        try {
            return new URI(this.endpoint + hubPath + "/messages" + "?api-version=" + API_VERSION);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * コネクションストリングからエンドポイントへパースする
     *
     * @param connectionString コネクションストリング
     * @return エンドポイント
     */
    protected String parseConnectionStringForEndpoint(String connectionString) {
        String[] parts = connectionString.split(";");
        for (String part : parts) {
            if (part.startsWith("Endpoint")) {
                return "https" + part.substring(11);
            }
        }
        throw new RuntimeException("");
    }

}
