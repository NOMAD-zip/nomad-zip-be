package com.nz.nomadzip.relation.domain;


public enum RoleType {
    LEADER, MEMBER;

    public boolean isLeader(){
        return this == LEADER;
    }

    public boolean isMember(){
        return this == MEMBER;
    }
}
