package fr.wildcodeschool.chantome.wildoldschool;

import java.util.Date;
import java.util.Map;

/**
 * Created by chantome on 22/09/2016.
 */
public class User{
    private String email;
    private String firstname;//*
    private String lastname;//*
    private String pseudo;
    private String genre;//*
    private int age;
    private String desc;
    private int photo;
    private int categorie;
    private Date created_on;
    private boolean online;
    private boolean writing;
    private int profil_complete;
    private Map<String,Chat> created_chats;
    private Map<String,String> list_added_chats;
    private String adresse;
    private String ecole_wcs;
    private String formation;

    public User(){
    }

    public User(String pseudo){
        setPseudo(pseudo);
    }

    public User(String pseudo, boolean online){
        setPseudo(pseudo);
        setOnline(online);
    }

    //setter
    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public void setPseudo(String pseudo){ this.pseudo = pseudo; }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public void setCategorie(int categorie) {
        this.categorie = categorie;
    }

    public void setCreated_on(Date created_on) {
        this.created_on = created_on;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    public void setProfil_complete(int profil_complete) {
        this.profil_complete = profil_complete;
    }

    public void setCreated_chats(Map<String, Chat> created_chats) {
        this.created_chats = created_chats;
    }

    public void setList_added_chats(Map<String, String> list_added_chats) {
        this.list_added_chats = list_added_chats;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public void setEcole_wcs(String ecole_wcs) {
        this.ecole_wcs = ecole_wcs;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    //getter

    public String getEmail() {
        return this.email;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public String getPseudo(){ return this.pseudo; }

    public String getLastname() {
        return this.lastname;
    }

    public String getGenre() {
        return this.genre;
    }

    public int getAge() {
        return this.age;
    }

    public String getDesc() {
        return this.desc;
    }

    public int getPhoto() {
        return this.photo;
    }

    public int getCategorie() {
        return this.categorie;
    }

    public Date getCreated_on() {
        return this.created_on;
    }

    public boolean isOnline() {
        return this.online;
    }

    public boolean isWriting() {
        return this.writing;
    }

    public int getProfil_complete() {
        return this.profil_complete;
    }

    public Map<String, Chat> getCreated_chats() {
        return this.created_chats;
    }

    public Map<String, String> getList_added_chats() {
        return this.list_added_chats;
    }

    public String getAdresse() {
        return this.adresse;
    }

    public String getEcole_wcs() {
        return this.ecole_wcs;
    }

    public String getFormation() {
        return this.formation;
    }
}
