import java.util.*;
public class LZW {

    private static final int DICT_LIMIT = 4096; // Limite do dicionário (12 bits)

    // Compressão LZW: transforma um array de bytes em um array comprimido
    public static byte[] compressao(byte[] input) {
        if (input == null || input.length == 0) return new byte[0];

        Map<List<Byte>, Integer> dicionario = new HashMap<>();
        int tamDicio = 256;
        for (int i = 0; i < 256; i++) {
            dicionario.put(Arrays.asList((byte) i), i);
        }

        List<Integer> resultado = new ArrayList<>();
        List<Byte> w = new ArrayList<>();

        for (byte b : input) {
            List<Byte> wb = new ArrayList<>(w);
            wb.add(b);
            if (dicionario.containsKey(wb)) {
                w = wb;
            } else {
                resultado.add(dicionario.get(w));
                if (tamDicio < DICT_LIMIT) {
                    dicionario.put(wb, tamDicio++);
                }
                w = new ArrayList<>();
                w.add(b);
            }
        }
        if (!w.isEmpty()) {
            resultado.add(dicionario.get(w));
        }

        // Escreve os códigos como 2 bytes cada
        byte[] output = new byte[resultado.size() * 2];
        for (int i = 0; i < resultado.size(); i++) {
            int code = resultado.get(i);
            output[i * 2] = (byte) ((code >> 8) & 0xFF);
            output[i * 2 + 1] = (byte) (code & 0xFF);
        }
        return output;
    }

    // Descompressão LZW: transforma um array comprimido em um array original
    public static byte[] descompressao(byte[] input) {
        if (input == null || input.length == 0) return new byte[0];
        if (input.length % 2 != 0) throw new IllegalArgumentException("Arquivo comprimido corrompido.");

        Map<Integer, List<Byte>> dicio = new HashMap<>();
        int tamDicio = 256;
        for (int i = 0; i < 256; i++) {
            dicio.put(i, Arrays.asList((byte) i));
        }

        // Lê os códigos de 2 em 2 bytes
        List<Integer> codes = new ArrayList<>();
        for (int i = 0; i < input.length; i += 2) {
            int code = ((input[i] & 0xFF) << 8) | (input[i + 1] & 0xFF);
            codes.add(code);
        }
        
        List<Byte> resultado = new ArrayList<>();
        List<Byte> w = new ArrayList<>(dicio.get(codes.get(0)));
        resultado.addAll(w);

        for (int i = 1; i < codes.size(); i++) {
            int k = codes.get(i);
            List<Byte> entry;
            if (dicio.containsKey(k)) {
                entry = dicio.get(k);
            } else if (k == tamDicio) {
                entry = new ArrayList<>(w);
                entry.add(w.get(0));
            } else {
                throw new IllegalArgumentException("Código inválido durante a descompressão: " + k);
            }
            resultado.addAll(entry);

            // Adiciona nova entrada ao dicionário
            if (tamDicio < DICT_LIMIT) {
                List<Byte> newEntry = new ArrayList<>(w);
                newEntry.add(entry.get(0));
                dicio.put(tamDicio++, newEntry);
            }

            w = entry;
        }

        // Converte List<Byte> para byte[]
        byte[] output = new byte[resultado.size()];
        for (int i = 0; i < resultado.size(); i++) {
            output[i] = resultado.get(i);
        }
        return output;
    }
}