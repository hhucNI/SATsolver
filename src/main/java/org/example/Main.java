package org.example;

import org.example.assign.AssignStrategy;
import org.example.entities.Clause;
import org.example.assign.RandomAssign;

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
        Set<Integer> allLiterals=new HashSet<>();
        List<Clause> clauseList=new ArrayList<>();
        List<List<>>


        //read data from file
        Scanner scan=new Scanner(new File(""));
        Clause c = new Clause();
        while(scan.hasNextInt()){


            int a = scan.nextInt();
            if(a==0){
                //set other attributes in clause
                c.counter=c.literals.size();

                //set watch var
                c.watch1=c.literals.get(0);
                c.watch2=c.literals.get(1);
                clauseList.add(c);
                c=new Clause();
                continue;
            }
            //init literal and all literal dataStructure
            Integer positiveA=a>0?a:-a;
            if(!allLiterals.contains(positiveA)){
                allLiterals.add(positiveA);
            }
            c.literals.add(a);

        }
        //根据clause list建立倒排索引 var->clause

        Map<Integer, List<Clause>> literal2Clause=new HashMap<>();
        for (Clause clause : clauseList) {
            for (int lit : clause.literals) {
                int positiveLit=lit>0?lit:-lit;
                if(literal2Clause.getOrDefault(positiveLit,null)==null){
                    literal2Clause.put(positiveLit,new ArrayList<>());
                }
                //存的是地址，跟下标也没啥区别
                literal2Clause.get(positiveLit).add(clause);
            }
        }

        //
        AssignStrategy assignStrategy=new RandomAssign();
        assignStrategy.assgin();






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
     *
     * @return
     */
    public static boolean search(int backtrackingLevel){

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
    public static boolean deduce(){

    }


}