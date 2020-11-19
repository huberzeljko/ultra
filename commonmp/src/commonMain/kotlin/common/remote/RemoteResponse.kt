package de.peekandpoke.ultra.common.remote

interface RemoteResponse {
    val request: RemoteRequest
    val body: String
    val status: Int
    val statusText: String
    val ok: Boolean
}