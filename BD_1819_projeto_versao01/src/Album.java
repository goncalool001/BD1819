import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private String nome_album, descricao, data_lancamento;
    private ArrayList<Musica> lista_musicas;
    private ArrayList<Utilizador> lista_utilizadores_editar_album;
    Album(){}

    Album(String nome_album, String descricao, String data_lancamento){
        this.nome_album = nome_album;
        this.data_lancamento = data_lancamento;
        this.descricao = descricao;
    }
    public void remover_musica(Musica m){
        for(Musica item:lista_musicas){
            if(item.equals(m)){
                lista_musicas.remove(item);
                return;
            }
        }
        System.out.println("Musica inexistente");
    }
    public void add_musica(Musica m){
        lista_musicas.add(m);
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao, Utilizador u) {
/*
        if(u.getEditor()){
            if(lista_utilizadores_editar_album.contains(u) == false)
                lista_utilizadores_editar_album.add(u);

        }*/
        this.descricao = descricao;
    }

    public String getData_lancamento() {
        return data_lancamento;
    }

    public void setData_lancamento(String data_lancamento, Utilizador u) {
        /*if(u.getEditor()){
            if(lista_utilizadores_editar_album.contains(u)==false)
                lista_utilizadores_editar_album.add(u);

        }*/
        this.data_lancamento = data_lancamento;
    }

    public String getNome_album() {
        return nome_album;
    }

    public void setNome_album(String nome_album, Utilizador u) {
        if(u.getEditor()){
            if(lista_utilizadores_editar_album.add(u)==false)
                lista_utilizadores_editar_album.add(u);
            this.nome_album = nome_album;
        }
    }

}
