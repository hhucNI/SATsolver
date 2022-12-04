package org.example.entities;

import lombok.Data;

import java.util.List;
import java.util.Objects;


@Data
public class GraphNode {
    public List<GraphNode> pre;
    public int decisionLevel;
    public boolean assignedValue = false;
    public int var;
//    int varAndValue;

    //true : decision var  false : implication
    boolean nodeType = true;

    public GraphNode(List<GraphNode> pre, int decisionLevel, boolean assignedValue, int var) {
        this.pre = pre;
        this.decisionLevel = decisionLevel;

        this.assignedValue = assignedValue;
        this.var = var;


//        this.varAndValue = assignedValue?
    }

    public GraphNode(List<GraphNode> pre, int decisionLevel, boolean assignedValue, int var, boolean nodeType) {
        this.pre = pre;
        this.decisionLevel = decisionLevel;

        this.assignedValue = assignedValue;
        this.var = var;
        this.nodeType = nodeType;


//        this.varAndValue = assignedValue?
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return var == graphNode.var || var==-graphNode.var;
    }

    @Override
    public int hashCode() {
        return Objects.hash(var);
    }
}
