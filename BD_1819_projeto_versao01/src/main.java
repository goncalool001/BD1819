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
            //c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/bd1819","postgres", "default");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/base_dados_1819","postgres", "default");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        menu_inicial();base_dados_1819@localhost

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
                    System.out.println("Pretende [1]adicionar, [2]editar ou [3]eliminar um artista [0]Voltar");
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
                    System.out.println("Pretende [1]adicionar, [2]editar ou [3]eliminar uma album [0]Voltar");
                    o = sc.nextInt();
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
                    o = sc.nextInt();
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
    private static void editar_musica(){ //System.out.println("Nome -Letra- Concerto Album Artista Funcao_do_artista");
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        Scanner sc = new Scanner(System.in);
        String nome_musica=null,letra_musica=null, concerto_musica=null;
        String [] b;
        int opc;

        System.out.println("Qual o nome da musica que pretende editar?");
        try {
            nome_musica = reader.readLine();
            System.out.println("Letra da musica ou (--n)");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Pretende editar [1]Letra ou [2]Concerto [0]Voltar?\n");
        opc = sc.nextInt();
        if(opc == 0)
            return;
        if(verifica_musica(nome_musica)){
            try {
                c.setAutoCommit(false);
                switch (opc){
                    case 1://alterar a letra
                        System.out.println("Qual é a letra?");
                        letra_musica = reader.readLine();
                        PreparedStatement stmt = c.prepareStatement("UPDATE musica SET letra = ? WHERE nome=?");
                        stmt.setString(1,letra_musica);
                        stmt.setString(2,nome_musica);
                        stmt.executeUpdate();

                        stmt.close();
                        c.commit();
                        break;
                    case 2://alterar a o concerto
                        System.out.println("Qual o novo género do álbum?");
                        concerto_musica = reader.readLine();
                        PreparedStatement stmt1 = c.prepareStatement("UPDATE musica SET concertos = ? WHERE nome=?");
                        System.out.println("HEHEHEHEHEHHEHEH");
                        stmt1.setString(1,concerto_musica);
                        stmt1.setString(2,nome_musica);
                        stmt1.executeUpdate();

                        stmt1.close();
                        c.commit();
                        break;
                    default:
                        System.out.println("Escolha uma opcao válida");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Musica não encontrada");
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
        System.out.println("Pretende editar [1]Descricao ou [2]genero [0]Voltar?\n");
        opc = sc.nextInt();
        if(opc == 0)
            return;
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
    private static void eliminar_musica(){
        String nome;
        int id_musica;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome da musica que quer eliminar?");
        nome = sc.nextLine();
        id_musica = verifica_id_musica(nome);
        if(id_musica==0){
            System.out.println("Musica inexsitente");
            return;
        }
        if(verifica_musica(nome)){
            try{
                c.setAutoCommit(false);
                //apagar da tabela musica
                PreparedStatement stmt =  c.prepareStatement("DELETE FROM musica WHERE nome=?");
                stmt.setString(1,nome);
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
        }else{
            System.out.println("Musica não encontrada...");
        }
    }

    private static int verifica_id_musica(String nome){
        int id_musica = 0;
        PreparedStatement stmt = null;
        try {
            ResultSet rs = stmt.executeQuery("SELECT musica.idmusica as linha FROM musica WHERE musica.nome==nome;");
            while(rs.next()){
                id_musica = rs.getInt("linha");
            }
            return id_musica;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            return 0;
        }
        return id_musica;
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
    private static void elimina_album(){
        String nome;
        Scanner sc = new Scanner(System.in);
        System.out.println("Qual o nome do album que pretende eliminar?");
        nome = sc.nextLine();
        if(verificaAlbum(nome,)){
            try{
                c.setAutoCommit(false);
                PreparedStatement stmt = c.prepareStatement("DELETE FROM album where id_album=?");
                stmt.setInt(1,id);
                stmt.executeUpdate();

                stmt.close();
                c.commit();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.println("Album eliminado");
        }else{
            System.out.println("Artista não encontrado");
        }
    }
    private static void inserir_musica(){ //falta inserir a duracao da musica
        String a,nome_musica=null, letra_musica=null, concerto_musica=null, album_musica=null, nome_artista_musica=null, funcao_artista_musica=null, tipo_artista=null;
        int posicao_musica, ciclo_go=0;
        Date album_musica_data_lancamento=null;
        String [] b;
        Scanner sc = new Scanner(System.in);
        int id_musica=(int)(Math.random()+1)*Integer.MAX_VALUE;

        while(ciclo_go == 0) {
            System.out.println("Nome -Letra- Concerto Album Artista Funcao_do_artista");
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
                ciclo_go=0;
            }
            else if (tipo_artista == null) {
                System.out.println("Artista Inexistente");
                ciclo_go=0;
            }else
                ciclo_go=1;
            System.out.println("Continuar? (0-Sim | 1-Nao)");
            ciclo_go = sc.nextInt();
        }
        posicao_musica = verifia_tamanho_album(album_musica);
        if(!verifica_musica(nome_musica)){
            try{
                c.setAutoCommit(false);
                //inserir na tabela musica
                PreparedStatement stmt = c.prepareStatement("INSERT INTO musica(id_musica, nome_musica, letra_musica, concerto_musica, posicao_musica) "+"VALUES (?,?,?,?,?)");
                insere_musica_tabela_musica(stmt, id_musica, nome_musica, letra_musica, concerto_musica, posicao_musica);
                //inserir na tabela musica_album
                stmt = c.prepareStatement("INSERT INTO musica_album(id_musica, album_musica, album_musica_data_lancamento) "+"VALUES (?,?,?)");
                insere_musica_tabela_musica_album(stmt, id_musica, album_musica, album_musica_data_lancamento);
                stmt = c.prepareStatement("INSERT INTO musica_artista(funcao_artista_musica, nome_artista_musica, tipo_artista, id_musica) "+"VALUES (?,?,?,?)");
                insere_musica_musica_artista(stmt, funcao_artista_musica, nome_artista_musica, tipo_artista, id_musica);
            } catch (SQLException e) {
                System.out.println(e);
            }
        }else{
            System.out.println("Musica já existente!");
        }
    }
    private static void insere_musica_musica_artista(PreparedStatement stmt, String funcao_artista_musica, String nome_artista_musica, String tipo_artista, int id_musica){
        try {
            stmt.setString(1,funcao_artista_musica);
            stmt.setString(2,nome_artista_musica);
            stmt.setString(3, tipo_artista);
            stmt.setInt(4, id_musica);
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static String verifica_tipo_artista(String nome_artista){
        String tipo_artista = null;
        PreparedStatement stmt = null;
        try {
            ResultSet rs = stmt.executeQuery("SELECT artista.tipo_artista as linha FROM artista WHERE artista.nome == nome_artista;");
            while(rs.next()){
                tipo_artista = rs.getString("linha");
            }
            return tipo_artista;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            return null;
        }
        return tipo_artista;
    }
    private static Date verifica_data_lancamento_album(String nome_album){
        Date data_lancamento = null;
        PreparedStatement stmt = null;
        try {
            ResultSet rs = stmt.executeQuery("SELECT album.data_lancamento as linha FROM album WHERE album.nome == nome_album;");
            while(rs.next()){
                data_lancamento = rs.getDate("linha");
            }
            return data_lancamento;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            return null;
        }
        return data_lancamento;
    }
    private static void insere_musica_tabela_musica_album(PreparedStatement stmt, int id_musica, String album_musica, Date album_musica_data_lancamento){
        try {
            stmt.setInt(1,id_musica);
            stmt.setString(2,album_musica);
            stmt.setDate(3, (java.sql.Date) album_musica_data_lancamento);
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static void insere_musica_tabela_musica(PreparedStatement stmt, int id_musica, String nome_musica, String letra_musica, String concerto_musica, int posicao_musica){
        try {
            stmt.setInt(1,id_musica);
            stmt.setString(2,nome_musica);
            stmt.setString(3, letra_musica);
            stmt.setString(4, concerto_musica);
            stmt.setInt(5,posicao_musica);
            stmt.executeUpdate();
            stmt.close();
            c.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static int verifia_tamanho_album(String nome_album){
        int tamanho = 0;
        PreparedStatement stmt = null;
        try {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM album;");
            while(rs.next()){
                tamanho = rs.getInt("total");
            }
            return tamanho;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tamanho;
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
    private static boolean verifica_musica(String nome){
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM musica where nome=?;");
            stmt.setString(1,nome);
            return verifica(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static boolean verificaAlbum(String nome){
        try{
            PreparedStatement stmt = c.prepareStatement("SELECT * FROM album where nome=? ;");
            stmt.setString(1,nome);
            stmt.setDate(2, data);
            return verifica(stmt);
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
            return verifica(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    private static boolean verifica(PreparedStatement stmt){
        try {
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
