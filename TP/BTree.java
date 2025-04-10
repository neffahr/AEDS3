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
        protected byte num_elem;

        public Pagina (ArrayList<Integer> ids, ArrayList<Long> ends, ArrayList<Long> filhos) {
            this.ids = ids;
            this.ends = ends;
            this.filhos = filhos;
            this.num_elem = (byte) ids.size();
        }
        public Pagina() {
            this.ids = new ArrayList<>(ordem-1);
            this.ends = new ArrayList<>(ordem-1);
            this.filhos = new ArrayList<>(ordem);
            this.num_elem = 0;
        }
        public int calcTamPag() {
            /* 
            num_elem (1 byte) + 
            ids (4 para cada) + 
            endereços (4 para cada) + 
            endereço dos filhos (8 bytes para cada)
            */
            return 1 + ((ordem-1)*4) + ((ordem-1)*8) + ((ordem)*8); 
        }
    }

    

    public BTree (int ordem) {
        this.ordem = ordem;
    }

    
}

