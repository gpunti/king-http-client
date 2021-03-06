// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http.netty.requestbuilder;


import com.king.platform.net.http.*;
import com.king.platform.net.http.netty.NettyHttpClient;
import com.king.platform.net.http.netty.ServerInfo;
import com.king.platform.net.http.netty.request.HttpBody;
import com.king.platform.net.http.netty.request.NettyHttpClientRequest;
import com.king.platform.net.http.netty.util.UriUtil;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;

import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class BuiltNettyClientRequest implements BuiltClientRequest {

	private final NettyHttpClient nettyHttpClient;
	private final HttpVersion httpVersion;
	private final HttpMethod httpMethod;
	private final String uri;

	private final String defaultUserAgent;


	private final int idleTimeoutMillis;
	private final int totalRequestTimeoutMillis;

	private final boolean followRedirects;
	private final boolean acceptCompressedResponse;
	private final boolean keepAlive;

	private final RequestBodyBuilder requestBodyBuilder;
	private final String contentType;
	private final Charset bodyCharset;

	private final List<Param> queryParameters;
	private final List<Param> headerParameters;

	public BuiltNettyClientRequest(NettyHttpClient nettyHttpClient, HttpVersion httpVersion, HttpMethod httpMethod, String uri, String defaultUserAgent, int idleTimeoutMillis, int totalRequestTimeoutMillis, boolean followRedirects, boolean acceptCompressedResponse, boolean keepAlive, RequestBodyBuilder requestBodyBuilder, String contentType, Charset bodyCharset, List<Param> queryParameters, List<Param> headerParameters) {
		this.nettyHttpClient = nettyHttpClient;
		this.httpVersion = httpVersion;
		this.httpMethod = httpMethod;
		this.uri = uri;
		this.defaultUserAgent = defaultUserAgent;
		this.idleTimeoutMillis = idleTimeoutMillis;
		this.totalRequestTimeoutMillis = totalRequestTimeoutMillis;
		this.followRedirects = followRedirects;
		this.acceptCompressedResponse = acceptCompressedResponse;
		this.keepAlive = keepAlive;
		this.requestBodyBuilder = requestBodyBuilder;
		this.contentType = contentType;
		this.bodyCharset = bodyCharset;
		this.queryParameters = new ArrayList<>(queryParameters);
		this.headerParameters = new ArrayList<>(headerParameters);
	}

	@Override
	public Future<FutureResult<String>> execute() {
		return internalExecute(null, new StringResponseBody(), null);
	}

	@Override
	public Future<FutureResult<String>> execute(HttpCallback<String> httpCallback) {
		return internalExecute(httpCallback, new StringResponseBody(), null);
	}

	@Override
	public Future<FutureResult<String>> execute(HttpCallback<String> httpCallback, NioCallback nioCallback) {
		return internalExecute(httpCallback, new StringResponseBody(), nioCallback);
	}

	@Override
	public <T> Future<FutureResult<T>> execute(HttpCallback<T> httpCallback, ResponseBodyConsumer<T> responseBodyConsumer) {
		return internalExecute(httpCallback, responseBodyConsumer, null);
	}

	@Override
	public Future<FutureResult<String>> execute(NioCallback nioCallback) {
		return internalExecute(null, null, nioCallback);
	}

	@Override
	public <T> Future<FutureResult<T>> execute(HttpCallback<T> httpCallback, ResponseBodyConsumer<T> responseBodyConsumer, NioCallback nioCallback) {
		return internalExecute(httpCallback, responseBodyConsumer, nioCallback);
	}

	@Override
	public <T> Future<FutureResult<T>> execute(ResponseBodyConsumer<T> responseBodyConsumer) {
		return internalExecute(null, responseBodyConsumer, null);
	}

	private <T> Future<FutureResult<T>> internalExecute(HttpCallback<T> httpCallback, ResponseBodyConsumer<T> responseBodyConsumer, NioCallback nioCallback) {
		String completeUri = UriUtil.getUriWithParameters(uri, queryParameters);

		ServerInfo serverInfo = null;
		try {
			serverInfo = ServerInfo.buildFromUri(completeUri);
		} catch (URISyntaxException e) {
			return nettyHttpClient.dispatchError(httpCallback, e);
		}

		String relativePath = UriUtil.getRelativeUri(completeUri);


		DefaultHttpRequest defaultHttpRequest = new DefaultHttpRequest(httpVersion, httpMethod, relativePath);

		HttpBody httpBody = null;

		if (requestBodyBuilder != null) {
			httpBody = requestBodyBuilder.createHttpBody(contentType, bodyCharset, serverInfo.isSecure());
		}

		NettyHttpClientRequest<T> nettyHttpClientRequest = new NettyHttpClientRequest<>(serverInfo, defaultHttpRequest, httpBody);

		HttpHeaders headers = nettyHttpClientRequest.getNettyHeaders();

		for (Param headerParameter : headerParameters) {
			headers.add(headerParameter.getName(), headerParameter.getValue());
		}


		if (acceptCompressedResponse && !headers.contains(HttpHeaders.Names.ACCEPT_ENCODING)) {
			headers.set(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP + "," + HttpHeaders.Values.DEFLATE);
		}

		if (httpBody != null) {
			if (httpBody.getContentLength() < 0) {
				headers.set(HttpHeaders.Names.TRANSFER_ENCODING, HttpHeaders.Values.CHUNKED);
			} else {
				headers.set(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(httpBody.getContentLength()));
			}

			if (httpBody.getContentType() != null) {
				headers.set(HttpHeaders.Names.CONTENT_TYPE, httpBody.getContentType());
			}
		}


		if (!headers.contains(HttpHeaders.Names.ACCEPT)) {
			headers.set(HttpHeaders.Names.ACCEPT, "*/*");
		}

		if (!headers.contains(HttpHeaders.Names.USER_AGENT)) {
			headers.set(HttpHeaders.Names.USER_AGENT, defaultUserAgent);
		}

		if (serverInfo.getPort() == 80 || serverInfo.getPort() == 443) {	//Don't write the ports for default ports: Host = "Host" ":" host [ ":" port ] ;
			headers.set(HttpHeaders.Names.HOST, serverInfo.getHost());
		} else {
			headers.set(HttpHeaders.Names.HOST, serverInfo.getHost() + ":" + serverInfo.getPort());
		}


		nettyHttpClientRequest.setKeepAlive(keepAlive);

		return nettyHttpClient.execute(nettyHttpClientRequest, httpCallback, nioCallback, responseBodyConsumer, idleTimeoutMillis, totalRequestTimeoutMillis,
			followRedirects, keepAlive);
	}
}
