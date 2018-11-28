import org.apache.commons.io.FileUtils;
import org.postgresql.util.PSQLException;


import java.io.*;
import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;
import java.sql.*;

import static java.rmi.Naming.list;

public class main {

    private static Connection c = null;
    private static Utilizador utilizador_corrente;
    private static int cartao_cidadao;

    public static void main(String args[]) throws IOException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bd1819","postgres", "default");
            //c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/base_dados_1819", "postgres", "default");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        menu_inicial();


    }

    private static void menu_inicial() throws IOException {
        Scanner sc = new Scanner(System.in);
        String frase, username, password;
        String[] frase_chave_valor;
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        String mensagem;
        String opcao_s = null;
        int opcao = 0, tipo_utilizador;

        do {
            System.out.println("-----------Menu Inicial-----------\n"
                    + "[1]Login\n"
                    + "[2]Registar\n"
                    + "[3]Sair\n");

            opcao_s = sc.next();
            try {
                opcao = Integer.parseInt(opcao_s);
            } catch (NumberFormatException n) {
                System.out.println("Insira um numero valido");
                continue;
            }

            switch (opcao) {
                case 1: //login
                    System.out.println("Nome Passe");
                    frase = reader.readLine();
                    frase_chave_valor = frase.split(" ");
                    if (frase_chave_valor.length < 2) {
                        System.out.println("Insira todos os parametros");
                        continue;
                    }
                    username = frase_chave_valor[0];
                    password = frase_chave_valor[1];
                    tipo_utilizador = login(username, password);
                    if (tipo_utilizador == 1) {
                        /*if(verificaUser(username)){
                            menu_login_editor();
                            para que esta funcao?! se o login devolve e porque encontrou o user
                        }*/
                        menu_login_normal();
                    } else if (tipo_utilizador == 2) {
                        menu_login_editor();
                    } else {
                        menu_inicial();
                    }
                    //}else
                    break;
                case 2://registar
                    System.out.println("Nome Passe CC");
                    frase = reader.readLine();
                    frase_chave_valor = frase.split(" ");
                    username = frase_chave_valor[0];
                    password = frase_chave_valor[1];
                    cartao_cidadao = Integer.parseInt(frase_chave_valor[2]);
                    registar(username, password);
                    break;
            }
        } while (opcao != 3);
    }

    private static int login(String username, String passe) {//verifica se existe um utilizador com username e pass indicada
        //devolve 0 se nao tiver utilizador ou passe errada
        //devolve 1 se o utilizador nao for editor
        //devolve 2 se o utilizador for editor
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("SELECT * FROM utilizador where nome = ? and passe = ?;");
            stmt.setString(1, username);
            stmt.setString(2, passe);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            utilizador_corrente = new Utilizador(username, passe, rs.getBoolean(4));
            cartao_cidadao = rs.getInt(1);
            if (rs.getBoolean(4))//tem direitos
                return 2;
            else if (!rs.getBoolean(4))//nao tem direitos
                return 1;
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return 0;
    }

    private static void registar(String username, String passe) {
        if (verificaUserEmpty()) {//se nao houver users na bd é editor
            try {
                c.setAutoCommit(false);
                Utilizador util = new Utilizador(username, passe, true); //mudar caso seja primeiro
                PreparedStatement stmt = c.prepareStatement("INSERT INTO utilizador(cartao_cidadao, nome, passe, utilizador_tipo)" + "VALUES (?,?,?,true)");
                stmt.setInt(1, cartao_cidadao);
                stmt.setString(2, util.getUsername());
                stmt.setString(3, util.getPassword());
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            try {
                c.setAutoCommit(false);
                Utilizador util = new Utilizador(username, passe, false);
                PreparedStatement stmt = c.prepareStatement("INSERT INTO utilizador(cartao_cidadao, nome, passe, utilizador_tipo)" + "VALUES (?,?,?,false)");
                stmt.setInt(1, cartao_cidadao);
                stmt.setString(2, util.getUsername());
                stmt.setString(3, util.getPassword());
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
        }

    }

    private static void menu_login_normal() {
        Scanner reader = new Scanner(System.in);
        int opcao = 0;
        String opcao_s = null;
        do {//manter o login aberto a nao ser que peça para sair
            System.out.println("-----------Menu-----------\n"
                    + "[1]Listar música\n"
                    + "[2]Consultar detalhes album\n"
                    + "[3]Consultar detalhes artista\n"
                    + "[4]Upload de música\n"
                    + "[5]Download de música\n"
                    + "[6]Partilhar uma música\n"
                    + "[7]Pesquisar uma musica\n"
                    + "[0]Logout");
            opcao_s = reader.next();
            try {
                opcao = Integer.parseInt(opcao_s);
            } catch (NumberFormatException e) {
                System.out.println("Insira uma opcao valida");
                continue;
            }

            switch (opcao) {
                case 1: //listar as musicas 13
                    System.out.println("A listar musicas");
                    listar(13);
                    break;

                case 2: //detalhes do album numero 14
                    pesquisar(14);
                    break;
                case 3: //detalhe artista numero 15
                    pesquisar(15);
                    break;
                case 4: //upload de musica
                    /*server_i.enviaStringAoMulticast("16");
                    System.out.println("Qual o nome da Música a fazer upload?\n");
                    nomeMusica = sc.nextLine();
                    TimeUnit.MILLISECONDS.sleep(200);
                    uploadTCP(nomeMusica);*/
                    break;
                case 5: //download de musica
                    break;
                case 6: //partilhar musica
                    break;
                case 7://pesquisar musica numero 13
                    pesquisar(14);
                    break;
                case 12://teste para album
                    listar(14);
                    break;
                case 13://teste para artista
                    listar(15);
                    break;
                case 14://teste para utilizador 16
                    listar(16);
                    break;
                case 0:
                    System.out.println("Logout");
                    break;
                default:
                    System.out.println("Insira uma opção válida!");
            }
        } while (opcao != 0);

    }

    private static void menu_login_editor() {
        Scanner reader = new Scanner(System.in);
        /*if( System.getProperty( "os.name" ).startsWith( "Window" ) )
            Runtime.getRuntime().exec("cls");
        else
            Runtime.getRuntime().exec("clear");*/
        int opcao = 0, o;
        String nomeMusica, opcao_s = null;
        Scanner sc = new Scanner(System.in);
        do {//manter o login aberto a nao ser que peça para sair
            System.out.println("-----------Menu de Editor-----------\n"
                    + "[1]Listar música\n"
                    + "[2]Gerir artistas\n"
                    + "[3]Gerir álbuns\n"
                    + "[4]Gerir músicas\n"
                    + "[5]Privilégios de editor a um utilizador\n"
                    + "[6]Consultar detalhes album\n"
                    + "[7]Consultar detalhes artista\n"
                    + "[8]Upload de música\n"
                    + "[9]Download de música\n"
                    + "[10]Partilhar uma música\n"
                    + "[11]Pesquisar uma musica\n"
                    + "[12]Listar album\n"
                    + "[13]Listar artista\n"
                    + "[14]Listar utilizadores\n"
                    + "[15]Gerir Playlist\n"
                    + "[16]Listar todas as playlists\n"
                    + "[0]Logout");
            opcao_s = reader.next();
            try {
                opcao = Integer.parseInt(opcao_s);
            } catch (NumberFormatException e) {
                System.out.println("Insira uma opcao valida");
                continue;
            }

            switch (opcao) {
                case 1: //listar as musicas 13
                    System.out.println("A listar musicas");
                    listar(13);
                    break;
                case 2: //gerir artistas
                    System.out.println("Pretende [1]adicionar, [2]editar ou [3]eliminar um artista [0]Voltar");
                    opcao_s = sc.next();
                    try {
                        o = Integer.parseInt(opcao_s);
                    } catch (NumberFormatException e) {
                        System.out.println("Insira opcao valida");
                        continue;
                    }
                    switch (o) {
                        case 1:
                            inserir_artista();
                            break;
                        case 2:
                            break;
                        case 3:
                            elimina_artista();
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Introduza uma opção válida!");
                            break;
                    }

                    break;
                case 3: ///gerir album
                    System.out.println("Pretende [1]adicionar, [2]editar ou [3]eliminar uma album [0]Voltar");
                    opcao_s = sc.next();
                    try {
                        o = Integer.parseInt(opcao_s);
                    } catch (NumberFormatException e) {
                        System.out.println("Insira opcao valida");
                        continue;
                    }

                    switch (o) {
                        case 1:
                            inserir_album();
                            break;
                        case 2:
                            editar_album();
                            break;
                        case 3:
                            elimina_album();
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Introduza uma opção válida!");
                            break;
                    }
                    break;
                case 4: //gerir musica
                    System.out.printf("Pretende [1]adicionar, [2]editar ou [3]eliminar uma musica [0]Voltar");
                    opcao_s = sc.next();
                    try {
                        o = Integer.parseInt(opcao_s);
                    } catch (NumberFormatException e) {
                        System.out.println("Insira opcao valida");
                        continue;
                    }

                    switch (o) {
                        case 1:
                            inserir_musica();
                            break;
                        case 2:
                            editar_musica();
                            break;
                        case 3:
                            eliminar_musica();
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Introduza uma opção válida!");
                            break;
                    }
                    break;
                case 5: // privilégios de editor
                    System.out.println("Pretende [1]dar privilegios ou [2]retirar privilegios [0]Voltar");
                    opcao_s = sc.next();
                    try {
                        o = Integer.parseInt(opcao_s);
                    } catch (NumberFormatException e) {
                        System.out.println("Insira opcao valida");
                        continue;
                    }
                    if (o == 0)
                        return;
                    privilegio(o);
                    break;
                case 6: //detalhes do album numero 14
                    pesquisar(14);
                    break;
                case 7: //detalhe artista numero 15
                    pesquisar(15);
                    break;
                case 8: //upload de musica
                    upload();
                    break;
                case 9: //download de musica
                    download();
                    break;
                case 10: //partilhar musica
                    break;
                case 11://pesquisar musica numero 13
                    pesquisar(13);
                    break;
                case 12://teste para album
                    listar(14);
                    break;
                case 13://teste para artista
                    listar(15);
                    break;
                case 14://teste para utilizador 16
                    listar(16);
                    break;
                case 15://gerir playlist
                    System.out.printf("Pretende [1]adicionar, [2]editar ou [3]eliminar [4]listar [0]Voltar");
                    opcao_s = sc.next();
                    try {
                        o = Integer.parseInt(opcao_s);
                    } catch (NumberFormatException e) {
                        System.out.println("Insira opcao valida");
                        continue;
                    }

                    switch (o) {
                        case 1:
                            criar_playlist();
                            break;
                        case 2:
                            System.out.println("Pretende [1]adicionar musica, [2]remover uma musica ou [3]editar informcacao da playlist");
                            editar_playlist(Integer.parseInt(sc.next()));
                            break;
                        case 3:
                            //eliminar_playlist();
                            eliminar_playlist();
                            break;
                        case 4://codigo 18 para listar playlist do utilizador_corrente
                            listar(18);
                            break;
                        case 0:
                            break;
                        default:
                            System.out.println("Introduza uma opção válida!");
                            break;
                    }
                    break;
                case 16://listar playlist existentes com codigo 17
                    listar(17);
                    break;
                case 0:
                    System.out.println("Logout");
                    break;
                default:
                    System.out.println("Insira uma opção válida!");
            }
        } while (opcao != 0);

    }

    private static void  upload(){
        String nome_musica;
        int id_musica, id_utilizador;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome da música que pretende dar upload?");
        nome_musica = sc.nextLine();
        id_utilizador = getUtilizadorId(utilizador_corrente.getUsername());
        id_musica = getMusicId(nome_musica);

        try {
            c.setAutoCommit(false);
            PreparedStatement stmt = c.prepareStatement("INSERT INTO upload(ficheiro_id, ficheiro, utilizador_id, musica_id) VALUES(DEFAULT ,?,?,?);");
            File f = new File("C:\\Users\\gonca\\Desktop\\BD_1819_projeto_versao01\\Musicas\\"+nome_musica+".mp3");
            FileReader fr = new FileReader(f);
            FileInputStream fis = new FileInputStream(f);
            byte [] bytes = IOUtils.toByteArray(fis);

            if (fis != null) {
                // fetches input stream of the upload file for the blob column
                stmt.setBytes(1, bytes);
            }
            stmt.setInt(2,id_utilizador);
            stmt.setInt(3,id_musica);

            stmt.executeUpdate();

            stmt.close();
            c.commit();


        } catch (FileNotFoundException e) {
            System.out.println("Não encontrou o ficheiro");
        } catch (SQLException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    private static void download(){
        String nome_musica;
        int id_ficheiro,id_musica;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome da música que pretende fazer download?");
        nome_musica = sc.nextLine();
        id_musica = getMusicId(nome_musica);
        id_ficheiro = getFicheiroIdWithMusicId(id_musica);


        //cria a pasta para o user
        String fileName = "C:\\Users\\gonca\\Desktop\\BD_1819_projeto_versao01\\Musicas\\"+utilizador_corrente.getUsername();
        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Directory already exists");
        }

        try{
            File f = new File(fileName+"\\"+nome_musica+".mp3");
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM upload where ficheiro_id = ? ");
            stmt.setInt(1,id_ficheiro);

            ResultSet rs = stmt.executeQuery();
            rs.next();

            byte [] b = rs.getBytes("ficheiro");
            FileUtils.writeByteArrayToFile(f, b);


        } catch (SQLException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    private static void eliminar_playlist() {
        String nome;
        int id_playlist;
        Scanner sc = new Scanner(System.in);
        PreparedStatement stmt;
        System.out.println("Qual o nome da playlist que quer eliminar?");
        nome = sc.nextLine();
        id_playlist = procura_id_playlist(nome);
        if (id_playlist == 0) {
            System.out.println("Playlist inexistente");
            return;
        }
        if (verifica_playlist(nome)) {
            try {
                c.setAutoCommit(false);
                //apagar da tabela playlist_utilziador
                stmt = c.prepareStatement("DELETE FROM playlist_utilizador WHERE playlist_utilizador.playlist_id_playlist=?");
                stmt.setInt(1, id_playlist);
                stmt.close();

                //apagar da tabela playlist_musica
                stmt = c.prepareStatement("DELETE FROM playlist_musica WHERE playlist_musica.playlist_id_playlist=?");
                stmt.setInt(1, id_playlist);
                stmt.close();

                //apagar da tabela playlist
                stmt = c.prepareStatement("DELETE FROM playlist WHERE nome=?");
                stmt.setString(1, nome);
                stmt.executeUpdate();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
            System.out.println("Playlist Eliminada!");
        } else {
            System.out.println("Playlist não encontrada...");
        }
    }

    private static void editar_playlist(int opcao) {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        Scanner sc = new Scanner(System.in);
        String nome_playlist_corrente = null, nome_playlist_muda, nome_musica = null;
        int id_musica, id_playlist;
        PreparedStatement stmt;
        System.out.println("Qual o nome da playlist");
        try {
            nome_playlist_corrente = reader.readLine();
        } catch (IOException e) {
            System.out.println(e);
        }

        if (verifica_playlist(nome_playlist_corrente)) {
            System.out.println("Escreva o nome da musica");
            try {
                nome_musica = reader.readLine();
            } catch (IOException e) {
                System.out.println(e);
            }

            if (opcao == 1) {//quer adicionar musicas
                listar(13); //listar as musicas existentes
                try {
                    c.setAutoCommit(false);

                    id_musica = verifica_id_musica(nome_musica);
                    id_playlist = procura_id_playlist(nome_playlist_corrente);
                    stmt = c.prepareStatement("INSERT INTO playlist_musica(playlist_id_playlist, playlist_utilizador_cartao_cidadao, musica_idmusica) " + "VALUES (?,?,?)");
                    stmt.setInt(1, id_playlist);
                    stmt.setInt(2, cartao_cidadao);
                    stmt.setInt(3, id_musica);
                    stmt.executeUpdate();

                    stmt.close();
                    c.commit();
                } catch (SQLException e) {
                    System.out.println(e);
                }

            } else if (opcao == 2) {//remover uma musica da playlist
                try {
                    c.setAutoCommit(false);
                    //apagar da tabela playlist_utilziador
                    id_musica = verifica_id_musica(nome_musica);
                    stmt = c.prepareStatement("DELETE FROM playlist_musica WHERE playlist_musica.musica_idmusica=?");
                    stmt.setInt(1, id_musica);
                    stmt.close();
                    c.commit();
                } catch (SQLException e) {
                    System.out.println(e);
                }

            } else if (opcao == 3) {
                System.out.println("Pretende editar [1]Nome ou [2]Privacidade(Publico) ou [3]Privacidade(Privado) [0]Voltar?\n");
                switch (sc.nextInt()) {
                    case 1://caso pretenda editar o nome da playlist
                        System.out.println("Qual é o nome?");
                        try {
                            c.setAutoCommit(false);

                            nome_playlist_muda = reader.readLine();
                            stmt = c.prepareStatement("UPDATE playlist SET nome = ? WHERE nome=?");
                            stmt.setString(1, nome_playlist_muda);
                            stmt.setString(2, nome_playlist_corrente);
                            stmt.executeUpdate();

                            stmt.close();
                            c.commit();
                        } catch (IOException e) {
                            System.out.println(e);
                        } catch (SQLException e) {
                            System.out.println(e);
                        }
                        break;
                    case 2://caso pretenda editar a privacidade para publico
                        try {
                            c.setAutoCommit(false);

                            stmt = c.prepareStatement("UPDATE playlist SET privacidade = true WHERE nome=?");
                            stmt.setString(1, nome_playlist_corrente);
                            stmt.executeUpdate();

                            stmt.close();
                            c.commit();
                        } catch (SQLException e) {
                            System.out.println(e);
                        }
                        break;
                    case 3://caso pretenda mudar a privacidade para privado
                        try {
                            c.setAutoCommit(false);

                            stmt = c.prepareStatement("UPDATE playlist SET privacidade = false WHERE nome=?");
                            stmt.setString(1, nome_playlist_corrente);
                            stmt.executeUpdate();

                            stmt.close();
                            c.commit();
                        } catch (SQLException e) {
                            System.out.println(e);
                        }
                        break;
                    default:
                        break;
                }
            }
        } else {
            System.out.println("Playlist " + nome_playlist_corrente + " inexistente");
        }
    }

    private static void criar_playlist() {
        int privacidade;
        int id_playlist = (int) (Math.random() + 1) * Integer.MAX_VALUE;
        Date data = new Date();
        java.sql.Date data_criacao_album = new java.sql.Date(data.getTime());
        String nome;
        Scanner sc = new Scanner(System.in);
        System.out.println("Nome");
        nome = sc.next();
        System.out.println("A playlist " + nome + " sera [1]publica ou [2]privada");
        privacidade = sc.nextInt();
        PreparedStatement stmt;
        if (!verifica_playlist(nome)) {
            try {
                c.setAutoCommit(false);
                //por na tabela playlist
                stmt = c.prepareStatement("INSERT INTO playlist(id_playlist, nome, data_criacao, privacidade, utilizador_cartao_cidadao)" + "VALUES (?,?,?,?,?)");
                stmt.setInt(1, id_playlist);
                stmt.setString(2, nome);
                stmt.setDate(3, data_criacao_album);
                if (privacidade == 1)
                    stmt.setBoolean(4, true);
                if (privacidade == 2)
                    stmt.setBoolean(4, false);
                stmt.setInt(5, cartao_cidadao);
                stmt.executeUpdate();

                //por na tabela playlist_utilizador
                //verificar aqui o atributo playlist_utilizador_cartao_cidadao
                System.out.println(cartao_cidadao);

                stmt = c.prepareStatement("INSERT INTO playlist_utilizador(playlist_id_playlist, playlist_utilizador_cartao_cidadao, utilizador_cartao_cidadao)" + "VALUES (?,?,?)");
                stmt.setInt(1, id_playlist);
                System.out.println(cartao_cidadao);
                stmt.setInt(2, cartao_cidadao);
                stmt.setInt(3, cartao_cidadao);
                stmt.executeUpdate();

                c.commit();
                System.out.println("PlayList adicionada");
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("PlayList já existente!");
        }

    }

    private static void pesquisar(int tipo_info) {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader_linha = new BufferedReader(input);
        String nome;
        PreparedStatement stmt;
        Scanner sc = new Scanner(System.in);
        int opcao = 1;
        while (opcao == 1) {
            System.out.println("Insira o nome: ");
            try {
                nome = reader_linha.readLine();
                switch (tipo_info) {
                    case 13://musica
                        try {
                            stmt = c.prepareStatement("SELECT * FROM musica where nome=?;");
                            stmt.setString(1, nome);
                            ResultSet rs = stmt.executeQuery();
                            rs.next();
                            System.out.println(rs.getString(1) + " : " + rs.getString(2) + " : "
                                    + rs.getString(3));
                        } catch (SQLException e) {
                            System.out.println("Musica inexistente. Tentar outra vez? (1 - Sim | 0 - Nao)");
                        }
                        break;
                    case 14://album
                        try {
                            stmt = c.prepareStatement("SELECT * FROM album where nome=?;");
                            stmt.setString(1, nome);
                            ResultSet rs = stmt.executeQuery();
                            rs.next();
                            System.out.println(rs.getString(1) + " : " + rs.getString(2) +
                                    " : " + rs.getString(3) + " : " + rs.getString(4));
                        } catch (SQLException e) {
                            System.out.println("Album inexistente. Tentar outra vez? (1 - Sim | 0 - Nao)");
                        }
                        break;
                    case 15://artista
                        try {
                            stmt = c.prepareStatement("SELECT * FROM artista where nome=?;");
                            stmt.setString(1, nome);
                            ResultSet rs = stmt.executeQuery();
                            rs.next();
                            System.out.println(rs.getString(0) + " : " + rs.getString(1)
                                    + " : " + rs.getString(2));
                        } catch (SQLException e) {
                            System.out.println("Artista inexistente. Tentar outra vez? (1 - Sim | 0 - Nao)");
                        }
                        break;
                    default:
                        break;
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            opcao = sc.nextInt();
        }
    }

    private static void listar(int tipo_info) {
        PreparedStatement stmt;
        switch (tipo_info) {
            case 14://album
                try {
                    stmt = c.prepareStatement("SELECT * FROM album;");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        System.out.println("\nAlbum: " + rs.getString(1)
                                + " : " + rs.getString(2) + " : " + rs.getString(3) + " : " + rs.getString(4));
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                }
                break;
            case 15://artista
                try {
                    stmt = c.prepareStatement("SELECT * FROM artista;");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        System.out.println("\nArtista: " + rs.getString(1)
                                + " : " + rs.getString(2) + " : " + rs.getString(3));
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                }
                break;
            case 13://musica
                try {
                    stmt = c.prepareStatement("SELECT * FROM musica;");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        System.out.println("\nMusica: " + rs.getString(1) + " : " + rs.getString(2)
                                + " : " + rs.getString(3) + " : " + rs.getString(4));
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                }
                break;
            case 16://utilizador
                try {
                    stmt = c.prepareStatement("SELECT * FROM utilizador;");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        System.out.println("\nUtilizador: " + rs.getString(1) + " : " + rs.getString(2));
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                }
                break;
            case 17: //playlist
                try {
                    stmt = c.prepareStatement("SELECT * from playlist, utilizador where playlist.utilizador_cartao_cidadao = utilizador.cartao_cidadao;");
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        System.out.println("\nPlaylist: " + rs.getString(2) + " : " + rs.getString(3) + " : " + rs.getString(4) +
                                " : from : " + rs.getString(6) + " : " + rs.getString(7));
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                }
                break;
            case 18://playlist de utilizador corrente
                try {
                    stmt = c.prepareStatement("SELECT * FROM playlist_utilizador where playlist_utilizador.playlist_utilizador_cartao_cidadao=?;");
                    stmt.setInt(1, cartao_cidadao);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        System.out.println("\nPlaylist: " + rs.getString(1) + " : " + rs.getString(2) + " : " + rs.getString(3));
                    }
                } catch (SQLException e) {
                    System.out.println(e);
                }
                break;
            default:
                break;
        }
    }

    private static boolean verificaUser(String username) {//verifica se user existe
        String user = null;
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM utilizador where nome=?;");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            user = rs.getString("nome");
            if (user.equals(username)) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private static boolean verificaUserEmpty() {
        try {
            Statement statement = c.createStatement();
            String sql = "SELECT * FROM utilizador";

            ResultSet rs = statement.executeQuery(sql);

            if (!rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private static void privilegio(int tipo) {
        //dar privilegio 1
        //retirar privilegio 2
        String nome;
        Scanner sc = new Scanner(System.in);
        if (tipo == 1)
            System.out.println("Qual o utilizador a quem quer dar privilégios de editor?");
        else
            System.out.println("Qual o utilizador a quem quer dar privilégios de editor?");
        nome = sc.nextLine();
        PreparedStatement stmt;
        if (verificaUser(nome)) {
            try {
                c.setAutoCommit(false);
                if (tipo == 1)
                    stmt = c.prepareStatement("UPDATE utilizador SET utilizador_tipo = true WHERE nome=?");
                else
                    stmt = c.prepareStatement("UPDATE utilizador SET utilizador_tipo = false WHERE nome=?");
                stmt.setString(1, nome);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
            System.out.println("Privilégios atualizados");
        } else {
            System.out.println("Utilizador não encontrado");
        }
    }

    private static void editar_musica() { //System.out.println("Nome -Letra- Concerto Album Artista Funcao_do_artista");
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        Scanner sc = new Scanner(System.in);
        String nome_musica = null, letra_musica = null, concerto_musica = null;
        String[] b;
        int opc;

        System.out.println("Qual o nome da musica que pretende editar?");
        try {
            nome_musica = reader.readLine();
            System.out.println("Letra da musica ou (--n)");
        } catch (IOException e) {
            System.out.println(e);
        }

        System.out.println("Pretende editar [1]Letra ou [2]Concerto [0]Voltar?\n");
        opc = sc.nextInt();
        if (opc == 0)
            return;
        if (verifica_musica(nome_musica)) {
            try {
                c.setAutoCommit(false);
                switch (opc) {
                    case 1://alterar a letra
                        System.out.println("Qual é a letra?");
                        letra_musica = reader.readLine();
                        PreparedStatement stmt = c.prepareStatement("UPDATE musica SET letra = ? WHERE nome=?");
                        stmt.setString(1, letra_musica);
                        stmt.setString(2, nome_musica);
                        stmt.executeUpdate();

                        stmt.close();
                        c.commit();
                        break;
                    case 2://alterar a o concerto
                        System.out.println("Qual o novo género do álbum?");
                        concerto_musica = reader.readLine();
                        PreparedStatement stmt1 = c.prepareStatement("UPDATE musica SET concertos = ? WHERE nome=?");
                        System.out.println("HEHEHEHEHEHHEHEH");
                        stmt1.setString(1, concerto_musica);
                        stmt1.setString(2, nome_musica);
                        stmt1.executeUpdate();

                        stmt1.close();
                        c.commit();
                        break;
                    default:
                        System.out.println("Escolha uma opcao válida");
                }
            } catch (SQLException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Musica não encontrada");
        }
    }

    private static void editar_album() {
        String nome, descricao, genero;
        int opc;
        Scanner sc = new Scanner(System.in);
        Scanner sc1 = new Scanner(System.in);
        System.out.println("Qual o nome do album que pretende editar? ");
        nome = sc.nextLine();

        System.out.println("Pretende editar [1]Descricao ou [2]genero [0]Voltar?\n");
        opc = sc.nextInt();
        if (opc == 0)
            return;
        if (verificaAlbum(nome)) {
            try {
                c.setAutoCommit(false);
                switch (opc) {
                    case 1:
                        System.out.println("Qual a nova descricao?");
                        descricao = sc1.nextLine();
                        PreparedStatement stmt = c.prepareStatement("UPDATE album SET descricao = ? WHERE nome=? ");
                        stmt.setString(1, descricao);
                        stmt.setString(2, nome);
                        stmt.executeUpdate();

                        stmt.close();
                        c.commit();
                        break;
                    case 2:
                        System.out.println("Qual o novo género do álbum?");
                        genero = sc1.nextLine();
                        PreparedStatement stmt1 = c.prepareStatement("UPDATE album SET genero = ? WHERE nome=?");

                        stmt1.setString(1, genero);
                        stmt1.setString(2, nome);
                        stmt1.executeUpdate();

                        stmt1.close();
                        c.commit();
                        break;
                    default:
                        System.out.println("Escolha uma opcao válida");
                }
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Album não encontrado");
        }
    }

    private static void eliminar_musica() {
        String nome;
        int id_musica;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome da musica que quer eliminar?");
        nome = sc.nextLine();
        id_musica = verifica_id_musica(nome);
        if (id_musica == 0) {
            System.out.println("Musica inexsitente");
            return;
        }
        if (verifica_musica(nome)) {
            try {
                c.setAutoCommit(false);
                //apagar da tabela musica
                PreparedStatement stmt = c.prepareStatement("DELETE FROM musica WHERE nome=?");
                stmt.setString(1, nome);
                stmt.executeUpdate();
                //apagar da tabela musica_album
                stmt = c.prepareStatement("DELETE FROM musica_album WHERE musica_album.musica_idmusica=?");
                stmt.setInt(1, id_musica);
                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
            System.out.println("Musica Eliminado!");
        } else {
            System.out.println("Musica não encontrada...");
        }
    }

    private static int verifica_id_musica(String nome) {
        int id_musica = 0;
        PreparedStatement stmt = null;

        try {
            stmt = c.prepareStatement("SELECT musica.idmusica FROM musica WHERE musica.nome = ?");
            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            id_musica = rs.getInt(1);

            return id_musica;
        } catch (SQLException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            return 0;
        }
        return id_musica;
    }

    private static void elimina_artista() {
        String nome, tipo;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome do artista que quer eliminar?");
        nome = sc.nextLine();
        System.out.println("Qual o tipo do artista que quer eliminar?");
        tipo = sc.nextLine();
        if (verificaArtista(nome, tipo)) {
            try {
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("DELETE FROM artista WHERE nome=?");
                stmt.setString(1, nome);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
            System.out.println("Artista Eliminado!");
        } else {
            System.out.println("Artista não encontrado...");
        }


    }

    private static void elimina_album() {
        int id;
        String nome;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome do album que pretende eliminar?");
        nome = sc.nextLine();
        if (verificaAlbum(nome)) {
            try {
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("DELETE FROM album where nome=?");
                stmt.setString(1, nome);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
            System.out.println("Album eliminado");
        } else {
            System.out.println("Album não encontrado");
        }
    }

    private static void inserir_musica() { //falta inserir a duracao da musica
        String a, nome_musica = null, letra_musica = null, concerto_musica = null, album_musica = null, nome_artista_musica = null, funcao_artista_musica = null, tipo_artista = null;
        int posicao_musica, ciclo_go = 0;
        Date album_musica_data_lancamento = null;
        String[] b;
        Scanner sc = new Scanner(System.in);
        int id_musica = (int) (Math.random() + 1) * Integer.MAX_VALUE;

        while (ciclo_go == 0) {
            System.out.println("Nome -Letra- Concerto Album Artista Funcao_do_artista");
            //teste teste teste teste teste teste
            a = sc.nextLine();
            b = a.split(" ");
            nome_musica = b[0];
            letra_musica = b[1];
            concerto_musica = b[2];
            album_musica = b[3];
            nome_artista_musica = b[4];
            funcao_artista_musica = b[5];
            album_musica_data_lancamento = verifica_data_lancamento_album(album_musica);
            tipo_artista = verifica_tipo_artista(nome_artista_musica);
            //verificacoes para tudo correr bem
            if (album_musica_data_lancamento == null) {
                System.out.println("Album inexistente");
                ciclo_go = 0;
            } else if (tipo_artista == null) {
                System.out.println("Artista Inexistente");
                ciclo_go = 0;
            } else
                ciclo_go = 1;

            if (ciclo_go == 0) {
                System.out.println("Continuar? (0-Sim | 1-Nao)");
                ciclo_go = sc.nextInt();
            }
        }
        System.out.println("Antes de verifica");
        posicao_musica = verifia_tamanho_album(album_musica);
        System.out.println("Antes de fazer print depois de verifica");
        System.out.println("\nverifica posicao: " + posicao_musica);
        if (!verifica_musica(nome_musica)) {
            try {
                System.out.println("\ndentro de sql");
                c.setAutoCommit(false);
                //inserir na tabela musica
                PreparedStatement stmt = c.prepareStatement("INSERT INTO musica(idmusica, nome, letra, concertos, posicao) " + "VALUES (default ,?,?,?,?)");
                insere_musica_tabela_musica(stmt, nome_musica, letra_musica, concerto_musica, posicao_musica);
                //inserir na tabela musica_album
                stmt = c.prepareStatement("INSERT INTO musica_album(musica_idmusica, album_nome, album_data_lancamento) " + "VALUES (DEFAULT ,?,?)");
                insere_musica_tabela_musica_album(stmt, album_musica, album_musica_data_lancamento);
                stmt = c.prepareStatement("INSERT INTO musica_artista(funcao, artista_nome, artista_tipo_artista, musica_idmusica) " + "VALUES (?,?,?,?)");
                insere_musica_musica_artista(stmt, funcao_artista_musica, nome_artista_musica, tipo_artista, id_musica);
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Musica já existente!");
        }
    }

    private static void insere_musica_musica_artista(PreparedStatement stmt, String funcao_artista_musica, String nome_artista_musica, String tipo_artista, int id_musica) {
        try {
            stmt.setString(1, funcao_artista_musica);
            stmt.setString(2, nome_artista_musica);
            stmt.setString(3, tipo_artista);
            stmt.setInt(4, id_musica);
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static String verifica_tipo_artista(String nome_artista) {
        String tipo_artista = null;
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("SELECT artista.tipo_artista FROM artista where nome=?;");
            stmt.setString(1, nome_artista);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            tipo_artista = rs.getString(1);
            return tipo_artista;
        } catch (SQLException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            return null;
        }
        return tipo_artista;
    }

    private static Date verifica_data_lancamento_album(String nome_album) {
        Date data_lancamento = null;
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("SELECT album.data_lancamento FROM album where nome=?;");
            stmt.setString(1, nome_album);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            data_lancamento = rs.getDate(1);
            return data_lancamento;
        } catch (SQLException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            return null;
        }
        return data_lancamento;
    }

    private static void insere_musica_tabela_musica_album(PreparedStatement stmt, String
            album_musica, Date album_musica_data_lancamento) {
        try {
            stmt.setString(1, album_musica);
            stmt.setDate(2, (java.sql.Date) album_musica_data_lancamento);
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static void insere_musica_tabela_musica(PreparedStatement stmt, String
            nome_musica, String letra_musica, String concerto_musica, int posicao_musica) {
        try {

            stmt.setString(1, nome_musica);
            stmt.setString(2, letra_musica);
            stmt.setString(3, concerto_musica);
            stmt.setInt(4, posicao_musica);
            stmt.executeUpdate();
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private static int verifia_tamanho_album(String nome_album) {
        int tamanho = 0;
        PreparedStatement stmt = null;
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM album;");
            while (rs.next()) {
                tamanho = rs.getInt("total");
            }
            return tamanho;
        } catch (SQLException e) {
            System.out.println(e);
        } catch (NullPointerException n) {
            return 0;
        }
        return tamanho;
    }

    private static void inserir_artista() {
        String a, nome, tipo, informacao;
        String[] b;
        Scanner sc = new Scanner(System.in);
        System.out.println("Nome Tipo_artista Informação");
        a = sc.nextLine();
        b = a.split(" ");
        nome = b[0];
        tipo = b[1];
        informacao = b[2];
        if (!verificaArtista(nome, tipo)) {
            try {
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("INSERT INTO artista(nome, tipo_artista, informacao) " + "VALUES (?,?,?)");
                stmt.setString(1, nome);
                stmt.setString(2, tipo);
                stmt.setString(3, informacao);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Artista já existente!");
        }
    }

    @Deprecated
    private static void inserir_album() {
        String a, nome, genero, descricao;
        java.sql.Date data;
        String[] b, d;
        int dia, mes, ano;
        Scanner sc = new Scanner(System.in);
        System.out.println("Nome Data_lancamento(dd/mm/aaaa) Genero Descricao");
        a = sc.nextLine();
        b = a.split(" ");
        d = b[1].split("/");
        nome = b[0];
        genero = b[2];
        descricao = b[3];
        dia = Integer.parseInt(d[0]);
        mes = Integer.parseInt(d[1]) - 1;
        ano = Integer.parseInt(d[2]) - 1900;
        data = new java.sql.Date(ano, mes, dia);
        if (!verificaAlbum(nome)) {
            try {
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("INSERT INTO album(nome, data_lancamento, genero, descricao)" + "VALUES (?,?,?,?)");
                stmt.setString(1, nome);
                stmt.setDate(2, data);
                stmt.setString(3, genero);
                stmt.setString(4, descricao);

                stmt.executeUpdate();
                c.commit();
                System.out.println("Artista adicionado");
            } catch (SQLException e) {
                System.out.println(e);
            }
        } else {
            System.out.println("Album já existente!");
        }

    }

    private static boolean verifica_musica(String nome) {
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM musica where nome=?;");
            stmt.setString(1, nome);
            return verifica(stmt);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private static boolean verificaAlbum(String nome) {
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM album where nome=? ;");
            stmt.setString(1, nome);
            return verifica(stmt);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;

    }

    private static boolean verificaArtista(String nome, String tipo) {
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM artista where nome=? AND tipo_artista=?;");
            stmt.setString(1, nome);
            stmt.setString(2, tipo);
            return verifica(stmt);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private static boolean verifica_playlist(String nome) {
        try {
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM playlist where nome=?;");
            stmt.setString(1, nome);
            return verifica(stmt);
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private static boolean verifica(PreparedStatement stmt) {
        try {
            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) {
                stmt.close();
                System.out.println("false");
                return false;
            } else {
                System.out.println("true");
                stmt.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }

    private static int procura_id_playlist(String nome) {
        int id_playlist = 0;
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("SELECT playlist.id_playlist FROM playlist WHERE playlist.nome = ?");
            stmt.setString(1, nome);

            ResultSet rs = stmt.executeQuery();
            rs.next();
            id_playlist = rs.getInt(1);

            return id_playlist;
        } catch (SQLException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            return 0;
        }
        return id_playlist;
    }
    private static int getMusicId(String nome){
        int id = 0;
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT idmusica FROM musica where nome=?");
            stmt.setString(1,nome);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            id = rs.getInt("idmusica");
        } catch (SQLException e) {
           // System.out.println("Musica não encontrada");
        }
        return id;
    }
    private static int getUtilizadorId(String nome){
        int id = 0;
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT cartao_cidadao FROM utilizador where nome=?");
            stmt.setString(1,nome);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            id = rs.getInt("cartao_cidadao");
        } catch (SQLException e) {
            System.out.println("Não encontrou utilizador");
        }
        return id;
    }
    private static int getFicheiroIdWithMusicId(int music_id){
        int id = 0;
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT ficheiro_id FROM upload where musica_id=?");
            stmt.setInt(1,music_id);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            id = rs.getInt("ficheiro_id");
        } catch (SQLException e) {
            System.out.println("Não encontrou ficheiro");
        }
        return id;
    }


}