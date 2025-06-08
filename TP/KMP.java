import java.util.ArrayList;
import java.util.List;

public class KMP {
    private static int[] calcVetorFalhas(byte[] padrao) {
        int[] falhas = new int[padrao.length];
        int len = 0; // Comprimento do prefixo mais longo
        falhas[0] = 0; // O primeiro caractere não tem prefixo

        for (int i = 1; i < padrao.length; i++) {
            if (padrao[i] == padrao[len]) {
                falhas[i] = ++len;
            }
            else {
                if (len != 0) { 
                    len = falhas[len - 1];
                } else {
                    // Se não houver prefixo, o comprimento é 0
                    falhas[i] = 0;
                }
            }
        }
        return falhas;
    }

    public static List<Integer> buscarPadrao(byte[] texto, byte[] padrao) {
        int n = texto.length;
        int m = padrao.length;
        int[] vf = calcVetorFalhas(padrao);
        List<Integer> ocorrencias = new ArrayList<>();

        int i = 0; // Índice para texto
        int j = 0; // Índice para padrão
        while (i<n) {
            if (texto[i] == padrao[j]) {
                i++;
                j++;

                // Se j atingir o comprimento do padrão, encontramos uma ocorrência
                if (j == m) {
                    ocorrencias.add(i - j);
                    
                    // Pega a ultima posição de sucesso para calcular shift
                    // Continua procurando outras ocorrências
                    j = vf[j - 1];
                }
            } 
            else {
                // Usar pega a ultima posição de sucesso do vetor de falhas
                if (j != 0)
                    j = vf[j - 1];
                else
                    i++;
            }
        }
        return ocorrencias;
    }

}
