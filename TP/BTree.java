import java.util.*;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class BTree {
    protected int ordem;
    protected int nivel;
    public static final String INDEX_FILE = "./arqs/btree_index.bin";

    /*      PAGINA      */
    private class Pagina {
        protected ArrayList<ParIdEndereco> regs;
        protected ArrayList<Long> filhos;
        protected int num_elem;

        public Pagina (ArrayList<ParIdEndereco> regs, ArrayList<Long> filhos) {
            this.regs = regs;
            this.filhos = filhos;
            this.num_elem = regs.size();
        }
        public Pagina() {
            this.regs = new ArrayList<>(ordem-1);
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
            this.filhos.clear();
            this.regs.clear();
            ParIdEndereco reg;

            for (int i=0; i < chaves; i++) {
                this.filhos.add(idxf.readLong());

                reg = new ParIdEndereco(idxf.readInt(), idxf.readLong());
                this.regs.add(reg);
            }
            this.filhos.add(idxf.readLong());
            num_elem = chaves;
            idxf.seek(0);
        }
    }

    /*  PAR ID E ENDEREÇO  */
    private class ParIdEndereco {
        protected int id;
        protected long end;
    
        public ParIdEndereco(int id, long end) {
            this.id = id;
            this.end = end;
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
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        Registro reg = new Registro();
        dataf.seek(4); // Pula cabeçalho

        while (dataf.getFilePointer() < dataf.length()) {
            long pos = dataf.getFilePointer();
            dataf.seek(pos+5); // Pula lapide e o tamanho do reg

            reg = Registro.readIB(dataf);
            create(reg, dataf); // cria par id, pos na arvore
        }

        dataf.close();
    }


    /*         CRUD OPERATIONS         */
    // CREATE
    public void create(Registro reg, RandomAccessFile dataf) throws FileNotFoundException, IOException{
        RandomAccessFile idxf = new RandomAccessFile(INDEX_FILE, "rw");
        idxf.seek(0);
        dataf.seek(0);

        // Definição da posição inicial
        long raiz;
        try {
            raiz = idxf.readLong();
        }
        catch (EOFException e) {
            idxf.writeLong(8);
            raiz = 8;
        }
        idxf.seek(raiz);
        int nivel_local = 1;

        Registro.create(reg, dataf); // Inserção no arquivo de dados
        ParIdEndereco idxreg = new ParIdEndereco(reg.getId(), Registro.getPos(reg, dataf));
        create(idxreg, raiz, -1, nivel_local, dataf, idxf); // Inserção no arquivo de indices

        dataf.close();
        idxf.close();
    }

    private ParIdEndereco create(ParIdEndereco newreg, long pos, long pai, int nivel, RandomAccessFile dataf, RandomAccessFile idxf) 
    throws FileNotFoundException, IOException{
        // Criação no arquivo de indices
        idxf.seek(0);
        ParIdEndereco subiu; // Usado para armazenar par id+end de reg que subiu para a pagina pai após inserção

        // Carrega a pagina atual na memoria
        Pagina pag = new Pagina();
        pag.populate(pos, idxf);

        // Avança na arvore até o ultimo nivel
        if (nivel < this.nivel) {
            int i=0;
            for(ParIdEndereco reg : pag.regs) {
                if (newreg.id < reg.id) { break; }
                i++;
            }
            long filho = pag.filhos.get(i);
            pag = null; // descarrega pag da memoria para recursão
            subiu = create(newreg, filho, pos, nivel+1, dataf, idxf);

            // Verifica se filho cresceu após inserção
            // Se sobrar espaço na pagina acha lugar e insere
            // Se estourar limite, divide e sobe o do meio (split)
            if (subiu != null) {
                // Carrega pag na memoria novamente
                pag = new Pagina();
                pag.populate(pos, idxf);

                i=0;
                if (pag.num_elem < ordem-1) {
                    while (i < pag.num_elem && pag.regs.get(i).id < subiu.id) {
                        i++;
                    }
                    pag.regs.add(i, subiu);
                    pag.filhos.add(i + 1, subiu.end); // Insere ponteiro da nova pag criada
                    subiu = null;
                }
                else {
                    subiu = splitInside(pag, pos, pai, subiu, idxf);
                }

                writeData(pos, pag, idxf); // Atualiza arquivo de indices
            }
        }

        else {
            if (pag.num_elem < ordem-1) {
                int i=0;
                while (i < pag.num_elem && pag.regs.get(i).id < newreg.id) {
                    i++;
                }
                pag.regs.add(i, newreg);
                pag.filhos.add(i + 1, (long) -1);
                subiu = null;
            }
            else {
                subiu = splitLeaf(pag, pos, pai, newreg, idxf);
            }
            writeData(pos, pag, idxf); // Atualiza arquivo de indices
        }

        pag = null; // descarrega pag da memoria
        return subiu; // Retorna id que subiu após insersão
    }

    // Função de escrita no arquivo de indices
    private void writeData(long pos, Pagina pag, RandomAccessFile idxf) throws IOException{
        idxf.seek(pos);
        idxf.writeInt(pag.num_elem);
        for (int i=0; i<pag.num_elem; i++) {
            idxf.writeLong(pag.filhos.get(i));
            idxf.writeInt(pag.regs.get(i).id);
            idxf.writeLong(pag.regs.get(i).end);
        }
        idxf.writeLong(pag.filhos.get(pag.num_elem+1));

        for (int i=pag.num_elem; i<ordem-1; i++) {
            idxf.writeInt(-1);
            idxf.writeLong(-1);
            idxf.writeLong(-1);
        }
    }

    // Função para split da pagina folha
    private ParIdEndereco splitLeaf(Pagina pag, long pos, long pai, ParIdEndereco reg, RandomAccessFile idxf) 
    throws IOException{
        // Carrega pag nova e pag pai para mudanças
        Pagina newpag = new Pagina();
        Pagina pagpai = new Pagina();
        pagpai.populate(pai, idxf);
        
        // Definição variavel de retorno e Arraylist auxiliar para distribuição
        ParIdEndereco subiu;
        ArrayList<ParIdEndereco> aux = new ArrayList<>(pag.regs);

        // Loops para colocar elementos no arraylist aux
        int i=0, j=0;
        while (i < aux.size() && aux.get(i).id < reg.id) i++;
        aux.add(i, reg);

        // Separação dos regs e dos filhos em duas pags
        long endprox=-1;
        for (i=(aux.size()/2), j=i; i<aux.size(); i++) {
            newpag.regs.add(aux.get(j));
            newpag.filhos.add((long)-1);
            aux.remove(j);
            endprox = pag.filhos.remove(j);
        }
        newpag.filhos.add(endprox);
        pag.regs = aux;
        subiu = newpag.regs.get(0);

        // Reorganiza numero de registros das paginas
        pag.num_elem = pag.regs.size();
        newpag.num_elem = newpag.regs.size();

        // Cria pag nova e conecta na pagina antiga
        long endNewPag = idxf.length();
        writeData(endNewPag, newpag, idxf);
        pag.filhos.add(endNewPag);

        // Salva pos da nova pag para retorno
        subiu.end = endNewPag;

        // Atualiza pagina antiga
        writeData(pos, pag, idxf);
        
        pagpai=null;
        newpag=null;
        aux = null;
        return subiu;
    }

    // Função para split da pagina interna
    private ParIdEndereco splitInside(Pagina pag, long pos, long pai, ParIdEndereco reg, RandomAccessFile idxf) 
    throws IOException{
        // Cria nova página e carrega o pai
        Pagina newpag = new Pagina();
        Pagina pagpai = new Pagina();
        pagpai.populate(pai, idxf);

        // ArrayLists auxiliares
        ArrayList<ParIdEndereco> auxRegs = new ArrayList<>(pag.regs);
        ArrayList<Long> auxFilhos = new ArrayList<>(pag.filhos);

        // Insere o novo registro no lugar certo
        int i = 0;
        while (i < auxRegs.size() && auxRegs.get(i).id < reg.id) i++;
        auxRegs.add(i, reg);
        auxFilhos.add(i + 1, reg.end); // novo filho à direita do registro inserido

        // Divide os registros e filhos
        int meio = auxRegs.size() / 2;
        ParIdEndereco subiu = auxRegs.get(meio);

        // Nova página recebe metade superior dos registros e filhos
        for (int j = meio + 1; j < auxRegs.size(); j++) {
            newpag.regs.add(auxRegs.get(j));
        }
        for (int j = meio + 1; j < auxFilhos.size(); j++) {
            newpag.filhos.add(auxFilhos.get(j));
        }

        // Página original fica com a metade inferior
        pag.regs = new ArrayList<>(auxRegs.subList(0, meio));
        pag.filhos = new ArrayList<>(auxFilhos.subList(0, meio+1));

        // Atualiza contadores
        pag.num_elem = pag.regs.size();
        newpag.num_elem = newpag.regs.size();

        // Escreve nova página no final do arquivo
        long endNewPag = idxf.length();
        writeData(endNewPag, newpag, idxf);
        
        // Salva pos da nova pag para retorno
        subiu.end = endNewPag;

        // Atualiza página atual
        writeData(pos, pag, idxf);

        pagpai = null;
        newpag = null;
        auxRegs = null;
        auxFilhos = null;
        return subiu;
    }

    // READ
    public Registro read(int id) throws IOException {
        Registro reg = search(id, id).get(0);
        return reg;
    }

    // READ MULTIPLO
    public ArrayList<Registro> search(int id_start, int id_fim) throws FileNotFoundException, IOException{
        RandomAccessFile idxf = new RandomAccessFile(INDEX_FILE, "rw");
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        idxf.seek(0);
        dataf.seek(0);

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
            for(ParIdEndereco reg : pag.regs) {
                if (id_start < reg.id) {
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
            for(ParIdEndereco reg : pag.regs) {
                if (reg.id >= id_start && reg.id <= id_fim) {
                    long pos_reg = pag.regs.get(end).end;
                    dataf.seek(pos_reg+5); // Pula lapide e tam_reg
                    regs.add(Registro.readIB(dataf));
                }
            }
            if (pag.regs.get(pag.regs.size()-1).id < id_fim) {
                pos = pag.filhos.get(pag.filhos.size()-1);
                search(id_start, id_fim, regs, nivel, pos, dataf, idxf);
            }
            pag=null;
        }
    }

    // UPDATE
    public boolean update(Registro newreg) throws IOException{
        RandomAccessFile idxf = new RandomAccessFile(INDEX_FILE, "rw");
        RandomAccessFile dataf = new RandomAccessFile(Registro.DB_BINARIO, "rw");
        idxf.seek(0);
        dataf.seek(0);

        // Set da posição inicial (raiz) e do nivel inicial (1).
        // Se não tiver elementos retorna falso
        long raiz = idxf.readLong();
        if (raiz == -1) {return false;}
        idxf.seek(raiz);
        int nivel_local = 1;

        boolean resp = update(newreg, nivel_local, raiz, dataf, idxf);
        dataf.close();
        idxf.close();
        return resp;
    }

    private boolean update(Registro newreg, int nivel, long pos, RandomAccessFile dataf, RandomAccessFile idxf) throws IOException{
        // Carrega a pagina atual na memoria
        Pagina pag = new Pagina();
        pag.populate(pos, idxf);

        // Avança na arvore até o ultimo nivel
        if (nivel < this.nivel) {
            int i=0;
            for(ParIdEndereco reg : pag.regs) {
                if (newreg.id < reg.id) {
                    break;
                }
                i++;
            }
            pos = pag.filhos.get(i);
            pag=null;
            return update(newreg, nivel+1, pos, dataf, idxf);
        }

        // Se já estiver no ultimo nivel, acha chave, pega end e atualiza
        // Se a atualização mover o registro para o final, atualiza end
        else {
            int end=0;
            for(ParIdEndereco reg : pag.regs) {
                if (reg.id == newreg.id) {
                    long pos_reg = pag.regs.get(end).end;
                    dataf.seek(pos_reg+1);
                    int tam_reg = dataf.readInt();

                    if(newreg.calcTamReg() <= tam_reg) {
                        dataf.seek(pos_reg);
                        Registro.writeData(newreg, tam_reg, dataf);
                    } else {
                        dataf.seek(pos);
                        dataf.writeByte(1);

                        pag.regs.get(end).end = dataf.length();
                        writeData(pos, pag, idxf);

                        dataf.seek(dataf.length());
                        Registro.writeData(newreg, newreg.calcTamReg(), dataf);
                    }
                }
            }
            pag=null;
            return true;
        }
    }

    // DELETE
    public boolean delete(int id) {
        return true;
    };
}

