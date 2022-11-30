package org.example.entities;

import lombok.Data;

import java.util.List;


@Data
public class GraphNode {
    List<GraphNode> pre;
    int decisionLevel;
    boolean assignedValue;
    int var;

    public GraphNode(List<GraphNode> pre, int decisionLevel, boolean assignedValue, int var) {
        this.pre = pre;
        this.decisionLevel = decisionLevel;
        this.assignedValue = assignedValue;
        this.var = var;

    }


}
