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
                    // Update len to the previous lps value 
                    // to avoid redundant comparisons
                    len = falhas[len - 1];
                } else {
                    // If no matching prefix found, set lps[i] to 0
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

                // If the entire pattern is matched 
                // store the start index in result
                if (j == m) {
                    ocorrencias.add(i - j);
                    
                    // Use LPS of previous index to 
                    // skip unnecessary comparisons
                    j = vf[j - 1];
                }
            } 
            // If there is a mismatch
            else {
                // Use lps value of previous index
                // to avoid redundant comparisons
                if (j != 0)
                    j = vf[j - 1];
                else
                    i++;
            }
        }
        return ocorrencias;
    }

}
