package com.todeal.domain.invite.controller

import com.todeal.domain.invite.dto.InviteDto
import com.todeal.domain.invite.dto.InviteResponse
import com.todeal.domain.invite.service.InviteService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/invites")
class InviteController(
    private val inviteService: InviteService
) {

    @PostMapping
    fun create(@RequestBody request: InviteDto): InviteResponse {
        return inviteService.create(request)
    }

    @GetMapping
    fun getAll(): List<InviteResponse> {
        return inviteService.getAll()
    }
}
