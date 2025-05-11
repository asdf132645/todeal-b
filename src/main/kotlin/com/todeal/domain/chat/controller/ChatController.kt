package com.todeal.domain.chat.controller

import com.todeal.domain.chat.dto.*
import com.todeal.domain.chat.service.ChatService
import com.todeal.global.response.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping("/room")
    fun createRoom(@RequestBody dto: ChatRoomDto): ApiResponse<ChatRoomResponse> {
        return ApiResponse.success(chatService.createChatRoom(dto))
    }

    @GetMapping("/room")
    fun getRooms(@RequestHeader("X-USER-ID") userId: Long): ApiResponse<List<ChatRoomResponse>> {
        return ApiResponse.success(chatService.getUserChatRooms(userId))
    }

    @GetMapping("/messages")
    fun getMessages(
        @RequestParam chatRoomId: Long,
        @RequestParam(required = false) lastMessageId: Long?,
        @RequestParam(defaultValue = "30") limit: Int
    ): ApiResponse<List<ChatMessageResponse>> {
        return ApiResponse.success(chatService.getRecentMessages(chatRoomId, lastMessageId, limit))
    }

    @PostMapping("/message")
    fun sendMessage(@RequestBody request: ChatMessageRequest): ApiResponse<ChatMessageResponse> {
        return ApiResponse.success(chatService.sendMessage(request))
    }

    @PostMapping("/read-receipt")
    fun markAsRead(@RequestBody request: ReadReceiptRequest): ApiResponse<String> {
        chatService.markMessagesAsRead(request.chatRoomId, request.userId)
        return ApiResponse.success("읽음 처리 완료")
    }

}
