package arquivo;

import lista.Lista;

import java.nio.file.*;
import java.nio.ByteBuffer;
import java.io.IOException;
import java.nio.channels.FileChannel;
// classe que controla as operacoes no arquivo binario
public class ArquivoBinario {
//--> ATRIBUTOS
	private static final Path caminho_arquivo =  Paths.get("archive/books.bin");
//--> CONSTRUTOR
	public ArquivoBinario () { //cria o arquivo binario e adiciona o campo para ultimo id add
		String str = "0000000000000";
		byte[] byte_str = str.getBytes();
		ByteBuffer buffer = ByteBuffer.wrap(byte_str);	
		try {
			FileChannel f_channel = FileChannel.open(caminho_arquivo, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
			f_channel.write(buffer); //utilizando o filechanel pq randomaccessfile é legado
			f_channel.close();
		} catch (IOException e) {

		}
	}
//--> METODOS

	public void appendArquivoBin (Lista<byte[]> data_lista) throws IOException { //adiciona info ao arquivo bin e utiliza o append do filechanel
		int count = 0;
		try (FileChannel f_channel = FileChannel.open(caminho_arquivo, StandardOpenOption.WRITE, StandardOpenOption.APPEND, StandardOpenOption.CREATE)) {
			for (byte[] data : data_lista) {
				ByteBuffer buffer = ByteBuffer.wrap(data);
				f_channel.write(buffer);
				count++;
				if(data_lista.getSize() == count) { //adiciona os dados bin ao aruqivo
					//le o isbn do buffer
					System.out.println(buffer);
					byte[] isbn = new byte[13];
					//char c = buffer.getChar();
					//int i = buffer.getInt();
					buffer.position(6);
					buffer.get(isbn);
					ByteBuffer temp = ByteBuffer.wrap(isbn);
					this.inserirIdInicio(temp);
				}
				
			}
			System.out.println("Dados Binários adicionados ao Arquivo.");

		}
	}
	public void inserirIdInicio (ByteBuffer temp) { //altera o campo ultimo id inserido no comeco do arquiv
		try (FileChannel f_temp = FileChannel.open(caminho_arquivo, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
			f_temp.position(0);
			f_temp.write(temp);
		} catch (IOException e) {

		}
	}
}//END_ARQUIVOBINARIO
