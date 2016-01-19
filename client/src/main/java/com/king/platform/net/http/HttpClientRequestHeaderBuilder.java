// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http;


public interface HttpClientRequestHeaderBuilder<T extends HttpClientRequestHeaderBuilder> {

	/**
	 * Add custom header to the request
	 * @param name Header name
	 * @param value Header value
	 * @return the builder
	 */
	T withHeader(String name, String value);

	/**
	 * Set if the connection should be kept alive or not.<br>
	 * If keepAlive is false, header "Connection" is set to false. <br>
	 * Overrides {@link ConfKeys#KEEP_ALIVE}
	 * @param keepAlive true if the connection should be kept alive against the server
	 * @return the builder
	 */
	T keepAlive(boolean keepAlive);

	/**
	 * Set if the client should accept compressed responses from the server <br>
	 * Overrides {@link ConfKeys#ACCEPT_COMPRESSED_RESPONSE}
	 * @param acceptCompressedResponse true if the client accepts compressed responses
	 * @return the builder
	 */
	T acceptCompressedResponse(boolean acceptCompressedResponse);

	/**
	 * Set the idle timeout in milliseconds <br>
	 * Overrides {@link ConfKeys#IDLE_TIMEOUT_MILLIS}
	 * @param idleTimeoutMillis the idle timeout in milliseconds
	 * @return the builder
	 */
	T idleTimeoutMillis(int idleTimeoutMillis);

	/**
	 * Set the total request timeout in milliseconds <br>
	 * Overrides {@link ConfKeys#TOTAL_REQUEST_TIMEOUT_MILLIS}
	 * @param totalRequestTimeoutMillis the total request timeout in milliseconds
	 * @return the builder
	 */
	T totalRequestTimeoutMillis(int totalRequestTimeoutMillis);

	/**
	 * Set if the request should automatically follow redirects (up to 5 redirects). <br>
	 * Overrides {@link ConfKeys#HTTP_FOLLOW_REDIRECTS}
	 * @param followRedirects if the request should follow redirects
	 * @return the builder
	 */
	T followRedirects(boolean followRedirects);


	/**
	 * Add one or more query parameter to the current uri.
	 * @param name name of the query parameter
	 * @param value value of the query parameter
	 * @return the builder
	 */
	T withQueryParameter(String name, String value);

}
