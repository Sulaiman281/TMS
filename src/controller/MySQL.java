package controller;

import models.Game;
import models.Match;
import models.Tournament;
import sample.Singleton;

import java.sql.*;
import java.util.ArrayList;

public class MySQL {
    public Connection connection;
    public void authorize(){
        try{
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/copdatabase","root","Kiskomara8");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public ArrayList<Tournament> loadTournaments(){
        ArrayList<Tournament> tournaments = new ArrayList<>();
        String query = "SELECT * FROM tournament;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();
            while(results.next()){
                int id = results.getInt(1);
                String title = results.getString(2);
                String gameId = getGame(results.getInt(3));
                String winnerId = getWinner(results.getInt(5));

                Tournament tournament = new Tournament();
                tournament.setId(id);
                tournament.setTitle(title);
                tournament.setGameID(gameId);
                tournament.setWinnerID(winnerId);
                tournament.setSize(results.getInt(4));
                tournament.setState(results.getString(6));
                tournaments.add(tournament);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            // show the alert box as error.
        }
        return tournaments;
    }

    private String getWinner(Integer anInt) throws SQLException {
        if(anInt == null || anInt <=0){
            return "Undefined";
        }
        String query = "SELECT * FROM participant WHERE ID = "+anInt+";";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet results = statement.executeQuery();
        results.next();
        return results.getString(2);
    }

    private String getGame(int anInt) throws SQLException {
        String query = "SELECT * FROM game WHERE ID = "+anInt+";";
        PreparedStatement statement = connection.prepareStatement(query);
        ResultSet results = statement.executeQuery();
        results.next();
        return results.getString(2);
    }
    public int getGame(String gName) {
        try {
            String query = "SELECT * FROM game WHERE name = '" + gName + "';";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getInt(1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return -1;
    }

    public void addGame(Game game){
        try {
            PreparedStatement statement = connection.prepareStatement(game.getQuery());
            statement.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public Match getMatch(int id){
        Match match = new Match();
        String query = "SELECT * FROM matches WHERE ID = "+id+";";
        try{
            PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
            ResultSet r = statement.executeQuery();
            while(r.next()){
                match.setId(r.getInt(1));
                match.setTournamentID(r.getInt(2));
                match.setParticipant1ID(r.getInt(3));
                match.setParticipant2ID(r.getInt(4));
                match.setStages(r.getInt(5));
                match.setOrder(r.getInt(6));
                match.setWinnerParticipantID(r.getInt(7));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return match;
    }

    public ArrayList<Game> getGames(){
        ArrayList<Game> games = new ArrayList<>();
        String query = "SELECT * FROM game;";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                Game game = new Game();
                game.setId(result.getInt(1));
                game.setName(result.getString(2));
                games.add(game);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return games;
    }

    public ArrayList<Integer> getParticipantFromTournament(int tournamentID){
        ArrayList<Integer> p_ids = new ArrayList<>();
        String query = "SELECT * FROM participantintournament WHERE TournamentID = "+tournamentID+";";
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();

            while(result.next()){
                p_ids.add(result.getInt(3));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return p_ids;
    }
    public String getParticipantName(int id){
        String name = "";
        try {
            String query = "SELECT * FROM participant WHERE id = " + id + ";";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet result = statement.executeQuery();
            if(result.next())
                name = result.getString(2);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return name;
    }
}