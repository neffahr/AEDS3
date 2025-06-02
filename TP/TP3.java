import java.io.*;
import java.util.*;

public class TP3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Carregar dados");
            System.out.println("2 - Compactar arquivo");
            System.out.println("3 - Descompactar arquivo");
            System.out.println("4 - Procurar padrão (KMP)");
            System.out.println("5 - Procurar padrão (BM)");
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
                        // Compressão Huffman + LZW
                        break;

                    case 3:
                        // Descompressão Huffman + LZW
                        break;

                    case 4:
                        // Casamento de Padrão por KMP
                        break;

                    case 5:
                        // Casamento de padrão por Boyer Moore
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
}