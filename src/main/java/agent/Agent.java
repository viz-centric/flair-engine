package agent;

import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.Instrumentation;
import java.util.jar.JarFile;

@Slf4j
public class Agent {
    private volatile static Instrumentation instrumentation;

    public static void premain(String args, Instrumentation instrumentation) {
        log.info("Premain args {} instr {}", args, instrumentation);
        Agent.instrumentation = instrumentation;
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        log.info("Agentmain args {} instr {}", agentArgs, instrumentation);
        Agent.instrumentation = instrumentation;
    }

    public static void appendJarFile(JarFile file) {
        log.info("Appending jar file {} to instr {}", file, instrumentation);
        if (instrumentation != null) {
            instrumentation.appendToSystemClassLoaderSearch(file);
        }
    }
}
