import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree {
    protected int ordem;
    protected int nivel;
    public static final String INDEX_FILE = "./arqs/btree_index.bin";

    /*      PAGINA      */
    private class Pagina {
        protected ArrayList<Integer> ids;
        protected ArrayList<Long> ends;
        protected ArrayList<Long> filhos;
        protected int num_elem;

        public Pagina (ArrayList<Integer> ids, ArrayList<Long> ends, ArrayList<Long> filhos) {
            this.ids = ids;
            this.ends = ends;
            this.filhos = filhos;
            this.num_elem = ids.size();
        }
        public Pagina() {
            this.ids = new ArrayList<>(ordem-1);
            this.ends = new ArrayList<>(ordem-1);
            this.filhos = new ArrayList<>(ordem);
            this.num_elem = 0;
        }

        public int calcTamPag() {
            /* 
            numero de elementos     (4 bytes - int) + 
            ids                     (4 para cada - int) + 
            endereços               (8 para cada - long) + 
            endereço dos filhos     (8 bytes para cada - long)
            */
            return 4 + ((ordem-1)*4) + ((ordem-1)*8) + ((ordem)*8); 
        }

        // Método que recebe a posição da pagina na árvore e carrega as informações na memoria
        public void populate(long pos, RandomAccessFile idxf) throws IOException {
            idxf.seek(pos);
            int chaves = idxf.readInt();

            for (int i=0; i < chaves; i++) {
                this.filhos.add(idxf.readLong());
                this.ids.add(idxf.readInt());
                this.ends.add(idxf.readLong());
            }
            this.filhos.add(idxf.readLong());
            num_elem = chaves;
        }
    }

    /*       ARVORE B      */
    public BTree (int ordem) {
        this.ordem = ordem;
        this.nivel = 0;
    }

    public int getOrdem(){
        return this.ordem;
    }
    public void setOrdem(int ordem) {
        this.ordem = ordem;
    }

    public void loadBtree() throws IOException{
        RandomAccessFile idxf = new RandomAccessFile(INDEX_FILE, "rw");
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        Registro reg = new Registro();
        dataf.seek(4); // Pula cabeçalho

        while (dataf.getFilePointer() < dataf.length()) {
            long pos = dataf.getFilePointer();
            dataf.seek(pos+5); // Pula lapide e o tamanho do reg

            reg = Registro.readIB(dataf);

            create(reg, dataf, idxf); // cria par id, pos na arvore
        }

        dataf.close();
        idxf.close();
    }


    /*         CRUD OPERATIONS         */
    // CREATE
    public void create(Registro reg) throws FileNotFoundException, IOException{
        RandomAccessFile idxf = new RandomAccessFile(INDEX_FILE, "rw");
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");

        create(reg, dataf, idxf);

        dataf.close();
        idxf.close();
    }
    private void create(Registro reg, RandomAccessFile dataf, RandomAccessFile idxf) throws FileNotFoundException, IOException{
        Registro.create(reg, dataf);

        // // Criação no arquivo de indices
        // idxf.seek(0);
        // // Verifica se arquivo está vazio. 
        // // Se sim escreve 0 para quantia de registros. Se não lê a quantia de registros
        // int lastId;
        // try {
        //     lastId = idxf.readInt();
        // } catch (EOFException e){
        //     lastId = 0;
        //     file.writeInt(lastId);
        // }

        // int cbc = lastId + 1;
        // file.seek(0);
        // file.writeInt(cbc); // Cabeçalho (int): 4 bytes
        // file.seek(file.length()); // vai para final do arquivo
        // writeData(reg, reg.calcTamReg(), file);
    }

    // READ
    public Registro read(int id) throws FileNotFoundException, IOException {
        RandomAccessFile idxf = new RandomAccessFile(INDEX_FILE, "rw");
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        Registro reg = new Registro();
        idxf.seek(0);

        while (idxf.getFilePointer() < idxf.length()) {
            
        }

        dataf.close();
        idxf.close();
        return null;
    }

    // UPDATE
    public boolean update(Registro newReg) {
        return true;
    }

    // DELETE
    public boolean delete(int id) {
        return true;
    };

    // BUSCA
    public ArrayList<Registro> search(int id_start, int id_fim) throws FileNotFoundException, IOException{
        RandomAccessFile idxf = new RandomAccessFile(INDEX_FILE, "rw");
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        idxf.seek(0);

        // Set da posição inicial (raiz) e do nivel inicial (1).
        // Se não tiver elementos retorna nulo
        long raiz = idxf.readLong();
        if (raiz == -1) {return null;}
        idxf.seek(raiz);
        int nivel_local = 1;

        ArrayList<Registro> regs = new ArrayList<>();
        search(id_start, id_fim, regs, nivel_local, raiz, dataf, idxf);
        dataf.close();
        idxf.close();
        return regs;
    }

    private void search(int id_start, int id_fim, ArrayList<Registro> regs, int nivel, long pos, RandomAccessFile dataf, RandomAccessFile idxf)
    throws IOException {
        // Carrega a pagina atual na memoria
        Pagina pag = new Pagina();
        pag.populate(pos, idxf);

        // Avança na arvore até o ultimo nivel
        if (nivel < this.nivel) {
            int i=0;
            for(int id : pag.ids) {
                if (id < id_start) {
                    break;
                }
                i++;
            }
            pos = pag.filhos.get(i);
            pag=null;
            search(id_start, id_fim, regs, nivel+1, pos, dataf, idxf);
        }

        // Se já estiver no ultimo nivel pega intervalo de regs da pagina
        // Se acabar a pagina e ainda ter regs a serem buscados, passa para prox pagina
        else {
            int end=0;
            for(int id : pag.ids) {
                if (id>=id_start && id<=id_fim) {
                    long pos_reg = pag.ends.get(end);
                    dataf.seek(pos_reg+5); // Pula lapide e tam_reg
                    regs.add(Registro.readIB(dataf));
                }
            }
            if (pag.ids.get(pag.ids.size()-1) < id_fim) {
                pos = pag.filhos.get(pag.filhos.size()-1);
                search(id_start, id_fim, regs, nivel, pos, dataf, idxf);
            }
        }
    }
    
}

