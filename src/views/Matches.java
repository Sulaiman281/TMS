package views;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import models.Match;
import models.Tournament;
import sample.Singleton;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Matches {

    private HBox root;
    private Tournament tournament;
    public Matches(Tournament tournament){
        this.tournament = tournament;

        root = new HBox();
        root.setAlignment(Pos.TOP_LEFT);

        if(tournament.getState().equals(tournament.CREATED)){
            notStarted();
        }else{
            setupStages();
        }
    }
    private void notStarted(){
        Button start = Singleton.getInstance().getButton("Start Tournament");
        start.setOnAction(e->{
            // change the state of tournament
            String query = "UPDATE tournament SET TournamentState ='"+tournament.STARTED+"' WHERE ID = "+tournament.getId()+";";
            try {
                PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                statement.execute();
                createStages(Singleton.getInstance().sql.getParticipantFromTournament(tournament.getId()).size());

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            // start the tournament here.
        });
        root.getChildren().add(start);
    }

    int stage_count = 0;
    private void createStages(int total_participants){
        System.out.println("Participants: "+total_participants+","+tournament.getSize());
        int order = 0;
        do{
            if(total_participants % 2 == 0)
                total_participants = total_participants/2;
            else
                total_participants = total_participants/2+1;
            System.out.println(total_participants);
            try {
                stage_count++;
                for (int i = 0; i < total_participants; i++) {
                    String query = "INSERT INTO matches (tournamentID,stage,orderBy) " +
                            "VALUES("+tournament.getId()+","+ stage_count +","+(++order)+");";
                    PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                    System.out.println(!statement.execute() ? "YES" : "NO");
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

        }while(total_participants > 1);
        moveParticipantToStage1();
        root.getChildren().clear();
        setupStages();
    }

    ArrayList<Integer> stages = new ArrayList<>();

    private void setupStages(){
        String query = "SELECT * FROM matches WHERE tournamentID = "+tournament.getId()+";";
        try{
            PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
            ResultSet r = statement.executeQuery();
            while(r.next()){
                stages.add(r.getInt(5));
//                System.out.println("Stage: "+r.getInt(5));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        stages = removeDuplicate();
        validateStages();
        for (Integer integer : stages) {
            root.getChildren().add(getStages(integer));
        }
    }

    private ListView<HBox> getStages(int stage){
        ListView<HBox> listView = new ListView<>();
        listView.setPrefWidth(400);

        // heading for stage.
        Label heading = Singleton.getInstance().getLabel(
                "Stage "+stage,
                Color.BLUEVIOLET,
                22
        );
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(heading);
        listView.getItems().add(box);

        String query = "SELECT * FROM matches WHERE stage = "+stage+" AND tournamentID = "+tournament.getId()+";";
        try{
            PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
            ResultSet r = statement.executeQuery();
            while(r.next()){
                listView.getItems().add(getMatch(r.getInt(1)));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return listView;
    }

    private ArrayList<Integer> removeDuplicate(){
        ArrayList<Integer> nStages = new ArrayList<>();

        for (Integer integer : stages) {
            if(!nStages.contains(integer)){
                nStages.add(integer);
            }
        }
        return nStages;
    }

    private HBox getMatch(int match_id){
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(30);

        Label vs = Singleton.getInstance().getLabel("VS", Color.DARKBLUE,18);
        box.getChildren().add(vs);
        Match match = Singleton.getInstance().sql.getMatch(match_id);

        if(match.getParticipant1ID() == 0 && match.getParticipant2ID() == 0){ // when there is no participant in stage.
            Label msg = Singleton.getInstance().getLabel("Match Not Fixed",Color.YELLOWGREEN,18);
            box.getChildren().clear();
            box.getChildren().add(msg);
        } else if(match.getParticipant2ID() == 0) {
            VBox p = playerBox(match.getWinnerParticipantID(),match.getParticipant1ID());
            box.getChildren().clear();
            box.getChildren().add(p);
        }else if(match.getWinnerParticipantID() == 0){ // while winner is not announced yet.
            // participant 1 details
            VBox p1Box = new VBox();
            p1Box.setSpacing(5);
            p1Box.setSpacing(5);
            p1Box.setAlignment(Pos.TOP_CENTER);
            HBox b_Box = new HBox();
            b_Box.setSpacing(5);
            Button w_btn1 = new Button("W");
            w_btn1.setFont(Font.font("Arial",11));
            w_btn1.setStyle(
                    "-fx-background-color: green"
            );
            w_btn1.setTextFill(Color.WHITE);
            w_btn1.setPrefWidth(40);
            w_btn1.setPrefHeight(30);
            w_btn1.setOnAction(e->{
                onWinEvent(match.getParticipant1ID(),match_id);
                nextStage(match.getParticipant1ID(),match.getStages()); // move player to next stage
                setupStages();
            });
            Button l_btn1 = new Button("L");
            l_btn1.setOnAction(e->{
                onWinEvent(match.getParticipant2ID(),match_id);
                nextStage(match.getParticipant2ID(),match.getStages()); // move player to next stage
                setupStages();
            });
            l_btn1.setFont(Font.font("Arial",11));
            l_btn1.setStyle(
                    "-fx-background-color: red"
            );
            l_btn1.setTextFill(Color.WHITE);
            l_btn1.setPrefWidth(40);
            l_btn1.setPrefHeight(30);
            b_Box.getChildren().addAll(w_btn1,l_btn1);
            Label p1Name = Singleton.getInstance().getLabel(
                    Singleton.getInstance().sql.getParticipantName(match.getParticipant1ID()),
                    Color.web("#b0f551"),
                    14
            );
            p1Box.getChildren().addAll(b_Box,p1Name);

            box.getChildren().add(0,p1Box);

            // participant 1 details
            VBox p2Box = new VBox();
            p2Box.setSpacing(5);
            p2Box.setAlignment(Pos.TOP_CENTER);
            HBox b_Box2 = new HBox();
            b_Box2.setSpacing(5);
            Button w_btn2 = new Button("W");
            w_btn2.setFont(Font.font("Arial",11));
            w_btn2.setStyle(
                    "-fx-background-color: green"
            );
            w_btn2.setTextFill(Color.WHITE);
            w_btn2.setPrefWidth(40);
            w_btn2.setPrefHeight(30);
            w_btn2.setOnAction(e->{
                onWinEvent(match.getParticipant2ID(),match_id);
                nextStage(match.getParticipant2ID(),match.getStages()); // move player to next stage
                setupStages();
            });
            Button l_btn2 = new Button("L");
            l_btn2.setFont(Font.font("Arial",11));
            l_btn2.setOnAction(e->{
                onWinEvent(match.getParticipant1ID(),match_id);
                nextStage(match.getParticipant1ID(),match.getStages()); // move player to next stage
                setupStages();
            });
            l_btn2.setStyle(
                    "-fx-background-color: red"
            );
            l_btn2.setTextFill(Color.WHITE);
            l_btn2.setPrefWidth(40);
            l_btn2.setPrefHeight(30);
            b_Box2.getChildren().addAll(w_btn2,l_btn2);
            Label p2Name = Singleton.getInstance().getLabel(
                    Singleton.getInstance().sql.getParticipantName(match.getParticipant2ID()),
                    Color.web("#b0f551"),
                    14
            );
            p2Box.getChildren().addAll(b_Box2,p2Name);
            box.getChildren().add(p2Box);
        }else{
            VBox p1 = playerBox(match.getWinnerParticipantID(), match.getParticipant2ID());
            VBox p2 = playerBox(match.getWinnerParticipantID(), match.getParticipant1ID());
            box.getChildren().add(0,p1);
            box.getChildren().add(p2);
        }
        return box;
    }

    private void onWinEvent(int participantID, int matchId){
        try{
            String query = "UPDATE matches SET winner = "+participantID+" WHERE ID = "+matchId+";";
            Singleton.getInstance().sql.connection.prepareStatement(query).execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        root.getChildren().clear();
    }

    private VBox playerBox(int winnerID, int pId){
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);
        Label state = Singleton.getInstance().getLabel(
                winnerID == pId ? "W" : "L",
                winnerID == pId ? Color.GREEN : Color.RED,
                18
        );
        Label name = Singleton.getInstance().getLabel(
                Singleton.getInstance().sql.getParticipantName(pId),
                Color.ORANGE,
                12
        );
        box.getChildren().addAll(state,name);
        return box;
    }

    private void moveParticipantToStage1(){
        int p1,p2 = 0;
        ArrayList<Integer> matchIds = getMatchesFromStage(1);

        try {
            String q = "SELECT * FROM participantintournament WHERE TournamentID = " + tournament.getId() + ";";
            PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(q);
            ResultSet rs = statement.executeQuery();
            int matchIndex = 0;
            while (rs.next()) {
                if(matchIndex >= matchIds.size()) break;
                p1 = rs.getInt(3);
                if (rs.next())
                    p2 = rs.getInt(3);


                String update = "UPDATE matches SET participant1 = " + p1 + ", participant2 = " + p2 + " WHERE ID = " + matchIds.get(matchIndex++) + ";";
                Singleton.getInstance().sql.connection.prepareStatement(update).execute();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    private void nextStage(int p1,int currentStage){
        // check if there is next stage.
        int nextStage = currentStage+1;
        if(stages.contains(nextStage)){
            String query = "SELECT * FROM matches WHERE stage = "+nextStage+" AND tournamentID = "+tournament.getId()+";";
            int emptySlotInMatch;
            try{
                PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
                ResultSet r = statement.executeQuery();
                while(r.next()){
                    if(r.getInt(3) == 0 ) {
                        emptySlotInMatch = r.getInt(1);
                        String update = "UPDATE matches SET participant1 = " + p1 + " WHERE ID = " + emptySlotInMatch + ";";
                        Singleton.getInstance().sql.connection.prepareStatement(update).execute();
                        break;
                    }else if(r.getInt(4) == 0){
                        emptySlotInMatch = r.getInt(1);
                        String update = "UPDATE matches SET participant2 = " + p1 + " WHERE ID = " + emptySlotInMatch + ";";
                        Singleton.getInstance().sql.connection.prepareStatement(update).execute();
                        break;
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private void validateStages(){
        int size = 1;
        for (Integer stage : stages) {// check every match in stages if they all have winner. then check if a single match has solo participant make him winner.
            int count = 0;
            Match not_complete = null;
            ArrayList<Integer> matches = getMatchesFromStage(stage);
            for (Integer match : matches) {
                Match m = Singleton.getInstance().sql.getMatch(match);
                if(m.getParticipant1ID() == 0 && m.getParticipant2ID() == 0) continue; // the match is not fixed yet.
                if(m.getWinnerParticipantID() != 0) count++;
                else not_complete = m;
            }
            if(matches.size() == count){ // means all the matches all finished.
                // do nothing. hehe
            }else if(matches.size()-1 == count){
                if(not_complete == null) return;
                try {
                    if(not_complete.getParticipant1ID() != 0 && not_complete.getParticipant2ID() == 0) { // checking again if the it's solo or not.
                        int p = not_complete.getParticipant1ID();
                        String update = "UPDATE matches SET winner = " + p + " WHERE ID = " + not_complete.getId() + ";";
                        Singleton.getInstance().sql.connection.prepareStatement(update).execute();
                        nextStage(p, not_complete.getStages());
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            // check if it's last stage. then check if it's has the winner.
            if(size == stages.size()){
                ArrayList<Integer> final_match = getMatchesFromStage(size);
                System.out.println("In final stage: "+size+" total matches "+final_match.size());
                if(final_match.size() == 1){
                    Match m = Singleton.getInstance().sql.getMatch(final_match.get(0));
                    if(m.getWinnerParticipantID() != 0){ // check if it has the winner make it tournament winner
                        String update = "UPDATE tournament SET WinnerParticipantID = " + m.getWinnerParticipantID() + ", TournamentState = '"+tournament.FINISHED+"' WHERE ID = " + tournament.getId() + ";";
                        try {
                            Singleton.getInstance().sql.connection.prepareStatement(update).execute();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
                    }
                }
            }
            size++;
        }
    }

    private ArrayList<Integer> getMatchesFromStage(int stage){
        String query = "SELECT * FROM matches WHERE stage = "+stage+" AND tournamentID = "+tournament.getId()+";";
        ArrayList<Integer> matchIds = new ArrayList<>();
        try{
            PreparedStatement statement = Singleton.getInstance().sql.connection.prepareStatement(query);
            ResultSet r = statement.executeQuery();
            while(r.next()){
                matchIds.add(r.getInt(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return matchIds;
    }

    public HBox getRoot() {
        return root;
    }
}
