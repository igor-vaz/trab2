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

	public String toString() {
	    return "(Andar Numero: " + numero + " fila: " + fila + ")";
	}
}

class Elevador extends Thread {
	private static int id_gen;
	private int id;
	private int capacidade;
	private Andar andarAtual;
	private int qtdPessoas;

	public Elevador(int capacidade, Andar andarAtual) {
		this.id = id_gen++;
		this.id = id;
		this.capacidade = capacidade;
		this.andarAtual = andarAtual;
		this.qtdPessoas = 0;
	}

	public int getCapacidade() {
		return this.capacidade;
	}

	public Andar getAndarAtual() {
		return this.andarAtual;
	}

	public int getQtdPessoas() {
		return this.qtdPessoas;
	}

	public String toString() {
	    return "(Elevador ID: " + id + " capacidade: " + capacidade + " andarAtual: " + andarAtual + " # de Pessoas: " + qtdPessoas + ")";
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

	public String toString() {
	    return "(Requisicao ID: " + id + " Destino: " + destino + ")";
	}
}

public class SCE {
	// ArrayList<Andar> andar;
	public static void main(String[] args) {
		
		// //le cada elemento do arquivo
		// String [] content = new String[200];
		// Scanner s = null;
		// int i =0;
  //       try {
  //           s = new Scanner(new BufferedReader(new FileReader("file.txt")));

  //           while (s.hasNext()) {
		// 		content[i] = s.next();
  //               //System.out.println(content[i]);
		// 		i++;
  //           }
  //       }catch(FileNotFoundException e){
			
		// }
		//  finally {
  //           if (s != null) {
  //               s.close();
  //           }
  //       }


		int qtdAndares = 0;
		int qtdElevadores = 0;
		int capacidade = 0;
		ArrayList<Andar> andares = new ArrayList<Andar>();
		ArrayList<Elevador> elevadores = new ArrayList<Elevador>();
		
	   	//le cada linha do arquivo
		// ArrayList<String[]> splitted = new ArrayList<String[]>();
		String[] splitted;

		try{
			// Open the file
			FileInputStream fstream = new FileInputStream("file.txt");
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			//Read File Line By Line

			for(int linha = 0 ; (strLine = br.readLine()) != null ; linha++) {
			// split the line on your splitter(s)
				// splitted.add(strLine.split(" ")); // here - is used as the delimiter
				splitted = strLine.split(" ");

				switch(linha) {
					case 0:
						qtdAndares = Integer.parseInt(splitted[0]);
						qtdElevadores = Integer.parseInt(splitted[1]);
						capacidade = Integer.parseInt(splitted[2]);

						for (int i = 0 ; i < qtdAndares ; i++)
							andares.add(new Andar(i));
						
						break;

					case 1:
						for (int i = 0 ; i < qtdElevadores ; i++)
							elevadores.add(new Elevador(capacidade, andares.get(Integer.parseInt(splitted[i]))));

						break;

					default:
						int qtd_pessoas = Integer.parseInt(splitted[0]);

						for(int i = 0 ; i < qtd_pessoas ; i++)
							andares.get(linha-2).addRequisicaoFila(new Requisicao(Integer.parseInt(splitted[i+1])));
							
				}
			}

			//Close the input stream
			in.close();
	    } catch(Exception e) { //Catch exception if any
		  	System.err.println("Error: " + e.getMessage());
		}



		// System.out.println(splitted.size());
		// for (int j = 0 ; j < splitted.size() ; j++){
		for (int j = 0 ; j < 5 ; j++){
			// for(String s1 : splitted.get(j))
			// 	System.out.println(s1+"|");
			System.out.println(andares.get(j));
		}

				
		// for (int i = 0 ; i < N ; i++)  {
		// 	this.andar.add(new Andar(i));

		// 	foreach (destinos as destino)  {
		// 		this.andar.get(i).addRequisicaoFila(new Requisicao(destino));
		// 	}
		// }
	}
}