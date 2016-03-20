package io.github.phantamanta44.tiabot.util.http;

import java.io.IOException;
import java.net.URI;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import io.github.phantamanta44.tiabot.util.MathUtils;

public class HttpUtils {

	private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36";
	private static final CloseableHttpClient HTTP_CLI = HttpClients.custom()
			.setUserAgent(USER_AGENT)
			.build();
	
	public static String requestXml(String uri, String... headers) throws HttpException, ClientProtocolException, IOException {
		return requestXml(URI.create(uri.replaceAll("\\s", "+")), headers);
	}
	
	public static String requestXml(URI uri, String... headers) throws HttpException, ClientProtocolException, IOException {
		HttpUriRequest req = new HttpGet(uri);
		if (headers.length % 2 != 0)
			throw new IllegalArgumentException("Headers must come in name-value pairs!");
		for (int i = 0; i < headers.length; i+= 2)
			req.addHeader(headers[i], headers[i + 1]);
		try (CloseableHttpResponse resp = HTTP_CLI.execute(req)) {
			int status = resp.getStatusLine().getStatusCode();
			if (MathUtils.bounds(status, 400, 600))
				throw new HttpException(status);
			return EntityUtils.toString(resp.getEntity());
		}
	}
	
}
