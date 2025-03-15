import java.util.*;
import java.time.LocalDate;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.EOFException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.nio.charset.StandardCharsets;

class Registro {
    
    protected int id;
    protected String org_title;
    protected String title;
    protected byte[] org_language;
    protected String ovr;
    protected LocalDate release_date;
    protected float popularity;
    protected int vote_count;
    protected float vote_average;
    protected int runtime;
    protected byte adult;
    protected List<String> genre_name;

    public static final String DB_BINARIO = "registros.bin";
	public static final String FONTE_CSV = "horror_movies.csv";

    public Registro() {
        org_language = new byte[2];
        popularity = 0;
        vote_count = 0;
        vote_average = 0;
        runtime=0;
        adult = 0;
        genre_name = new ArrayList<>();
    }

    public Registro(int id, String org_title, String title, byte[] org_language, String ovr, LocalDate release_date, float popularity, int vote_count, float vote_average, int runtime, byte adult, List<String> genre_name){
        this.id = id;
        this.org_title = org_title;
        this.title = title;
        this.org_language = org_language;
        this.ovr = ovr;
        this.release_date = release_date;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.runtime = runtime;
        this.adult = adult;
        this.genre_name = genre_name;
    }
    
    @Override
    public String toString() {
        return "Registro {" +
                "id=" + id +
                ", org_title='" + org_title + '\'' +
                ", title='" + title + '\'' +
                ", org_language='" + new String(org_language) + '\'' + // Converte bytes para String
                ", overview='" + ovr + '\'' +
                ", release_date=" + release_date +
                ", popularity=" + popularity +
                ", vote_count=" + vote_count +
                ", vote_average=" + vote_average +
                ", runtime=" + runtime +
                ", adult=" + (adult == 1 ? "Yes" : "No") + // Converte byte para legibilidade
                ", genre_name=" + genre_name +
                '}';
    }

    public int getId(){
        return id;
    }

    public String getOrgTitle(){
        return org_title;
    }

    public String getTitle(){
        return title;
    }

    public byte[] getOrgLanguage(){
        return org_language;
    }

    public String getOvr(){
        return ovr;
    }
    
    public LocalDate getReleaseDate(){
        return release_date;
    }

    public float getPopularity(){
        return popularity;
    }

    public int getVoteCount(){
        return vote_count;
    }

    public float getVoteAverage(){
        return vote_average;
    }

    public int getRuntime(){
        return runtime;
    }

    public byte getAdult(){
        return adult;
    }

    public List<String> getGenreName(){
        return genre_name;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setOrgTitle(String org_title){
        this.org_title = org_title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setOrgLanguage(byte[] org_language){
        this.org_language = org_language;
    }

    public void setOvr(String ovr){
        this.ovr = ovr;
    }
    
    public void setReleaseDate(LocalDate release_date){
        this.release_date = release_date;
    }

    public void setPopularity(float popularity){
        this.popularity = popularity;
    }

    public void setVoteCount(int vote_count){
        this.vote_count = vote_count;
    }

    public void setVoteAverage(float vote_average){
        this.vote_average = vote_average;
    }

    public void setRuntime(int runtime){
        this.runtime = runtime;
    }

    public void setAdult(byte adult){
        this.adult = adult;
    }

    public void setGenreName(List<String> genre_name){
        this.genre_name = genre_name;
    }

    // Define o tamanho de um registro para escrita
    public int calcTamReg() {
        int tam_reg = 38 + this.org_title.length() + this.title.length() + 
                      this.ovr.length();
        for (String str : this.genre_name) {
            tam_reg += 2 + str.length();
        }
        return tam_reg;
    }

    // Método que recebe uma linha lida do aquivo csv e peenche as informações do objeto
    private void populate(String line) throws FileNotFoundException, IOException{
        List<String> data = splitCSV(line);

        this.id = Integer.parseInt(data.get(0));
        this.org_title = data.get(2);
        this.title = data.get(3);
        this.org_language = data.get(4).getBytes(StandardCharsets.UTF_8);
        this.ovr = data.get(5);
        this.release_date = LocalDate.parse(data.get(6));
        this.popularity = Float.parseFloat(data.get(7));
        this.vote_count = Integer.parseInt(data.get(8));
        this.vote_average = Float.parseFloat(data.get(9));
        this.runtime = Integer.parseInt(data.get(10));

        byte adult = data.get(11).equals("FALSE") ? (byte) 0 : (byte) 1;
        this.adult = adult;

        String[] genre_name = data.get(12).split(",", -1);
        this.genre_name = Arrays.asList(genre_name);
    }

    // Método escrito com ajuda do Chat GPT para uso das bibliotecas Matcher e Pattern e definição do Regex
    // Método que separa os dados da linha lida em um array list de Strings
    private List<String> splitCSV(String line) {
        List<String> data = new ArrayList<>();

        // Regex com 2 agrupamentos:
        // 1) "([^"]*)" --> Captura valores dentro das aspas duplas (usado
        // para obter o overview, que tem virgulas dentro)
        // 2) ([^,]+)   --> Captura valores fora das aspas duplas
        Matcher matcher = Pattern.compile("\"((?:[^\"]|\"\")*)\"|([^,]+)").matcher(line);

        while (matcher.find()) {
            if (matcher.group(1) != null) { 
                data.add(matcher.group(1)); // Captura o agrupamento 1 se corresponder
            } else {
                data.add(matcher.group(2)); // Captura o agrupamento 2 se não corresponder ao 1
            }
        }

        return data;
    }

    public static void loadData() throws FileNotFoundException, IOException {
        RandomAccessFile raf = new RandomAccessFile(FONTE_CSV, "rw");
        RandomAccessFile bin = new RandomAccessFile(DB_BINARIO, "rw");
        raf.seek(0);
        raf.readLine();
        Registro reg = new Registro();

        while (raf.getFilePointer() < raf.length()) {
            String line = new String(raf.readLine().getBytes("ISO-8859-1"), "UTF-8");
            reg.populate(line);
            create(reg, bin);
        }
        
        raf.close();
        bin.close();
    }

    /*         CRUD OPERATIONS         */
    // CREATE
    private static void writeData(Registro reg, int tam_reg, RandomAccessFile file) throws FileNotFoundException, IOException {
        file.writeByte(0);                                    // Lapide (int)                              1 byte
        file.writeInt(tam_reg);                                 // Tamanho do Registro (int)                 4 bytes
        file.writeInt(reg.getId());                             // ID (int)                                  4 bytes
        file.writeShort(reg.getOrgTitle().length());            // Tamanho string original title (short)     2 bytes
        file.writeBytes(reg.getOrgTitle());                     // original title (string)                   variavel
        file.writeShort(reg.getTitle().length());               // Tamanho string title (short)              2 bytes
        file.writeBytes(reg.getTitle());                        // Title (string)                            variavel
        file.write(reg.getOrgLanguage());                       // original language (string fixa)           2 bytes
        file.writeShort(reg.getOvr().length());                 // Tamanho string overview (short)           2 bytes
        file.writeBytes(reg.getOvr());                          // Overview (string)                         variavel
        file.writeLong(reg.getReleaseDate().toEpochDay());      // Release date (long)                       8 bytes
        file.writeFloat(reg.getPopularity());                   // Popularity (float)                        4 bytes   
        file.writeInt(reg.getVoteCount());                      // Vote count (int)                          4 bytes
        file.writeFloat(reg.getVoteAverage());                  // Vote average (float)                      4 bytes
        file.writeInt(reg.getRuntime());                        // Runtime (int)                             4 bytes
        file.writeByte(reg.getAdult());                         // Adult (boolean)                           1 byte
        file.writeByte(reg.getGenreName().size());              // Quantidade de itens da lista (byte)       1 byte
        
        for (String str : reg.getGenreName()) {
            file.writeShort(str.length());                      // Tamanho da string genre name (short)      2 bytes
            file.writeBytes(str);                               // Genre name (String)                       variavel
        }
    }
    public static void create(Registro reg, RandomAccessFile file) throws FileNotFoundException, IOException {
        file.seek(0);
        
        // Verifica se arquivo está vazio. 
        // Se sim escreve 0 para quantia de registros. Se não lê a quantia de registros
        int lastId;
        try {
            lastId = file.readInt();
        } catch (EOFException e){
            lastId = 0;
            file.writeInt(lastId);
        }

        int cbc = lastId + 1;
        file.seek(0);
        file.writeInt(cbc); // Cabeçalho (int): 4 bytes
        file.seek(file.length()); // vai para final do arquivo
        writeData(reg, reg.calcTamReg(), file);
    }

    // READ (ID)
    public static Registro read(int id) throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile(DB_BINARIO, "rw");
        file.seek(0);
        int totalRegistros = file.readInt();
        
        for (int i=0; i<totalRegistros; i++) {
            long pos = file.getFilePointer();

            byte lapide = file.readByte();
            int tam_reg = file.readInt();
            int reg_id = file.readInt();
            
            
            if (lapide == 0 && reg_id == id) {
                Registro reg = new Registro();
                reg.setId(reg_id);
                
                short tam_org_title = file.readShort();
                byte[] bytes = new byte[tam_org_title];
                String org_title = new String(bytes, 0, file.read(bytes), "UTF-8");
                reg.setOrgTitle(org_title);
                
                short tam_title = file.readShort();
                bytes = new byte[tam_title];
                String title = new String(bytes, 0, file.read(bytes), "UTF-8");
                reg.setTitle(title);
                
                byte[] lang = new byte[2];
                file.readFully(lang);
                reg.setOrgLanguage(lang);
                
                short tam_ovr = file.readShort();
                bytes = new byte[tam_ovr];
                String ovr = new String(bytes, 0, file.read(bytes), "UTF-8");
                reg.setOvr(ovr);
                
                reg.setReleaseDate(LocalDate.ofEpochDay(file.readLong()));
                reg.setPopularity(file.readFloat());
                reg.setVoteCount(file.readInt());
                reg.setVoteAverage(file.readFloat());
                reg.setRuntime(file.readInt());
                reg.setAdult(file.readByte());
                
                byte qtd_genres = file.readByte();
                List<String> genres = new ArrayList<>();
                for (int j = 0; j < qtd_genres; j++) {
                    short tam_genre = file.readShort();
                    bytes = new byte[tam_genre];
                    String genre = new String(bytes, 0, file.read(bytes), "UTF-8");
                    genre = genre.strip();
                    genres.add(genre);
                }
                reg.setGenreName(genres);
                
                file.close();
                return reg;
            } else {
                i--;
                file.seek(pos + tam_reg + 5);
            }
        }
        
        file.close();
        return null;
    }

    // READ (TITLE)
    public static Registro read(String title) throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile(DB_BINARIO, "rw");
        file.seek(0);
        int totalRegistros = file.readInt();
        
        for (int i=0; i<totalRegistros; i++) {
            long pos = file.getFilePointer();

            byte lapide = file.readByte();
            int tam_reg = file.readInt();
            int reg_id = file.readInt();
            
            
            if (lapide == 0) {
                Registro reg = new Registro();
                reg.setId(reg_id);

                short tam_org_title = file.readShort();
                byte[] bytes = new byte[tam_org_title];
                String org_title = new String(bytes, 0, file.read(bytes), "UTF-8");
                reg.setOrgTitle(org_title);

                short tam_title = file.readShort();
                bytes = new byte[tam_title];
                String titlereg = new String(bytes, 0, file.read(bytes), "UTF-8");
                reg.setTitle(title);

                if(title.equals(titlereg)) {
                    byte[] lang = new byte[2];
                    file.readFully(lang);
                    reg.setOrgLanguage(lang);
                    
                    short tam_ovr = file.readShort();
                    bytes = new byte[tam_ovr];
                    String ovr = new String(bytes, 0, file.read(bytes), "UTF-8");
                    reg.setOvr(ovr);
                    
                    reg.setReleaseDate(LocalDate.ofEpochDay(file.readLong()));
                    reg.setPopularity(file.readFloat());
                    reg.setVoteCount(file.readInt());
                    reg.setVoteAverage(file.readFloat());
                    reg.setRuntime(file.readInt());
                    reg.setAdult(file.readByte());
                    
                    byte qtd_genres = file.readByte();
                    List<String> genres = new ArrayList<>();
                    for (int j = 0; j < qtd_genres; j++) {
                        short tam_genre = file.readShort();
                        bytes = new byte[tam_genre];
                        String genre = new String(bytes, 0, file.read(bytes), "UTF-8");
                        genre = genre.strip();
                        genres.add(genre);
                    }
                    reg.setGenreName(genres);
                    
                    file.close();
                    return reg;
                }
            } else {
                i--;
            }
            file.seek(pos + tam_reg + 5);
        }
        
        file.close();
        return null;
    }

    // UPDATE
    public static boolean update(Registro newreg) throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile(DB_BINARIO, "rw");
        file.seek(0);
        int totalRegistros = file.readInt();
        
        for (int i=0; i<totalRegistros; i++) {
            long pos = file.getFilePointer();

            byte lapide = file.readByte();
            int tam_reg = file.readInt();
            int reg_id = file.readInt();
            
            if (lapide == 0 && reg_id == newreg.getId()) {
                if(newreg.calcTamReg() <= tam_reg) {
                    file.seek(pos);
                    writeData(newreg, tam_reg, file);
                } else {
                    file.seek(pos);
                    file.writeByte(1);
                    file.seek(file.length());
                    writeData(newreg, newreg.calcTamReg(), file);
                }
                return true;
            }
            file.seek(pos + tam_reg + 5);
        }
        file.close();
        return false;
    }

    // DELETE
    public static boolean delete(int id) throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile(DB_BINARIO, "rw");
		int totalRegistros = file.readInt();
		long pos;

		while (file.getFilePointer() < file.length()) {
			pos = file.getFilePointer();

			byte lapide = file.readByte();
			int tam_reg = file.readInt();
			int reg_id = file.readInt();

			if (lapide != 1 && reg_id == id) {
				file.seek(pos);

				file.writeByte(1);
                file.seek(0);
                file.writeInt(totalRegistros-1);

				file.close();
				return true;
			} else {
				file.seek(file.getFilePointer() + tam_reg - 4);
			}
		}

		file.close();
		return false;
    }


    /*          ORDENAÇÃO EXTERNA            */
    private static void insertionSort(Registro[] regs) {
        for (int i = 1; i < regs.length; i++) {
            Registro chave = regs[i];
            int j = i - 1;
    
            while (j >= 0 && regs[j].getId() > chave.getId()) {
                regs[j + 1] = regs[j];
                j--;
            }
            regs[j + 1] = chave;
        }
    }

    private static Registro readIB(RandomAccessFile fp) throws IOException{
        Registro reg = new Registro();
        int reg_id = fp.readInt();
        reg.setId(reg_id);
                
        short tam_org_title = fp.readShort();
        byte[] bytes = new byte[tam_org_title];
        String org_title = new String(bytes, 0, fp.read(bytes), "UTF-8");
        reg.setOrgTitle(org_title);
                    
        short tam_title = fp.readShort();
        bytes = new byte[tam_title];
        String title = new String(bytes, 0, fp.read(bytes), "UTF-8");
        reg.setTitle(title);
                    
        byte[] lang = new byte[2];
        fp.readFully(lang);
        reg.setOrgLanguage(lang);
                    
        short tam_ovr = fp.readShort();
        bytes = new byte[tam_ovr];
        String ovr = new String(bytes, 0, fp.read(bytes), "UTF-8");
        reg.setOvr(ovr);
                    
        reg.setReleaseDate(LocalDate.ofEpochDay(fp.readLong()));
        reg.setPopularity(fp.readFloat());
        reg.setVoteCount(fp.readInt());
        reg.setVoteAverage(fp.readFloat());
        reg.setRuntime(fp.readInt());
        reg.setAdult(fp.readByte());
                    
        byte qtd_genres = fp.readByte();
        List<String> genres = new ArrayList<>();
        for (int l = 0; l < qtd_genres; l++) {
            short tam_genre = fp.readShort();
            bytes = new byte[tam_genre];
            String genre = new String(bytes, 0, fp.read(bytes), "UTF-8");
            genre = genre.strip();
            genres.add(genre);
        }
        reg.setGenreName(genres);
        return reg;
    }
    private static void distribute(int n_reg, int n_arq, RandomAccessFile fp, RandomAccessFile[] tmps) throws IOException {
        int tmp_cnt=0;
        int totalRegistros = fp.readInt();
        Registro[] regs = new Registro[n_reg];

        for (int i=0; i<totalRegistros;) { 
            for (int j=0; j<n_reg && i<totalRegistros; j++, i++) {
                long pos = fp.getFilePointer();
                byte lapide = fp.readByte();
                int tam_reg = fp.readInt();

                if (lapide == 0) {
                    Registro reg = readIB(fp);
                    regs[j] = reg;
                } else {
                    j--;
                    i--;
                }
                fp.seek(pos + tam_reg + 5);
            }

            insertionSort(regs);
            for (int j=0; j<n_reg; j++) {
                create(regs[j], tmps[tmp_cnt]);
            }
            tmp_cnt = (tmp_cnt + 1) % n_arq;
        }
    }

    public static void merge(int n_reg, int n_arq, RandomAccessFile fp, RandomAccessFile[] entrada)
			throws IOException {
		// Variáveis para controlar a intercalação
        int tam_bloco = n_reg;   // Tamanho inicial do bloco (igual ao usado na distribuição)
        boolean end = false;     // Flag para controlar o término da intercalação

        // Lista de arquivos temporários de saída
		RandomAccessFile[] saida = new RandomAccessFile[n_arq];
        // Inicializa os arquivos de entrada com os temporários criados na distribuição
		for (int i = 0; i < n_arq; i++) {
			saida[i] = new RandomAccessFile("output" + i + ".bin", "rw");
			entrada[i].seek(0);
			saida[i].seek(0);
		}

        // Continua intercalando até que todos os registros estejam ordenados
		while (!end) {

            // Define numero de registros de cada arquivo temp
			int[] contadores = new int[n_arq];
			for (int i = 0; i < n_arq; i++) {
				try {
					contadores[i] = entrada[i].readInt();
				} catch (EOFException e) {
					contadores[i] = 0;
				}
			}

            // Verifica se há mais de um arquivo com registros 
            // Se houver, não é o último nível de intercalação
			int arquivosComRegistros = 0;
			for (int i = 0; i < n_arq; i++) {
				if (contadores[i] > 0) {
					arquivosComRegistros++;
				}
			}

            // Se só há um arquivo com registros, a intercalação está completa
			if (arquivosComRegistros <= 1) {
				int arquivoFinal = -1;
				for (int i = 0; i < n_arq; i++) {
					if (contadores[i] > 0) {
						arquivoFinal = i;
						break;
					}
				}

                // Copia o arquivo final para o arquivo original
				fp.setLength(0);
				entrada[arquivoFinal].seek(0);

                // Copia o cabeçalho (número de registros)
				int totalRegs = entrada[arquivoFinal].readInt();
				fp.writeInt(totalRegs);

                // Copia todos os registros
				byte[] buffer = new byte[4096]; // Buffer para cópia eficiente
				int bytesRead;

				while ((bytesRead = entrada[arquivoFinal].read(buffer)) != -1) {
					fp.write(buffer, 0, bytesRead);
				}

				end = true;
				break;
			}

            // Se ainda deve fazer mais intercalações (+1 arquivo com regs)
            // Processa blocos de arquivos de entrada
			while (true) {
                // Em cada nível, resetamos os n arquivos de saída
                for (int i = 0; i < n_arq; i++) {
                    saida[i].setLength(0); // Limpa o arquivo de saída
                    saida[i].writeInt(0); // Inicializa o contador de registros
                }

                // Intercala registros dos n arquivos para o arquivo de saída atual
				intercalar(entrada, saida, tam_bloco, n_arq);

                // Se esta foi a ultima intercalação (apenas 1 arq de saída cheio),
                boolean lastItc = true;
                for (int i=1; i<n_arq; i++) {
                    if (saida[i].length() != 4) {
                        lastItc = false;
                    }
                }

                // Se é ultima intercalação escreve resultado no arquivo de dados
                if (lastItc) {
                    fp.seek(0);
                    int totalRegs = fp.readInt();
                    fp.setLength(0); // Limpa o arquivo de saída
                    fp.seek(0);
                    saida[0].seek(0);

                    byte[] buffer = new byte[4096];
                    int bytesRead;

                    while ((bytesRead = saida[0].read(buffer)) != -1) {
                        fp.write(buffer, 0, bytesRead);
                    }

                    fp.seek(0);
                    fp.writeInt(totalRegs);

                    end = true;
                    break;
                }

                // Dobra o tamanho do bloco para a próxima intercalação
			    tam_bloco *= 2;

                // Troca os arquivos de entrada e saída para a próxima fase
                RandomAccessFile[] temp = entrada;
                entrada = saida;
                saida = temp;
			}
		}

        // Fecha e remove todos os arquivos temporarios
		for (int i = 0; i < n_arq; i++) {
			entrada[i].close();
			saida[i].close();
			new java.io.File("temp" + i + ".bin").delete();
			new java.io.File("output" + i + ".bin").delete();
		}
	}

    private static void intercalar(RandomAccessFile[] entrada, RandomAccessFile[] saida, int tam_seg, int n_arq) throws IOException {
        // Armazena a posição dos regs lidos os respectivos registros (uso do id)
		long[] posicoes = new long[n_arq]; // Ponteiro de cada arquivo
		Registro[] regs = new Registro[n_arq];
        int[] count_arq = new int[n_arq]; // contagem de vezes que um arq foi lido
        int ativos = 0; // determina qnts arquivos com regs tem

        int qnt_regs_seg = 0; // quantidade de registros por segmento
        int tam_maior_arq = -1; // tamanho do maior arq (para calculos)

        for (int i=0; i<n_arq; i++) {
            entrada[i].seek(0);
            int tam_arq = entrada[i].readInt();
            // Acha o maior arq
            if(tam_arq > tam_maior_arq) {
                tam_maior_arq = tam_arq;
            }
            // Calc qnt de arq com regs
            if (tam_arq > 0) {
                ativos += 1;
            }
        }
        // quantidade de segmentos totais para iterar
        int qnt_segs = (int) Math.ceil(tam_maior_arq/(double)tam_seg);
        // calc de registros por segmento
        qnt_regs_seg += tam_seg*ativos;

        int arq_saida = 0;
        for (int seg = 0; seg < qnt_segs; seg++) { // Iteração sobre os segmentos
            int idxmenor = -1;
            for (int i=0; i<n_arq; i++) {
                count_arq[i] = 0; // contagem inicia em 0
            }

            for (int i = 0; i < qnt_regs_seg; i++) { // Iteração sobre os registros dos segmentos
                // Iteração sobre os ids de cada arq
                // Se for 1a iteração, le todos os ids da coluna
                // Se não, avança com a posição do menor lido na ultima iteração e lê o id novo
                for (int j=0; j<n_arq; j++) {
                    if (i==0) {
                        try {
                            posicoes[j] = entrada[j].getFilePointer();
                            entrada[j].readByte();
                            entrada[j].readInt();
                            regs[j] = readIB(entrada[j]);
                            posicoes[j] = entrada[j].getFilePointer();
                            count_arq[j]++;
                        } catch (EOFException e) {
                            // Se chegar no fim do arq ids devem ser os maiores para não
                            // atrapalhar a escolha do menor
                            if (regs[j] == null) {
                                regs[j] = new Registro();
                            }
                            regs[j].setId(Integer.MAX_VALUE);
                        }
                    } else {
                        j = idxmenor;
                        try {
                            entrada[j].readByte();
                            entrada[j].readInt();
                            // Restrição para não passar do limite do segmento no arquivo
                            if (count_arq[j] < tam_seg) { 
                                regs[j] = readIB(entrada[j]);
                                posicoes[j] = entrada[j].getFilePointer();
                                count_arq[j]++;
                            } else {
                                regs[j].setId(Integer.MAX_VALUE);
                                entrada[j].seek(posicoes[j]);
                            }
                            break; 
                        } catch (EOFException e){
                            if (regs[j] == null) {
                                regs[j] = new Registro();
                            }
                            regs[j].setId(Integer.MAX_VALUE);
                            break;
                        }
                    }
                }
                
                // Acha o menor id dentre os ids lidos e coloca no arq de saída
                Registro menor = new Registro();
                menor.setId(Integer.MAX_VALUE);
                for (int j=0; j<n_arq; j++) {
                    if (regs[j].getId() < menor.getId()) {
                        menor = regs[j];
                        idxmenor = j;
                    }
                }
                if (menor.getId() != Integer.MAX_VALUE) {
                    create(menor, saida[arq_saida]);
                }
            }
            // Atualiza numero do arquivo de saida após cada segmento
            arq_saida = (arq_saida + 1) % n_arq;
        }
	}

	public static void intercalacaoBalanceada(int n_reg, int n_arq)
			throws FileNotFoundException, IOException {
		RandomAccessFile fp = new RandomAccessFile(DB_BINARIO, "rw");
		fp.seek(0);

		RandomAccessFile[] tmps = new RandomAccessFile[n_arq];
		for (int i = 0; i < n_arq; i++) {
			tmps[i] = new RandomAccessFile("temp" + i + ".bin", "rw");
			tmps[i].seek(0);
		}

		distribute(n_reg, n_arq, fp, tmps);
		merge(n_reg, n_arq, fp, tmps);

        fp.close();
	}
}

public class TP1 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcao;

        do {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1 - Carregar dados");
            System.out.println("2 - Criar registro");
            System.out.println("3 - Ler registro por ID");
            System.out.println("4 - Ler registro por título");
            System.out.println("5 - Atualizar registro");
            System.out.println("6 - Deletar registro");
            System.out.println("7 - Ordenar arquivo");
            System.out.println("0 - Sair");
            System.out.print("Escolha uma opção: ");

            opcao = scanner.nextInt();
            scanner.nextLine(); 

            try {
                switch (opcao) {
                    case 1:
                        Registro.loadData();
                        System.out.println("Dados carregados com sucesso.");
                        break;

                    case 2:
                        Registro reg = inputReg(scanner);
                        RandomAccessFile file = new RandomAccessFile(Registro.DB_BINARIO, "rw");
                        Registro.create(reg, file);
                        System.out.println("Registro criado com sucesso.");
                        file.close();
                        break;

                    case 3:
                        System.out.print("ID do registro: ");
                        int id = scanner.nextInt();
                        System.out.println(Registro.read(id));
                        break;

                    case 4:
                        System.out.print("Título do registro: ");
                        String title = scanner.nextLine();
                        System.out.println(Registro.read(title));
                        break;

                    case 5:
                        Registro regAtualizado = inputReg(scanner);
                        boolean resultup = Registro.update(regAtualizado);
                        if(resultup) {
                            System.out.println("Registro atualizado com sucesso.");
                        } else {
                            System.out.println("Erro ao atualizar. Registro não existe.");
                        }
                        break;

                    case 6:
                        System.out.print("ID do registro a ser deletado: ");
                        int idDel = scanner.nextInt();
                        boolean resultdel = Registro.delete(idDel);
                        if(resultdel) {
                            System.out.println("Registro deletado com sucesso.");
                        } else {
                            System.out.println("Erro ao deletar. Registro não existe.");
                        }
                        break;

                    case 7:
						System.out.print("Numero de registros em cada bloco: ");
						int n_reg = scanner.nextInt();
						System.out.print("Numero de arquivos temporarios a serem usados: ");
						int n_arq = scanner.nextInt();
						System.out.println("Iniciando ordenação por intercalação balanceada...");
						Registro.intercalacaoBalanceada(n_reg, n_arq);
						System.out.println("Ordenação concluída com sucesso!");
						break;

                    case 0:
                        System.out.println("Saindo...");
                        break;

                    default:
                        System.out.println("Opção inválida! Tente novamente.");
                        break;
                }
            } catch (IOException e) {
                System.out.println("Erro ao executar a operação: " + e.getMessage());
            }
        } while (opcao != 0);

        scanner.close();
    }

    private static Registro inputReg(Scanner scanner) {
        System.out.print("ID: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Título original: ");
        String orgTitle = scanner.nextLine();

        System.out.print("Título: ");
        String title = scanner.nextLine();

        System.out.print("Idioma original (2 caracteres): ");
        String orgLanguage = scanner.nextLine();

        System.out.print("Descrição: ");
        String ovr = scanner.nextLine();

        System.out.print("Data de lançamento (YYYY-MM-DD): ");
        LocalDate releaseDate = LocalDate.parse(scanner.nextLine());

        System.out.print("Popularidade: ");
        float popularity = scanner.nextFloat();

        System.out.print("Número de votos: ");
        int voteCount = scanner.nextInt();

        System.out.print("Média de votos: ");
        float voteAverage = scanner.nextFloat();

        System.out.print("Duração em minutos: ");
        int runtime = scanner.nextInt();

        System.out.print("Filme adulto? (1 = Sim, 0 = Não): ");
        byte adult = scanner.nextByte();
        scanner.nextLine(); 

        System.out.print("Gêneros (separados por vírgula): ");
        String[] genresArray = scanner.nextLine().split(",");
        List<String> genreList = Arrays.asList(genresArray);

        return new Registro(id, orgTitle, title, orgLanguage.getBytes(), ovr, releaseDate, popularity, voteCount, voteAverage, runtime, adult, genreList);
    }
}