package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;

public final class SetGenerator {
    
    public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {
        
    	if (g == null) throw new NullPointerException("g nao pode ser nula.");
        
    	Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g); 
    	
    	/*
    	 * Se X é terminal FIRST(X) = {X} 
         * Se X->ε, ε pertence a FIRST(X)
         * Se X-> Y1Y2...Yk, FIRST(Y1Y2...Yk) esta contido em FIRST(X)
         * FIRST(Y1Y2...Yk) eh
         * FIRST(Y1) caso ε nao pertenca a FIRST(Y1)
         * FIRST(Y1) - {ε} U FIRST(Y2...Yk) caso ε pertenca a FIRST(Y1)
         * Se ε pertence a FIRST(Yj) para todo j de 1 a k, ε pertence a FIRST(Y1Y2...Yk)
    	 */
    	for(Nonterminal key: first.keySet()){
    		Set<GeneralSymbol> valor = first.get(key); //retorna o que tem no first de tal nao terminal
    		valor = firstOf(g, key, valor, first);
    		
    	}
        return first;
    	
    }
    
    public static Set<GeneralSymbol> firstOf(Grammar g, Symbol key, Set<GeneralSymbol> valor, Map<Nonterminal, Set<GeneralSymbol>> first){
    	if(key.isTerminal()){
    		valor.add(key); //Se X é terminal FIRST(X) = {X} 
    	} else {
    		for (Production p: g.getProductions()) { //vejo producao por producao
    			if(p.getNonterminal().equals(key) ){ //analiso somente as producoes do nao terminal da vez
    				Iterator<GeneralSymbol> it = p.iterator();
    				while(it.hasNext()){ //lista de simbolos de uma producao do nao terminal q quero
    					GeneralSymbol s = it.next();
    					if(s instanceof Terminal){  
    						valor.add(s);
    						break;
    					} else if(s.equals(SpecialSymbol.EPSILON)){ //Se X->ε, ε pertence a FIRST(X)
    						valor.add(SpecialSymbol.EPSILON);
    						break;
    					} else { //Se X-> Y1Y2...Yk, FIRST(Y1Y2...Yk) esta contido em FIRST(X)
    						Set<GeneralSymbol> valorS = first.get(s);
    						valorS = firstOf(g, (Nonterminal) s, valorS, first);
    						if(!valorS.contains(SpecialSymbol.EPSILON)){ //FIRST(Y1) caso ε nao pertenca a FIRST(Y1)
    							valor.addAll(valorS);
								break;
							} else {
								if(it.hasNext()){ //FIRST(Y1) - {ε} U FIRST(Y2...Yk) caso ε pertenca a FIRST(Y1)
									valor.addAll(valorS);
									valor.remove(SpecialSymbol.EPSILON);
								} else { //Se ε pertence a FIRST(Yj) para todo j de 1 a k, ε pertence a FIRST(Y1Y2...Yk)
									valor.addAll(valorS);
								}
							}						
						}
					}
				}
	        }
    	}
        return valor;
    }

    
    public static Map<Nonterminal, Set<GeneralSymbol>> getFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first) {
        
        if (g == null || first == null)
            throw new NullPointerException();
                
        Map<Nonterminal, Set<GeneralSymbol>> follow = initializeNonterminalMapping(g);
        
        /*
         * $ ∈ FOLLOW(S), onde S é o símbolo inicial e $ é fim da entrada 
         * Se existe uma produção A → αBβ, tudo que pertence a FIRST(β) exceto ε está em FOLLOW(B) 
         * Se existe uma produção A → αB, então tudo que estiver em FOLLOW(A) estará em FOLLOW(B) 
         * Se existe uma produção A → αBβ, e ε ∈ FIRST(β), tudo que estiver em FOLLOW(A) estará em FOLLOW(B) 
         */
        follow.get(g.getStartSymbol()).add(SpecialSymbol.EOF); //$ ∈ FOLLOW(S), onde S é o símbolo inicial e $ é fim da entrada 
        /*
         * Para cada producao: criar um set auxiliar
         * Adicionar a esse set o que tem no follow do nt da producao
         * Ler a producao da direita p esquerda
         * Para cada simbolo lido: 
         * 		se for nao terminal adiciono o que tem no trailer
         * 			se no first desse nao terminal tiver epsilon adiciono o que tem no first dele no trailer e excluo epsilon
         * 			se nao tiver apago o que tem no trailer e adiciono o first dele
         * 		se nao (sera terminal ou epsilon) apago o que tem no trailer e adiciono no trailer ele mesmo
         */
        boolean mudou = true;
        while(mudou){
        	mudou = false;
        	for(Production p: g.getProductions()){ //analisar producao por producao
        		Set<GeneralSymbol> aux = new HashSet<GeneralSymbol>();

        		for(int i = p.getProduction().size() - 1; i >= 0; i--){ //ler os simbolos da producao da esquerda para a direita
        			GeneralSymbol s = p.getProduction().get(i);
        			if(i == p.getProduction().size() -1){
            			aux.addAll(follow.get(p.getNonterminal()));
        			}
        			if(s instanceof Nonterminal){
        				
        				if(aux != null) {
            				mudou = follow.get(s).addAll(aux);
        				}
        				Set<GeneralSymbol> fst = first.get(s);
        				if(fst.contains(SpecialSymbol.EPSILON)){
        					aux.addAll(fst);
        					aux.remove(SpecialSymbol.EPSILON);
        				} else {
            				aux = new HashSet<GeneralSymbol>();
            				aux.addAll(fst);
        				}
        			} else {
        				aux = new HashSet<GeneralSymbol>();
        				aux.add(s);
        			}
        			
        		}
        	}
        	
        }
        
        return follow;
    }
   
    
    //método para inicializar mapeamento nãoterminais -> conjunto de símbolos
    private static Map<Nonterminal, Set<GeneralSymbol>> initializeNonterminalMapping(Grammar g) {
    Map<Nonterminal, Set<GeneralSymbol>> result = new HashMap<Nonterminal, Set<GeneralSymbol>>();

    for (Nonterminal nt: g.getNonterminals())
        result.put(nt, new HashSet<GeneralSymbol>());

    return result;
}

}