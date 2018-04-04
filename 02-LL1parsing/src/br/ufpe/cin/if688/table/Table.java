package br.ufpe.cin.if688.table;


import br.ufpe.cin.if688.parsing.analysis.*;
import br.ufpe.cin.if688.parsing.grammar.*;
import java.util.*;


public final class Table {
	private Table() {    }

	public static Map<LL1Key, List<GeneralSymbol>> createTable(Grammar g) throws NotLL1Exception {
        if (g == null) throw new NullPointerException();

        Map<Nonterminal, Set<GeneralSymbol>> first = SetGenerator.getFirst(g);
        Map<Nonterminal, Set<GeneralSymbol>> follow = SetGenerator.getFollow(g, first);

        Map<LL1Key, List<GeneralSymbol>> parsingTable = new HashMap<LL1Key, List<GeneralSymbol>>();

        /*
         * Para cada produção A → α de G
         * Para todo a ∈ FIRST(α), adicione A → α em M[A,a]
         * Se ε ∈ FIRST(α), então, para todo b ∈ FOLLOW(A), adicione A → α em M[A,b]
         * a regra acima leva em conta também o símbolo $ 
         */
       
        for(Production p: g.getProductions()){
			GeneralSymbol s = p.getProduction().get(0);
			Nonterminal nt = p.getNonterminal();
        	if(s instanceof Nonterminal){
        		for(GeneralSymbol a: first.get(s)){
        			if(a.equals(SpecialSymbol.EPSILON)){
        				for(GeneralSymbol b: follow.get(s)){
        					LL1Key key = searchTable(parsingTable, nt, b);
        					if(key == null){
        		        		parsingTable.put(new LL1Key(nt, b), new ArrayList<GeneralSymbol>());
        		        		key = searchTable(parsingTable, nt, b);
        					}
                			parsingTable.get(key).addAll(p.getProduction()); //Se ε ∈ FIRST(α), então, para todo b ∈ FOLLOW(A), adicione A → α em M[A,b]
        				}
        			} else {
    					LL1Key key = searchTable(parsingTable, nt, a);
    					if(key == null){
    		        		parsingTable.put(new LL1Key(nt, a), new ArrayList<GeneralSymbol>());
    		        		key = searchTable(parsingTable, nt, a);
    					}
        				parsingTable.get(key).addAll(p.getProduction()); //Para todo a ∈ FIRST(α), adicione A → α em M[A,a]
        			}

        		}
        	} else if(s instanceof Terminal){
				LL1Key key = searchTable(parsingTable, nt, s);
        		if(key == null){
	        		parsingTable.put(new LL1Key(nt, s), new ArrayList<GeneralSymbol>());
	        		key = searchTable(parsingTable, nt, s);
        		}
        		parsingTable.get(key).addAll(p.getProduction()); //Para todo a ∈ FIRST(α), adicione A → α em M[A,a]
        	} else { //epsilon
        		for(GeneralSymbol b: follow.get(nt)){
					LL1Key key = searchTable(parsingTable, nt, b);
					if(key == null){
		        		parsingTable.put(new LL1Key(nt, b), new ArrayList<GeneralSymbol>());
		        		key = searchTable(parsingTable, nt, b);
					}
        			parsingTable.get(key).addAll(p.getProduction()); //Se ε ∈ FIRST(α), então, para todo b ∈ FOLLOW(A), adicione A → α em M[A,b]
				}
        	}
        }
        
        return parsingTable;
    }
	public static LL1Key searchTable(Map<LL1Key, List<GeneralSymbol>> table, Nonterminal nonterminal, GeneralSymbol symbol) {
		LL1Key key = new LL1Key(nonterminal, symbol);
		
		for(LL1Key tableKey : table.keySet()) {
			if(key.equals(tableKey)) {
				return tableKey;
			}
		}
		
		return null;
	}

}
