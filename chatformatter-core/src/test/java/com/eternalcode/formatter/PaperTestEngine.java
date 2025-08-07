package com.eternalcode.formatter;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.jupiter.engine.config.CachingJupiterConfiguration;
import org.junit.jupiter.engine.config.DefaultJupiterConfiguration;
import org.junit.jupiter.engine.config.JupiterConfiguration;
import org.junit.jupiter.engine.descriptor.ClassTestDescriptor;
import org.junit.jupiter.engine.descriptor.JupiterEngineDescriptor;
import org.junit.jupiter.engine.descriptor.TestMethodTestDescriptor;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;

public class PaperTestEngine implements TestEngine {

    private final ClassLoader loader = new
    private final JupiterTestEngine baseEngine = new JupiterTestEngine();

    @Override
    public String getId() {
        return "paper-engine";
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        JupiterConfiguration configuration = new CachingJupiterConfiguration(new DefaultJupiterConfiguration(
            discoveryRequest.getConfigurationParameters(), discoveryRequest.getOutputDirectoryProvider()));
        JupiterEngineDescriptor engineDescriptor = new JupiterEngineDescriptor(uniqueId, configuration);

        for (ClassSelector selector : discoveryRequest.getSelectorsByType(ClassSelector.class)) {
            Class<?> testClass = selector.getJavaClass();
            List<Method> paperTests = Stream.of(testClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PaperTest.class))
                .toList();

            if (paperTests.isEmpty()) {
                continue;
            }

            TestDescriptor classDescriptor = new ClassTestDescriptor(uniqueId.append("class", testClass.getName()), testClass, configuration);

            for (Method method : paperTests) {
                TestDescriptor methodDescriptor = new TestMethodTestDescriptor(
                    classDescriptor.getUniqueId().append("method", method.getName()),
                    testClass,
                    method,
                    () -> List.of(),
                    configuration
                );
                classDescriptor.addChild(methodDescriptor);
            }

            engineDescriptor.addChild(classDescriptor);
        }

        return engineDescriptor;
    }

    @Override
    public void execute(ExecutionRequest request) {
        ServerDownloader downloader = new ServerDownloader();
        Path download = downloader.download();
        EmbeddedServer embeddedServer = new EmbeddedServer(download);
        try {

            embeddedServer.start(() -> {
                baseEngine.execute(request);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            embeddedServer.stop();
        }
    }

}