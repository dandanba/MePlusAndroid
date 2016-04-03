package com.meplus.punub;


import com.meplus.events.BaseEvent;

/**
 * Created by dandanba on 3/3/16.
 */
public class CommandEvent extends BaseEvent {
    private Command mCommand;

    public CommandEvent(String status) {
        super(status);
    }

    public Command getCommand() {
        return mCommand;
    }

    public void setCommand(Command command) {
        mCommand = command;
    }
}
