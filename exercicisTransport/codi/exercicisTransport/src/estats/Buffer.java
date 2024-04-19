/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package estats;

/**
 *
 * @author upcnet
 */
public class Buffer {
  
  private Object[] dades;
	private int[] lect_pend, p_lec, num_lec;
	private int p_esc, num_esc, CP, N;

	public Buffer(int CP, int N) {
		this.CP = CP;
		this.N = N;
		dades = new Object[CP];
		lect_pend = new int[CP];
		p_lec = new int[N];
		num_lec = new int[N];
	}

	public synchronized void put(Object value) {
		while(lect_pend[p_esc]!=0)
			{try{wait();}catch(Exception e){}}
		dades[p_esc]=value;
		lect_pend[p_esc]=N;
		p_esc = (++p_esc) % CP;
		num_esc++;
		notifyAll();
	}

	public synchronized Object get(int id) {
		while(num_lec[id]==num_esc)
			{try{wait();}catch(Exception e){}}
		Object result = dades[p_lec[id]];
		lect_pend[p_lec[id]]--;
		p_lec[id] = (++p_lec[id]) % CP;
		num_lec[id]++;
		notifyAll();
		return result;
	}
}
