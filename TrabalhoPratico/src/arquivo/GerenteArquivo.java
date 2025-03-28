package arquivo;

import lista.Lista;
import lista.ListaCheiaException;
import livro.Livro;
import java.io.IOException;

public class GerenteArquivo {
//--> ATRIBUTOS
	
//--> CONSTRUTOR
	
//--> METODOS
	public boolean cargaArquivo () {//carrega o arquivo texto para byte
		Lista<Livro> t_lista = new Lista<>(); //lista que guarda os registro livros lidos
		try {
			ArquivoTexto t_arq = new ArquivoTexto("archive/books.csv");	
			ArquivoBinario b_arq = new ArquivoBinario();
			while (!t_arq.estaVazio()) { //enquanto tiver conteudo para ser lido no arquivo texto
				if (t_lista.isFull()) { //se o buffer da lista estiver cheio
					Lista<byte[]> b_lista = new Lista();
					for (Livro livro : t_lista) {
						b_lista.addLista(livro.toBin()); //converte e salva os livro em uma lista de forma binaria
					}
					b_arq.appendArquivoBin(b_lista); //adiciona registro no arquiv bin
					t_lista.clear();
				}
				t_lista.addLista(Livro.strToLivro(t_arq.lerLinha()));
			}
		} catch (IOException e) {
			System.out.println(e);
		} catch (ListaCheiaException e) {
			System.out.println(e);
		}
		return true;
	}

}//END_GERENTEARQUIVO
