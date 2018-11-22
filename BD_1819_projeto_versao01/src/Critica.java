import java.io.Serializable;
import java.util.ArrayList;

public class Critica implements Serializable {
    private Utilizador pessoa_critica;
    private String justificacao;
    private int pontuacao;
    private Album album_criticado;
    private ArrayList<Utilizador> lista_utilizadores_editar_critica;

    Critica(){}

    Critica(String justificacao, int pontuacao){
        this.justificacao = justificacao;
        this.pontuacao = pontuacao;
    }

    public Album getAlbum_criticado() {
        return album_criticado;
    }

    public void setAlbum_criticado(Album album_criticado) {
        this.album_criticado = album_criticado;
    }

    public Utilizador getPessoa_critica() {
        return pessoa_critica;
    }

    public void setPessoa_critica(Utilizador pessoa_critica) {
        this.pessoa_critica = pessoa_critica;
    }

    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public String getJustificacao() {
        return justificacao;
    }

    public void setJustificacao(String justificacao, Utilizador u) {
        if(u.getEditor()){
            if(lista_utilizadores_editar_critica.contains(u) == false)
                lista_utilizadores_editar_critica.add(u);

            this.justificacao = justificacao;
        }
    }
}
