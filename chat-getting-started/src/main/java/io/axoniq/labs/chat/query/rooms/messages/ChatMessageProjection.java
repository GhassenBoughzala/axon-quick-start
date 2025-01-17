package io.axoniq.labs.chat.query.rooms.messages;

import io.axoniq.labs.chat.coreapi.RoomMessagesQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMessageProjection {

    private final ChatMessageRepository repository;
    private final QueryUpdateEmitter updateEmitter;

    public ChatMessageProjection(ChatMessageRepository repository, QueryUpdateEmitter updateEmitter) {
        this.repository = repository;
        this.updateEmitter = updateEmitter;
    }

    @QueryHandler
    public List<ChatMessage> handle(RoomMessagesQuery query){
        return repository.findAllByRoomIdOrderByTimestamp(query.getRoomId());
    }

    // TODO: Create the query handler to read data from this model.

    // TODO: Emit updates when new message arrive to notify subscription query by modifying the event handler.
}
