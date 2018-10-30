package com.example.steve.learnfirestore;

public class client {

    private String documentID;
    private String name;
    private String email;


    // public constructor needed for firestore
    public client(){}


    public client(String name, String email){
        this.name = name;
        this.email = email;
    }




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }
}
