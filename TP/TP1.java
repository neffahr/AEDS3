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

    // Método que recebe uma linha lida do aquivo csv e peenche as informações do objeto
    private void populate(String line) throws FileNotFoundException, IOException{
        List<String> data = splitCSV(line);

        this.id = Integer.parseInt(data.get(1));
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
        Matcher matcher = Pattern.compile("\"([^\"]*)\"|([^,]+)").matcher(line);

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
        RandomAccessFile raf = new RandomAccessFile("horror_movies.csv", "rw");
        raf.seek(0);
        raf.readLine(); // To do: check if it goes to bottom line
        Registro reg = new Registro();

        while (raf.getFilePointer() < raf.length()) { // To do: check condition
            String line = raf.readUTF();
            reg.populate(line);
            create(reg);
        }
        
        raf.close();
    }

    /*         CRUD OPERATIONS         */
    // CREATE
    public static void create(Registro reg) throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile("registros.csv", "rw");
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
        file.writeInt(cbc);                                     // Cabeçalho (int)                           4 bytes

        file.seek(file.length()); // vai para final do arquivo

        // Define o tamanho de um registro para escrita
        int tam_reg = 38 + reg.getOrgTitle().getBytes().length + reg.getTitle().getBytes().length + 
                      reg.getOvr().getBytes().length;
        for (String str : reg.getGenreName()) {
            tam_reg += 2 + str.getBytes().length;
        }

        file.writeByte(0);                                    // Lapide (int)                              4 bytes
        file.writeInt(tam_reg);                                 // Tamanho do Registro (int)                 4 bytes
        file.writeInt(reg.getId());                             // ID (int)                                  4 bytes
        file.write((short)reg.getOrgTitle().length());          // Tamanho string original title (short)     2 bytes
        file.writeUTF(reg.getOrgTitle());                       // original title (string)                   variavel
        file.write((short)reg.getTitle().length());             // Tamanho string title (short)              2 bytes
        file.writeUTF(reg.getTitle());                          // Title (string)                            variavel
        file.write(reg.getOrgLanguage());                       // original language (string fixa)           2 bytes
        file.writeInt((short)reg.getOvr().length());            // Tamanho string overview (short)           2 bytes
        file.writeUTF(reg.getOvr());                            // Overview (string)                         variavel
        file.writeLong(reg.getReleaseDate().toEpochDay());      // Release date (long)                       8 bytes
        file.writeFloat(reg.getPopularity());                   // Popularity (float)                        4 bytes   
        file.writeInt(reg.getVoteCount());                      // Vote count (int)                          4 bytes
        file.writeFloat(reg.getVoteAverage());                  // Vote average (float)                      4 bytes
        file.writeInt(reg.getRuntime());                        // Runtime (int)                             4 bytes
        file.writeByte(reg.getAdult());                         // Adult (boolean)                           1 byte
        file.writeByte(reg.getGenreName().size());              // Quantidade de itens da lista (byte)       1 byte
        
        for (String str : reg.getGenreName()) {
            file.write((short)str.length());                    // Tamanho da string genre name (short)      2 bytes
            file.writeUTF(str);                                 // Genre name (String)                       variavel
        }

        file.close();
    }

    // READ (ID)
    public static void read(int id) throws FileNotFoundException, IOException {
        
    }

    // READ (TITLE)
    public static void read(String title) throws FileNotFoundException, IOException {
        
    }

    // UPDATE
    public static void update(Registro reg) throws FileNotFoundException, IOException {
        
    }

    // DELETE
    public static void delete(int id) throws FileNotFoundException, IOException {
        
    }
}

public class TP1 {
    public static void main(String[] args) {
        
    }
}