import java.io.Serializable;
import java.util.ArrayList;

public class Artista implements Serializable {
    private String nome_artista;
    private String tipo;
    private String informacao;
    private ArrayList<Utilizador> lista_utilizadores_editar_artista;

    Artista(){}

    Artista(String nome_artista, String tipo){
        this.nome_artista = nome_artista;
        this.tipo =tipo;
        informacao=null;

    }
    Artista(String nome_artista, String tipo, String informacao){
        this.nome_artista = nome_artista;
        this.tipo = tipo;
        this.informacao = informacao;
    }

    public String getNome_artista() {
        return nome_artista;
    }

    public void setNome_artista(String nome_artista, Utilizador u) {
        if(u.getEditor()){
            if(lista_utilizadores_editar_artista.contains(u) == false)
                lista_utilizadores_editar_artista.add(u);
            this.nome_artista = nome_artista;
        }
    }

    public String getInformacao() {
        return informacao;
    }

    public void setInformacao(String informacao, Utilizador u) {
        if(u.getEditor()){
            if(lista_utilizadores_editar_artista.contains(u)==false)
                lista_utilizadores_editar_artista.add(u);
            this.informacao = informacao;
        }
    }

    public String getTipo() {
        return tipo;
    }

    public void setCompositor(String tipo, Utilizador u) {
        if(u.getEditor()){
            if(lista_utilizadores_editar_artista.contains(u)== false)
                lista_utilizadores_editar_artista.add(u);
            this.tipo = tipo;
        }
    }
}
