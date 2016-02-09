import java.util.*;
import java.io.*;
import java.lang.Thread;
import java.io.File;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

class Andar {
	private int numero;
	private ArrayList<Requisicao> fila;

	public Andar(int numero) {
		this.numero = numero;
		this.fila = new ArrayList<Requisicao>();
	}

	public int getNumero() {
		return this.numero;
	}

	public ArrayList<Requisicao> getFila() {
		return this.fila;
	}

	public void setFila(ArrayList<Requisicao> fila) {
		this.fila = fila;
	}

	public void addRequisicaoFila(Requisicao requisicao) {
		this.fila.add(requisicao);
	}
}

class Requisicao {
	private static int id_gen;
	private int id;
	private int destino;

	public Requisicao(int destino) {
		this.id = id_gen++;
		this.destino = destino;
	}

	public int getId() {
		return this.id;
	}

	public int getDestino() {
		return this.destino;
	}
}

class Elevador extends Thread {
	private int capacidade;
	private int pessoas;
	private Andar andarAtual;

	public Elevador(int capacidade, Andar andar) {
		this.capacidade = capacidade;
		this.pessoas = 0;
		this.andarAtual = andar;
	}

	public int getCapacidade() {
		return this.capacidade;
	}

	public int getPessoas() {
		return this.pessoas;
	}

	public Andar getAndarAtual() {
		return this.andarAtual;
	}
}

public class SCE {
	// ArrayList<Andar> andar;
	public static void main(String[] args) {
		
		Scanner s = null;

        try {
            s = new Scanner(new BufferedReader(new FileReader("file.txt")));

            while (s.hasNext()) {
                System.out.println(s.next());
            }
        }catch(FileNotFoundException e){
			
		}
		 finally {
            if (s != null) {
                s.close();
            }
        }
				
		// for (int i = 0 ; i < N ; i++)  {
		// 	this.andar.add(new Andar(i));

		// 	foreach (destinos as destino)  {
		// 		this.andar.get(i).addRequisicaoFila(new Requisicao(destino));
		// 	}
		// }
	}
}