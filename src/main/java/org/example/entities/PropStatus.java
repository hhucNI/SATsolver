package org.example.entities;


public class PropStatus {

    /**
     * conflictVar 暂且认为是正
     */
    public int conflictVar;

    public boolean isConflict;
    public boolean isAllDone = false;

    public PropStatus(int conflictVar) {
        this.conflictVar = conflictVar;
        this.isConflict = true;
    }

    public GraphNode conflictNode;

    public PropStatus(GraphNode conflictNode) {
        this.conflictVar = conflictNode.var;
        this.isConflict = true;
        this.isAllDone = false;
        this.conflictNode = conflictNode;
    }

    public PropStatus(int conflictVar, boolean isConflict, boolean isAllDone) {
        this.conflictVar = conflictVar;
        this.isConflict = isConflict;
        this.isAllDone = isAllDone;
    }

    public PropStatus(int conflictVar, boolean isConflict) {
        this.conflictVar = conflictVar;
        this.isConflict = isConflict;
    }

    public PropStatus() {
        conflictVar = 0;
        isConflict = false;
    }
}
