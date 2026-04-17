package domain; //описывает образец (ID, имя)

import java.util.Objects;

public final class Sample {
    private final long id;
    private final String name;

    public Sample(long id, String name) {
        this.id = id;
        this.name = name;
    }
//читать, но не менять
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sample sample = (Sample) o;
        return id == sample.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return name + " (ID: " + id + ")";
    }
}


//здесь нет валидации (название не проверяется на пустоту и длину,
// потому что в нашем коде мы создаём образцы вручную с корректными именами