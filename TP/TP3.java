import java.io.*;
import java.util.*;

public class TP3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

       do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Carregar dados");
            System.out.println("2 - Compactar arquivo (Huffman & LZW)");
            System.out.println("3 - Descompactar arquivo (Huffman & LZW)");
            System.out.println("4 - Procurar padrão (KMP)");
            System.out.println("5 - Procurar padrão (BM)");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");
            String entrada = scanner.nextLine();
            try {
                opcao = Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                opcao = -1;
            }

            try {
                HashMap<Byte, String> codigos;
                byte[] dados;

                switch (opcao) {
                    case 1:
                        Registro.loadData();
                        System.out.println("Dados carregados com sucesso.");
                        break;

                    case 2:
                        System.out.print("Digite a versão da compressão (ex: 1): ");
                        String versaoComp = scanner.nextLine();
                        String nomeSaidaLZW = "./arqs/registrosLZWCompressao" + versaoComp + ".bin";
                        String nomeSaidaHuffman = "./arqs/registrosHuffmanCompressao" + versaoComp + ".bin";
                        dados = lerArquivo(Registro.DB_BINARIO);
                        
                        //Compressao LZW
                        long inicioCompLZW = System.currentTimeMillis();
                        byte[] comprimidoLZW = LZW.compressao(dados);
                        long fimCompLZW = System.currentTimeMillis();
                        escreverArquivo(nomeSaidaLZW, comprimidoLZW);

                        // Compressao Huffman
                        long inicioCompHuffman = System.currentTimeMillis();
                        codigos = Huffman.getHuffmanHash(dados, Integer.parseInt(versaoComp));
                        byte[] comprimidoHuffman = Huffman.comprimir(dados, codigos);
                        long fimCompHuffman = System.currentTimeMillis();
                        escreverArquivo(nomeSaidaHuffman, comprimidoHuffman);

                        double ganhoLZW = 100.0 * (1.0 - ((double)comprimidoLZW.length / dados.length));
                        System.out.printf("Arquivo compactado (LZW) salvo como: %s\n", nomeSaidaLZW);
                        System.out.printf("Tempo de execução: %.2f ms\n", (fimCompLZW - inicioCompLZW) * 1.0);
                        System.out.printf("Ganho de compressão: %.2f%%\n", ganhoLZW);

                        double ganhoHuffman = 100.0 * (1.0 - ((double)comprimidoHuffman.length / dados.length));
                        System.out.printf("Arquivo compactado (Huffman) salvo como: %s\n", nomeSaidaHuffman);
                        System.out.printf("Tempo de execução: %.2f ms\n", (fimCompHuffman - inicioCompHuffman) * 1.0);
                        System.out.printf("Ganho de compressão: %.2f%%\n", ganhoHuffman);
                        
                        break;

                    case 3:
                        System.out.print("Digite a versão da compressão (ex: 1): ");
                        String versaoDescomp = scanner.nextLine();
                        String nomeCompLZW = "./arqs/registrosLZWCompressao" + versaoDescomp + ".bin";
                        String nomeCompHuffman = "./arqs/registrosLZWCompressao" + versaoDescomp + ".bin";
                        byte[] dadosCompLZW = lerArquivo(nomeCompLZW);
                        byte[] dadosCompHuffman = lerArquivo(nomeCompHuffman);
                        dados = lerArquivo(Registro.DB_BINARIO);

                        //Descompressao LZW
                        long inicioDescompLZW = System.currentTimeMillis();
                        byte[] descomprimidoLZW = LZW.descompressao(dadosCompLZW);
                        long fimDescompLZW = System.currentTimeMillis();
                        escreverArquivo(Registro.DB_BINARIO, descomprimidoLZW);

                        //Descompressao Huffman
                        long inicioDescompHuffman = System.currentTimeMillis();

                        java.io.RandomAccessFile raf = new java.io.RandomAccessFile("./arqs/ArvHuffman" + versaoDescomp + ".bin", "r");
                        long bitsValidos = raf.readLong(); // lê os 8 primeiros bytes
                        HuffmanNode arv = Huffman.lerArv(raf);
                        
                        byte[] descomprimidoHuffman = Huffman.decodificar(dadosCompHuffman, arv, bitsValidos);
                        long fimDescompHuffman = System.currentTimeMillis();
                        escreverArquivo("./arqs/registros2.bin", descomprimidoLZW);
                        
                        System.out.printf("Tempo de execução (LZW): %.2f ms\n", (fimDescompLZW - inicioDescompLZW) * 1.0);
                        double perdaLZW = 100.0 * (1.0 - ((double)dadosCompLZW.length / descomprimidoLZW.length));
                        System.out.printf("Perda de compressão: %.2f%%\n", perdaLZW);

                        System.out.printf("Tempo de execução: %.2f ms\n", (fimDescompHuffman - inicioDescompHuffman) * 1.0);
                        double perdaHuffman = 100.0 * (1.0 - ((double)dadosCompHuffman.length / descomprimidoHuffman.length));
                        System.out.printf("Perda de compressão: %.2f%%\n", perdaHuffman);
                        break;
                    
                    case 4:
                        //Busca KMP
                        System.out.print("Digite o nome do arquivo para busca: ");
                        String arquivoKMP = "./arqs/" + scanner.nextLine();
                        System.out.print("Digite o padrão a ser buscado: ");
                        String textpadraoKMP = scanner.nextLine();

                        byte[] textoKMP = lerArquivo(arquivoKMP);
                        byte[] padraoKMP = textpadraoKMP.getBytes();
                        long inicioKMP = System.currentTimeMillis();
                        List<Integer> posicoesKMP = KMP.buscarPadrao(textoKMP, padraoKMP);
                        long fimKMP = System.currentTimeMillis();

                        if (posicoesKMP.isEmpty()) {
                            System.out.println("Padrão não encontrado.");
                        } else {
                            System.out.println("Padrão encontrado nas posições: " + posicoesKMP);
                        }
                        System.out.printf("Tempo de execução: %.2f ms\n", (fimKMP - inicioKMP) * 1.0);
                        break;

                    case 5:
                        //Busca Boyer-Moore
                        System.out.print("Digite o nome do arquivo para busca: ");
                        String arquivoBM = "./arqs/" + scanner.nextLine();
                        System.out.print("Digite o padrão a ser buscado: ");
                        String textpadraoBM = scanner.nextLine();

                        byte[] textoBuscaBM = lerArquivo(arquivoBM);
                        byte[] padraoBM = textpadraoBM.getBytes();
                        long inicioBM = System.currentTimeMillis();
                        List<Integer> posicoesBM = BM.buscarPadrao(textoBuscaBM, padraoBM);
                        long fimBM = System.currentTimeMillis();

                        if (posicoesBM.isEmpty()) {
                            System.out.println("Padrão não encontrado.");
                        } else {
                            System.out.println("Padrão encontrado nas posições: " + posicoesBM);
                        }
                        System.out.printf("Tempo de execução: %.2f ms\n", (fimBM - inicioBM) * 1.0);
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

    // Função utilitária para ler arquivo binário
    private static byte[] lerArquivo(String nomeArquivo) throws IOException {
        File file = new File(nomeArquivo);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return data;
    }

    // Função utilitária para escrever arquivo binário
    private static void escreverArquivo(String nomeArquivo, byte[] dados) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
            fos.write(dados);
        }
    }
}