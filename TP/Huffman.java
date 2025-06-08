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

public class Huffman {
    // Calcula o tamanho em bits da sequencia codificada
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

    // Gera a árvore de Huffman e retorna o mapa de códigos
    public static HashMap<Byte, String> getHuffmanHash(byte[] sequencia, int versao) throws java.io.IOException {
        HashMap<Byte, Integer> freqMap = new HashMap<>();

        // Conta a frequência de cada byte na sequência
        for (byte c : sequencia) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        // Cria uma fila de prioridade com os nós para construir a árvore de Huffman
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (Byte b : freqMap.keySet()) {
            pq.add(new HuffmanNode(b, freqMap.get(b)));
        }

        // Constrói a árvore de Huffman
        while (pq.size() > 1) {
            HuffmanNode esq = pq.poll();
            HuffmanNode dir = pq.poll();

            HuffmanNode pai = new HuffmanNode((byte)0, esq.frequencia + dir.frequencia);
            pai.esq = esq;
            pai.dir = dir;

            pq.add(pai);
        }

        // Adiciona raiz da árvore que é o único nó restante na fila de prioridade
        HuffmanNode raiz = pq.poll();
        HashMap<Byte, String> codigos = new HashMap<>();
        constroiCodigos(raiz, "", codigos);

        // Salva a árvore de Huffman em um arquivo binário para decodificação
        salvarArv(raiz, versao, getInputBitLength(sequencia, codigos));

        return codigos;
    }

    // Salva a árvore de Huffman em um arquivo binário
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

    // Lê a árvore de Huffman de um arquivo binário
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

    // Constrói os códigos (hash) dos bytes a partir da árvore
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

    // Transforma a sequência de bytes em uma sequência comprimida usando os códigos do hash
    public static byte[] comprimir(byte[] sequencia, HashMap<Byte, String> codigos) {
        StringBuilder input = new StringBuilder();

        // Obtem bits referentes a cada byte da sequência e junta em uma string
        for (byte b : sequencia) {
            input.append(codigos.get(b));
        }

        // Converte a string de bits para um array de bytes
        // Calcula tamanho necessario para guardar bytes, arredondando para cima
        // os bits que sobrarem
        int tamanho = (input.length() + 7) / 8; 
        byte[] output = new byte[tamanho];

        // Preenche o array de bytes com os bits da string
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '1') {
                // Insere bit na posição correta do byte
                // (1 << (7 - (i % 8))) para deslocar os bits corretamente
                // |= é um or bit a bit para adcionar o bit ao byte
                // (i / 8) para selecionar o byte correto
                output[i / 8] |= (1 << (7 - (i % 8)));
            }
        }
        return output;
    }    

    // Decodifica a sequência comprimida de volta para a sequência original
    // Vai montando os codigos a partir da árvore de Huffman salva
    public static byte[] decodificar(byte[] sequencia, HuffmanNode raiz, long bitsValidos) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HuffmanNode atual = raiz;
        for (int i = 0; i < bitsValidos; i++) {
            int byteIndex = i / 8; // posição do byte na sequencia
            int bitIndex = 7 - (i % 8); // posição do bit dentro do byte
            boolean bit = ((sequencia[byteIndex] >> bitIndex) & 1) == 1; // pega o bit

            atual = bit ? atual.dir : atual.esq; // vai para dir se bit for 1 e esq se for 0
            if (atual.esq == null && atual.dir == null) { // folha
                output.write(atual.b); // adiciona byte ao arq de saida
                atual = raiz; // volta para raiz
            }
        }
        return output.toByteArray();
    }
}
