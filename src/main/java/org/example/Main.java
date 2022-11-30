package org.example;

import org.example.assign.AssignStrategy;
import org.example.entities.Clause;
import org.example.assign.RandomAssign;
import org.example.entities.GraphNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    static int curDecisionLevel;
//    GRASP()
//    {
//        return ((Search (0, ) != SUCCESS) ? FAILURE : SUCCESS);
//    }

    //    TODO 简化策略 for simplicity : ALL DECISIONs made till now are a Learned Clause

    public static void main(String[] args) throws FileNotFoundException {

        //init
        Set<Integer> allLiterals = new HashSet<>();
        List<Clause> clauseList = new ArrayList<>();

        //每个列表第一个为decision node
        //不需要区分正负
        List<List<GraphNode>> level2GraphNode = new ArrayList<>();

        List<List<Clause>> literal2Clause;


        boolean[] isAssignPos;
        boolean[] isAssignNeg;
        int bound = 0;

        //read data from file
        Scanner scan = new Scanner(new File(""));
        Clause c = new Clause();
        while (scan.hasNextInt()) {


            int a = scan.nextInt();
            if (a == 0) {
                //set other attributes in clause
                c.counter = c.literals.size();

                //set watch var
                c.watch1 = c.literals.get(0);
                c.watch2 = c.literals.get(1);
                clauseList.add(c);
                c = new Clause();
                continue;
            }
            //init literal and all literal dataStructure

            int positiveA = Utils.getPositive(a);
            if (positiveA > bound) bound = positiveA;

            if (!allLiterals.contains(positiveA)) {
                allLiterals.add(positiveA);
            }
            c.literals.add(a);

        }
        isAssignPos = new boolean[bound + 1];
        isAssignNeg = new boolean[bound + 1];

        //根据clause list建立倒排索引 var->clause
        literal2Clause = new ArrayList<>();
        //哨兵
        literal2Clause.add(0,null);
        for(int i=0;i<bound;i++){
            literal2Clause.add(new ArrayList<>());
        }
        for (Clause clause : clauseList) {
            for (int lit : clause.literals) {
                int positiveLit = Utils.getPositive(lit);

//                if (literal2Clause.getOrDefault(positiveLit, null) == null) {
//                    literal2Clause.put(positiveLit, new ArrayList<>());
//                }
                //存的是地址，跟下标也没啥区别
                literal2Clause.get(positiveLit).add(clause);
            }
        }

        //
//        AssignStrategy assignStrategy=new RandomAssign();
//        assignStrategy.assgin();

        for (int i = 1; i < isAssignPos.length; i++) {
            boolean b = isAssignPos[i];
            if (!b) {
                //choose this one
                isAssignPos[i] = true;
                isAssignNeg[i] = true;
                //待定
                boolean assignedValue = true;

                //Construct Graph
                GraphNode graphNode = new GraphNode(null, curDecisionLevel, assignedValue, i);
                List<GraphNode> levelList = new ArrayList<>();

                level2GraphNode.add(levelList);
                levelList.add(graphNode);

                //find all affected clauses
                List<Clause> affectedClause = literal2Clause.get(i);

                for (Clause clause : affectedClause) {
                    if (clause.getCounter() == 2) {
                        //unit clause
                        //propagate
                        int watch1pos = Utils.getPositive(clause.watch1);
                        int watch2pos = Utils.getPositive(clause.watch2);
                        if(i==watch1pos){
                            //推出watch2
                            //add to graph

                            //这里图节点给出直接前驱就可以
//                            new GraphNode()
                            level2GraphNode.get(curDecisionLevel).add()

                        }

                        //
                    } else {

                    }

                }


            }
        }


    }


//    Search (d, & )
//    {
//        if (Decide (d) == SUCCESS)
//            return SUCCESS;
//        while (TRUE) {
//            if (Deduce (d) != CONFLICT) {
//                if (Search (d + 1, ) == SUCCESS) return SUCCESS;
//                else if ( != d) { Erase(); return CONFLICT;}
//            }
//            if (Diagnose (d, ) == CONFLICT) {Erase(); return CONFLICT;}
//            Erase();
//        }
//    }

    /**
     * @return
     */
    public static boolean search(int backtrackingLevel) {

    }

    //    Deduce (d)
//    {
//        while (unit clauses in or clauses unsatisfied) {
//        if (exists unsatisfied clause ) {
//            add conflict vertex to I;
//            record ;
//            return CONFLICT;
//        }
//        if (exists unit clause with free literal or ) {
//            record ;
//            δ(x) = d;
//            set if or if ;
//        }
//    }
//        return SUCCESS;
//    }
    public static boolean deduce() {

    }


}