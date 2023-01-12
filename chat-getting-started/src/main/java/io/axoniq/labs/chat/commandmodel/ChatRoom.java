package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import io.axoniq.labs.chat.query.rooms.messages.ChatMessage;
import io.axoniq.labs.chat.query.rooms.messages.ChatMessageRepository;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.Timestamp;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Set;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
public class ChatRoom {

    @AggregateIdentifier
    private String roomId;
    private boolean roomConfirmed;
    private Set<String> participants;
    private String msg;
    private final ChatMessageRepository repository;

    @Autowired
    protected ChatRoom(ChatMessageRepository chatMessageRepository) {
        this.repository = chatMessageRepository;
    }

    @CommandHandler
    public ChatRoom(CreateRoomCommand command, ChatMessageRepository repository) {
        this.repository = repository;
        apply(new RoomCreatedEvent(command.getRoomId(), command.getName()));
    }

    @EventSourcingHandler
    public void on(RoomCreatedEvent event) {
        this.roomId = event.getRoomId();
        roomConfirmed = false;
    }

    @CommandHandler
    public void handle(JoinRoomCommand command) {
        if (!participants.contains(command.getParticipant())) {
            apply(new ParticipantJoinedRoomEvent(roomId, command.getParticipant()));
        }
    }

    @EventSourcingHandler
    public void on(ParticipantJoinedRoomEvent event) {
        this.participants.add(event.getParticipant());
    }

    @CommandHandler
    public ChatRoom(LeaveRoomCommand command, ChatMessageRepository repository) {
        this.repository = repository;
        apply(new ParticipantLeftRoomEvent(command.getRoomId(), command.getParticipant()));
    }

    @EventSourcingHandler
    public void on(ParticipantLeftRoomEvent event) {
        this.roomId = event.getRoomId();
    }

    @CommandHandler
    public ChatRoom(PostMessageCommand command, ChatMessageRepository repository) {
        this.repository = repository;
        apply(new MessagePostedEvent(command.getRoomId(), command.getParticipant(), command.getMessage()));
    }

    @EventHandler
    public void on(MessagePostedEvent event, @Timestamp Instant timestamp) {
        ChatMessage chatMessage = new ChatMessage(
                event.getParticipant(),
                event.getRoomId(),
                event.getMessage());
                //event.toEpocMilli()
        repository.save(chatMessage);
        //updateEmitter.emit(RoomMessagesQuery.class, query -> query.getRoomId().equals(event.getRoomId()), chatMessage );

    }

}
