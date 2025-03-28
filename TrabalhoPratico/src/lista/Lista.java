package lista;
import java.io.*;
import java.util.*;
//Lista dinamica gerenica que funciona como buffer
public class Lista<T> implements Iterable<T> {
	private Node<T> head;
	private Node<T> tail;
	private int size;
	private static final int MAX_SIZE = 500;
	
	class Node<T> {
		private T data;
		private Node<T> next;

		Node (T data) {
			this.data = data;
			this.next = null;	
		}
	}//END_NODE
	
	public Lista () {
		this.head = null;
		this.tail = null;
		this.size = 0;
	}
	public int getSize() { //tamanho atual da lista
		return this.size;
	}
	public void addLista (T data) throws ListaCheiaException { //adiciona a lista
		if (size == MAX_SIZE) 
			throw new ListaCheiaException("O \"buffer\"(lista) esta cheio!");
		if (head == null) {
			head = new Node<>(data);
			size++;
			return;
		}else if (tail == null) {
			tail = new Node<>(data);
			head.next = tail;
			size++;
			return;
		}
		tail.next = new Node<>(data);
		tail = tail.next;
		size++;
		return;

	}
	
	public void clear() { //limpa a lista
		size = 0;
		head = tail = null;
	}
	public boolean isEmpty() {  //lista esta vazi
		return (head == null && tail == null) ? true : false;
	}
	public boolean isFull () { //lista esta cheia
		return size == MAX_SIZE ? true : false;
	}

	@Override //interface iterable para poder utilizar o for each loop
	public Iterator<T> iterator () {
		return new Iterator<T> () {
			private Node<T> temp = head;

			@Override
			public boolean hasNext () {
				return temp != null;
			}

			@Override
			public T next () {
				if(!hasNext())
					throw new IllegalStateException("no more elements");
				T data = temp.data;
				temp = temp.next;
				return data;
			}
		};
	}
 
}//END_LISTA
