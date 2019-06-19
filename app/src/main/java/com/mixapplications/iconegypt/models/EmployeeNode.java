package com.mixapplications.iconegypt.models;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmployeeNode {
    private String email = "";
    private ArrayList<EmployeeNode> children = new ArrayList<>();
    private EmployeeNode parent = null;

    public EmployeeNode(String email) {
        this.email = email;
    }

    public void addChild(EmployeeNode child) {
        this.children.add(child);
        child.parent = this;
    }

    public void addChild(String email) {
        EmployeeNode child = new EmployeeNode(email);
        this.children.add(child);
        child.parent = this;
    }

    public void addChildren(ArrayList<EmployeeNode> children) {
        for (EmployeeNode child : children) {
            child.parent = this;
        }
        this.children.addAll(children);
    }


    public ArrayList<EmployeeNode> getChildren() {
        return children;
    }

    public EmployeeNode getParent() {
        return this.parent;
    }

    public void setParent(EmployeeNode parent) {
        this.parent = parent;
        parent.addChild(this);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isRoot() {
        return (this.parent == null);
    }

    public boolean isLeef() {
        return (this.children.size() == 0);
    }

    public void removeParent() {
        this.parent = null;
    }

    public EmployeeNode findChild(String email) {
        if (this.email.equalsIgnoreCase(email))
            return this;
        EmployeeNode result = null;
        for (EmployeeNode child : this.children) {
            if (child.email.equalsIgnoreCase(email)) {
                result = child;
                break;
            } else {
                result = child.findChild(email);
                if (result != null)
                    break;
            }
        }
        return result;
    }


    public List<EmployeeNode> returnAllNodes(EmployeeNode node) {
        List<EmployeeNode> listOfNodes = new ArrayList<EmployeeNode>();
        addAllNodes(node, listOfNodes);
        return listOfNodes;
    }

    private void addAllNodes(EmployeeNode node, List<EmployeeNode> listOfNodes) {
        if (node != null) {
            listOfNodes.add(node);
            List<EmployeeNode> children = node.getChildren();
            if (children != null) {
                for (EmployeeNode child : children) {
                    addAllNodes(child, listOfNodes);
                }
            }
        }
    }


    public List<Employee> listChildren(DatabaseReference ref) {
        List<EmployeeNode> listOfNodes = new ArrayList<>();
        final List<Employee> listOfEmployee = new ArrayList<>();

        for (int i = 0; i < returnAllNodes(this).size(); ++i) {
            EmployeeNode n = returnAllNodes(this).get(i);
            List<EmployeeNode> children = n.getChildren();
            if (children != null) {
                for (EmployeeNode child : children) {
                    if (!listOfNodes.contains(child)) {
                        listOfNodes.add(child);
                    }
                }
            }
        }

        for (EmployeeNode employeeNode : listOfNodes) {
            Query fireQuery =  ref.child("employees").orderByChild("email").equalTo(employeeNode.getEmail());
            fireQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {
                    for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                        Employee em = snapshot1.getValue(Employee.class);
                        listOfEmployee.add(em);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        while (listOfEmployee.size() < listOfNodes.size()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return listOfEmployee;
    }
}
