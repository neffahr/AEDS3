import java.util.*;
import java.time.LocalDate;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;

public class BTree {
    protected int ordem;

    public static final String INDEX_FILE = "./arqs/btree_index.bin";

    private class Pagina {
        protected ArrayList<Integer> ids;
        protected ArrayList<Long> ends;
        protected ArrayList<Long> filhos;
        protected long prox;
        protected int num_elem;
        protected int TAM_PAG;

        public Pagina (ArrayList<Integer> ids, ArrayList<Long> ends, ArrayList<Long> filhos, long prox) {
            this.ids = ids;
            this.ends = ends;
            this.filhos = filhos;
            this.prox = prox;
            this.num_elem = ids.size();
            this.TAM_PAG = 12 + (ids.size()*4) + (ends.size()*8) + (filhos.size()*8);
        }
        public Pagina() {
            this.ids = new ArrayList<>(ordem-1);
            this.ends = new ArrayList<>(ordem-1);
            this.filhos = new ArrayList<>(ordem);
            this.prox = -1;
            this.num_elem = 0;
            this.TAM_PAG = 12 + (ids.size()*4) + (ends.size()*8) + (filhos.size()*8);
        }
        
    }
}

