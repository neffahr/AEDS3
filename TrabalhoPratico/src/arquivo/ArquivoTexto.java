package arquivo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ArquivoTexto {
//--> ATRIBUTOS
	private BufferedReader b_reader;
	private FileReader f_reader;

//--> CONSTRUTOR
	public ArquivoTexto(String caminho_arquivo) throws IOException{
		this.f_reader = new FileReader(caminho_arquivo);
		this.b_reader = new BufferedReader(f_reader);
	}
//--> METODOS
	public String lerLinha() throws IOException {
		return b_reader.readLine();
	}
	public boolean estaVazio() throws IOException { //funcao para saber se o arquivo esta vazio
		b_reader.mark(1);
		int primeiro_caractere = b_reader.read(); //tenta ler caractere
		b_reader.reset();

		return primeiro_caractere == -1;
	}	
 	public void fechar() throws IOException {
		if (b_reader != null)
			b_reader.close();
		if (f_reader != null)
			f_reader.close();
	}

}//END_ARQUIVOTEXTO
