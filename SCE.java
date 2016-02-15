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

	public void addRequisicao(Requisicao requisicao) {
		this.fila.add(requisicao);
	}

	public void removeRequisicao(Requisicao requisicao) {
		this.fila.remove(requisicao);
	}

	public int qtdRequisicoes() {
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
	public PrintWriter writer;



	public Elevador(int capacidade, Andar andarAtual, Monitor monitor) {
		this.numero = id_gen++;
		this.capacidade = capacidade;
		this.andarAtual = andarAtual;
		this.monitor = monitor;
		this.fila = new ArrayList<Requisicao>();
		try{
			this.writer = new PrintWriter("thread"+this.numero+".txt", "UTF-8");
		}catch(Exception e){}
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

	public int qtdRequisicoes() {
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

	    for (int i = 0 ; i < this.fila.size() ; i++) {
	        final int diff = Math.abs(fila.get(i).getDestino() - andarAtual.getNumero());

	        if (diff < min) {
	            min = diff;
	            closest = i;
	        }
	    }
	    return fila.get(closest);
	}

	public  void executarRequisicoes(){
		int tamanho = this.qtdRequisicoes();
		Requisicao rq;
		
		for(int i = 0; i < tamanho; i++){
			rq = this.proxDestino();
			writer.println("Elevador "+this.numero+" executando " +rq+", restando "+ (this.qtdRequisicoes()-1) +" requisicoes");
			this.setAndarAtual(this.monitor.getAndares().get(rq.getDestino()));
			this.removeRequisicao(rq);
		}
	}

	public String toString() {
	    return "(Elevador ID: " + numero + " capacidade: " + capacidade + " andarAtual: " + andarAtual + " # de Pessoas: " + fila.size() + ")";
	}

	//metodo executado pelas threads
	public void run() {
		while(this.monitor.getFlag() || this.monitor.haRequisicoes() || this.qtdRequisicoes() > 0) {
			if(this.fila.size() == 0 && this.monitor.haRequisicoes()) {
				this.monitor.buscarRequisicoes(this);
			}

			if(this.qtdRequisicoes() > 0) {
				writer.println("Elevador " + this.numero + " executa requisicoes");
				this.executarRequisicoes();
			}
		}
		writer.close();
	}
}

class Monitor{
	private ArrayList<Andar> andares = new ArrayList<Andar>();
	private boolean flag = true;
	
	public Monitor(ArrayList<Andar> _andares){
		this.andares = _andares;
	}

	public ArrayList<Andar> getAndares(){
		return andares;
	}

	public void setFlag(boolean _flag){
		this.flag = _flag;
	}

	public boolean getFlag(){
		return flag;
	}	

	public boolean haRequisicoes() {
		for(Andar andar : this.andares)
			if(andar.qtdRequisicoes() > 0)
				return true;
		return false;
	}

	/**
	 *	Calcula o próximo destino baseado na proximidade
	 *	e na quantidade de requisições, qnd o elevador está vazio
	 */
	public Andar proxAndarComRequisicao(Elevador elevador){
		
		int numeroAndarAtual = elevador.getAndarAtual().getNumero();
	    int min = andares.size();
	    int closest = numeroAndarAtual;

	    for (int i = 0 ; i < andares.size() ; i++) {
	    	if(andares.get(i).qtdRequisicoes() != 0) {
		        final int diff = Math.abs(i - numeroAndarAtual);

		        if (diff < min) {
		            min = diff;
		            closest = i;
		        }
		    }
	    }

	    int closestRange = Math.abs(numeroAndarAtual - closest);
		int numeroProxAndar = 0;
		int qtdRequisicoes = 0;

		if(numeroAndarAtual - closestRange > 0 && andares.get(numeroAndarAtual - closestRange).qtdRequisicoes() > qtdRequisicoes) {
	    	numeroProxAndar = numeroAndarAtual - closestRange;
	    	qtdRequisicoes = andares.get(numeroAndarAtual - closestRange).qtdRequisicoes();
		}

	    if(numeroAndarAtual + closestRange < andares.size() && andares.get(numeroAndarAtual + closestRange).qtdRequisicoes() > qtdRequisicoes) {
	    	numeroProxAndar = numeroAndarAtual + closestRange;
	    	qtdRequisicoes = andares.get(numeroAndarAtual + closestRange).qtdRequisicoes();
	    }
	    return andares.get(numeroProxAndar);
	}

	public synchronized void buscarRequisicoes(Elevador elevador) {
		Andar proxAndar = this.proxAndarComRequisicao(elevador);
		elevador.writer.println("Elevador " + elevador.getNumero() + " busca requisicoes no andar " + proxAndar.getNumero());

		int tamanho = proxAndar.qtdRequisicoes();

		for(int i = 0 ; i < tamanho && i < elevador.getCapacidade() ; i++) {
			Requisicao rq = proxAndar.getFila().get(0);
			elevador.addRequisicao(rq);
			proxAndar.removeRequisicao(rq);
		}

		elevador.setAndarAtual(proxAndar);
		elevador.writer.println("Elevador " + elevador.getNumero() + " buscou requisicoes no andar " + proxAndar.getNumero() + " com fila no elevador = " + elevador.getFila());
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
			PrintWriter writer = new PrintWriter("main.txt", "UTF-8");	
			try{
				// abre arquivo
				FileInputStream fstream = new FileInputStream("file.txt");
				// pega objeto de DataInputStream
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String strLine;
				//le arquivo por linha

				for(int linha = 0 ; (strLine = br.readLine()) != null ; linha++) {
					// quebra a linha por espaço
					splitted = strLine.split(" ");

					switch(linha) {
						case 0:
							qtdAndares = Integer.parseInt(splitted[0]);
							qtdElevadores = Integer.parseInt(splitted[1]);
							capacidade = Integer.parseInt(splitted[2]);
							writer.println("# de Andares:"+qtdAndares+"\n# de Elevadores: "+qtdElevadores+"\nCapacidade dos Elevadores: "+capacidade);

							for (int i = 0 ; i < qtdAndares ; i++)
								andares.add(new Andar(i));
							
							break;

						case 1:
							for (int i = 0 ; i < qtdElevadores ; i++){
								//cria threads
								writer.println("Criando thread do elevador " + i);
								Elevador el = new Elevador(capacidade, andares.get(Integer.parseInt(splitted[i])), monitor);
								elevadores.add(el);
								el.start();
							}
							break;

						default:

							int qtd_pessoas = Integer.parseInt(splitted[0]);

							for(int i = 0 ; i < qtd_pessoas ; i++){
								Requisicao rq = new Requisicao(Integer.parseInt(splitted[i+1]));
								writer.println("Criando requisicao para o andar "+(linha-2)+" = "+rq);
								andares.get(linha-2).addRequisicao(rq);
							}
									
					}
				}
				
				monitor.setFlag(false);
				//fecha input stream
				in.close();
		    } catch(Exception e) {
			  	System.err.println("Error: " + e.getMessage());
			}

			//espera pelo termino de todas as threads elevadores
			for (int i = 0; i < qtdElevadores; i++) {
				try {
					elevadores.get(i).join();
				} catch (InterruptedException e) {
			  		System.err.println("Error: " + e.getMessage());
				}
			}
			writer.println("terminando execucao!");
			writer.close();
		}catch(Exception e){}
	}
}