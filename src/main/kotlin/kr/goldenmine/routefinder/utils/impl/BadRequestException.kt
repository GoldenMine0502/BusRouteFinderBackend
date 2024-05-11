package kr.goldenmine.dowayobackend.util.impl

class BadRequestException(
    override val message: String
): RuntimeException(message) {
}