package com.romanobori;

import com.romanobori.commands.ArbCommand;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;

public class CommandsRunner{

    BlockingQueue<ArbCommand> commandsQueue = new ArrayBlockingQueue<ArbCommand>(10);

    public void start(List<ArbCommand> commandsToStart) throws InterruptedException, ExecutionException {
        commandsQueue.addAll(commandsToStart);
        looping();
    }

    private void looping() throws InterruptedException, ExecutionException {
        while(true) {
            ArbCommand command = commandsQueue.take();
            command.execute(commandsQueue);
        }
    }

}
