package com.todeal.domain.invite.entity

import jakarta.persistence.*

@Entity
@Table(name = "invites")
data class InviteEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val inviterId: Long,

    @Column(nullable = false)
    val inviteeEmail: String
)
