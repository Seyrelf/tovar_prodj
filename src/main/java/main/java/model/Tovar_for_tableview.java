package main.java.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Objects;

@Data
@AllArgsConstructor
public class Tovar_for_tableview {
    private int id;
    private String name;
    private String type;

    @Override
    public String toString() {
        return "Tovar_for_tableview{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tovar_for_tableview that = (Tovar_for_tableview) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, type);
    }
}
