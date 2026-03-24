package service;

import domain.Sample;
import validation.SampleValidator;
import validation.ValidationException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SampleService {
    private final Set<Sample> samples = new HashSet<>();

    public SampleService() {
        try {
            addSample("River water #3");
            addSample("Soil batch A");
            addSample("Blank solution");
        } catch (ValidationException e) {
        }
    }
    private long generateId() {
        return System.currentTimeMillis() + samples.size();
    }

    public Sample addSample(String name) throws ValidationException {
        Sample sample = new Sample(generateId(), name);
        SampleValidator.validate(sample);
        samples.add(sample);
        return sample;
    }

    public Optional<Sample> getSample(long id) {
        return samples.stream()
                .filter(s -> s.getId() == id)
                .findFirst();
    }
    public boolean exists(long id) {
        return getSample(id).isPresent();
    }

    public Set<Sample> getSamples() {
        return new HashSet<>(samples);
    }
}
