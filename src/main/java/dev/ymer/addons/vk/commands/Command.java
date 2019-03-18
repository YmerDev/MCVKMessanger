package dev.ymer.addons.vk.commands;

public interface Command {
    String getName();
    String[] getAliases();

    boolean execute(String[] args);
}
