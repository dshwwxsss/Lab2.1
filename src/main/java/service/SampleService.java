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
        // Начальные образцы теперь без владельца (или с SYSTEM)
        try {
            addSample("River water #3", "SYSTEM");
            addSample("Soil batch A", "SYSTEM");
            addSample("Blank solution", "SYSTEM");
        } catch (ValidationException e) {
            // ignore
        }
    }

    private long generateId() {
        return System.currentTimeMillis() + samples.size();
    }

    public Sample addSample(String name, String ownerUsername) throws ValidationException {
        Sample sample = new Sample(generateId(), name, ownerUsername);
        SampleValidator.validate(sample);
        samples.add(sample);
        return sample;
    }

    public Optional<Sample> getSample(long id) {
        return samples.stream().filter(s -> s.getId() == id).findFirst();
    }

    public boolean exists(long id) {
        return getSample(id).isPresent();
    }

    public Set<Sample> getSamples() {
        return new HashSet<>(samples);
    }

    public void deleteSample(long id, String currentUser) throws ValidationException {
        Sample sample = getSample(id)
                .orElseThrow(() -> new ValidationException("Образец с id=" + id + " не найден"));

        if (!sample.getOwnerUsername().equals(currentUser)) {
            throw new ValidationException("Ошибка: у вас нет прав на удаление этого образца");
        }

        samples.remove(sample);
    }

    public void replaceAll(Set<Sample> newSamples) {
        samples.clear();
        samples.addAll(newSamples);
    }
}