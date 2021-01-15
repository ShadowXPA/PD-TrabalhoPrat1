package pt.isec.deis.lei.pd.trabprat.model;

import java.io.Serializable;
import java.util.Objects;

public class GenericPair<K, V> implements Serializable {

    public final K key;
    public final V value;

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.key);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GenericPair<?, ?> other = (GenericPair<?, ?>) obj;
        return Objects.equals(this.key, other.key);
    }

    @Override
    public String toString() {
        return "Pair{" + "key=" + key + ", value=" + value + '}';
    }

    public GenericPair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
