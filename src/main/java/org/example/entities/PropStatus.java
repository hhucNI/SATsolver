package org.example.entities;


public class PropStatus {
    public int conflictVar;

    public boolean isConflict;

    public PropStatus(int conflictVar) {
        this.conflictVar = conflictVar;
        this.isConflict=true;
    }

    public PropStatus(int conflictVar, boolean isConflict) {
        this.conflictVar = conflictVar;
        this.isConflict = isConflict;
    }

    public PropStatus() {
        conflictVar=0;
        isConflict=false;
    }
}
