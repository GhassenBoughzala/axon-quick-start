package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.*;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

import java.util.Set;

@Aggregate
public class ChatRoom {

    @AggregateIdentifier
    private String roomId;
    private boolean roomConfirmed;
    private Set<String> participants;
    private String msg;

    protected ChatRoom(){}

    @CommandHandler
    public ChatRoom(CreateRoomCommand command){
        AggregateLifecycle.apply(new RoomCreatedEvent(command.getRoomId(), command.getName() ));
    }
    @EventSourcingHandler
    public void on(RoomCreatedEvent event){
        this.roomId = event.getRoomId();
        roomConfirmed = false;
    }

    @CommandHandler
    public void handle(JoinRoomCommand command){
        if(!participants.contains(command.getParticipant())){
            AggregateLifecycle.apply(new ParticipantJoinedRoomEvent(command.getRoomId(), command.getParticipant()));
        }
    }
    @EventSourcingHandler
    public void on(ParticipantJoinedRoomEvent event){
      this.participants.add(event.getParticipant());
    }

    @CommandHandler
    public ChatRoom(LeaveRoomCommand command){
        AggregateLifecycle.apply(new ParticipantLeftRoomEvent(command.getRoomId(), command.getParticipant()));
    }
    @EventSourcingHandler
    public void on(ParticipantLeftRoomEvent event){
        this.roomId = event.getRoomId();
    }

    @CommandHandler
    public ChatRoom(PostMessageCommand command){
        AggregateLifecycle.apply(new MessagePostedEvent(command.getRoomId(), command.getParticipant(), command.getMessage()));
    }
    @EventSourcingHandler
    public void on(MessagePostedEvent event){
        if (this.msg.equals(event.getMessage())){
            throw new RuntimeException();
        }
        this.msg = event.getMessage();
    }

}
