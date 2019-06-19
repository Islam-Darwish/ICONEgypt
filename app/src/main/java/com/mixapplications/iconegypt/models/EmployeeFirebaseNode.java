package com.mixapplications.iconegypt.models;

import java.util.ArrayList;

public class EmployeeFirebaseNode {
    private String email = "";
    private ArrayList<EmployeeFirebaseNode> children = new ArrayList<>();
    //private EmployeeFirebaseNode parent = null;

    public EmployeeFirebaseNode(String email) {
        this.email = email;
    }

    public void addChild(EmployeeFirebaseNode child) {
        this.children.add(child);
        //  child.parent = this;
    }

    public void addChild(String email) {
        EmployeeFirebaseNode child = new EmployeeFirebaseNode(email);
        this.children.add(child);
        //child.parent = this;
    }

    public void addChildren(EmployeeFirebaseNode... children) {
        for (EmployeeFirebaseNode child : children) {
            this.addChild(child);
        }
    }


    public ArrayList<EmployeeFirebaseNode> getChildren() {
        return children;
    }

    public void setParent(EmployeeFirebaseNode parent) {
        //this.parent = parent;
        parent.addChild(this);
    }

    /*
        public EmployeeFirebaseNode getParent() {
            return this.parent;
        }
    */
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /*  public boolean isRoot(){
          return (this.parent == null);
      }
      */
    public boolean isLeef() {
        return (this.children.size() == 0);
    }

    /*public void removeParent(){
        this.parent = null;
    }*/
    public EmployeeNode toEmployeeNode() {
        EmployeeNode employeeNode = new EmployeeNode(this.email);
        if (!this.isLeef()) {
            for (EmployeeFirebaseNode node : this.getChildren()) {
                employeeNode.addChild(node.toEmployeeNode());
            }
        }
        return employeeNode;
    }
}
