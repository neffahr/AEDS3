import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TP2 {
    public static final String HASH_BUCKETS = "./arqs/hash_buckets.bin";
    public static final String HASH_DIRETORIO = "./arqs/hash_diretorio.bin";
    public static final String HASH_METADADOS = "./arqs/hash_meta.bin";
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
        boolean dadosCarregados = false;

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
                        inicializarArvoreB();
                        break;
                    case 2:
                        estruturaAtual = EstruturaDados.HASH;
                        System.out.println("Estrutura de dados selecionada: Hash");
                        inicializarHash();
                        break;
                    case 3:
                        estruturaAtual = EstruturaDados.LISTA_INVERTIDA;
                        System.out.println("Estrutura de dados selecionada: Lista Invertida");
                        inicializarListaInvertida();
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
    
    private static void inicializarArvoreB() {
        try {
            // Código para inicializar a estrutura de árvore B
            System.out.println("Inicializando estrutura de Árvore B...");
            // ArvoreB.inicializar();
            // TO DO: inicializar arquivo
            // TO DO: tacar menu
        } catch (Exception e) {
            System.out.println("Erro ao inicializar Árvore B: " + e.getMessage());
        }
    }
    
    private static void inicializarHash() {
        try {
            // Código para inicializar a estrutura hash
            System.out.println("Inicializando estrutura Hash...");
            // Hash.inicializar();
        } catch (Exception e) {
            System.out.println("Erro ao inicializar Hash: " + e.getMessage());
        }
    }
    
    private static void inicializarListaInvertida() {
        try {
            // Código para inicializar a lista invertida
            System.out.println("Inicializando Lista Invertida...");
            // ListaInvertida.inicializar();
        } catch (Exception e) {
            System.out.println("Erro ao inicializar Lista Invertida: " + e.getMessage());
        }
    }
    
    private static int menuArvoreB(Scanner scanner) {
        System.out.println("\n=== MENU ÁRVORE B ===");
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
                    // ArvoreB.inserir(reg);
                    System.out.println("Registro criado com sucesso na Árvore B.");
                    break;
                case 2:
                    System.out.print("ID do registro: ");
                    int id = scanner.nextInt();
                    // Registro resultado = ArvoreB.buscar(id);
                    // System.out.println(resultado != null ? resultado : "Registro não encontrado.");
                    System.out.println("Busca implementada para Árvore B.");
                    break;
                case 3:
                    Registro regAtualizado = inputReg(scanner);
                    // boolean resultAtualizacao = ArvoreB.atualizar(regAtualizado);
                    // System.out.println(resultAtualizacao ? "Registro atualizado com sucesso." : "Erro ao atualizar. Registro não existe.");
                    System.out.println("Atualização implementada para Árvore B.");
                    break;
                case 4:
                    System.out.print("ID do registro a ser deletado: ");
                    int idDel = scanner.nextInt();
                    // boolean resultDelecao = ArvoreB.deletar(idDel);
                    // System.out.println(resultDelecao ? "Registro deletado com sucesso." : "Erro ao deletar. Registro não existe.");
                    System.out.println("Deleção implementada para Árvore B.");
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
    
    private static int menuHash(Scanner scanner) throws Exception {
        int capBucket;

        if (new File(HASH_METADADOS).exists()) {
            DataInputStream dis = new DataInputStream(new FileInputStream(new File(HASH_METADADOS)));
            capBucket = dis.readInt();
        } else {
            System.out.println("\n=== INICIALIZAÇÃO ===");
            System.out.print("Escolha a capacidade de um bucket: ");
            capBucket = scanner.nextInt();
            scanner.nextLine();

            new DataOutputStream(new FileOutputStream(new File(HASH_METADADOS))).writeInt(capBucket);
        }

        IdxHash idx = new IdxHash(capBucket, HASH_DIRETORIO, HASH_BUCKETS);

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
                    System.out.print("ID do registro: ");
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
                    System.out.print("ID do registro: ");
                    id = scanner.nextInt();
                    Registro regAtualizado = inputReg(scanner);
                    pos = Registro.getLength();

                    if (Registro.update(regAtualizado)) {
                        idx.delete(id);
                        idx.create(id, pos);
                    }

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
    
    private static int menuListaInvertida(Scanner scanner) {
        System.out.println("\n=== MENU LISTA INVERTIDA ===");
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
                    // ListaInvertida.inserir(reg);
                    System.out.println("Registro criado com sucesso na Lista Invertida.");
                    break;
                case 2:
                    System.out.print("ID do registro: ");
                    int id = scanner.nextInt();
                    // Registro resultado = ListaInvertida.buscarPorId(id);
                    // System.out.println(resultado != null ? resultado : "Registro não encontrado.");
                    System.out.println("Busca por ID implementada para Lista Invertida.");
                    break;
                case 3:
                    Registro regAtualizado = inputReg(scanner);
                    // boolean resultAtualizacao = ListaInvertida.atualizar(regAtualizado);
                    // System.out.println(resultAtualizacao ? "Registro atualizado com sucesso." : "Erro ao atualizar. Registro não existe.");
                    System.out.println("Atualização implementada para Lista Invertida.");
                    break;
                case 4:
                    System.out.print("ID do registro a ser deletado: ");
                    int idDel = scanner.nextInt();
                    // boolean resultDelecao = ListaInvertida.deletar(idDel);
                    // System.out.println(resultDelecao ? "Registro deletado com sucesso." : "Erro ao deletar. Registro não existe.");
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
    
    private static void exibirResultados(List<Registro> resultados) {
        if (resultados == null || resultados.isEmpty()) {
            System.out.println("Nenhum registro encontrado.");
            return;
        }
        
        System.out.println("Registros encontrados: " + resultados.size());
        for (Registro reg : resultados) {
            System.out.println(reg);
        }
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