package org.example;

import org.example.entities.Clause;
import org.example.entities.GraphNode;
import org.example.entities.PropStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    //todo 可以直接使用level2GraphNode.size()替换curDesicionlevel

//    public static Map<>
//    public static boolean[] isDisappear;

    /**
     * back tracing时 删除倒二到最新的所有赋值，尽管有些和root reason无关
     */
    public static ArrayList<ArrayList<GraphNode>> level2GraphNode;

    /**
     * lit2GraphNode lit正负皆可
     */
    public static Map<Integer, GraphNode> lit2GraphNode = new HashMap<>();
    /**
     *     全为正，仅作查表使用，或许没用
     */
    public static Set<Integer> allLiterals = new HashSet<>();

    public static List<Clause> clauseList = new ArrayList<>();
    public static int curDecisionLevel;

    /**
     * literal2Clause : literal 为正,lit索引clause
     */
    public static List<List<Clause>> literal2Clause;
    public static boolean[] isAssignPos;
    public static boolean[] isAssignNeg;

    public static void main(String[] args) throws FileNotFoundException {
        List<Integer> res=new ArrayList<>();

        initSingleFileDataStructure();
        PropStatus status = assignAndPropagate(0);
        while (true) {
            if (status.unSAT) {
                //结束

                System.out.println("-----------------------------------------------");
                System.out.println("--------                 无解                     ------");
                System.out.println("-----------------------------------------------");
                break;
            }
            else if (status.isConflict) {
                //处理冲突
                //todo 一般不会reason就是自己，但很有可能reason包含自己（decision var错了）
                Set<GraphNode> rootReason = findRootReason(status.conflictNode);
                //1.flip之后 被flip的变量就是implication了，那么若再冲突会回溯到更低层
                int highestLevel = -1;
                int secondHigh = -2;
                GraphNode highestNode = null;
                GraphNode secondHighestNode = null;
                for (GraphNode graphNode : rootReason) {
                    if (graphNode.decisionLevel > highestLevel) {
                        secondHigh = highestLevel;
                        secondHighestNode = highestNode;
                        highestLevel = graphNode.decisionLevel;
                        highestNode = graphNode;

                    } else if (graphNode.decisionLevel > secondHigh) {
                        secondHigh = graphNode.decisionLevel;
                        secondHighestNode = graphNode;
                    }
                }
//            secondHighDecisionLevel=secondHighestNode.
                //add clause and flip
                // 新clause的推导就跟着倒数第二个
                List<Integer> clauseLit = new ArrayList<>();
                for (GraphNode graphNode : rootReason) {
                    //交集取非
                    clauseLit.add(-graphNode.var);
                }
                Clause learnedClause = new Clause();
                learnedClause.literals = clauseLit;
                learnedClause.isGenerated = true;
                if (learnedClause.literals.size() == 1) learnedClause.bornUnit = true;
                // update all relevant data structure
                clauseList.add(learnedClause);

                //Todo 给新clause的剩余变量附上值,这里两个一样有点不好
                learnedClause.watch1 = -highestNode.var;
                if (secondHighestNode != null) {
                    int secVar = secondHighestNode.var;
                    learnedClause.watch2 = -secVar;
                }


                for (Integer var : clauseLit) {
                    literal2Clause.get(Utils.getPositive(var)).add(learnedClause);
                }

                //delete high level assign and propagation

                for (int i = highestLevel; i >= secondHigh && i>=0; i--) {
                    //delete reference and GC
                    List<GraphNode> deletedNode = level2GraphNode.get(i);
                    //unassigned var 数量一定在同一层由 2->0
                    for (GraphNode node : deletedNode) {
                        int var = node.var;
                        lit2GraphNode.remove(var);
                        int pvar = Utils.getPositive(var);

                        if (var > 0) isAssignPos[pvar] = false;
                        else isAssignNeg[pvar] = false;
                        if (node.var != 0) {
                            List<Clause> clauses = literal2Clause.get(Utils.getPositive(node.var));
                            for (Clause cc : clauses) {
//                            if(cc==learnedClause) continue;
                                cc.isSatisfied = false;

                            }
                        }
                    }
                    level2GraphNode.set(i, null);
                }

                curDecisionLevel = secondHigh>0?secondHigh:0;
                //GC
                rootReason = null;

                //todo 新的clause也需要watch list,因为上面的assgin也可以backtracking,通过倒数第二推出倒数第一，所以backtracking可以把倒
                // todo 二也delete了，然后倒二赋上相同的值，重新推,推出倒一的neg

                if (secondHighestNode != null) {
                    //重推
                    int svar = secondHighestNode.var;
                    secondHighestNode = null;
                    highestNode=null;

                    status = assignAndPropagate(svar);
                } else {
                    //flip 赋值

                    secondHighestNode = null;
                    int hvar = highestNode.var;
                    highestNode=null;
                    status = assignAndPropagate(-hvar);
                }


            }
            else if (status.isAllDone) {
                //结束
                System.out.println("------------------------RESULT-------------------------");
                for (int key : lit2GraphNode.keySet()) {
                    int var = lit2GraphNode.get(key).var;
                    res.add(var);
                    System.out.print(var + "=");
                }
                System.out.println("------------------------RESULT-------------------------");
                break;
            }
            else {
                //暂时采用随机赋值
                curDecisionLevel++;
                status = assignAndPropagate(0);
            }
        }


    }

    private static void initSingleFileDataStructure() throws FileNotFoundException {
        //init

        //每个列表第一个为decision node
        //不需要区分正负


//        List<List<Clause>> literal2Clause;


        int bound = 0;
        int givenBound = 0;
        int clauseNum = 0;

        //read data from file
        Scanner scan = new Scanner(new File("D:\\1javawork\\software_analysis_projs\\SATsolver\\src\\main\\resources\\Test\\nby_USAT2.cnf"));
        Clause c = new Clause();
        //完善数据处理，标题部分
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            if (line.startsWith("c")) continue;
            if (line.startsWith("p")) {
                //break
                String[] info = line.split(" ");
                givenBound = Integer.parseInt(info[info.length - 2]);
                clauseNum = Integer.parseInt(info[info.length - 1]);
                break;
            }
            //如果没有title呢

        }
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
        level2GraphNode = new ArrayList<>(bound);
        for (int i = 0; i < bound; i++) {
            level2GraphNode.add(null);
        }
    }

    public static Set<GraphNode> findRootReason(GraphNode conflictNode) {
        Set<GraphNode> container = new HashSet<>();
        DFS(container, conflictNode);
        return container;
    }

    private static void DFS(Set<GraphNode> container, GraphNode root) {
        if (root == null) return;
        List<GraphNode> rootPre = root.getPre();

        if (rootPre == null || rootPre.size() == 0) {
            //没有儿子了，可以加入
            container.add(root);
            return;
        }
        for (GraphNode pre : rootPre) {
            DFS(container, pre);
        }
    }

    private static PropStatus assignAndPropagate(int assignedVar) {
        int candidateDecisionVar = assignedVar;
        boolean hasVar = false;
        boolean useBornUnitAssign = false;
        //pick an unsigned
        if (assignedVar == 0) {
            //先找只有一个变量的clause
            for (Clause clause : clauseList) {
                if (clause.bornUnit) {
                    int var = clause.literals.get(0);
                    if ((var > 0 && !isAssignPos[var]) || (var < 0 && !isAssignNeg[-var])) {
                        candidateDecisionVar = var;
                        hasVar = true;
                        useBornUnitAssign = true;
                        clause.isSatisfied = true;
                        break;
                    }
                }
            }
            if (!hasVar) {
                for (int i = 1; i < isAssignPos.length; i++) {

                    boolean b = isAssignPos[i] || isAssignNeg[i];
                    if (!b && allLiterals.contains(i)) {
                        //没有外部值，随机赋值,这里直接取正了
                        candidateDecisionVar = i;
                        hasVar = true;
                        break;
                    }
                }
            }

            //所有var都已经赋值，SUCCESS
            if (!hasVar) return new PropStatus(0, false, true);
        }

        //choose this one
        if (candidateDecisionVar > 0) isAssignPos[candidateDecisionVar] = true;
        else isAssignNeg[-candidateDecisionVar] = true;
//        System.out.println("选择 : "+candidateDecisionVar);
        //insight 未满足的clause一定剩至少两个var，如果其中一个watch lit被assign
        //             另一个立即被推出，故不存在clause仅有一个unassigned var 的情况

        //Construct Graph
        GraphNode graphNode = new GraphNode(null, curDecisionLevel, candidateDecisionVar, 0);
        Queue<GraphNode> queue = new LinkedList<>();
        ArrayList<GraphNode> levelList = new ArrayList<>();
        level2GraphNode.set(curDecisionLevel, levelList);

        queue.offer(graphNode);
        while (!queue.isEmpty()) {
            GraphNode node = queue.poll();
            //将修改状态数组挪到这里
            if (node.var > 0) isAssignPos[node.var] = true;
            else isAssignNeg[Utils.getPositive(node.var)] = true;
            //查CONFLICT
            boolean isConflict = false;
            if (node.var > 0) {
                isConflict = isAssignNeg[node.var];
            } else if (node.var < 0) {
                isConflict = isAssignPos[-node.var];
            }

            lit2GraphNode.put(node.var, node);
            levelList.add(node);
            //uniform operation
            if (isConflict) {
                if (useBornUnitAssign) {
                    //永远无法满足
                    return new PropStatus(true);
                }
                //generate a virtual Conflict Node And do backtrack
                GraphNode node1 = lit2GraphNode.get(node.var);
                GraphNode node2 = lit2GraphNode.get(-node.var);
                List<GraphNode> preList = new ArrayList<>();
                preList.add(node1);
                preList.add(node2);
                GraphNode conflictNode = new GraphNode(preList, node.decisionLevel, 0, 2);
                levelList.add(conflictNode);
                PropStatus conflictStatus = new PropStatus(conflictNode);
                return conflictStatus;
            }

            //find all affected clauses

            List<Clause> affectedClause = literal2Clause.get(Utils.getPositive(node.var));

            for (Clause clause : affectedClause) {

                //insight 未满足的clause一定剩至少两个var，如果其中一个watch lit被assign
                //             另一个立即被推出，故不存在clause仅有一个unassigned var 的情况

                if (clause.isSatisfied) continue;
                for (Integer clit : clause.literals) {
                    if (clit == node.var) {
                        //直接满足
                        clause.isSatisfied = true;
                        break;
                    }
                }
                if (clause.isSatisfied) continue;
                if (clause.bornUnit) {
                    if (node.var == clause.literals.get(0)) continue;
                    else return new PropStatus(true);
                }
                int watch1pos = Utils.getPositive(clause.watch1);
                int watch2pos = Utils.getPositive(clause.watch2);
                int nodeVarPos = Utils.getPositive(node.var);
                //todo 12-16 1:15 node.var->nodeVarPos , 加入true直接satisfied的逻辑
                boolean isCandidateWatch1;
                if (nodeVarPos == watch1pos) {
                    if (node.var == clause.watch1) {
                        //satisfy directly
                        clause.isSatisfied = true;
                        continue;
                    }
                    isCandidateWatch1 = true;
                    //推出watch2
                    //add to graph
                    //这里图节点给出直接前驱就可以,也就是clause的其他literal
                    //由于unit prop，clause中的其他literal一定已经赋值
                    //还要维护literal->graph node的map，但注意，每次清除后，不需要
                    //处理这个map，不需要进行remove，后来的进行覆盖即可？

                    //check 2 condition of watch list
                    boolean unitFlag = checkIfUnitClause(clause, watch1pos, isCandidateWatch1);
                    //check done ，no other unassigned var
                    //unit prop
                    //考虑到每次var赋值都会把所有涉及的clause处理一遍，所以conflict
                    //应该只能是本层
                    //-----------------------------------------------UNIT --------------------------------------------
                    if (unitFlag) {
                        clause.isSatisfied = true;
                        //unit时直接保持符号跟watch一致


                        //bug 这里修改状态数组会导致跟 还在队列中未加入图中的node冲突

//                        if (clause.watch2 > 0) isAssignPos[watch2pos] = true;
//                        else isAssignNeg[watch2pos] = true;


                        //保证该clause为true，再看有没有conflict
                        //构造前驱pre graphNode
                        //也就是所有var都已经赋值
                        //那怎么索引这些var的graphNode呢，那就再搞个数据结构
                        //反正back jump 用论文的方法的话 o(n)跑不了
                        GraphNode newNode = generateGraphNode(clause, !isCandidateWatch1);
                        queue.offer(newNode);
                        //change : 数据结构修改 改在了开头

                    }

                }
                else if (nodeVarPos == watch2pos) {
                    if (node.var == clause.watch2) {
                        //satisfy directly
                        clause.isSatisfied = true;
                        continue;
                    }
                    isCandidateWatch1 = false;
                    boolean unitFlag = checkIfUnitClause(clause, watch2pos, isCandidateWatch1);
                    if (unitFlag) {
                        //unit时直接保持符号跟watch一致
                        clause.isSatisfied = true;
//                        if (clause.watch1 > 0) isAssignPos[watch1pos] = true;
//                        else isAssignNeg[watch1pos] = true;
                        //保证该clause为true，再看有没有conflict
                        GraphNode newNode = generateGraphNode(clause, !isCandidateWatch1);
                        queue.offer(newNode);

//                        levellist(也就是level2GraphNode的每层) 放在开头add node
                    }
                }
                //查看queue里的元素
                System.out.println("mark");
            }
        }
        level2GraphNode.set(curDecisionLevel, levelList);
        //queue空了 返回既不conflict又不all done

        //todo 怎么跑到这一行，queue空，赋值-1，q为啥空，-1-> -2  3&-3 conflict 没道理到这
        return new PropStatus(0, false, false);


    }

    /**
     * check done
     *
     * @param clause
     * @param secLastpos
     * @param isWatch1   本次选择变量是否命中watch1
     * @return
     */
    private static boolean checkIfUnitClause(Clause clause, int secLastpos, boolean isWatch1) {
        boolean unitFlag = true;
        for (int lit : clause.literals) {
            int literal = lit > 0 ? lit : -lit;
            //watchpos1->secLastpos
//            if (literal == secLastpos) continue;
            if (lit == clause.watch1 || lit == clause.watch2) continue;

            if (!isAssignPos[literal] && !isAssignNeg[literal]) {
                //存在非watch list中的未赋值元素
                //替换watch1 到watch list
                if (isWatch1) clause.watch1 = lit;
                else clause.watch2 = lit;
                unitFlag = false;
            }
        }
        return unitFlag;
    }

    /**
     * check done
     *
     * @param clause
     * @param isGenWatch1
     * @return
     */
    public static GraphNode generateGraphNode(Clause clause, boolean isGenWatch1) {
        List<GraphNode> preNodes = new ArrayList<>();
        for (Integer lit : clause.literals) {
            if (isGenWatch1) {
                if (lit == clause.watch1) continue;
            } else {
                if (lit == clause.watch2) continue;
            }
            //正负
            GraphNode gNode = lit2GraphNode.getOrDefault(lit, null);
            if (gNode == null) gNode = lit2GraphNode.get(-lit);

            //前驱加入
            preNodes.add(gNode);
        }
        // maintain data structure
        GraphNode newNode;
        if (isGenWatch1) newNode = new GraphNode(preNodes, curDecisionLevel, clause.watch1, 1);
        else newNode = new GraphNode(preNodes, curDecisionLevel, clause.watch2, 1);

        return newNode;
    }
}


