import java.io.Serializable;
import java.util.ArrayList;

public class Musica implements Serializable {
    private String nome_musica, compositor, duracao;
    //private Upload upload;

    Musica(){}

    Musica(String nome_musica, String compositor, String duracao){
        this.nome_musica = nome_musica;
        this.compositor = compositor;
        this.duracao = duracao;
    }

    //public Upload getUpload() {return upload;}

    //public void setUpload(Upload upload) {this.upload = upload;}

    public String getNome_musica() {
        return nome_musica;
    }

    public void setNome_musica(String nome_musica) {
        this.nome_musica = nome_musica;
    }

    public String getCompositor() {
        return compositor;
    }

    public void setCompositor(String compositor) {
        this.compositor = compositor;
    }

    public String getDuracao() {
        return duracao;
    }

    public void setDuracao(String duracao) {
        this.duracao = duracao;
    }
}
