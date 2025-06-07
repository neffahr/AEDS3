import java.io.ByteArrayOutputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.PriorityQueue;

class HuffmanNode implements Comparable<HuffmanNode> {
    byte b;
    int frequencia;
    HuffmanNode esq, dir;

    public HuffmanNode(byte b, int f) {
        this.b = b;
        this.frequencia = f;
        esq = dir = null;
    }

    @Override
    public int compareTo(HuffmanNode o) {
        return this.frequencia - o.frequencia;
    }
}

class HuffmanResult {
    public final byte[] dados;
    public final int bitsValidos;
    public HuffmanResult(byte[] dados, int bitsValidos) {
        this.dados = dados;
        this.bitsValidos = bitsValidos;
    }
}

public class Huffman {
    private static long getInputBitLength(byte[] sequencia, HashMap<Byte, String> codigos) {
        long tamanho = 0;
        for (byte b : sequencia) {
            String codigo = codigos.get(b);
            if (codigo != null) {
                tamanho += codigo.length();
            }
        }
        return tamanho;
    }

    public static HashMap<Byte, String> getHuffmanHash(byte[] sequencia, int versao) throws java.io.IOException {
        HashMap<Byte, Integer> freqMap = new HashMap<>();
        for (byte c : sequencia) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Byte b : freqMap.keySet()) {
            pq.add(new HuffmanNode(b, freqMap.get(b)));
        }

        while (pq.size() > 1) {
            HuffmanNode esq = pq.poll();
            HuffmanNode dir = pq.poll();

            HuffmanNode pai = new HuffmanNode((byte)0, esq.frequencia + dir.frequencia);
            pai.esq = esq;
            pai.dir = dir;

            pq.add(pai);
        }

        HuffmanNode raiz = pq.poll();
        HashMap<Byte, String> codigos = new HashMap<>();
        constroiCodigos(raiz, "", codigos);
        salvarArv(raiz, versao, getInputBitLength(sequencia, codigos));

        return codigos;
    }

    public static void salvarArv(HuffmanNode raiz, int versao, long bitsValidos) throws java.io.IOException {
        try (java.io.DataOutputStream out = new java.io.DataOutputStream(new java.io.FileOutputStream("./arqs/ArvHuffman" + versao + ".bin"))) {
            out.writeLong(bitsValidos); // Escreve a quantidade de bits válidos no início
            salvarArv(raiz, out);
        }
    }

    private static void salvarArv(HuffmanNode no, java.io.DataOutputStream out) throws java.io.IOException {
        if (no == null) {
            out.writeByte(0); // nó nulo
            return;
        }
        if (no.esq == null && no.dir == null) {
            out.writeByte(1); // folha
            out.writeByte(no.b);
        } else {
            out.writeByte(2); // nó interno
            salvarArv(no.esq, out);
            salvarArv(no.dir, out);
        }
    }

    public static HuffmanNode lerArv(RandomAccessFile in) throws java.io.IOException {
        int tipo = in.readByte();
        if (tipo == 0) return null; // nó nulo
        if (tipo == 1) { // folha
            byte b = in.readByte();
            return new HuffmanNode(b, 0);
        }
        // tipo == 2, nó interno
        HuffmanNode no = new HuffmanNode((byte)0, 0);
        no.esq = lerArv(in);
        no.dir = lerArv(in);
        return no;
    }

    private static void constroiCodigos(HuffmanNode no, String codigo, HashMap<Byte, String> codigos) {
        if (no == null) {
            return;
        }

        if (no.b != 0) {
            codigos.put(no.b, codigo);
        }

        constroiCodigos(no.esq, codigo + "0", codigos);
        constroiCodigos(no.dir, codigo + "1", codigos);
    }

    public static byte[] comprimir(byte[] sequencia, HashMap<Byte, String> codigos) {
        StringBuilder input = new StringBuilder();
        for (byte b : sequencia) {
            input.append(codigos.get(b));
        }

        // Converte a sequência de bits para um array de bytes
        int tamanho = (input.length() + 7) / 8; // Arredonda para cima
        byte[] output = new byte[tamanho];
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '1') {
                output[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
        return output;
    }    

    // Versão buscando na tabela de códigos.
    public static byte[] decodificar(byte[] sequencia, HuffmanNode raiz, long bitsValidos) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HuffmanNode atual = raiz;
        for (int i = 0; i < bitsValidos; i++) {
            int byteIndex = i / 8;
            int bitIndex = 7 - (i % 8);
            boolean bit = ((sequencia[byteIndex] >> bitIndex) & 1) == 1;
            atual = bit ? atual.dir : atual.esq;
            if (atual.esq == null && atual.dir == null) { // folha
                output.write(atual.b);
                atual = raiz;
            }
        }
        return output.toByteArray();
    }
}
