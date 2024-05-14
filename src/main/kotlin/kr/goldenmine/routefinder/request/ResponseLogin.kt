package kr.goldenmine.dowayobackend.auth.models

data class ResponseLogin(
    val accessToken: String,
    val refreshToken: String,
    val nickname: String,
    val isAdmin: Boolean,
) {
}