package org.example.entities;

public class AssignedVar {
    int lit;//正负都可,按照实际赋值来，
    // e.g. 若-10，那么在有-10的句子里是true，直接子句成立，
    //        若子句里面为10，则该项为false

    //direct reason可以直接通过unit所在的子句推断，但是root reason不行
    //还是得维护关系图


}
