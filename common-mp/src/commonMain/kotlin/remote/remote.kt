package de.peekandpoke.common.remote

expect fun remote(
    baseUrl: String,
    requestInterceptors: List<RequestInterceptor>,
    responseInterceptors: List<ResponseInterceptor>
): RemoteRequest
