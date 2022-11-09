package models;

public class Game {
    private int id;
    private String name;

    public Game(){

    }

    public Game(String _name){
        this.name = _name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuery(){
        return "INSERT INTO game (name) VALUES ('"+getName()+"')";
    }
}
