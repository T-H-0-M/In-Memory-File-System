package com.thom.filesystem;

import com.thom.filesystem.cli.CommandDispatcher;
import com.thom.filesystem.core.InMemoryFileSystem;
import com.thom.filesystem.errors.FileSystemException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class App {
    public static void main(String[] args) throws Exception {
        run(System.in, System.out, System.err);
    }

    static void run(InputStream in, PrintStream out, PrintStream err) throws Exception {
        var fs = new InMemoryFileSystem();
        var dispatcher = new CommandDispatcher(fs);

        out.println("Welcome to the in-memory file system.");
        out.println("Type 'quit' or 'exit' to leave.");

        try (var reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            while (true) {
                out.print("€ ");
                out.flush();

                var line = reader.readLine();
                if (line == null) {
                    return;
                }

                var trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if ("exit".equals(trimmed) || "quit".equals(trimmed)) {
                    return;
                }

                try {
                    var result = dispatcher.dispatch(line);
                    for (var outputLine : result.outputLines()) {
                        out.println(outputLine);
                    }
                } catch (FileSystemException e) {
                    err.println(e.getMessage());
                }
            }
        }
    }
}
