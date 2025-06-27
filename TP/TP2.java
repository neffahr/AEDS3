import java.io.*;
import java.util.*;
import java.time.LocalDate;

public class TP2 {
    private static enum EstruturaDados {
        NENHUMA,
        ARVORE_B,
        HASH,
        LISTA_INVERTIDA
    }
    
    private static EstruturaDados estruturaAtual = EstruturaDados.NENHUMA;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int opcao;
        boolean dadosCarregados = true;
        
        RandomAccessFile raf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        if (raf.length() == 0) {dadosCarregados = false;}
        raf.close();

        do {
            if (!dadosCarregados) {
                System.out.println("\n=== MENU PRINCIPAL ===");
                System.out.println("1 - Carregar dados");
                System.out.println("0 - Sair");
                System.out.print("Escolha uma opção: ");
                
                opcao = scanner.nextInt();
                scanner.nextLine();
                
                switch (opcao) {
                    case 1:
                        try {
                            Registro.loadData();
                            System.out.println("Dados carregados com sucesso.");
                            dadosCarregados = true;
                        } catch (IOException e) {
                            System.out.println("Erro ao carregar dados: " + e.getMessage());
                        }
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                        break;
                }
            } else if (estruturaAtual == EstruturaDados.NENHUMA) {
                System.out.println("\n=== ESCOLHA A ESTRUTURA DE DADOS ===");
                System.out.println("1 - Árvore B");
                System.out.println("2 - Hash");
                System.out.println("3 - Lista Invertida");
                System.out.println("0 - Sair");
                System.out.print("Escolha uma opção: ");
                
                opcao = scanner.nextInt();
                scanner.nextLine();
                
                switch (opcao) {
                    case 1:
                        estruturaAtual = EstruturaDados.ARVORE_B;
                        System.out.println("Estrutura de dados selecionada: Árvore B");
                        inicializarArvoreB(scanner);
                        break;
                    case 2:
                        estruturaAtual = EstruturaDados.HASH;
                        System.out.println("Estrutura de dados selecionada: Hash");
                        inicializarHash(scanner);
                        break;
                    case 3:
                        estruturaAtual = EstruturaDados.LISTA_INVERTIDA;
                        System.out.println("Estrutura de dados selecionada: Lista Invertida");
                        inicializarListaInvertida(scanner);
                        break;
                    case 0:
                        System.out.println("Saindo...");
                        break;
                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                        break;
                }
            } else {
                switch (estruturaAtual) {
                    case ARVORE_B:
                        opcao = menuArvoreB(scanner);
                        break;
                    case HASH:
                        opcao = menuHash(scanner);
                        break;
                    case LISTA_INVERTIDA:
                        opcao = menuListaInvertida(scanner);
                        break;
                    default:
                        opcao = 0;
                        break;
                }
                
                if (opcao == -1) {
                    estruturaAtual = EstruturaDados.NENHUMA;
                    opcao = 1;
                }
            }
        } while (opcao != 0);

        scanner.close();
    }
    
    private static void inicializarArvoreB(Scanner scanner) {
        try {
            // Código para inicializar a estrutura de árvore B
            System.out.println("Inicializando estrutura de Árvore B...");
            menuArvoreB(scanner);
        } catch (Exception e) {
            System.out.println("Erro ao inicializar Árvore B: " + e.getMessage());
        }
    }
    
    private static void inicializarHash(Scanner scanner) {
        try {
            // Código para inicializar a estrutura hash
            System.out.println("Inicializando estrutura Hash...");
            menuHash(scanner);
        } catch (Exception e) {
            System.out.println("Erro ao inicializar Hash: " + e.getMessage());
        }
    }
    
    private static void inicializarListaInvertida(Scanner scanner) {
        try {
            // Código para inicializar a lista invertida
            System.out.println("Inicializando Lista Invertida...");
            menuListaInvertida(scanner);
        } catch (Exception e) {
            System.out.println("Erro ao inicializar Lista Invertida: " + e.getMessage());
        }
    }
    
    private static int menuArvoreB(Scanner scanner) throws Exception {
        int ordem;
        Btree arvb;

        if (new File(Btree.INDEX_FILE).exists()) {
            DataInputStream dis = new DataInputStream(new FileInputStream(new File(Btree.METADADOS_FILE)));
            ordem = dis.readInt();
            arvb = new Btree(ordem);
            dis.close();
        } else {
            RandomAccessFile meta = new RandomAccessFile(Btree.METADADOS_FILE, "rw");
            System.out.println("\n=== INICIALIZAÇÃO ===");
            System.out.print("Ordem da Arvore B+: ");
            ordem = scanner.nextInt();
            scanner.nextLine();
            arvb = new Btree(ordem);

            meta.seek(0);
            meta.writeInt(ordem);
            meta.close();

            // arvb.loadBtree();

        }

        System.out.println("\n=== MENU ÁRVORE B ===");
        System.out.println("1 - Criar registro");
        System.out.println("2 - Buscar registro por ID");
        System.out.println("3 - Buscar lista de registros por ID");
        System.out.println("4 - Atualizar registro");
        System.out.println("5 - Deletar registro");
        System.out.println("6 - Voltar ao menu de estruturas");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
        
        int opcao = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (opcao) {
                case 1:
                    RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
                    Registro reg_create = inputReg(scanner);

                    Registro.create(reg_create, dataf); // Inserção no arquivo de dados
                    arvb.create(reg_create, dataf);

                    System.out.println("Registro criado com sucesso na Árvore B.");
                    dataf.close();
                    break;

                case 2:
                    System.out.print("ID do registro: ");
                    int id = scanner.nextInt();

                    Registro reg_read = arvb.read(id);
                    if(reg_read == null) {System.out.println("Registro não encontrado");} 
                    else {System.out.println(reg_read);}
                    break;

                case 3:
                    System.out.print("ID inicial: ");
                    int id_init = scanner.nextInt();
                    System.out.print("ID final: ");
                    int id_fim = scanner.nextInt();

                    ArrayList<Registro> regs = arvb.search(id_init, id_fim);
                    if (regs == null) {System.out.println("Registros não encontrados");}
                    else {
                        for (Registro reg : regs) {System.out.println(reg);}
                    }
                    break;

                case 4:
                    Registro newreg = inputReg(scanner);
                    if(!arvb.update(newreg)) {System.out.println("Registro não encontrado");}
                    else {System.out.println("Registro atualizado com sucesso");}
                    break;

                case 5:
                    System.out.print("ID do registro a ser deletado: ");
                    int id_del = scanner.nextInt();

                    if(!arvb.delete(id_del)) {System.out.println("Registro não encontrado");}
                    else {System.out.println("Registro deletado com sucesso");}

                    break;

                case 6:
                    return -1;
                case 0:
                    return 0;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Erro ao executar a operação: " + e.getMessage());
        }
        return opcao;
    }
    
    private static int menuHash(Scanner scanner) throws Exception {
        int capBucket;
        IdxHash idx;

        if (new File(IdxHash.HASH_METADADOS).exists()) {
            DataInputStream dis = new DataInputStream(new FileInputStream(new File(IdxHash.HASH_METADADOS)));
            capBucket = dis.readInt();
            idx = new IdxHash(capBucket);
        } else {
            System.out.println("\n=== INICIALIZAÇÃO ===");
            System.out.print("Escolha a capacidade de um bucket: ");
            capBucket = scanner.nextInt();
            scanner.nextLine();
            new DataOutputStream(new FileOutputStream(new File(IdxHash.HASH_METADADOS))).writeInt(capBucket);
            idx = new IdxHash(capBucket);
            idx.loadHash();
        }

        System.out.println("\n=== MENU HASH ===");
        System.out.println("1 - Criar registro");
        System.out.println("2 - Buscar registro por ID");
        System.out.println("3 - Atualizar registro");
        System.out.println("4 - Deletar registro");
        System.out.println("5 - Voltar ao menu de estruturas");
        System.out.println("0 - Sair");
        System.out.print("Escolha uma opção: ");
        
        int opcao = scanner.nextInt();
        scanner.nextLine();
        
        try {
            switch (opcao) {
                case 1:
                    Registro reg = inputReg(scanner);
                    long pos = Registro.getLength();
                    RandomAccessFile file_create = new RandomAccessFile(Registro.DB_BINARIO, "rw");
                    Registro.create(reg, file_create);
                    idx.create(Registro.getLastId(), pos);
                    System.out.println("Registro criado com sucesso na tabela Hash.");
                    file_create.close();
                    break;
                case 2:
                    System.out.print("ID: ");
                    int id = scanner.nextInt();
                    pos = idx.read(id);
                    if (pos < 0)
                        System.out.println("Elemento não encontrado");
                    else {
                        RandomAccessFile file_read = new RandomAccessFile(Registro.DB_BINARIO, "rw");
                        file_read.seek(pos+5); // pular lapide + tam_reg
                        System.out.println(Registro.readIB(file_read));
                        file_read.close();
                    }
                    System.out.println("Busca por ID implementada para Hash.");
                    break;
                case 3:
                    Registro newreg = inputReg(scanner);
                    pos = Registro.getLength();
                    Registro.update(newreg);
                    
                    System.out.println("Atualização implementada para Hash.");
                    break;
                case 4:
                    System.out.print("ID do registro a ser deletado: ");
                    int idDel = scanner.nextInt();
                    Registro.delete(idDel);
                    idx.delete(idDel);
                    System.out.println("Deleção implementada para Hash.");
                    break;
                case 5:
                    return -1;
                case 0:
                    return 0;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Erro ao executar a operação: " + e.getMessage());
        }
        
        return opcao;
    }
    
    private static int menuListaInvertida(Scanner scanner) throws Exception {
        ListaInvertida lista;
        RandomAccessFile file = new RandomAccessFile(Registro.DB_BINARIO, "rw");

        if (!(new File(ListaInvertida.LIST_DICIONARIO).exists())) {
            lista = new ListaInvertida(100);
            lista.loadList();
        } else {lista = new ListaInvertida(100);}
        
        System.out.println("\n=== MENU LISTA INVERTIDA ===");
        System.out.println("1 - Criar registro");
        System.out.println("2 - Buscar registro por título");
        System.out.println("3 - Atualizar registro");
        System.out.println("4 - Deletar registro");
        System.out.println("5 - Voltar ao menu de estruturas");
        System.out.println("0 - Sair"); 
        System.out.print("Escolha uma opção: ");
        
        int opcao = scanner.nextInt();
        scanner.nextLine();
        
        try {
            long pos;
            Registro reg;
            switch (opcao) {
                case 1:
                    reg = inputReg(scanner);
                    pos = file.length();
                    Registro.create(reg, file);

                    if (lista.create(reg, pos)) 
                        System.out.println("Registro criado com sucesso na Lista Invertida.");
                    else 
                        System.out.println("Falha em criar. Registro já existe");
                    break;

                case 2:
                    System.out.print("Título do registro: ");
                    String title_read = scanner.nextLine();
                    reg = lista.read(title_read);

                    if (reg == null)
                        System.out.println("Elemento não encontrado");
                    else {
                        System.out.println(reg);
                        System.out.println("Busca implementada para Lista Invertida.");
                    }
                    break;

                case 3:
                    System.out.println("Título a ser atualizado: ");
                    String title_up = scanner.nextLine();
                    Registro newreg = inputReg(scanner);

                    if (!Registro.update(newreg)) { System.out.println("Registro não encontrado");}
                    else {
                        int tam = lista.read(title_up).calcTamReg();
                        lista.delete(title_up);
                        if (newreg.calcTamReg() <= tam) {
                            lista.create(newreg, Registro.getPos(newreg, file));
                        }
                        else {lista.create(newreg, file.length());}
                        System.out.println("Registro atualizado com sucesso.");
                    }
                    break;

                case 4:
                    System.out.print("Nome do registro a ser deletado: ");
                    String title_del = scanner.nextLine();
                    Registro.delete(lista.read(title_del).getId());
                    lista.delete(title_del);
                    System.out.println("Registro deletado com sucesso.");
                    System.out.println("Deleção implementada para Lista Invertida.");
                    break;
                case 5:
                    return -1;
                case 0:
                    return 0; 
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        } catch (Exception e) {
            System.out.println("Erro ao executar a operação: " + e.getMessage());
        }
        
        return opcao;
    }

    private static Registro inputReg(Scanner scanner) {
        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Título original: ");
        String orgTitle = scanner.nextLine();

        System.out.print("Título: ");
        String title = scanner.nextLine();

        System.out.print("Idioma original (2 caracteres): ");
        String orgLanguage = scanner.nextLine();

        System.out.print("Descrição: ");
        String ovr = scanner.nextLine();

        System.out.print("Data de lançamento (YYYY-MM-DD): ");
        LocalDate releaseDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Popularidade: ");
        float popularity = scanner.nextFloat();

        System.out.print("Número de votos: ");
        int voteCount = scanner.nextInt();

        System.out.print("Média de votos: ");
        float voteAverage = scanner.nextFloat();

        System.out.print("Duração em minutos: ");
        int runtime = scanner.nextInt();

        System.out.print("Filme adulto? (1 = Sim, 0 = Não): ");
        byte adult = scanner.nextByte();
        scanner.nextLine(); 

        System.out.print("Gêneros (separados por vírgula): ");
        String[] genresArray = scanner.nextLine().split(",");
        List<String> genreList = Arrays.asList(genresArray);

        return new Registro(id, orgTitle, title, orgLanguage.getBytes(), ovr, releaseDate, popularity, voteCount, voteAverage, runtime, adult, genreList);
    }
}