package kr.goldenmine.routefinder.model

import jakarta.persistence.*
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.NoArgsConstructor
import java.time.Instant


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "token")
    val token: String,

    @Column(name = "expire")
    val expire: Instant,

    @Column(name = "userId")
    val userId: Int,
    ) {

}