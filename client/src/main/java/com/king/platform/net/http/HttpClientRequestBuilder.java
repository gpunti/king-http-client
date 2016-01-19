// Copyright (C) king.com Ltd 2015
// https://github.com/king/king-http-client
// Author: Magnus Gustafsson
// License: Apache 2.0, https://raw.github.com/king/king-http-client/LICENSE-APACHE

package com.king.platform.net.http;


public interface HttpClientRequestBuilder extends HttpClientRequestHeaderBuilder<HttpClientRequestBuilder> {

	/**
	 * Build the request
	 * @return the built request
	 */
	BuiltClientRequest build();


}
