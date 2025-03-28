import java.util.*;
import java.time.LocalDate;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TP2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Carregar dados");
            System.out.println("2 - Criar registro");
            System.out.println("3 - Ler registro por ID");
            System.out.println("4 - Ler registro por título");
            System.out.println("5 - Atualizar registro");
            System.out.println("6 - Deletar registro");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();
            scanner.nextLine(); 

            try {
                switch (opcao) {
                    case 1:
                        Registro.loadData();
                        System.out.println("Dados carregados com sucesso.");
                        break;

                    case 2:
                        Registro reg = inputReg(scanner);
                        RandomAccessFile file = new RandomAccessFile(Registro.DB_BINARIO, "rw");
                        Registro.create(reg, file);
                        System.out.println("Registro criado com sucesso.");
                        file.close();
                        break;

                    case 3:
                        System.out.print("ID do registro: ");
                        int id = scanner.nextInt();
                        System.out.println(Registro.read(id));
                        break;

                    case 4:
                        System.out.print("Título do registro: ");
                        String title = scanner.nextLine();
                        System.out.println(Registro.read(title));
                        break;

                    case 5:
                        Registro regAtualizado = inputReg(scanner);
                        boolean resultup = Registro.update(regAtualizado);
                        if(resultup) {
                            System.out.println("Registro atualizado com sucesso.");
                        } else {
                            System.out.println("Erro ao atualizar. Registro não existe.");
                        }
                        break;

                    case 6:
                        System.out.print("ID do registro a ser deletado: ");
                        int idDel = scanner.nextInt();
                        boolean resultdel = Registro.delete(idDel);
                        if(resultdel) {
                            System.out.println("Registro deletado com sucesso.");
                        } else {
                            System.out.println("Erro ao deletar. Registro não existe.");
                        }
                        break;

                    case 0:
                        System.out.println("Saindo...");
                        break;

                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Erro ao executar a operação: " + e.getMessage());
            }
        } while (opcao != 0);

        scanner.close();
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