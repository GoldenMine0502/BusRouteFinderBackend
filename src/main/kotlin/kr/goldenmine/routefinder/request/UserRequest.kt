package kr.goldenmine.routefinder.request

class UserRequest(
    val id: Int,
    val accountId: String,
    var password: String,
    val nickname: String,
    val isAdmin: Boolean
) {
}