package org.example.entities;

import lombok.Data;

import java.util.List;
import java.util.Objects;


@Data
public class GraphNode {
    public List<GraphNode> pre;
    public int decisionLevel;

    /**
     *  正负均可，assignedValue后面去掉
     */
    public int var;
//    int varAndValue;

    /**
     *     0 : decision var  1 : implication 2: conflict node
     */
    int nodeType;



    public GraphNode(List<GraphNode> pre, int decisionLevel, int var, int nodeType) {
        this.pre = pre;
        this.decisionLevel = decisionLevel;

        this.var = var;
        this.nodeType = nodeType;


//        this.varAndValue = assignedValue?
    }

    public GraphNode(List<GraphNode> pre, int decisionLevel, int nodeType) {
        this.pre = pre;
        this.decisionLevel=decisionLevel;
        this.nodeType = nodeType;
    }

    /**
     *  var绝对值一样即相等
      * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GraphNode graphNode = (GraphNode) o;
        return var == graphNode.var || var==-graphNode.var;
    }

    @Override
    public int hashCode() {
        int v=var>0?var:-var;
        return Objects.hash(v);
    }
}
