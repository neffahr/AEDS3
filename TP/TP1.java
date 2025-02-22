import java.util.*;
import java.time.LocalDate;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

class Registro {
    
    protected boolean lapide; // ?byte
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
    protected boolean adult; // ?byte
    protected byte qnt_items;
    List<String> gen_name;

    public Registro() {
        lapide = false;
        org_language = new byte[2];
        popularity = 0;
        vote_count = 0;
        vote_average = 0;
        runtime=0;
        adult = false;
        qnt_items = 0;
        gen_name = new ArrayList<>();
    }

    public Registro(boolean lapide, int id, String org_title, String title, byte[] org_language, String ovr, LocalDate release_date, float popularity, int vote_count, float vote_average, int runtime, boolean adult, byte qnt_items, List<String> gen_name){
        this.lapide = lapide;
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
        this.qnt_items = qnt_items;
        this.gen_name = gen_name;
    }

    public boolean getLapide(){
        return lapide;
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

    public int getLRuntime(){
        return runtime;
    }

    public boolean getAdult(){
        return adult;
    }

    public byte getQntItems(){
        return qnt_items;
    }

    public List<String> getGenName(){
        return gen_name;
    }

    public void setLapide(boolean lapide){
        this.lapide = lapide;
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

    public void setLRuntime(int runtime){
        this.runtime = runtime;
    }

    public void setAdult(boolean adult){
        this.adult = adult;
    }

    public void setQntItems(byte qnt_items){
        this.qnt_items = qnt_items;
    }

    public void setGenName(List<String> gen_name){
        this.gen_name = gen_name;
    }

    public void populate(int id) throws FileNotFoundException, IOException{
        RandomAccessFile file = new RandomAccessFile("horror_movies.csv", "r");
        file.seek(0);
        file.readLine();
        
        
    }

    public void writeReg() throws FileNotFoundException, IOException {
        RandomAccessFile file = new RandomAccessFile("registros.csv", "w");
        
    }
}

public class TP1 {
    public static void main(String[] args) {
        
    }
}