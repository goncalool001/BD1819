import java.io.Serializable;
import java.util.ArrayList;

public class Playlist implements Serializable {
    private String nome_playlist;
    private Boolean privacidade;
    private ArrayList<Musica> lista_musicas;
    Playlist(){
        nome_playlist = null;
        privacidade = false;
        lista_musicas = null;
    }

    Playlist(String nome_playlist, Boolean privacidade){
        this.nome_playlist = nome_playlist;
        this.privacidade = privacidade;
        this.lista_musicas = null;
    }
    public void add_musica(Musica m){
        lista_musicas.add(m);
    }
    public void remove_musica(Musica m){
        for(Musica item: lista_musicas){
            if (item.equals(m)) {
                lista_musicas.remove(m);
                return;
            }
        }
        System.out.println("Musica nao encontrada");
    }

    public Boolean getPrivacidade() {
        return privacidade;
    }

    public void setPrivacidade(Boolean privacidade) {
        this.privacidade = privacidade;
    }

    private String getNome_playlist() {
        return nome_playlist;
    }

    private void setNome_playlist(String nome_playlist) {
        this.nome_playlist = nome_playlist;
    }
}
