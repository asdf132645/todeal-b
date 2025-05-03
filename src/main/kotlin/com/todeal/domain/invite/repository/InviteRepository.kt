package com.todeal.domain.invite.repository

import com.todeal.domain.invite.entity.InviteEntity
import org.springframework.data.jpa.repository.JpaRepository

interface InviteRepository : JpaRepository<InviteEntity, Long>
