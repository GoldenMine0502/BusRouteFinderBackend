package kr.goldenmine.dowayobackend.util.impl

class ConflictException(
    override val message: String
): RuntimeException(message) {
}