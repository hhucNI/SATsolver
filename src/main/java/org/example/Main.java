package org.example;

import org.example.assign.AssignStrategy;
import org.example.entities.Clause;
import org.example.assign.RandomAssign;
import org.example.entities.GraphNode;
import org.example.entities.PropStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static final List<List<GraphNode>> level2GraphNode = new ArrayList<>();
    public static final List<GraphNode> lit2GraphNode = new ArrayList<>();
    public static final Set<Integer> allLiterals = new HashSet<>();
    public static final List<Clause> clauseList = new ArrayList<>();
    public static int curDecisionLevel;
    public static List<List<Clause>> literal2Clause;
    public static boolean[] isAssignPos;
    public static boolean[] isAssignNeg;
    //    GRASP()
//    {
//        return ((Search (0, ) != SUCCESS) ? FAILURE : SUCCESS);
//    }

    //    TODO 简化策略 for simplicity : ALL DECISIONs made till now are a Learned Clause

    public static void main(String[] args) throws FileNotFoundException {

        //init

        //每个列表第一个为decision node
        //不需要区分正负


//        List<List<Clause>> literal2Clause;


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
        literal2Clause.add(0, null);
        for (int i = 0; i < bound; i++) {
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

        // ------------------------------------------------------------------------------
        // -
        // -
        //-----------------------------------Assign----------------------------------
        // -
        // -
        // ------------------------------------------------------------------------------
        PropStatus status = assignAndPropagate();
        if(status.isConflict){
            //处理冲突
            Set<GraphNode> rootReason = findRootReason(status.conflictVar);
            //1.flip之后 被flip的变量就是implication了，那么若再冲突会回溯到更低层
            int highestLevel=-1;
            int secondHigh=-2;
            GraphNode highestNode=null;
            GraphNode secondHighestNode=null;
            for (GraphNode graphNode : rootReason) {
                if(graphNode.decisionLevel>highestLevel) {
                    highestLevel=graphNode.decisionLevel;
                    highestNode=graphNode;
                    secondHigh=highestLevel;
                    secondHighestNode=highestNode;
                }
                if(graphNode.decisionLevel>secondHigh){
                    secondHigh=graphNode.decisionLevel;
                    secondHighestNode=graphNode;
                }
            }

            //add clause and flip
            // 新clause的推导就跟着倒数第二个
            List<Integer> clauseLit=new ArrayList<>();
            for (GraphNode graphNode : rootReason) {
                //交集取非
                clauseLit.add(-graphNode.var);
            }
            Clause clause = new Clause();
            clause.literals=clauseLit;

            // update all relevant data structure
            clauseList.add(clause);

            for (Integer var : clauseLit) {
                literal2Clause.get(Utils.getPositive(var)).add(clause);
            }

            //Todo 给新clause的剩余变量附上值
        }
    }

    public static Set<GraphNode> findRootReason(int conflictVar){
        GraphNode graphNode = lit2GraphNode.get(conflictVar);
        Set<GraphNode> container=new HashSet<>();
        DFS(container,graphNode);
        return container;
    }

    private static void DFS(Set<GraphNode> container,GraphNode root){
        if(root==null) return;
        List<GraphNode> rootpre = root.getPre();
        if(rootpre==null || rootpre.size()==0) {
            //root
            container.add(root);
            return;
        }
        for (GraphNode pre : rootpre) {
            DFS(container,pre);
        }
    }


    private static PropStatus assignAndPropagate() {
        //todo 直接就算冲突也推完这一层，这样方便backtracking
        //todo 再写一个函数check conflict
        int candidateDecisionVar = 0;
        for (int i = 1; i < isAssignPos.length; i++) {
            boolean b = isAssignPos[i];
            //todo 这里要把循环去掉，只是选择一个var
            if (!b) {
                candidateDecisionVar = i;
            }
        }
        //choose this one
        isAssignPos[candidateDecisionVar] = true;
        isAssignNeg[candidateDecisionVar] = true;

        //待定
        //insight 未满足的clause一定剩至少两个var，如果其中一个watch lit被assign
        //             另一个立即被推出，故不存在clause仅有一个unassigned var 的情况
        boolean assignedValue = true;

        //Construct Graph
        GraphNode graphNode = new GraphNode(null, curDecisionLevel, assignedValue, candidateDecisionVar);
        lit2GraphNode.add(candidateDecisionVar, graphNode);

        List<GraphNode> levelList = new ArrayList<>();

        level2GraphNode.add(levelList);
        levelList.add(graphNode);

        //find all affected clauses
        List<Clause> affectedClause = literal2Clause.get(candidateDecisionVar);

        for (Clause clause : affectedClause) {
            //todo 把counter砍掉，不然backjump还需要访问counter
            //todo 直接维护一个赋值的data structure ，clause内仅在watchlist被访问时操作

            //insight 未满足的clause一定剩至少两个var，如果其中一个watch lit被assign
            //             另一个立即被推出，故不存在clause仅有一个unassigned var 的情况

            //unit clause
            //propagate
            int watch1pos = Utils.getPositive(clause.watch1);
            int watch2pos = Utils.getPositive(clause.watch2);
            if (candidateDecisionVar == watch1pos) {
                //推出watch2
                //add to graph

                //这里图节点给出直接前驱就可以,也就是clause的其他literal
                //由于unit prop，clause中的其他literal一定已经赋值

                //还要维护literal->graph node的map，但注意，每次清除后，不需要
                //处理这个map，不需要进行remove，后来的进行覆盖即可？


                isAssignPos[watch2pos] = true;
                isAssignNeg[watch2pos] = true;

                //check 2 condition of watch list
                boolean unitFlag = true;
                for (int lit : clause.literals) {
                    int literal = lit > 0 ? lit : -lit;
                    if (literal == watch2pos) continue;

                    if (isAssignPos[literal] || isAssignNeg[literal]) {
                        //存在非watch list中的未赋值元素
                        //替换watch1 到watch list
                        clause.watch1 = lit;
                        unitFlag = false;
                    }
                }

                //check done ，no other unassigned var
                //unit prop
                //考虑到每次var赋值都会把所有涉及的clause处理一遍，所以conflict
                //应该只能是本层

                if (unitFlag) {
                    //保证该clause为true，再看有没有conflict
                    boolean unitAssignedValue = clause.watch2 > 0;

                    //构造前驱pre graphNode
                    //也就是所有var都已经赋值
                    //那怎么索引这些var的graphNode呢，那就再搞个数据结构
                    //反正back jump 用论文的方法的话 o(n)跑不了


                    GraphNode newNode = generateGraphNode(clause, unitAssignedValue);

                    //check if conflict
                    for (GraphNode node : levelList) {
                        if (node.equals(newNode)) {
                            if (node.assignedValue != newNode.assignedValue) {
                                //Conflict
                                // 2 condition     1.flip or 2.back jump AND flip
                                //其实本质上一样，1.flip就相当于back jump到本层

                                //出现冲突其他东西就不重要了，直接return
                                //
                                return new PropStatus(watch1pos);

                            }
                        }

                    }

                    levelList.add(newNode);
                    lit2GraphNode.set(watch2pos, newNode);

                }

            } else if (candidateDecisionVar == watch2pos) {

            }
            level2GraphNode.set(curDecisionLevel, levelList);

            //


        }
    }

    public static GraphNode generateGraphNode(Clause clause, boolean unitAssignedValue) {
        List<GraphNode> preNodes = new ArrayList<>();
        for (Integer lit : clause.literals) {
            int plit = Utils.getPositive(lit);
            //前驱加入
            preNodes.add(lit2GraphNode.get(plit));
        }
        // maintain data structure
        GraphNode newNode = new GraphNode(preNodes, curDecisionLevel, unitAssignedValue, clause.watch2);
        return newNode;
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