import java.io.Serializable;

public class Genero implements Serializable {
    private String genero;

    Genero(){}

    Genero(String genero){
        this.genero = genero;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }
}
