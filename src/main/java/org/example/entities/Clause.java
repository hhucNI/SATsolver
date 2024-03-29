package org.example.entities;


import lombok.Data;

import java.util.*;


@Data
public class Clause {

    public boolean bornUnit = false;
    public boolean isGenerated = false;
    public int counter;
    //长度<10的子句:Array或者List
    //TODO 优化，根据文件名或者第一行指定ArrayList Initial Size


    /**
     * literals中 正负均有
     */
    public List<Integer> literals = new ArrayList<>();
    public HashSet<Integer> litSet;
    /**
     * watch1 : 正负均可
     */
    public int watch1;


    /**
     * watch2 : 正负均可
     */
    public int watch2;


    /**
     * 如果有一个false翻转成true，则直接满足
     */
    public boolean isSatisfied = false;


}
