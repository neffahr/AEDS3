import java.util.*;

public class BM {

    // Busca o padrão usando o algoritmo Boyer-Moore em um array de bytes
    public static List<Integer> buscarPadrao(byte[] texto, byte[] padrao) {
        List<Integer> ocorrencias = new ArrayList<>();
        if (padrao.length == 0 || texto.length < padrao.length) return ocorrencias;

        // Tabela de último índice de cada byte do padrão
        int[] charRuim = new int[256];
        Arrays.fill(charRuim, -1);
        for (int i = 0; i < padrao.length; i++) {
            charRuim[padrao[i] & 0xFF] = i;
        }

        int shift = 0;
        while (shift <= texto.length - padrao.length) {
            int j = padrao.length - 1;
            // Compara da direita para a esquerda
            while (j >= 0 && padrao[j] == texto[shift + j]) {
                j--;
            }
            if (j < 0) {
                ocorrencias.add(shift); // Encontrou o padrão
                shift += (shift + padrao.length < texto.length) ? padrao.length - charRuim[texto[shift + padrao.length] & 0xFF] : 1;
            } else {
                shift += Math.max(1, j - charRuim[texto[shift + j] & 0xFF]);
            }
        }
        return ocorrencias;
    }
}