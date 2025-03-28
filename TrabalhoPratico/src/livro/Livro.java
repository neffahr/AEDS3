package livro;

import java.util.regex.*;
import java.util.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Livro {
//--> ATRIBUTOS
	private String isbn13;
	private String isbn10;
	private String title;
	private String subtitle;
	private String[] authors;
	private String[] categories;
	private String thumbnail;
	private String description;
	private int published_year;
	private int num_pages;
	private int ratings_count;
	private float average_rating;
//--> CONSTRUTOR
	public Livro () {

	}
//--> GETTER SETTER

	public void setIsbn13 (String isbn13) {
		this.isbn13 = isbn13;	
	}
	public void setIsbn10 (String isbn10) {
		this.isbn10 = isbn10;	
	}

	public void setTitle (String title) {
		this.title = title;	
	}

	public void setSubtitle (String subtitle) {
		this.subtitle = subtitle;
	}
	public void setAuthors (String authors) {
		if(authors != null)
			this.authors = authors.split(";");
		else
			this.authors = null;	
	}
	public void setCategories (String categories) {
		if (categories != null)
			this.categories = categories.split(";");
		else	
			this.categories = null;
	}
	public void setThumbnail (String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public void setDescription (String description) {
		this.description = description;
	}
	public void setPublishedYear (int published_year) {
		this.published_year = published_year;
	}
	public void setAverageRating (float average_rating) {
		this.average_rating = average_rating;
	}
	public void setNumPages (int num_pages) {
		this.num_pages = num_pages;
	}
	public void setRatingsCount (int rating_counts) {
		this.ratings_count = ratings_count;
	}
	public String getIsbn13 () {
		return this.isbn13;
	}
	public String getIsbn10 () {
		return this.isbn10;
	}	

	public String getTitle () {
		return this.title;
	}	
	public String getSubtitle () {
		return this.subtitle;
	}
	public String[] getAuthors () {
		return this.authors.clone();
	}	
	public String[] getCategories () {
		return this.categories.clone();
	}

	public String getThumbnail () {
		return this.thumbnail;
	}	
	
	public String getDescription () {
		return this.description;
	}	
	public int getPublishedYear () {
		return this.published_year;
	}
	public float getAverageRating () {
		return this.average_rating;
	}
	public int getNumPages () {
		return this.num_pages;
	}
	public int getRatingsCount () {
		return this.ratings_count;
	}

//--> METODOS
	public static Livro strToLivro (String registro) { //Converte uma string para um objeto livro
		Pattern padrao = Pattern.compile("\"([^\"]*)\"|([^,]+)");
        	//Pattern padrao = Pattern.compile("\"([^\"]*)\"|([^,]*)");
		//Pattern padrao = Pattern.compile("\"([^\"]*)\"|([^,]*)");
		Matcher match = padrao.matcher(registro);
		Livro livro = new Livro();
		int index = 0;
	
		while (match.find()) {
			String temp = match.group(1) != null ? match.group(1) : match.group(2);
		
        		if (temp == null || temp.trim().isEmpty()) {
            			temp = null; // Se o valor estiver faltando, defina como null
        		}		
		
			switch (index) {
				case 0:
					livro.setIsbn13(temp);
					break;	
				case 1:
					livro.setIsbn10(temp);
					break;	
				case 2:
					livro.setTitle(temp);
					break;	
				case 3:
					livro.setSubtitle(temp);
					break;	
				case 4:
					livro.setAuthors(temp);
					break;	
				case 5:
					livro.setCategories(temp);
					break;	
				case 6:
					livro.setThumbnail(temp);
					break;	
				case 7:
					livro.setDescription(temp);
					break;	

				case 8:
					if (temp == null) 
						livro.setPublishedYear(0);
					else
						livro.setPublishedYear(Integer.parseInt(temp));
					break;	
				case 9:

					if (temp == null) 
						livro.setAverageRating(0);
					else
						livro.setAverageRating(Float.parseFloat(temp));
					break;	
				case 10:
					if (temp == null) 
						livro.setNumPages(0);
					else
						livro.setNumPages(Integer.parseInt(temp));
					break;
				case 11:
					if (temp == null)
						livro.setRatingsCount(0);
					else
						livro.setRatingsCount(Integer.parseInt(temp));
					break;	
			}
			index++;
		}
		return livro;
	}

	public byte[] toBin () { //converte um objeto livro para um byte array
		byte[][] b_array = new byte[12][];
		int tamanho_registro = 0;
		b_array[0] = this.getIsbn13().getBytes(StandardCharsets.UTF_8);
		b_array[1] = this.getIsbn10().getBytes(StandardCharsets.UTF_8);
		b_array[2] = this.getTitle().getBytes(StandardCharsets.UTF_8);
		b_array[3] = this.getSubtitle().getBytes(StandardCharsets.UTF_8);
		b_array[4] = String.join("|", this.getAuthors()).getBytes(StandardCharsets.UTF_8);
		b_array[5] = String.join("|", this.getCategories()).getBytes(StandardCharsets.UTF_8);
		b_array[6] = this.getThumbnail().getBytes(StandardCharsets.UTF_8);
		b_array[7] = this.getDescription().getBytes(StandardCharsets.UTF_8);
		b_array[8] = ByteBuffer.allocate(4).putInt(this.getPublishedYear()).array();
		b_array[9] = ByteBuffer.allocate(4).putFloat(this.getAverageRating()).array();
		b_array[10] = ByteBuffer.allocate(4).putInt(this.getNumPages()).array();
		b_array[11] = ByteBuffer.allocate(4).putInt(this.getRatingsCount()).array();
		//calculo do tamanho do registro
		tamanho_registro = 13 + 10 + Integer.BYTES + b_array[2].length + Integer.BYTES +b_array[3].length + Integer.BYTES + b_array[4].length + Integer.BYTES + b_array[5].length + Integer.BYTES + b_array[6].length + Integer.BYTES + b_array[7].length + Integer.BYTES + Float.BYTES + Integer.BYTES + Integer.BYTES;
		ByteBuffer buffer = ByteBuffer.allocate(tamanho_registro + 6);
		buffer.put((byte)'0');
		buffer.putInt(tamanho_registro);
		buffer.put(b_array[0]);
		buffer.put(b_array[1]);
		buffer.putInt(b_array[2].length);
		buffer.put(b_array[2]);
		buffer.putInt(b_array[3].length);
		buffer.put(b_array[3]);
		buffer.putInt(this.getAuthors().length);
		buffer.put(b_array[4]);
		buffer.putInt(this.getCategories().length);
		buffer.put(b_array[5]);
		buffer.putInt(b_array[6].length);
		buffer.put(b_array[6]);
		buffer.putInt(b_array[7].length);
		buffer.put(b_array[7]);
		buffer.put(b_array[8]);
		buffer.put(b_array[9]);
		buffer.put(b_array[10]);
		buffer.put(b_array[11]);

		return buffer.array();
	}
	public static Livro toText(byte[] data) {
		return null;	
	}

}//END_LIVRO
