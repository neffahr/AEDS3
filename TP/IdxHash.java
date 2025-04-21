import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Hashtable;

public class IdxHash {

    String nomeArquivoDiretorio;
    String nomeArquivoCestos;
    RandomAccessFile arqDiretorio;
    RandomAccessFile arqCestos;
    int quantidadeDadosPorCesto;
    Diretorio diretorio;

    public static final String HASH_BUCKETS = "./arqs/hash_buckets.bin";
    public static final String HASH_DIRETORIO = "./arqs/hash_diretorio.bin";
    public static final String HASH_METADADOS = "./arqs/hash_meta.bin";

    public class Bucket {

        short quantidadeMaxima;
        short bytesPorElemento;
        short bytesPorCesto;

        byte profundidadeLocal;
        short quantidade;
        ArrayList elementos;

        public Bucket(int qtdmax) throws Exception {
            this(qtdmax, 0);
        }

        public Bucket(int qtdmax, int pl) throws Exception {
            if (qtdmax > 32767)
                throw new Exception("Quantidade máxima de 32.767 elementos");
            if (pl > 127)
                throw new Exception("Profundidade local máxima de 127 bits");
            profundidadeLocal = (byte) pl;
            quantidade = 0;
            quantidadeMaxima = (short) qtdmax;
            elementos = new ArrayList(quantidadeMaxima);
            bytesPorElemento = (short) RegistroHashExtensivel.size();
            bytesPorCesto = (short) (bytesPorElemento * quantidadeMaxima + 3);
        }

        public byte[] toByteArray() throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeByte(profundidadeLocal);
            dos.writeShort(quantidade);
            int i = 0;
            while (i < quantidade) {
                dos.write(((RegistroHashExtensivel) elementos.get(i)).toByteArray());
                i++;
            }
            byte[] vazio = new byte[bytesPorElemento];
            while (i < quantidadeMaxima) {
                dos.write(vazio);
                i++;
            }
            return baos.toByteArray();
        }

        public void fromByteArray(byte[] ba) throws Exception {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);
            profundidadeLocal = dis.readByte();
            quantidade = dis.readShort();
            int i = 0;
            elementos = new ArrayList(quantidadeMaxima);
            byte[] dados = new byte[bytesPorElemento];
            RegistroHashExtensivel elem;
            while (i < quantidadeMaxima) {
                dis.read(dados);
                elem = new RegistroHashExtensivel();
                elem.fromByteArray(dados);
                elementos.add(elem);
                i++;
            }
        }

        public boolean create(RegistroHashExtensivel elem) {
            if (full())
                return false;
            int i = quantidade - 1;
            while (i >= 0 && elem.hashCode() < ((RegistroHashExtensivel) elementos.get(i)).hashCode())
                i--;
            elementos.add(i + 1, elem);
            quantidade++;
            return true;
        }

        public RegistroHashExtensivel read(int chave) {
            if (empty())
                return null;
            int i = 0;
            while (i < quantidade && chave > ((RegistroHashExtensivel) elementos.get(i)).hashCode())
                i++;
            if (i < quantidade && chave == ((RegistroHashExtensivel) elementos.get(i)).hashCode())
                return (RegistroHashExtensivel) elementos.get(i);
            else
                return null;
        }

        public boolean update(RegistroHashExtensivel elem) {
            if (empty())
                return false;
            int i = 0;
            while (i < quantidade && elem.hashCode() > ((RegistroHashExtensivel) elementos.get(i)).hashCode())
                i++;
            if (i < quantidade && elem.hashCode() == ((RegistroHashExtensivel) elementos.get(i)).hashCode()) {
                elementos.set(i, elem);
                return true;
            } else
                return false;
        }

        public boolean delete(int chave) {
            if (empty())
                return false;
            int i = 0;
            while (i < quantidade && chave > ((RegistroHashExtensivel) elementos.get(i)).hashCode())
                i++;
            if (chave == ((RegistroHashExtensivel) elementos.get(i)).hashCode()) {
                elementos.remove(i);
                quantidade--;
                return true;
            } else
                return false;
        }

        public boolean empty() {
            return quantidade == 0;
        }

        public boolean full() {
            return quantidade == quantidadeMaxima;
        }

        public String toString() {
            String s = "Profundidade Local: " + profundidadeLocal + "\nQuantidade: " + quantidade + "\n| ";
            int i = 0;
            while (i < quantidade) {
                s += elementos.get(i).toString() + " | ";
                i++;
            }
            while (i < quantidadeMaxima) {
                s += "- | ";
                i++;
            }
            return s;
        }

        public int size() {
            return bytesPorCesto;
        }
    }

    protected class Diretorio {

        byte profundidadeGlobal;
        long[] enderecos;

        public Diretorio() {
            profundidadeGlobal = 1;
            enderecos = new long[2];
            enderecos[0] = 0;
            enderecos[1] = 0;
        }

        public boolean atualizaEndereco(int p, long e) {
            if (p > Math.pow(2, profundidadeGlobal))
                return false;
            enderecos[p] = e;
            return true;
        }

        public byte[] toByteArray() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeByte(profundidadeGlobal);
            int quantidade = (int) Math.pow(2, profundidadeGlobal);
            int i = 0;
            while (i < quantidade) {
                dos.writeLong(enderecos[i]);
                i++;
            }
            return baos.toByteArray();
        }

        public void fromByteArray(byte[] ba) throws IOException {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            DataInputStream dis = new DataInputStream(bais);
            profundidadeGlobal = dis.readByte();
            int quantidade = (int) Math.pow(2, profundidadeGlobal);
            enderecos = new long[quantidade];
            int i = 0;
            while (i < quantidade) {
                enderecos[i] = dis.readLong();
                i++;
            }
        }

        public String toString() {
            String s = "\nProfundidade global: " + profundidadeGlobal;
            int i = 0;
            int quantidade = (int) Math.pow(2, profundidadeGlobal);
            while (i < quantidade) {
                s += "\n" + i + ": " + enderecos[i];
                i++;
            }
            return s;
        }

        protected long endereço(int p) {
            if (p > Math.pow(2, profundidadeGlobal))
                return -1;
            return enderecos[p];
        }

        protected boolean duplica() {
            if (profundidadeGlobal == 127)
                return false;
            profundidadeGlobal++;
            int q1 = (int) Math.pow(2, profundidadeGlobal - 1);
            int q2 = (int) Math.pow(2, profundidadeGlobal);
            long[] novosEnderecos = new long[q2];
            int i = 0;
            while (i < q1) {
                novosEnderecos[i] = enderecos[i];
                i++;
            }
            while (i < q2) {
                novosEnderecos[i] = enderecos[i - q1];
                i++;
            }
            enderecos = novosEnderecos;
            return true;
        }

        protected int hash(int chave) {
            return Math.abs(chave) % (int) Math.pow(2, profundidadeGlobal);
        }

        protected int hash2(int chave, int pl) {
            return Math.abs(chave) % (int) Math.pow(2, pl);
        }
    }

    public IdxHash(int n) throws Exception {
        quantidadeDadosPorCesto = n;
        nomeArquivoDiretorio = HASH_DIRETORIO;
        nomeArquivoCestos = HASH_BUCKETS;

        arqDiretorio = new RandomAccessFile(nomeArquivoDiretorio, "rw");
        arqCestos = new RandomAccessFile(nomeArquivoCestos, "rw");

        if (arqDiretorio.length() == 0 || arqCestos.length() == 0) {
            diretorio = new Diretorio();
            byte[] bd = diretorio.toByteArray();
            arqDiretorio.write(bd);

            Bucket c = new Bucket(quantidadeDadosPorCesto);
            bd = c.toByteArray();
            arqCestos.seek(0);
            arqCestos.write(bd);
        }
    }

    public void loadHash() throws Exception {
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        Registro reg = new Registro();
        dataf.seek(4); // Pula cabeçalho

        while (dataf.getFilePointer() < dataf.length()) {
            long pos = dataf.getFilePointer();
            dataf.seek(pos+5); // Pula lapide e o tamanho do reg

            reg = Registro.readIB(dataf);

            create(reg.getId(), pos); // cria par id, pos na arvore
        }

        dataf.close();
    }

    public boolean create(int id, long posicao) throws Exception {
        return create(new RegistroHashExtensivel(id, posicao));
    }

    public boolean create(RegistroHashExtensivel elem) throws Exception {
        byte[] bd = new byte[(int) arqDiretorio.length()];
        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        int i = diretorio.hash(elem.hashCode());

        long enderecoCesto = diretorio.endereço(i);
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        if (c.read(elem.hashCode()) != null)
            throw new Exception("Elemento já existe");

        if (!c.full()) {
            c.create(elem);
            arqCestos.seek(enderecoCesto);
            arqCestos.write(c.toByteArray());
            return true;
        }

        byte pl = c.profundidadeLocal;
        if (pl >= diretorio.profundidadeGlobal)
            diretorio.duplica();
        byte pg = diretorio.profundidadeGlobal;

        Bucket c1 = new Bucket(quantidadeDadosPorCesto, pl + 1);
        arqCestos.seek(enderecoCesto);
        arqCestos.write(c1.toByteArray());

        Bucket c2 = new Bucket(quantidadeDadosPorCesto, pl + 1);
        long novoEndereco = arqCestos.length();
        arqCestos.seek(novoEndereco);
        arqCestos.write(c2.toByteArray());

        int inicio = diretorio.hash2(elem.hashCode(), c.profundidadeLocal);
        int deslocamento = (int) Math.pow(2, pl);
        int max = (int) Math.pow(2, pg);
        boolean troca = false;
        for (int j = inicio; j < max; j += deslocamento) {
            if (troca)
                diretorio.atualizaEndereco(j, novoEndereco);
            troca = !troca;
        }

        bd = diretorio.toByteArray();
        arqDiretorio.seek(0);
        arqDiretorio.write(bd);

        for (int j = 0; j < c.quantidade; j++) {
            create((RegistroHashExtensivel) c.elementos.get(j));
        }
        create(elem);
        return true;
    }

    public long read(int chave) throws Exception {
        RegistroHashExtensivel elem = this.readImpl(chave);
        return elem == null ? -1 : elem.posicao;
    }

    public RegistroHashExtensivel readImpl(int chave) throws Exception {
        byte[] bd = new byte[(int) arqDiretorio.length()];
        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        int i = diretorio.hash(chave);

        long enderecoCesto = diretorio.endereço(i);
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        return c.read(chave);
    }

    public boolean update(RegistroHashExtensivel elem) throws Exception {
        byte[] bd = new byte[(int) arqDiretorio.length()];
        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        int i = diretorio.hash(elem.hashCode());

        long enderecoCesto = diretorio.endereço(i);
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        if (!c.update(elem))
            return false;

        arqCestos.seek(enderecoCesto);
        arqCestos.write(c.toByteArray());
        return true;
    }

    public boolean delete(int chave) throws Exception {
        byte[] bd = new byte[(int) arqDiretorio.length()];
        arqDiretorio.seek(0);
        arqDiretorio.read(bd);
        diretorio = new Diretorio();
        diretorio.fromByteArray(bd);

        int i = diretorio.hash(chave);

        long enderecoCesto = diretorio.endereço(i);
        Bucket c = new Bucket(quantidadeDadosPorCesto);
        byte[] ba = new byte[c.size()];
        arqCestos.seek(enderecoCesto);
        arqCestos.read(ba);
        c.fromByteArray(ba);

        if (!c.delete(chave))
            return false;

        arqCestos.seek(enderecoCesto);
        arqCestos.write(c.toByteArray());
        return true;
    }

    public void print() {
        try {
            byte[] bd = new byte[(int) arqDiretorio.length()];
            arqDiretorio.seek(0);
            arqDiretorio.read(bd);
            diretorio = new Diretorio();
            diretorio.fromByteArray(bd);
            System.out.println("\nDIRETÓRIO ------------------");
            System.out.println(diretorio);

            System.out.println("\nCESTOS ---------------------");
            arqCestos.seek(0);
            while (arqCestos.getFilePointer() != arqCestos.length()) {
                System.out.println("Endereço: " + arqCestos.getFilePointer());
                Bucket c = new Bucket(quantidadeDadosPorCesto);
                byte[] ba = new byte[c.size()];
                arqCestos.read(ba);
                c.fromByteArray(ba);
                System.out.println(c + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class RegistroHashExtensivel {
    int id;
    long posicao;

    public RegistroHashExtensivel(int id, long posicao) {
        this.id = id;
        this.posicao = posicao;
    }

    public RegistroHashExtensivel() {
        this.id = -1;
        this.posicao = -1;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.id);
    }

    public static int size() {
        return (Integer.SIZE + Long.SIZE) / 8;
    }

    public byte[] toByteArray() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        dos.writeInt(this.id);
        dos.writeLong(this.posicao);
        return baos.toByteArray();
    }

    public void fromByteArray(byte[] ba) throws Exception {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(ba));
        this.id = dis.readInt();
        this.posicao = dis.readLong();
    }
}