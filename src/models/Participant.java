package models;

public class Participant {

    private int id;
    private String name;
    private boolean isTemp;
    private boolean isTeam;

    public Participant(){

    }
    public Participant(String _name,boolean isTemp, boolean isTeam){
        setName(_name);
        setTemp(isTemp);
        setTeam(isTeam);
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

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public boolean isTeam() {
        return isTeam;
    }

    public void setTeam(boolean team) {
        isTeam = team;
    }
}
