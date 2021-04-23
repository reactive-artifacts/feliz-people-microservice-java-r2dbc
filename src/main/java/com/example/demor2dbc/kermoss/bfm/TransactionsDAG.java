package com.example.demor2dbc.kermoss.bfm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jgrapht.DirectedGraph;
import org.jgrapht.experimental.dag.DirectedAcyclicGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.EdgeReversedGraph;
import org.jgrapht.traverse.DepthFirstIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransactionsDAG {

	@Autowired(required = false)
	private List<GlobalTransactionWorker> gtxWorkers;
	@Autowired(required = false)
	private List<LocalTransactionWorker> ltxWorkers;		
	private DirectedGraph<String, DefaultEdge> dag;
	private DirectedGraph<String, DefaultEdge> reversedDag;
	
	@PostConstruct
    public void init() {
		dag = new DirectedAcyclicGraph<>(DefaultEdge.class);
		gtxWorkers.forEach(gw->dag.addVertex(gw.getMeta().getTransactionName()));
		ltxWorkers.forEach(lw->{
			dag.addVertex(lw.getMeta().getTransactionName());
			dag.addEdge(lw.getMeta().getChildOf(), lw.getMeta().getTransactionName());
		});
		
		reversedDag = new EdgeReversedGraph(dag);
    }
	
	public List<String> predecessors(String transactionName){
		DepthFirstIterator<String, DefaultEdge> iterator 
		  = new DepthFirstIterator<>(reversedDag,transactionName);
		List<String> p =new ArrayList<>();
		while (iterator.hasNext()) {
            p.add(iterator.next());
        }
		
		return p;
	}
	public List<String> successors(String transactionName){
		
		
		DepthFirstIterator<String, DefaultEdge> iterator 
		= new DepthFirstIterator<>(dag,transactionName);
		List<String> p =new ArrayList<>();
		while (iterator.hasNext()) {
			p.add(iterator.next());
		}
		
		return p;
	}

}
