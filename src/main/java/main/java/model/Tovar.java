package main.java.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Type;

import java.util.*;

@Data
@Table(name = "tovar")
@Entity
public class Tovar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_p")
    private int id_p;

    @Type(JsonType.class)
    @Column(name = "info")
    private Map<String,Object> object_info;

    public Tovar() {
    }

    public Tovar(Map<String,Object> object_info,int id_p) {
        this.object_info = object_info;
        this.id_p = id_p;
    }

    public Tovar(Tovar tovar){
        this.object_info = tovar.getObject_info();
        this.id = tovar.getId();
        this.id_p = tovar.getId_p();
    }

    @Override
    public String toString() {
        return "Tovar{" +
                "id=" + id +
                ", id_p=" + id_p +
                ",\nobject_info=" + object_info +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tovar tovar = (Tovar) o;
        return id == tovar.id && id_p == tovar.id_p && Objects.equals(object_info, tovar.object_info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, id_p, object_info);
    }
}
