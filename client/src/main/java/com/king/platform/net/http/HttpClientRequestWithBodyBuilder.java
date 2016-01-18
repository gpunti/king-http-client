// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http;


import com.king.platform.net.http.netty.request.HttpBody;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Map;

public interface HttpClientRequestWithBodyBuilder extends HttpClientRequestBuilder {

	HttpClientRequestWithBodyBuilder withHeader(String name, String value);

	HttpClientRequestWithBodyBuilder keepAlive(boolean keepAlive);

	HttpClientRequestWithBodyBuilder acceptCompressedResponse(boolean acceptCompressedResponse);

	HttpClientRequestWithBodyBuilder withQueryParameter(String name, String value);

	HttpClientRequestWithBodyBuilder idleTimeoutMillis(int readTimeoutMillis);

	HttpClientRequestWithBodyBuilder totalRequestTimeoutMillis(int requestTimeoutMillis);

	HttpClientRequestWithBodyBuilder followRedirects(boolean followRedirects);

	HttpClientRequestWithBodyBuilder contentType(String contentType);

	HttpClientRequestWithBodyBuilder content(byte[] content);

	HttpClientRequestWithBodyBuilder content(File file);

	HttpClientRequestWithBodyBuilder content(HttpBody httpBody);

	HttpClientRequestWithBodyBuilder content(InputStream inputStream);

	HttpClientRequestWithBodyBuilder bodyCharset(Charset charset);

	HttpClientRequestWithBodyBuilder addFormParameter(String name, String value);

	HttpClientRequestWithBodyBuilder addFormParameters(Map<String, String> parameters);


}