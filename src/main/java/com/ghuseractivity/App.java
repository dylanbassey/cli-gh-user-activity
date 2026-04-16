package com.ghuseractivity;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "ghuser", description = "Github user activity tracker using CLI", mixinStandardHelpOptions = true, subcommands = {
        userActivityService.class
})
public class App implements Runnable {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        System.out.println("Use a command. Try --help.");
    }
}
