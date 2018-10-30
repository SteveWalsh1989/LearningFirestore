package com.example.steve.learnfirestore;

public class client {

    private String documentID;
    private String name;
    private String email;
    private int filter;




    // public constructor needed for firestore
    public client(){}


    public client(String name, String email){
        this.name = name;
        this.email = email;
    }

    public client(String client_name, String client_email, int filter) {
        this.name = client_name;
        this.email = client_email;
        this.filter = filter;
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

    public int getFilter() {
        return filter;
    }

    public void setFilter(int filter) {
        this.filter = filter;
    }
}
