package service;

import domain.Sample;
import db.SampleRepository;
import validation.SampleValidator;
import validation.ValidationException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SampleService {
    private final SampleRepository repository;
    private final Set<Sample> cache = new HashSet<>();

    public SampleService(SampleRepository repository) throws SQLException {
        this.repository = repository;
        loadAllFromDb();
    }

    private void loadAllFromDb() throws SQLException {
        cache.clear();
        cache.addAll(repository.findAll());
    }

    public Sample addSample(String name, String ownerUsername) throws ValidationException, SQLException {
        Sample sample = new Sample(0, name, ownerUsername);
        SampleValidator.validate(sample);
        Sample saved = repository.save(sample);
        cache.add(saved);
        return saved;
    }

    public Optional<Sample> getSample(long id) {
        return cache.stream().filter(s -> s.getId() == id).findFirst();
    }

    public boolean exists(long id) {
        return getSample(id).isPresent();
    }

    public Set<Sample> getSamples() {
        return new HashSet<>(cache);
    }

    public void deleteSample(long id, String currentUser) throws ValidationException, SQLException {
        Sample sample = getSample(id).orElseThrow(() -> new ValidationException("Образец не найден"));
        if (!sample.getOwnerUsername().equals(currentUser)) {
            throw new ValidationException("Нет прав на удаление");
        }
        repository.deleteById(id);
        cache.remove(sample);
    }

    public void updateSample(Sample sample) throws SQLException {
        repository.update(sample);
        syncCache();
    }

    public void syncCache() throws SQLException {
        loadAllFromDb();
    }
}