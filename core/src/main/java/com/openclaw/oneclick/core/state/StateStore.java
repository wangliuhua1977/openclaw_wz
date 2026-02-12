package com.openclaw.oneclick.core.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclaw.oneclick.core.model.DeploymentSettings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class StateStore {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Path path;
    private DeploymentSettings settings;

    public StateStore(Path path, DeploymentSettings settings) {
        this.path = path;
        this.settings = settings;
    }

    public synchronized void markCurrentStep(String stepId) throws IOException {
        write(new DeployState(stepId, settings, null));
    }

    public synchronized void saveError(String stepId, String error) throws IOException {
        write(new DeployState(stepId, settings, error));
    }

    public synchronized Optional<DeployState> load() {
        if (!Files.exists(path)) {
            return Optional.empty();
        }
        try {
            return Optional.of(mapper.readValue(path.toFile(), DeployState.class));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    public synchronized void clear() throws IOException {
        Files.deleteIfExists(path);
    }

    private void write(DeployState state) throws IOException {
        Files.createDirectories(path.getParent());
        mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), state);
    }
}
