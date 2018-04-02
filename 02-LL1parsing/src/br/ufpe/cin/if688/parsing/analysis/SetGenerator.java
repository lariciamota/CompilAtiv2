package br.ufpe.cin.if688.parsing.analysis;

import java.util.*;

import br.ufpe.cin.if688.parsing.grammar.*;

public final class SetGenerator {
    
    public static Map<Nonterminal, Set<GeneralSymbol>> getFirst(Grammar g) {
        
    	if (g == null) throw new NullPointerException("g nao pode ser nula.");
        
    	Map<Nonterminal, Set<GeneralSymbol>> first = initializeNonterminalMapping(g); 
    	//aq tem um mapeamento de tds nao terminais com uma lista q vira a ser os firsts de cada nao terminal
    	
    	/*
    	 * Implemente aqui o método para retornar o conjunto first
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
    
    public static Set<GeneralSymbol> firstOf(Grammar g, Nonterminal key, Set<GeneralSymbol> valor, Map<Nonterminal, Set<GeneralSymbol>> first){
    	for (Production p: g.getProductions()) { //vejo producao por producao
			if(p.getNonterminal().equals(key) ){ //analiso somente as producoes do nao terminal da vez
				Iterator<GeneralSymbol> it = p.iterator();
				while(it.hasNext()){ //lista de simbolos de uma producao do nao terminal q quero
					GeneralSymbol s = it.next();
					if(s instanceof Terminal){ //Se X é terminal FIRST(X) = {X} 
						valor.add((Terminal) s);
						break;
					} else if(s.equals(SpecialSymbol.EPSILON)){ //Se X->ε, ε pertence a FIRST(X)
						valor.add(SpecialSymbol.EPSILON);
						break;
					} else { //Se X-> Y1Y2...Yk, FIRST(Y1Y2...Yk) esta contido em FIRST(X)
						Set<GeneralSymbol> valorS = first.get((Nonterminal) s);
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
        return valor;
    }

    
    public static Map<Nonterminal, Set<GeneralSymbol>> getFollow(Grammar g, Map<Nonterminal, Set<GeneralSymbol>> first) {
        
        if (g == null || first == null)
            throw new NullPointerException();
                
        Map<Nonterminal, Set<GeneralSymbol>> follow = initializeNonterminalMapping(g);
        
        /*
         * implemente aqui o método para retornar o conjunto follow
         */
        
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
