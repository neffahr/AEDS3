import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.math.BigInteger;
import java.util.ArrayList;

public class TP4 {
    public final static String ARQUIVO_VGN = "./arqs/registrosVGN.enc";
    public final static String ARQUIVO_RSA = "./arqs/registrosRSA.enc";
    public final static String ARQUIVO_CHAVES = "./arqs/chavesRSA.bin";

    public static void main(String[] args) throws ClassNotFoundException {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Carregar dados");
            System.out.println("2 - Vigênere: Criptografar Registro");
            System.out.println("3 - Vigênere: Descriptografar Registro");
            System.out.println("4 - RSA: Criptografar Registro");
            System.out.println("5 - RSA: Descriptografar Registro");
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

                    //  ========= CRIPTOGRAFIA VIGÊNERE =========
                    // Criptografa apenas o registro selecionado
                    case 2:
                        System.out.print("Digite o id do registro a ser criptografado: ");
                        int idVgn = scanner.nextInt();
                        scanner.nextLine(); // Limpar o buffer

                        System.out.print("Digite a chave de criptografia: ");
                        String chaveVgn = scanner.nextLine();

                        byte[] dadosCriptVgn = Registro.registroParaBytes(Registro.read(idVgn));
                        dadosCriptVgn = Vigenere.criptografar(dadosCriptVgn, chaveVgn.getBytes());
                        escreverArquivo(ARQUIVO_VGN, dadosCriptVgn);
                        System.out.println("Arquivo criptografado com sucesso em: " + ARQUIVO_VGN);
                        break;

                    // Descriptografa o registro criptografado e exibe
                    case 3:
                        System.out.print("Digite a chave de descriptografia: ");
                        String chaveDescriptVgn = scanner.nextLine();

                        byte[] dadosDescriptVgn = lerArquivo(ARQUIVO_VGN);
                        dadosDescriptVgn = Vigenere.descriptografar(dadosDescriptVgn, chaveDescriptVgn.getBytes());
                        Registro regVgn = Registro.bytesParaRegistro(dadosDescriptVgn);
                        System.out.println(regVgn);
                        break;

                    //  ========= CRIPTOGRAFIA RSA =========
                    // Criptografa o registro selecionado com RSA e salva as chaves
                    case 4:
                        System.out.print("Digite o id do registro a ser criptografado: ");
                        int idRSA = scanner.nextInt();
                        scanner.nextLine();

                        System.out.print("Digite o tamanho da chave (em bits): ");
                        int tamanhoChave = scanner.nextInt();
                        scanner.nextLine();

                        RSA.ParDeChaves chavesCriptRSA = RSA.gerarChaves(tamanhoChave);
                        escreverChavesRSA(ARQUIVO_CHAVES, chavesCriptRSA);

                        byte[] dadosRSA = Registro.registroParaBytes(Registro.read(idRSA));
                        BigInteger[] dadosCriptRSA = RSA.criptografar(new String(dadosRSA), chavesCriptRSA.getChavePublica());
                        escreverRSA(ARQUIVO_RSA, dadosCriptRSA);

                        System.out.println("Arquivo criptografado com sucesso em: " + ARQUIVO_RSA);
                        break;

                    // Descriptografa o registro criptografado com RSA e exibe
                    case 5:
                        BigInteger[] rawRSA = lerRSA(ARQUIVO_RSA);

                        // Lê as chaves RSA do arquivo
                        RSA.ParDeChaves chavesDecriptRSA = new RSA.ParDeChaves(
                                new RSA.ChavePublica(lerChavesRSA(ARQUIVO_CHAVES)[0], lerChavesRSA(ARQUIVO_CHAVES)[1]),
                                new RSA.ChavePrivada(lerChavesRSA(ARQUIVO_CHAVES)[0], lerChavesRSA(ARQUIVO_CHAVES)[2])
                        );

                        byte[] dadosDescriptRSA = (RSA.descriptografar(rawRSA, chavesDecriptRSA.getChavePrivada())).getBytes();
                        Registro regRSA = Registro.bytesParaRegistro(dadosDescriptRSA);
                        System.out.println(regRSA);
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

    // Função para escrever chaves RSA
    private static void escreverChavesRSA(String nomeArquivo, RSA.ParDeChaves parDeChaves) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
            // Chave pública
            BigInteger n = parDeChaves.getChavePublica().getModulo();
            BigInteger e = parDeChaves.getChavePublica().getExpoente();
            // Chave privada
            BigInteger d = parDeChaves.getChavePrivada().getExpoente();

            byte[] nBytes = n.toByteArray();
            byte[] eBytes = e.toByteArray();
            byte[] dBytes = d.toByteArray();

            // Escreve o tamanho e os bytes de n
            fos.write((nBytes.length >> 24) & 0xFF);
            fos.write((nBytes.length >> 16) & 0xFF);
            fos.write((nBytes.length >> 8) & 0xFF);
            fos.write(nBytes.length & 0xFF);
            fos.write(nBytes);

            // Escreve o tamanho e os bytes de e
            fos.write((eBytes.length >> 24) & 0xFF);
            fos.write((eBytes.length >> 16) & 0xFF);
            fos.write((eBytes.length >> 8) & 0xFF);
            fos.write(eBytes.length & 0xFF);
            fos.write(eBytes);

            // Escreve o tamanho e os bytes de d
            fos.write((dBytes.length >> 24) & 0xFF);
            fos.write((dBytes.length >> 16) & 0xFF);
            fos.write((dBytes.length >> 8) & 0xFF);
            fos.write(dBytes.length & 0xFF);
            fos.write(dBytes);
        }
    }

    private static BigInteger[] lerChavesRSA(String nomeArquivo) throws IOException {
        byte[] data = lerArquivo(nomeArquivo);
        BigInteger[] chaves = new BigInteger[3];
        int i = 0;
        for (int j = 0; j < 3; j++) {
            int len = ((data[i] & 0xFF) << 24) | ((data[i+1] & 0xFF) << 16) | ((data[i+2] & 0xFF) << 8) | (data[i+3] & 0xFF);
            i += 4;
            byte[] bloco = new byte[len];
            System.arraycopy(data, i, bloco, 0, len);
            chaves[j] = new BigInteger(bloco);
            i += len;
        }
        return chaves; // [n, e, d]
    }

    // Função para escrever dados criptografados RSA
    private static void escreverRSA(String nomeArquivo, BigInteger[] array) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(nomeArquivo)) {
            for (BigInteger bigInt : array) {
                byte[] bytes = bigInt.toByteArray();
                // Escreve o tamanho do array de bytes (4 bytes)
                fos.write((bytes.length >> 24) & 0xFF);
                fos.write((bytes.length >> 16) & 0xFF);
                fos.write((bytes.length >> 8) & 0xFF);
                fos.write(bytes.length & 0xFF);
                // Escreve os bytes do BigInteger
                fos.write(bytes);
            }
        }
    }

    private static BigInteger[] lerRSA(String nomeArquivo) throws IOException {
        byte[] data = lerArquivo(nomeArquivo);
        ArrayList<BigInteger> lista = new ArrayList<>();
        int i = 0;
        while (i < data.length) {
            // Lê o tamanho do bloco (4 bytes)
            int len = ((data[i] & 0xFF) << 24) | ((data[i+1] & 0xFF) << 16) | ((data[i+2] & 0xFF) << 8) | (data[i+3] & 0xFF);
            i += 4;
            // Lê os bytes do bloco
            byte[] bloco = new byte[len];
            System.arraycopy(data, i, bloco, 0, len);
            lista.add(new BigInteger(bloco));
            i += len;
        }
        return lista.toArray(new BigInteger[0]);
    }
}
