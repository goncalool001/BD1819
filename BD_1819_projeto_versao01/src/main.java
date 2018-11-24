import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Scanner;
import java.sql.*;

public class main {
    private static Connection c = null;
    public static void main(String args[]) throws IOException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bd1819","postgres", "default");
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

        int opcao = 0;

        do {
            System.out.println("-----------Menu Inicial-----------\n"
                    + "[1]Login\n"
                    + "[2]Registar\n"
                    + "[3]Sair\n");

            opcao = sc.nextInt();

            switch (opcao) {
                case 1: //login
                    System.out.println("Nome Passe");
                    frase = reader.readLine();
                    frase_chave_valor = frase.split(" ");
                    username = frase_chave_valor[0];
                    password = frase_chave_valor[1];
                    if(login(username, password)){
                        if(verificaUser(username)){
                            menu_login_editor();
                        }
                    }else{
                        menu_inicial();
                    }
                    //}else
                    break;
                case 2://registar
                    System.out.println("Nome Passe");
                    frase = reader.readLine();
                    frase_chave_valor = frase.split(" ");
                    username = frase_chave_valor[0];
                    password = frase_chave_valor[1];

                    registar(username, password);
                    break;
            }
        } while (opcao != 3);
    }
    private static boolean login(String username, String passe){//verifica se existe um utilizador com username e pass indicada
        PreparedStatement stmt = null;
        try {
            stmt = c.prepareStatement("SELECT * FROM utilizador where nome = ? and passe = ?;");
            stmt.setString(1,username);
            stmt.setString(2,passe);

            ResultSet rs = stmt.executeQuery();
            if(rs.next()!=false) {
                return true;
            }
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return false;
    }
    private static void registar(String username, String passe){
        if(verificaUserEmpty()){//se nao houver users na bd é editor
            try {
                c.setAutoCommit(false);
                Utilizador util = new Utilizador(username,passe,true);
                PreparedStatement stmt = c.prepareStatement("INSERT INTO utilizador(cartao_cidadao, nome, passe, utilizador_tipo)"+"VALUES (DEFAULT,?,?,true)");
                stmt.setString(1,util.getUsername());
                stmt.setString(2,util.getPassword());
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);;
            }
        }else {
            try {
                c.setAutoCommit(false);
                Utilizador util = new Utilizador(username, passe, false);
                PreparedStatement stmt = c.prepareStatement("INSERT INTO utilizador(cartao_cidadao, nome, passe, utilizador_tipo)" + "VALUES (DEFAULT,?,?,false)");
                stmt.setString(1, util.getUsername());
                stmt.setString(2, util.getPassword());
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            }catch (SQLException e){
                System.out.println(e);
            }
        }

    }
    private static void menu_login_editor(){
        Scanner reader = new Scanner(System.in);
        /*if( System.getProperty( "os.name" ).startsWith( "Window" ) )
            Runtime.getRuntime().exec("cls");
        else
            Runtime.getRuntime().exec("clear");*/
        int opcao;
        String nomeMusica;
        Scanner sc = new Scanner(System.in);
        do {//manter o login aberto a nao ser que peça para sair
            System.out.println("-----------Menu de Editor-----------\n"
                    + "[1]Listar música\n"
                    + "[2]Gerir artistas\n"
                    + "[3]Gerir álbuns\n"
                    + "[4]Gerir músicas\n"
                    + "[5]Dar privilégios de editor a um utilizador\n"
                    + "[6]Consultar detalhes album\n"
                    + "[7]Consultar detalhes artista\n"
                    + "[8]Upload de música\n"
                    + "[9]Download de música\n"
                    + "[10]Partilhar uma música\n"
                    + "[11]Pesquisar uma musica\n"
                    + "[0]Logout");

            opcao = reader.nextInt();
            int o, op;

            switch (opcao) {
                case 1: //listar as musicas
                    System.out.println("A listar musicas");
                    //listar_musicas();
                    break;
                case 2: //gerir artistas
                    System.out.println("Pretende [1]adicionar, [2]editar ou [3]eliminar um artista [0]Sair");
                    o = sc.nextInt();
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
                    System.out.println("Pretende [1]adicionar, [2]editar ou [3]eliminar uma album");
                    o = sc.nextInt();
                    switch (o) {
                        case 1:
                            inserir_album();
                            break;
                        case 2:
                            editar_album();
                            break;
                        case 3:
                            //elimina_album();
                            break;
                        default:
                            System.out.println("Introduza uma opção válida!");
                            break;
                    }
                    break;
                case 4: //gerir musica
                    System.out.printf("Pretende [1]adicionar, [2]editar ou [3]eliminar uma musica");
                    o = sc.nextInt();
                    switch (o) {
                        case 1:
                            //inserir_musica();
                            break;
                        case 2:
                            break;
                        case 3:
                            //elimina_musica();
                            break;
                        default:
                            System.out.println("Introduza uma opção válida!");
                            break;
                    }
                    break;
                case 5: // privilégios de editor
                    darPrivilegio();
                    break;
                case 6: //detalhes do album numero 14
                    //pesquisar(14);
                    break;
                case 7: //detalhe artista numero 15
                    //pesquisar(15); /// a fazer
                    break;
                case 8: //upload de musica
                    /*server_i.enviaStringAoMulticast("16");
                    System.out.println("Qual o nome da Música a fazer upload?\n");
                    nomeMusica = sc.nextLine();
                    TimeUnit.MILLISECONDS.sleep(200);
                    uploadTCP(nomeMusica);*/
                    break;
                case 9: //download de musica
                    break;
                case 10: //partilhar musica
                    break;
                case 11://pesquisar musica numero 13
                    //pesquisar(13);
                    break;
                case 0:
                    System.out.println("Logout");
                    break;
                default:
                    System.out.println("Insira uma opção válida!");
            }
        }while (opcao != 0);

    }
    private static boolean verificaUser(String username){//verifica se user existe
        String user = null;
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM utilizador where nome=?;");
            stmt.setString(1,username);
            ResultSet rs = stmt.executeQuery();
            rs.next();
            user = rs.getString("nome");
            if(user.equals(username)){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static boolean verificaUserEmpty(){
        try{
            Statement statement = c.createStatement();
            String sql = "SELECT * FROM utilizador";

            ResultSet rs = statement.executeQuery(sql);

            if(!rs.next()){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static void darPrivilegio(){
        String nome;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o utilizador a quem quer dar privilégios de editor?");
        nome = sc.nextLine();
        if(verificaUser(nome)){
            try {
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("UPDATE utilizador SET utilizador_tipo = true WHERE nome=?" );
                stmt.setString(1,nome);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
            System.out.println("Privilégios atualizados");
        }else{
            System.out.println("Utilizador não encontrado");
        }
    }
    private static void editar_album(){
        String nome,a,descricao,genero;
        String [] b;
        java.sql.Date data;
        int dia, mes, ano,opc;
        Scanner sc = new Scanner(System.in);
        Scanner sc1 = new Scanner(System.in);
        System.out.println("Qual o nome do album que pretende editar? ");
        nome = sc.nextLine();
        System.out.println("Data de lançamento do album(dd/mm/aaaa)");
        a = sc.nextLine();
        b = a.split("/");
        dia =  Integer.parseInt(b[0]);
        mes =  Integer.parseInt(b[1])-1;
        ano =  Integer.parseInt(b[2])-1900;
        data = new java.sql.Date(ano, mes, dia);
        System.out.println("Pretende editar [1]Descricao ou [2]genero?");
        opc = sc.nextInt();
        if(verificaAlbum(nome, data)){
            try {
                c.setAutoCommit(false);
                switch (opc){
                    case 1:
                        System.out.println("Qual a nova descricao?");
                        descricao = sc1.nextLine();
                        PreparedStatement stmt = c.prepareStatement("UPDATE album SET descricao = ? WHERE nome=? AND data_lancamento=?");
                        stmt.setString(1,descricao);
                        stmt.setString(2,nome);
                        stmt.setDate(3,data);
                        stmt.executeUpdate();

                        stmt.close();
                        c.commit();
                        break;
                    case 2:
                        System.out.println("Qual o novo género do álbum?");
                        genero = sc1.nextLine();
                        PreparedStatement stmt1 = c.prepareStatement("UPDATE album SET genero = ? WHERE nome=? AND data_lancamento=?");
                        System.out.println("HEHEHEHEHEHHEHEH");
                        stmt1.setString(1,genero);
                        stmt1.setString(2,nome);
                        stmt1.setDate(3,data);
                        stmt1.executeUpdate();

                        stmt1.close();
                        c.commit();
                        break;
                    default:
                        System.out.println("Escolha uma opcao válida");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Album não encontrado");
        }
    }
    private static void elimina_artista(){
        String nome,tipo;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome do artista que quer eliminar?");
        nome = sc.nextLine();
        System.out.println("Qual o tipo do artista que quer eliminar?");
        tipo = sc.nextLine();
        if(verificaArtista(nome,tipo)){
            try{
                c.setAutoCommit(false);
                PreparedStatement stmt =  c.prepareStatement("DELETE FROM artista WHERE nome=?");
                stmt.setString(1,nome);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                System.out.println(e);
            }
            System.out.println("Artista Eliminado!");
        }else{
            System.out.println("Artista não encontrado...");
        }


    }
    private static void inserir_artista(){
        String a,nome, tipo, informacao;
        String [] b;
        Scanner sc = new Scanner(System.in);
        System.out.println("Nome Tipo_artista Informação");
        a = sc.nextLine();
        b = a.split(" ");
        nome = b[0];
        tipo = b[1];
        informacao = b[2];
        if(!verificaArtista(nome, tipo)){
            try{
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("INSERT INTO artista(nome, tipo_artista, informacao) "+"VALUES (?,?,?)");
                stmt.setString(1,nome);
                stmt.setString(2,tipo);
                stmt.setString(3,informacao);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
        } catch (SQLException e) {
            System.out.println(e);
        }
        }else{
            System.out.println("Artista já existente!");
        }
    }
    @Deprecated
    private static void inserir_album(){
        String a,nome, genero, descricao;
        java.sql.Date data;
        String [] b,d;
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
        mes = Integer.parseInt(d[1])-1;
        ano = Integer.parseInt(d[2])-1900;
        data = new java.sql.Date(ano,mes,dia);
        if(!verificaAlbum(nome,data)){
            try{
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("INSERT INTO album(nome, data_lancamento, genero, descricao)"+"VALUES (?,?,?,?)");
                stmt.setString(1,nome);
                stmt.setDate(2, data);
                stmt.setString(3, genero);
                stmt.setString(4, descricao);

                stmt.executeUpdate();
                c.commit();
                System.out.println("Artista adicionado");
            } catch (SQLException e) {
                System.out.println(e);
            }
        }else {
            System.out.println("Album já existente!");
        }

    }
    private static boolean verificaAlbum(String nome, java.sql.Date data){
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM album where nome=? AND data_lancamento=?;");
            stmt.setString(1,nome);
            stmt.setDate(2, data);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                stmt.close();
                return false;
            }
            else{
                stmt.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }
    private static boolean verificaArtista(String nome, String tipo){
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM artista where nome=? AND tipo_artista=?;");
            stmt.setString(1,nome);
            stmt.setString(2,tipo);
            ResultSet rs = stmt.executeQuery();
            if(!rs.next()){
                stmt.close();
                System.out.println("false");
                return false;
            }
            else{
                System.out.println("true");
                stmt.close();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

