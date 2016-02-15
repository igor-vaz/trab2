import java.util.*;
import java.io.*;
import java.lang.Thread;
import java.io.File;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

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

	public int qtdRequisicoesFila() {
		return this.fila.size();
	}

	public String toString() {
	    return "(Andar Numero: " + numero + " fila: " + fila + ")";
	}
}

class Elevador extends Thread {
	private Monitor monitor;
	private static int id_gen;
	private int numero;
	private int capacidade;
	private Andar andarAtual;
	private ArrayList<Requisicao> fila;

	public Elevador(int capacidade, Andar andarAtual, Monitor monitor) {
		this.numero = id_gen++;
		this.capacidade = capacidade;
		this.andarAtual = andarAtual;
		this.monitor = monitor;
		this.fila = new ArrayList<Requisicao>();
	}

	public int getNumero() {
		return this.numero;
	}

	public ArrayList<Requisicao> getFila() {
		return this.fila;
	}

	public int getCapacidade() {
		return this.capacidade;
	}

	public Andar getAndarAtual() {
		return this.andarAtual;
	}

	public void setAndarAtual(Andar andarAtual) {
		this.andarAtual = andarAtual;
	}

	public int qtdResquisicoes() {
		return this.fila.size();
	}

	public void addRequisicao(Requisicao requisicao) {
		this.fila.add(requisicao);
	}

	public void removeRequisicao(Requisicao requisicao) {
		this.fila.remove(requisicao);
	}

	//Pega o proximo destino para o elevador, qnd ja existem pessoas nele
	public Requisicao proxDestino(){
		int min = this.monitor.getAndares().size();
	    int closest = andarAtual.getNumero();
	    //int cont = 0;

	    for (int i = 0 ; i < this.fila.size() ; i++) {
	    	//System.out.println(fila.get(i).getDestino() + " " + andarAtual.getNumero());
	        final int diff = Math.abs(fila.get(i).getDestino() - andarAtual.getNumero());

	        if (diff < min) {
	            min = diff;
	            closest = i;
	        }
	    }

	    return fila.get(closest);
	}


	public String toString() {
	    return "(Elevador ID: " + numero + " capacidade: " + capacidade + " andarAtual: " + andarAtual + " # de Pessoas: " + fila.size() + ")";
	}

	//metodo executado pelas threads
	public void run() {
		/*
		try{
			for(int i = 0;i < monitor.getAndares().size(); i++){
				monitor.irDestino(andarAtual.getNumero());
			}
		}catch(Exception e){}
		*/

		monitor.irDestino(this);

		
	}
}

class Monitor{
	private ArrayList<Andar> andares = new ArrayList<Andar>();
	
	public Monitor(ArrayList<Andar> _andares){
		this.andares = _andares;
	}


	public ArrayList<Andar> getAndares(){
		return andares;
	}



	/**
	 *	Calcula o próximo destino baseado na proximidade
	 *	e na quantidade de requisições, qnd o elevador está vazio
	 */
	public Andar proxAndarComRequisicao(Elevador elevador){
		int numeroAndarAtual = elevador.getAndarAtual().getNumero();

		/* testes...
		System.out.println("Elevador " + elevador.getNumero() + " andar atual: " + numeroAndarAtual);

		for (int numero = 0 ; numero < andares.size() ; numero++)
			System.out.println(numero + " " + andares.get(numero).qtdRequisicoesFila());
		*/

	    int min = andares.size();
	    int closest = numeroAndarAtual;

	    for (int i = 0 ; i < andares.size() ; i++) {
	    	if(andares.get(i).qtdRequisicoesFila() != 0) {
		        final int diff = Math.abs(i - numeroAndarAtual);

		        if (diff < min) {
		            min = diff;
		            closest = i;
		        }
		    }
	    }

	    int closestRange = Math.abs(numeroAndarAtual - closest);

		int numeroProxAndar = numeroAndarAtual - closestRange;
		int qtdResquisicoes = andares.get(numeroAndarAtual - closestRange).qtdRequisicoesFila();

	    if(andares.get(numeroAndarAtual + closestRange).qtdRequisicoesFila() > qtdResquisicoes) {
	    	numeroProxAndar = numeroAndarAtual + closestRange;
	    	qtdResquisicoes = andares.get(numeroAndarAtual + closestRange).qtdRequisicoesFila();
	    }
	    
	    return andares.get(numeroProxAndar);
	}

	public void reArranjaFila(Requisicao rq, Elevador elevador){
		System.out.println(rq);
		elevador.setAndarAtual(this.andares.get(rq.getDestino()));
		elevador.removeRequisicao(rq);
	}

	public synchronized void irDestino(Elevador elevador){

		Andar proxAndar = this.proxAndarComRequisicao(elevador);

		System.out.println("Elevador " + elevador.getNumero() + " andar atual: " + elevador.getAndarAtual().getNumero() + " proximo andar: " + proxAndar.getNumero());


		elevador.addRequisicao(new Requisicao(0));
		elevador.addRequisicao(new Requisicao(3));
		elevador.addRequisicao(new Requisicao(0));
		elevador.addRequisicao(new Requisicao(3));
		elevador.addRequisicao(new Requisicao(0));
		elevador.addRequisicao(new Requisicao(3));

		System.out.println(elevador.qtdResquisicoes());
		
		int tamanho = elevador.qtdResquisicoes();

		for(int i = 0; i < tamanho; i++){
			
			reArranjaFila(elevador.proxDestino(),elevador);
		}
		System.out.println(elevador.getFila());
		

	}

}

public class SCE {
	public static void main(String[] args) {
		int qtdAndares = 0;
		int qtdElevadores = 0;
		int capacidade = 0;
		ArrayList<Andar> andares = new ArrayList<Andar>();
		ArrayList<Elevador> elevadores = new ArrayList<Elevador>();
		Monitor monitor = new Monitor(andares);
			
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
							//cria threads
							elevadores.add(new Elevador(capacidade, andares.get(Integer.parseInt(splitted[i])), monitor));

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

		/*
		for (int j = 0 ; j < qtdAndares ; j++){
			System.out.println(andares.get(j));
		}
		*/

		//inicia as threads elevadores
		for (int i = 0; i < qtdElevadores; i++) {
			elevadores.get(i).start();
		}


		//espera pelo termino de todas as threads elevadores
		for (int i = 0; i < qtdElevadores; i++) {
			try {
				elevadores.get(i).join();
			} catch (InterruptedException e) {
		  		System.err.println("Error: " + e.getMessage());
			}
		}

	}
}