package models;

public class Match {
    private int id;
    private int tournamentID;
    private int participant1ID;
    private int participant2ID;
    private int stages;
    private int order;
    private int winnerParticipantID;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTournamentID() {
        return tournamentID;
    }

    public void setTournamentID(int tournamentID) {
        this.tournamentID = tournamentID;
    }

    public int getParticipant1ID() {
        return participant1ID;
    }

    public void setParticipant1ID(int participant1ID) {
        this.participant1ID = participant1ID;
    }

    public int getParticipant2ID() {
        return participant2ID;
    }

    public void setParticipant2ID(int participant2ID) {
        this.participant2ID = participant2ID;
    }

    public int getStages() {
        return stages;
    }

    public void setStages(int stages) {
        this.stages = stages;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getWinnerParticipantID() {
        return winnerParticipantID;
    }

    public void setWinnerParticipantID(int winnerParticipantID) {
        this.winnerParticipantID = winnerParticipantID;
    }
}
