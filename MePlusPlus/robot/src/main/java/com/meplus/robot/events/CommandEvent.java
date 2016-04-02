package com.meplus.robot.events;


import com.meplus.punub.Command;

/**
 * Created by dandanba on 3/3/16.
 */
public class CommandEvent extends Event {
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
