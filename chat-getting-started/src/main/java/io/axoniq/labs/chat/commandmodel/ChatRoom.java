package io.axoniq.labs.chat.commandmodel;

import io.axoniq.labs.chat.coreapi.CreateRoomCommand;
import io.axoniq.labs.chat.coreapi.RoomCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class ChatRoom {

    @AggregateIdentifier
    private String roomId;
    private boolean roomConfirmed;

    @CommandHandler
    public ChatRoom(CreateRoomCommand command){
        AggregateLifecycle.apply(new RoomCreatedEvent(command.getRoomId(), command.getName() ));
    }

    @EventSourcingHandler
    public void on(RoomCreatedEvent event){
        this.roomId = event.getRoomId();
        roomConfirmed = false;
    }

    protected ChatRoom(){}
}
